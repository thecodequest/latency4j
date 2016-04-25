package org.latency4j;

import java.util.Map;

/**
 * <p>
 * An interface which defines the contract to which alert handlers must conform.
 * An alert handler is invoked by a {@link LatencyMonitor latency monitor} when
 * a monitored {@link LatencyRequirement requirement} is breached.
 * </p>
 * <p>
 * The {@link LatencyMonitor monitor} will raise an alert each time the
 * {@link LatencyRequirement requirement} is breached. These alerts are passed
 * onto the alert handlers attached to the {@link LatencyMonitor monitor}. It is
 * then the job of these handlers to pass the alert onto the end-target e.g. by
 * sending a mail notification or by writing the alerts to a log file or
 * persistent store.
 * </p>
 */
public interface AlertHandler {
	/**
	 * <p>
	 * Returns the identifier of the handler. Each handler instance is expected
	 * to have a unique identifier.
	 * </p>
	 * 
	 * @return The handler's id.
	 */
	String getAlertHandlerId();

	/**
	 * <p>
	 * Sets the identifier of the handler.
	 * </p>
	 * 
	 * @param id
	 *            The identifier to assign to the handler. There is an implicit
	 *            assumption that this value is unique. This method should be
	 *            called prior to {@link #init() initialisation}.
	 */
	void setAlertHandlerId(String id);

	/**
	 * <p>
	 * Can be used to specify the implementation-specific parameters with which
	 * the instance should be initialised. This method should be called prior to
	 * {@link #init() initialisation}.
	 * </p>
	 * 
	 * @param parameters
	 *            The initialisation parameters.
	 */
	void setParameters(Map<String, String> parameters);

	/**
	 * <p>
	 * Initialises the handler.
	 * </p>
	 */
	void init();

	/**
	 * <p>
	 * Invoked by a monitor when the {@link WorkDuration duration/latency} of a
	 * given task has exceeded the tolerance specified in the
	 * {@link LatencyRequirement requirement} governing it. This is only
	 * applicable to {@link StatisticalLatencyRequirement statistical
	 * requirements}.
	 * </p>
	 * 
	 * @param requirement
	 *            The {@link StatisticalLatencyRequirement requirement} which
	 *            has been breached.
	 * @param duration
	 *            The {@link WorkDuration duration} of the task execution which
	 *            has breached the specified requirement.
	 * @param deviationFromMean
	 *            The deviation from the allowed tolerance specified in the
	 *            requirement.
	 * @param mean
	 *            The average {@link WorkDuration latency/duration} to date for
	 *            calls falling within the scope of this requirement.
	 * 
	 * @see StatisticalLatencyRequirement
	 */
	void latencyDeviationExceededTolerance(StatisticalLatencyRequirement requirement, WorkDuration duration,
			double deviationFromMean, double mean);

	/**
	 * <p>
	 * Invoked when a task execution has exceeded the
	 * {@link CappedLatencyRequirement#getExpectedLatency() fixed latency cap
	 * specified in its governing requirement}.
	 * </p>
	 * 
	 * @param requirement
	 *            The {@link CappedLatencyRequirement requirement} that has been
	 *            breached.
	 * @param duration
	 *            The {@link WorkDuration duration} of the task execution which
	 *            has breached the {@link LatencyRequirement requirement}.
	 * 
	 * @see CappedLatencyRequirement
	 */
	void latencyExceededCap(CappedLatencyRequirement requirement, WorkDuration duration);

	/**
	 * <p>
	 * Called by a {@link LatencyMonitor monitor} to indicate that a task failed
	 * to complete due to an exception. This method would only be invoked if
	 * {@link LatencyRequirement#isIgnoreErrors() error reporting is enabled for
	 * the requirement} being {@link LatencyMonitor monitored}.
	 * </p>
	 * 
	 * @param requirement
	 *            The {@link LatencyRequirement latency requirement} related to
	 *            the failed operation.
	 * @param duration
	 *            The {@link WorkDuration duration} of the operation up until
	 *            the point that the {@link Throwable exception or error}
	 *            occurred.
	 */
	void workCategoryFailed(LatencyRequirement requirement, WorkDuration duration);
}