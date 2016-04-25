package org.latency4j.processing;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import org.latency4j.CappedLatencyRequirement;
import org.latency4j.AlertHandler;
import org.latency4j.LatencyRequirement;
import org.latency4j.StatisticalLatencyRequirement;
import org.latency4j.WorkDuration;
import org.latency4j.persistence.WorkDurationPersistenceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * A {@link Thread thread} implementation which processes {@link WorkDuration
 * durations} generated from monitored
 * {@link LatencyRequirement#getWorkCategory() operations}. This class makes it
 * possible for {@link WorkDuration duration} processing to take place
 * asynchronously from the execution thread.
 * </p>
 * <p>
 * Internally, this class uses a queue to hold the
 * {@link #processTaskCompletion(LatencyRequirement, WorkDuration) incoming}
 * {@link WorkDuration durations} of completed
 * {@link LatencyRequirement#getWorkCategory() operations}. These are
 * {@link #run() asynchronously} removed from the queue and then processed,
 * triggering any relevant {@link AlertHandler alerts}.
 * </p>
 * 
 * @see AsynchronousLatencyMonitor
 * @see AsynchronousLatencyMonitorFactory
 */
public class LatencyProcessor extends Thread {

	/**
	 * <p>
	 * Internal logger.
	 * </p>
	 */
	private static final Logger logger = LoggerFactory.getLogger(LatencyProcessor.class);

	/**
	 * <p>
	 * Map of {@link RequirementStatisticsMap requirement specific} latency
	 * statistics.
	 * </p>
	 */
	private final Map<String, RequirementStatisticsMap> statisticsMap;

	/**
	 * <p>
	 * Provides a staging queue for {@link WorkDuration durations} waiting to be
	 * processed.
	 * </p>
	 * 
	 * @see ProcessingQueueEntry
	 */
	private final BlockingDeque<ProcessingQueueEntry> processingQueue;

	/**
	 * <p>
	 * Constructor.
	 * </p>
	 */
	public LatencyProcessor() {
		this.processingQueue = new LinkedBlockingDeque<ProcessingQueueEntry>();
		this.statisticsMap = new HashMap<String, RequirementStatisticsMap>();
		setDaemon(true);
	}

	/**
	 * <p>
	 * Asynchronous {@link WorkDuration duration} processing logic. This method
	 * dequeues {@link ProcessingQueueEntry#getDuration() durations} from the
	 * {@link #processingQueue inbound queue}, processes them and triggers any
	 * relevant {@link AlertHandler alerts}. After processing it persists
	 * the {@link WorkDuration durations} to storage using the
	 * {@link WorkDurationPersistenceManager persistence manager} specified for
	 * the {@link ProcessingQueueEntry#getRequirement() requirement} to which
	 * the {@link ProcessingQueueEntry#getDuration() duration} relates.
	 * </p>
	 */
	@Override
	public void run() {
		ProcessingQueueEntry processingQueueEntry;
		while (true) {
			try {
				processingQueueEntry = processingQueue.take();
				process(processingQueueEntry);
			} catch (InterruptedException intExce) {
				logger.warn("Asynchronous processing thread interrupted." + "Terminating Epsilon processor.");
				break;
			} catch (Throwable t) {
				logger.warn("Unexpected error in asynchronous processing " + "thread. Skipping WorkDuration instance.",
						t);
			}
		}
	}

	/**
	 * <p>
	 * Queues the {@link WorkDuration duration} for a completed
	 * {@link LatencyRequirement#getWorkCategory() operation} so that it can be
	 * processed asynchronously.
	 * </p>
	 * 
	 * @param requirement
	 *            The {@link LatencyRequirement requirement} to which the
	 *            {@link WorkDuration duration} relates.
	 * @param duration
	 *            The {@link WorkDuration duration} to be processed.
	 */
	protected void processTaskCompletion(final LatencyRequirement requirement, final WorkDuration duration) {
		/*
		 * We do not synchronize this method, because we want to ensure that
		 * monitors can enqueue durations at all times!
		 */
		ProcessingQueueEntry processingQueueEntry = new ProcessingQueueEntry(duration, requirement);
		processingQueue.add(processingQueueEntry);
	}

	/**
	 * <p>
	 * Compares the supplied {@link ProcessingQueueEntry#getDuration() duration}
	 * to the {@link ProcessingQueueEntry#getRequirement() requirement}, and the
	 * {@link WorkStatistics statistics} collated to date, in order to determine
	 * if an {@link AlertHandler alert} should be issued, and triggers
	 * the issuing of such alerts.
	 * </p>
	 * 
	 * @param processingQueueEntry
	 *            The {@link ProcessingQueueEntry Queue entry} encompassing both
	 *            the {@link WorkDuration duration} to process, and the
	 *            {@link LatencyRequirement requirement} to which it relates.
	 */
	private void process(final ProcessingQueueEntry processingQueueEntry) {
		WorkDuration duration = processingQueueEntry.getDuration();
		LatencyRequirement latencyRequirement = processingQueueEntry.getRequirement();

		double elapsedTime = duration.getElapsedTime();
		boolean persist = latencyRequirement.getPersistenceManager() != null;

		// then process requirement
		if (duration.isErrored()) {
			if (!latencyRequirement.isIgnoreErrors()) {
				persist = false;
				NotificationsIssuer.issueWorkCategoryFailureNotification(latencyRequirement, duration);
			}
		} else {
			if (latencyRequirement instanceof StatisticalLatencyRequirement) {
				processAccordingToStatisticalRequirement(duration, (StatisticalLatencyRequirement) latencyRequirement);
			} else {
				CappedLatencyRequirement cappedRequirement = (CappedLatencyRequirement) latencyRequirement;

				if (duration.isRoot() && elapsedTime > cappedRequirement.getExpectedLatency())
					NotificationsIssuer.issueTargetMissedNotification(cappedRequirement, duration);
			}
		}

		if (persist) latencyRequirement.getPersistenceManager().save(duration);
	}

	/**
	 * <p>
	 * Processes the {@link ProcessingQueueEntry#getDuration() duration} against
	 * a statistical {@link ProcessingQueueEntry#getRequirement() requirement}.
	 * Note that this method will only issue {@link AlertHandler alerts}
	 * if the
	 * {@link StatisticalLatencyRequirement#getObservationsSignificanceBarrier()
	 * significance barrier} is breached by the given
	 * {@link ProcessingQueueEntry#getDuration() duration}.
	 * </p>
	 * 
	 * @param duration
	 *            The {@link WorkDuration duration} to process.
	 * @param latencyRequirement
	 *            The {@link LatencyRequirement requirement} to which the
	 *            {@link WorkDuration duration} relates.
	 */
	private void processAccordingToStatisticalRequirement(final WorkDuration duration,
			final StatisticalLatencyRequirement latencyRequirement) {
		RequirementStatisticsMap requirementStatisticsMap = getOrCreateStatisticsMap(latencyRequirement);

		WorkStatistics statistics = requirementStatisticsMap.getOrCreateStatistics(duration);

		if (!statistics.isSignificanceBarrierBreached()) statistics.update(duration.getElapsedTime());
		else {

			long elapsedTime = duration.getElapsedTime();
			double deviationFromMean = elapsedTime - statistics.getRunningAverage();
			double allowedDeviation = statistics.getRunningAverage() * latencyRequirement.getToleranceLevel();

			if (deviationFromMean > allowedDeviation)
				NotificationsIssuer.issueToleranceExceededNotification(latencyRequirement, duration, deviationFromMean,
						statistics.getRunningAverage());
			// continue to collect statistics
			statistics.update(elapsedTime);
		}

	}

	/**
	 * <p>
	 * Returns the {@link RequirementStatisticsMap statistics} collated to date
	 * for the given {@link LatencyRequirement requirement} if any exist. Else,
	 * a new {@link RequirementStatisticsMap statistics entry} is created for
	 * the {@link LatencyRequirement requirement} and returned.
	 * </p>
	 * 
	 * @param latencyRequirement
	 *            The {@link LatencyRequirement requirement} whose
	 *            {@link RequirementStatisticsMap statistics} is to be returned.
	 * 
	 * @return The {@link RequirementStatisticsMap statistics} collated to date
	 *         for the given {@link LatencyRequirement requirement}.
	 */
	private RequirementStatisticsMap getOrCreateStatisticsMap(final StatisticalLatencyRequirement latencyRequirement) {
		RequirementStatisticsMap result = statisticsMap.get(latencyRequirement.getWorkCategory());

		if (result == null) {
			result = new RequirementStatisticsMap(latencyRequirement);
			statisticsMap.put(latencyRequirement.getWorkCategory(), result);
		}
		return result;
	}
}// end class def
