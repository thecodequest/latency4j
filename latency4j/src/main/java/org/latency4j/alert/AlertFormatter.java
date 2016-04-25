package org.latency4j.alert;

import static org.latency4j.alert.StandardHandlerConstants.DEVIATION_TOKEN;
import static org.latency4j.alert.StandardHandlerConstants.DURATION_TOKEN;
import static org.latency4j.alert.StandardHandlerConstants.EXCEPTION_MESSAGE_TOKEN;
import static org.latency4j.alert.StandardHandlerConstants.EXCEPTION_STACKTRACE_TOKEN;
import static org.latency4j.alert.StandardHandlerConstants.EXPECTED_LATENCY_TOKEN;
import static org.latency4j.alert.StandardHandlerConstants.MEAN_TOKEN;
import static org.latency4j.alert.StandardHandlerConstants.THREAD_ID_TOKEN;
import static org.latency4j.alert.StandardHandlerConstants.TOLERANCE_LEVEL_TOKEN;
import static org.latency4j.alert.StandardHandlerConstants.WORK_CATEGORY_TOKEN;

import org.latency4j.CappedLatencyRequirement;
import org.latency4j.LatencyRequirement;
import org.latency4j.StatisticalLatencyRequirement;
import org.latency4j.WorkDuration;
import org.latency4j.WorkDurationId;
import org.latency4j.util.ExceptionUtil;

/**
 * <p>
 * Utility class for formatting alert messages.
 * </p>
 */
public class AlertFormatter {
	/**
	 * <p>
	 * Formats an alert which indicates that the {@link WorkDuration duration}
	 * of a monitored {@link LatencyRequirement#getWorkCategory() task} has
	 * exceeded the {@link StatisticalLatencyRequirement#getToleranceLevel()
	 * permitted tolerance} in comparison to the mean latency.
	 * </p>
	 * <p>
	 * This involves replacing the filters, given by the following constants,
	 * with actual values taken from the method parameter list.
	 * </p>
	 * <ul>
	 * <li>{@link StandardHandlerConstants#WORK_CATEGORY_TOKEN}: Replaced with
	 * the return value of the {@link LatencyRequirement#getWorkCategory()
	 * category} as taken from the {@link LatencyRequirement requirement}
	 * specified in the argument list.</li>
	 * <li>{@link StandardHandlerConstants#THREAD_ID_TOKEN}: Replaced with the
	 * id of the thread under which the task was executed as obtained from
	 * {@link WorkDuration#getIdentifier() duration identifier}, and the
	 * {@link WorkDurationId#getThreadId() identifier's threadId field}.</li>
	 * <li>{@link StandardHandlerConstants#DEVIATION_TOKEN}: Replaced with the
	 * deviation from the mean, as specified in the parameter list.</li>
	 * <li>{@link StandardHandlerConstants#MEAN_TOKEN}: Replaced with the mean
	 * latency, as specified in the parameter list.</li>
	 * <li>{@link StandardHandlerConstants#TOLERANCE_LEVEL_TOKEN}: Replaced with
	 * the {@link StatisticalLatencyRequirement#getToleranceLevel() tolerance
	 * level} as taken from the {@link LatencyRequirement requirement} in the
	 * parameter list.</li>
	 * <li>{@link StandardHandlerConstants#DURATION_TOKEN}: The actual
	 * {@link WorkDuration duration} of the operation as specified in the
	 * parameter list.</li>
	 * </ul>
	 * 
	 * @param requirement
	 *            The {@link LatencyRequirement requirement} to which the alert
	 *            relates.
	 * @param duration
	 *            The {@link WorkDuration duration} of the task being monitored.
	 * @param deviationFromMean
	 *            The deviation of the {@link WorkDuration task's duration} from
	 *            the current observed average.
	 * @param mean
	 *            The current observed average duration.
	 * @param exceededToleranceMessage
	 *            The template message on which the pattern replacement is to be
	 *            performed.
	 * @return A formatted alert message with all specified filters replaced
	 *         with values taken from the parameter list.
	 */
	public static String formatLatencyExceededToleranceMessage(final StatisticalLatencyRequirement requirement,
			final WorkDuration duration, final double deviationFromMean, final double mean,
			final String exceededToleranceMessage) {
		String result;

		result = exceededToleranceMessage.replace(WORK_CATEGORY_TOKEN, requirement.getWorkCategory());
		result = result.replace(THREAD_ID_TOKEN, duration.getIdentifier().getThreadId());
		result = result.replace(DEVIATION_TOKEN, String.valueOf(deviationFromMean));
		result = result.replace(MEAN_TOKEN, String.valueOf(mean));
		result = result.replace(TOLERANCE_LEVEL_TOKEN, String.valueOf(requirement.getToleranceLevel() * 100)); // change
																												// to
																												// percent
		result = result.replace(DURATION_TOKEN, duration.toStringTimeOnly());
		return result;
	}

	/**
	 * <p>
	 * Formats an alert which indicates that the {@link WorkDuration duration}
	 * of a {@link LatencyRequirement#getWorkCategory() monitored task/
	 * operation} has exceeded a {@link CappedLatencyRequirement pre-specified
	 * cap}. This involves replacing the filters, given by the following
	 * constants, with real values taken from the parameter list.
	 * </p>
	 * <ul>
	 * <li>{@link StandardHandlerConstants#WORK_CATEGORY_TOKEN}: Replaced with
	 * the {@link LatencyRequirement#getWorkCategory() operation category} as
	 * taken from the {@link LatencyRequirement requirement} specified in the
	 * parameter list.</li>
	 * <li>{@link StandardHandlerConstants#THREAD_ID_TOKEN}: Replaced with the
	 * id of the thread under which the operation was executed. This is obtained
	 * from the {@link WorkDurationId#getThreadId() threadId field}, taken from
	 * {@link WorkDuration#getIdentifier() identifier} of the specified
	 * {@link WorkDuration duration}.</li>
	 * <li>{@link StandardHandlerConstants#EXPECTED_LATENCY_TOKEN}: The cap
	 * which has been breached. This value is given by the
	 * {@link CappedLatencyRequirement#getExpectedLatency() expected latency} of
	 * the specified {@link LatencyRequirement requirement}.</li>
	 * <li>{@link StandardHandlerConstants#DURATION_TOKEN}: The actual
	 * {@link WorkDuration duration} of the operation as specified in the
	 * parameter list.</li>
	 * </ul>
	 * 
	 * @param requirement
	 *            The {@link LatencyRequirement requirement} to which the alert
	 *            relates.
	 * @param duration
	 *            The {@link WorkDuration duration} of the monitored task.
	 * @param message
	 *            The template from which the alert message is generated.
	 * @return A formatted alert message with all filters replaced with actual
	 *         values taken from the parameter list.
	 */
	public static String formatTargetMissedMessage(final CappedLatencyRequirement requirement,
			final WorkDuration duration, final String message) {
		String result;

		result = message.replace(WORK_CATEGORY_TOKEN, requirement.getWorkCategory());
		result = result.replace(THREAD_ID_TOKEN, duration.getIdentifier().getThreadId());
		result = result.replace(EXPECTED_LATENCY_TOKEN, String.valueOf(requirement.getExpectedLatency()));
		result = result.replace(DURATION_TOKEN, duration.toStringTimeOnly());

		return result;
	}

	/**
	 * <p>
	 * Formats an alert which indicates that a
	 * {@link LatencyRequirement#getWorkCategory() monitored task} terminated
	 * with an exception. This involves replacing the filters, given by the
	 * following constants, with values taken from the parameter list.
	 * </p>
	 * <ul>
	 * <li>{@link StandardHandlerConstants#WORK_CATEGORY_TOKEN}: Replaced with
	 * the {@link LatencyRequirement#getWorkCategory() category} to which the
	 * failed task relates. This is taken from the {@link LatencyRequirement
	 * requirement} specified in the parameter list.</li>
	 * <li>{@link StandardHandlerConstants#THREAD_ID_TOKEN}: Replaced with the
	 * {@link WorkDurationId#getThreadId() id of the thread} under which the
	 * task was executed as obtained from the
	 * {@link WorkDuration#getIdentifier() identifier} of the specified
	 * {@link WorkDuration duration}.</li>
	 * <li>{@link StandardHandlerConstants#EXCEPTION_MESSAGE_TOKEN}: The message
	 * taken from the {@link WorkDuration#getError() exception} encapsulated in
	 * the specified {@link WorkDuration duration}.</li>
	 * <li>{@link StandardHandlerConstants#EXCEPTION_STACKTRACE_TOKEN}: The
	 * stack trace of the exception object {@link WorkDuration#getError()}.</li>
	 * </ul>
	 * 
	 * @param requirement
	 *            The {@link LatencyRequirement requirement} to which the alert
	 *            relates.
	 * @param duration
	 *            The {@link WorkDuration duration} of the monitored task.
	 * @param message
	 *            The template from which the message is built.
	 * @return A formatted message with all filters replaced with values taken
	 *         from the parameter list.
	 */
	public static String formatWorkCategoryFailureMessage(final LatencyRequirement requirement,
			final WorkDuration duration, final String message) {
		String result;
		result = message.replace(WORK_CATEGORY_TOKEN, requirement.getWorkCategory());
		result = result.replace(THREAD_ID_TOKEN, duration.getIdentifier().getThreadId());
		result = result.replace(EXCEPTION_MESSAGE_TOKEN, duration.getError().getMessage());
		result = result.replace(EXCEPTION_STACKTRACE_TOKEN, ExceptionUtil.getStackTrace(duration.getError()));

		return result;
	}
}