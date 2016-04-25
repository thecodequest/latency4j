package org.latency4j;

/**
 * <p>
 * A latency monitor records the {@link WorkDuration duration} of tasks [units
 * of work]. Where such work falls under the scope of a
 * {@link LatencyRequirement latency requirement}, the monitor will also check
 * that the {@link LatencyRequirement requirement} is met. Where
 * {@link LatencyRequirement requirements} are not met, or when errors occur,
 * the monitor also issues notifications, via one or more configured
 * {@link AlertHandler alert handlers}.
 * </p>
 */
public interface LatencyMonitor {
	/**
	 * <p>
	 * Invoked at the start of a {@link LatencyRequirement#getWorkCategory()
	 * task} to indicate the start of processing.
	 * </p>
	 */
	void taskStarted();

	/**
	 * <p>
	 * Invoked at the completion of a
	 * {@link LatencyRequirement#getWorkCategory() task} to indicate the end of
	 * processing.
	 * </p>
	 * 
	 * @return The duration of the task.
	 */
	WorkDuration taskCompleted();

	/**
	 * <p>
	 * Invoked if the execution of a task is aborted due to an exception.
	 * </p>
	 * 
	 * @param cause
	 *            The error which caused processing to be aborted.
	 *            Implementations should support <code>null</code> for this
	 *            field.
	 * 
	 * @return The {@link WorkDuration duration} of the task up until the point
	 *         the error occurred. The returned instance will also contain
	 *         details of the {@link Throwable error} that caused the task to
	 *         fail.
	 */
	WorkDuration taskErrored(Throwable cause);
}// end class def