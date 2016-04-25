package org.latency4j.processing;

import java.util.List;

import org.latency4j.CappedLatencyRequirement;
import org.latency4j.AlertHandler;
import org.latency4j.LatencyRequirement;
import org.latency4j.StatisticalLatencyRequirement;
import org.latency4j.WorkDuration;

/**
 * <p>
 * Utility class which handles the task of invoking {@link AlertHandler
 * alert handlers} in the event of {@link LatencyRequirement requirement}
 * breaches or failures.
 * </p>
 */
class NotificationsIssuer {
	/**
	 * <p>
	 * Issues a notification/alert that a {@link StatisticalLatencyRequirement
	 * statistical requirement} has been breached. The alert is passed to all
	 * {@link AlertHandler alert handlers} registered for the
	 * {@link StatisticalLatencyRequirement requirement} by invoking the method
	 * {@link AlertHandler#latencyDeviationExceededTolerance(StatisticalLatencyRequirement, WorkDuration, double, double)}
	 * </p>
	 * 
	 * @param latencyRequirement
	 *            The {@link LatencyRequirement requirement} to which the
	 *            notification relates.
	 * @param duration
	 *            The {@link WorkDuration duration} of the
	 *            {@link LatencyRequirement#getWorkCategory() task} which
	 *            triggered the alert.
	 * @param deviationFromMean
	 *            The deviation of the task's length/duration from the mean
	 *            {@link WorkDuration duration} observed for the specified
	 *            {@link LatencyRequirement requirement}.
	 * @param mean
	 *            The mean {@link WorkDuration duration} observed to date for
	 *            the specified {@link LatencyRequirement requirement}.
	 */
	static void issueToleranceExceededNotification(final StatisticalLatencyRequirement latencyRequirement,
			final WorkDuration duration, final double deviationFromMean, final double mean) {
		List<AlertHandler> handlers = latencyRequirement.getAlertHandlers();
		if (handlers != null) {
			for (AlertHandler missedTargetHandler : handlers)
				missedTargetHandler.latencyDeviationExceededTolerance(latencyRequirement, duration, deviationFromMean,
						mean);
		}
	}

	/**
	 * <p>
	 * Issues a notification/alert that a {@link CappedLatencyRequirement capped
	 * requirement} has been breached. The alert is passed to all
	 * {@link AlertHandler alert handlers} registered for the
	 * {@link LatencyRequirement requirement} by invoking the method
	 * {@link AlertHandler#latencyExceededCap(CappedLatencyRequirement, WorkDuration)}
	 * </p>
	 * 
	 * @param latencyRequirement
	 *            The {@link LatencyRequirement requirement} to which the
	 *            notification relates.
	 * @param duration
	 *            The {@link WorkDuration duration} of the
	 *            {@link LatencyRequirement#getWorkCategory() task} which
	 *            triggered the alert.
	 */
	static void issueTargetMissedNotification(final CappedLatencyRequirement latencyRequirement,
			final WorkDuration duration) {
		List<AlertHandler> handlers = latencyRequirement.getAlertHandlers();
		if (handlers != null) {
			for (AlertHandler missedTargetHandler : handlers)
				missedTargetHandler.latencyExceededCap(latencyRequirement, duration);
		}
	}

	/**
	 * <p>
	 * Issues a notification/alert that a monitored
	 * {@link LatencyRequirement#getWorkCategory() operation} was terminated
	 * with an exception. The alert is passed to all {@link AlertHandler
	 * alert handlers} registered for the specified {@link LatencyRequirement
	 * requirement} by invoking the method
	 * {@link AlertHandler#workCategoryFailed(LatencyRequirement, WorkDuration)}
	 * .
	 * </p>
	 * 
	 * @param latencyRequirement
	 *            The {@link LatencyRequirement requirement} to which the
	 *            notification relates.
	 * @param duration
	 *            The {@link WorkDuration duration} of the task which triggered
	 *            the alert with the error/exception details populated.
	 */
	static void issueWorkCategoryFailureNotification(final LatencyRequirement latencyRequirement,
			final WorkDuration duration) {
		List<AlertHandler> handlers = latencyRequirement.getAlertHandlers();
		if (handlers != null) {
			for (AlertHandler missedTargetHandler : handlers)
				missedTargetHandler.workCategoryFailed(latencyRequirement, duration);
		}
	}

}// end class def