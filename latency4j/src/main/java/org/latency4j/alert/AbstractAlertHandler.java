package org.latency4j.alert;

import static org.latency4j.alert.StandardHandlerConstants.CAP_EXCEEDED_MSG_PARAM_KEY;
import static org.latency4j.alert.StandardHandlerConstants.DEFAULT_MESSAGE_FOR_MISSED_TARGET;
import static org.latency4j.alert.StandardHandlerConstants.DEFAULT_MESSAGE_FOR_WORK_FAILURE;
import static org.latency4j.alert.StandardHandlerConstants.DEFAULT_TOLERANCE_EXCEEDED_MESSAGE;
import static org.latency4j.alert.StandardHandlerConstants.EXCEEDED_TOLERANCE_MSG_PARAM_KEY;
import static org.latency4j.alert.StandardHandlerConstants.WORK_CATEGORY_FAILED_MSG_PARAM_KEY;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.latency4j.CappedLatencyRequirement;
import org.latency4j.AlertHandler;
import org.latency4j.Latency4JException;
import org.latency4j.LatencyRequirement;
import org.latency4j.StatisticalLatencyRequirement;
import org.latency4j.WorkDuration;

/**
 * <p>
 * Abstract class which provides basic plumbing for {@link AlertHandler
 * alert handler} implementations. Developers wishing to implement their own
 * handlers are advised to simply extend this class as it already provides most
 * of the functionality mandated by the {@link AlertHandler handler}
 * interface.
 * </p>
 * <p>
 * Instances of this handler require the following {@link #setParameters(Map)
 * parameters}:
 * <ul>
 * <li>{@link StandardHandlerConstants#CAP_EXCEEDED_MSG_PARAM_KEY}: The template
 * for messages issued to indicate that a {@link WorkDuration duration} has
 * breached its corresponding {@link CappedLatencyRequirement latency
 * requirement}. Where this parameter is not specified, the default
 * {@link StandardHandlerConstants#DEFAULT_MESSAGE_FOR_MISSED_TARGET} is used.
 * See
 * {@link #prepareLatencyExceededCap(CappedLatencyRequirement, WorkDuration)}
 * <br>
 * </li>
 * <li>{@link StandardHandlerConstants#WORK_CATEGORY_FAILED_MSG_PARAM_KEY}: The
 * template for messages issued to indicate that a
 * {@link LatencyRequirement#getWorkCategory() monitored task} has failed with
 * an error. Where this parameter is not specified, the default
 * {@link StandardHandlerConstants#DEFAULT_MESSAGE_FOR_WORK_FAILURE} is used.
 * See {@link #prepareWorkCategoryFailedMsg(LatencyRequirement, WorkDuration)}
 * <br>
 * </li>
 * <li>{@link StandardHandlerConstants#EXCEEDED_TOLERANCE_MSG_PARAM_KEY}: The
 * template for messages issued to indicate that the {@link WorkDuration
 * duration} of a {@link LatencyRequirement#getWorkCategory() monitored task}
 * does not fall within {@link StatisticalLatencyRequirement#getToleranceLevel()
 * the specified tolerance} when compared to the long running average. Where
 * this parameter is not specified, the default
 * {@link StandardHandlerConstants#DEFAULT_TOLERANCE_EXCEEDED_MESSAGE} is used.
 * See
 * {@link #prepareDeviationExceededToleranceMsg(StatisticalLatencyRequirement, WorkDuration, double, double)}
 * . <br>
 * </li>
 * </ul>
 * </p>
 */
public abstract class AbstractAlertHandler implements AlertHandler {

	/**
	 * <p>
	 * Map which holds the parameters with which the handler is configured.
	 * </p>
	 * 
	 * @see AlertHandler#setParameters(Map)
	 */
	protected Map<String, String> parameters;

	/**
	 * <p>
	 * The identifier/name of the handler. Ensuring unique names/identifiers for
	 * {@link AlertHandler handlers} is strongly encouraged.
	 * </p>
	 * 
	 * @see AlertHandler#getAlertHandlerId()
	 */
	private String alertHandlerId;

	/**
	 * <p>
	 * An internal field which indicates if an instance has been initialised or
	 * not. Default value is <code>False</code>.
	 * </p>
	 */
	protected AtomicBoolean initialized;

	/**
	 * <p>
	 * Default constructor.
	 * </p>
	 */
	public AbstractAlertHandler() {
		this.initialized = new AtomicBoolean(false);
	}

	@Override
	public String getAlertHandlerId() {
		return alertHandlerId;
	}

	@Override
	public void setAlertHandlerId(final String id) {
		this.alertHandlerId = id;
	}

	@Override
	public void setParameters(final Map<String, String> parameters) {
		this.parameters = parameters;
	}

	/**
	 * <p>
	 * Returns a reference to the parameter/argument map with which the instance
	 * is initialised. It essentially returns the same value as was passed by
	 * the most recent call to {@link #setParameters(Map)}, or null if there has
	 * not been any such invocation.
	 * </p>
	 * 
	 * @return The parameters with which this listener instance is initialised.
	 */
	public Map<String, String> getParameters() {
		return parameters;
	}

	@Override
	public void init() {
		if (parameters == null) parameters = new HashMap<String, String>();

		if (getAlertHandlerId() == null) throw new Latency4JException("No identifier specified for alerts handler!");

		if (!parameters.containsKey(CAP_EXCEEDED_MSG_PARAM_KEY))
			parameters.put(CAP_EXCEEDED_MSG_PARAM_KEY, DEFAULT_MESSAGE_FOR_MISSED_TARGET);

		if (!parameters.containsKey(EXCEEDED_TOLERANCE_MSG_PARAM_KEY))
			parameters.put(EXCEEDED_TOLERANCE_MSG_PARAM_KEY, DEFAULT_TOLERANCE_EXCEEDED_MESSAGE);

		if (!parameters.containsKey(WORK_CATEGORY_FAILED_MSG_PARAM_KEY))
			parameters.put(WORK_CATEGORY_FAILED_MSG_PARAM_KEY, DEFAULT_MESSAGE_FOR_WORK_FAILURE);
	}

	/**
	 * <p>
	 * Internal assertion which raises an error if the handler instance has not
	 * been initialised. It is recommended that subclasses of this handler
	 * perform this assertion prior to servicing alerts.
	 * </p>
	 */
	protected void assertInitialized() {
		if (!initialized.get()) throw new Latency4JException("Epsilon Handler not initialised.");
	}

	/**
	 * <p>
	 * Internal method which is used to format notification messages where a
	 * {@link LatencyRequirement#getWorkCategory() task} {@link WorkDuration
	 * latency} has exceeded the mean by more than the
	 * {@link StatisticalLatencyRequirement#getToleranceLevel() pre-specified
	 * tolerance}.
	 * </p>
	 * <p>
	 * The message template is given by the value of the configuration parameter
	 * {@link StandardHandlerConstants#EXCEEDED_TOLERANCE_MSG_PARAM_KEY}.
	 * </p>
	 * <p>
	 * The message template can also include filters that can be used to infuse
	 * the completed message with details of the task, duration, etc. For more
	 * details, see the delegate method see
	 * {@link AlertFormatter#formatLatencyExceededToleranceMessage(StatisticalLatencyRequirement, WorkDuration, double, double, String)}
	 * </p>
	 * 
	 * @param requirement
	 *            The {@link StatisticalLatencyRequirement requirement} to which
	 *            the alert message relates.
	 * @param duration
	 *            The {@link WorkDuration duration} of the task which triggered
	 *            the alert for which the message is being formatted.
	 * @param deviationFromMean
	 *            The deviation of the task from the observed average
	 *            {@link WorkDuration duration}.
	 * @param mean
	 *            The observed average {@link WorkDuration duration} to date.
	 * @return A formatted notification message with all filters specified in
	 *         the template replaced with actual values from the parameters.
	 * 
	 * @see AlertHandler#latencyExceededCap(CappedLatencyRequirement,
	 *      WorkDuration)
	 */
	protected String prepareDeviationExceededToleranceMsg(final StatisticalLatencyRequirement requirement,
			final WorkDuration duration, final double deviationFromMean, final double mean) {
		String result;
		assertInitialized();
		String exceededToleranceMessage = parameters.get(EXCEEDED_TOLERANCE_MSG_PARAM_KEY);

		result = AlertFormatter.formatLatencyExceededToleranceMessage(requirement, duration, deviationFromMean, mean,
				exceededToleranceMessage);

		return result;
	}

	/**
	 * <p>
	 * Helper method which is used to format notification messages where a
	 * {@link WorkDuration duration} has exceeded a
	 * {@link CappedLatencyRequirement#getExpectedLatency() fixed latency cap}.
	 * The message template is given by the value of the initialisation
	 * parameter {@link StandardHandlerConstants#CAP_EXCEEDED_MSG_PARAM_KEY}.
	 * </p>
	 * <p>
	 * The message template can include filters, which can be used to infuse the
	 * completed message with details of the
	 * {@link LatencyRequirement#getWorkCategory() task}, {@link WorkDuration
	 * duration}, etc. For more details, see the delegate method see
	 * {@link AlertFormatter#formatTargetMissedMessage(CappedLatencyRequirement, WorkDuration, String)}
	 * </p>
	 * 
	 * @param requirement
	 *            The {@link CappedLatencyRequirement requirement} to which the
	 *            notification relates.
	 * @param duration
	 *            The {@link WorkDuration duration} of the
	 *            {@link LatencyRequirement#getWorkCategory() operation} which
	 *            triggered the alert.
	 * @return A formatted alert message with all filters specified in the
	 *         template replaced with actual values from the parameters.
	 */
	protected String prepareLatencyExceededCap(final CappedLatencyRequirement requirement,
			final WorkDuration duration) {
		String result;

		assertInitialized();
		String targetMissedMessage = parameters.get(CAP_EXCEEDED_MSG_PARAM_KEY);

		result = AlertFormatter.formatTargetMissedMessage(requirement, duration, targetMissedMessage);
		return result;
	}

	/**
	 * <p>
	 * Utility method which formats a notification message indicating that a
	 * {@link LatencyRequirement#getWorkCategory() monitored task} has failed
	 * with an exception. The message template is given by the value of the
	 * {@link #setParameters(Map) parameter}
	 * {@link StandardHandlerConstants#WORK_CATEGORY_FAILED_MSG_PARAM_KEY}.
	 * </p>
	 * <p>
	 * The message template can also include filters that can be used to infuse
	 * the completed message with details of the
	 * {@link LatencyRequirement#getWorkCategory() task}, {@link WorkDuration
	 * duration}, exception etc. For more details, see the delegate method
	 * {@link AlertFormatter#formatWorkCategoryFailureMessage(LatencyRequirement, WorkDuration, String)}
	 * </p>
	 * 
	 * @param requirement
	 *            The {@link LatencyRequirement requirement} covering the failed
	 *            operation.
	 * @param duration
	 *            The {@link WorkDuration duration} of the failed operation.
	 * @return A formatted alert message with all filters specified in the
	 *         template replaced with actual values from the given
	 *         {@link LatencyRequirement requirement} and {@link WorkDuration
	 *         duration}.
	 */
	protected String prepareWorkCategoryFailedMsg(final LatencyRequirement requirement, final WorkDuration duration) {
		String result;
		assertInitialized();
		String failureMessage = parameters.get(WORK_CATEGORY_FAILED_MSG_PARAM_KEY);

		result = AlertFormatter.formatWorkCategoryFailureMessage(requirement, duration, failureMessage);
		return result;
	}
}