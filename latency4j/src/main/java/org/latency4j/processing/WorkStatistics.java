package org.latency4j.processing;

import org.latency4j.LatencyMonitor;
import org.latency4j.StatisticalLatencyRequirement;
import org.latency4j.WorkDuration;

/**
 * <p>
 * Collates data for a {@link StatisticalLatencyRequirement statistical latency
 * requirement}. <b>Note</b> that instances of this class are not thread-safe.
 * </p>
 */
class WorkStatistics {
	/**
	 * <p>
	 * Total amount of time elapsed executing the
	 * {@link StatisticalLatencyRequirement#getWorkCategory() tasks} covered by
	 * the {@link StatisticalLatencyRequirement requirement} to which the
	 * instance relates.
	 * </p>
	 */
	private double totalTimeToDate;

	/**
	 * <p>
	 * The total number of
	 * {@link StatisticalLatencyRequirement#getWorkCategory() task} executions
	 * to date.
	 * </p>
	 */
	private long numberOfObservations;

	/**
	 * <p>
	 * The current average execution time.
	 * </p>
	 */
	private double runningAverage;

	/**
	 * <p>
	 * The
	 * {@link StatisticalLatencyRequirement#getObservationsSignificanceBarrier()
	 * significance barrier} for the {@link StatisticalLatencyRequirement
	 * requirement} to which the instance relates.
	 * </p>
	 */
	private final long significanceBarrier;

	/**
	 * <p>
	 * Constructor.
	 * </p>
	 * 
	 * @param significanceBarrier
	 *            The
	 *            {@link StatisticalLatencyRequirement#getObservationsSignificanceBarrier()
	 *            significance barrier} of the
	 *            {@link StatisticalLatencyRequirement requirement} being
	 *            {@link LatencyMonitor monitored}.
	 */
	WorkStatistics(final long significanceBarrier) {
		totalTimeToDate = 0.0d;
		numberOfObservations = 0;
		runningAverage = 0.0d;
		this.significanceBarrier = significanceBarrier;
	}

	/**
	 * <p>
	 * Returns the total amount of time elapsed executing the
	 * {@link StatisticalLatencyRequirement#getWorkCategory() tasks} governed by
	 * the {@link StatisticalLatencyRequirement requirement} to which the
	 * instance relates.
	 * </p>
	 * 
	 * @return The total amount of time in milliseconds that has elapsed, in the
	 *         current runtime, for all
	 *         {@link StatisticalLatencyRequirement#getWorkCategory() tasks}
	 *         that fall under a given {@link StatisticalLatencyRequirement
	 *         requirement}.
	 */
	double getTotalTimeToDate() {
		return totalTimeToDate;
	}

	/**
	 * <p>
	 * Returns the total number of task invocations that fall under the
	 * {@link StatisticalLatencyRequirement#getWorkCategory() category} of the
	 * {@link StatisticalLatencyRequirement requirement} to which the instance
	 * relates.
	 * </p>
	 * 
	 * @return The total number of
	 *         {@link StatisticalLatencyRequirement#getWorkCategory() task}
	 *         invocations to date which are covered by the
	 *         {@link StatisticalLatencyRequirement requirement} to which the
	 *         instance is related.
	 */
	long getNumberOfObservations() {
		return numberOfObservations;
	}

	/**
	 * <p>
	 * The current average execution time for
	 * {@link StatisticalLatencyRequirement#getWorkCategory() tasks} covered by
	 * the {@link StatisticalLatencyRequirement requirement} to which the
	 * instance relates. This is calculated both from historical data and
	 * invocations in the current runtime.
	 * </p>
	 * 
	 * @return The current average execution time for
	 *         {@link StatisticalLatencyRequirement#getWorkCategory() tasks}
	 *         covered by the {@link StatisticalLatencyRequirement requirement}
	 *         to which the instance relates.
	 */
	double getRunningAverage() {
		return runningAverage;
	}

	/**
	 * <p>
	 * Updates the internal state of an instance with the observed elapsed time
	 * of an executed {@link StatisticalLatencyRequirement#getWorkCategory()
	 * task}. The fields which are updated as follows:
	 * <ul>
	 * <li>{@link #getNumberOfObservations() numberOfObservations}, and if
	 * relevant the {@link #isSignificanceBarrierBreached()
	 * significance-barrier-breached flag}</li>
	 * <li>{@link #getTotalTimeToDate() totalTimeToDate}</li>
	 * <li>{@link #getRunningAverage() runningAverage}</li>
	 * </ul>
	 * </p>
	 * 
	 * @param elapsedTime
	 *            The observed {@link WorkDuration#getElapsedTime() elapsed
	 *            time} of a
	 *            {@link StatisticalLatencyRequirement#getWorkCategory() task}
	 *            invocation.
	 * 
	 * @return The current {@link #getRunningAverage() running average} of tasks
	 *         falling under the {@link StatisticalLatencyRequirement
	 *         requirement} to which the instance relates.
	 */
	double update(final long elapsedTime) {
		totalTimeToDate += elapsedTime;
		numberOfObservations++;
		runningAverage = totalTimeToDate / (numberOfObservations);

		return runningAverage;
	}

	/**
	 * <p>
	 * Indicates if the if the {@link #getNumberOfObservations() total
	 * observations to date} has breached the {@link #significanceBarrier
	 * significance barrier} as specified in the {@link #WorkStatistics(long)
	 * constructor}.
	 * </p>
	 * 
	 * @return <code>True</code> if the significance barrier has been breached
	 *         and <code>False</code> otherwise.
	 */
	boolean isSignificanceBarrierBreached() {
		return numberOfObservations >= significanceBarrier;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("Task-statistics: sum-" + totalTimeToDate);
		result.append(", #observations-" + numberOfObservations);
		result.append(", average-" + runningAverage);
		result.append(", reachedSignificance barrier-" + isSignificanceBarrierBreached());
		return result.toString();
	}
}// end class def