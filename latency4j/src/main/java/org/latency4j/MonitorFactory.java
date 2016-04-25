package org.latency4j;

import org.latency4j.alert.MissedTargetAlertLogger;

/**
 * <p>
 * A monitor factory implementation serves as a means of obtaining
 * {@link LatencyMonitor latency monitors}.
 * </p>
 */
public interface MonitorFactory {
	/**
	 * <p>
	 * Returns the {@link LatencyMonitor monitor} for the given
	 * {@link LatencyRequirement#getWorkCategory() category of work}.
	 * </p>
	 * 
	 * <p>
	 * In the case where the specified
	 * {@link LatencyRequirement#getWorkCategory() category} has not been
	 * configured explicitly, this method will create an implicit
	 * {@link StatisticalLatencyRequirement statistical requirement} for the
	 * {@link LatencyRequirement#getWorkCategory() category}. It will also
	 * associate a basic {@link MissedTargetAlertLogger alerts logger} to the
	 * {@link LatencyRequirement requirement}, prior to returning a
	 * {@link LatencyMonitor monitor} for it.
	 * </p>
	 * 
	 * <p>
	 * This call is not required to create a brand new {@link LatencyMonitor
	 * monitor} with each invocation. It is feasible that where a
	 * {@link LatencyMonitor monitor} already exists for the given
	 * {@link LatencyRequirement#getWorkCategory() category}, this method will
	 * simply return a reference to it rather than creating a fresh one.
	 * </p>
	 * 
	 * @param workCategory
	 *            The category of work for which the {@link LatencyMonitor
	 *            monitor} is being returned.
	 * @return A {@link LatencyMonitor monitor} initialised to observe the
	 *         {@link LatencyRequirement requirement} (implicit or explicit)
	 *         which governs the given {@link LatencyRequirement workCategory}.
	 */
	LatencyMonitor getMonitor(String workCategory);
}