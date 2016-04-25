package org.latency4j.spring;

import org.latency4j.LatencyMonitor;
import org.latency4j.LatencyRequirement;
import org.latency4j.MonitorFactory;
import org.latency4j.processing.AsynchronousLatencyMonitor;
import org.latency4j.processing.AsynchronousLatencyMonitorFactory;

/**
 * <p>
 * Utility/helper class which is used for proxy and AOP based
 * {@link LatencyMonitor latency monitoring}.
 * </p>
 */
class AOPInterceptorUtil {
	/**
	 * <p>
	 * Returns the {@link AsynchronousLatencyMonitor monitor} for the given
	 * {@link LatencyRequirement#getWorkCategory() category}, if one exists.
	 * Else it will create a new {@link AsynchronousLatencyMonitor monitor}
	 * using the method name as an implicit
	 * {@link LatencyRequirement#getWorkCategory() category}.
	 * </p>
	 * 
	 * @param qualifiedMethodName
	 *            The method within which the {@link LatencyMonitor monitor} is
	 *            requested.
	 * 
	 * @param workCategory
	 *            The {@link LatencyRequirement#getWorkCategory() category of
	 *            the operation} being {@link LatencyMonitor monitored}.
	 * 
	 * @param monitorFactory
	 *            The {@link MonitorFactory factory} from which the monitor will
	 *            be requested.
	 * 
	 * @return An {@link AsynchronousLatencyMonitor asynchronous monitor} which
	 *         {@link LatencyMonitor monitors} tasks falling under the specified
	 *         {@link LatencyRequirement#getWorkCategory() category}.
	 */
	static AsynchronousLatencyMonitor getLatencyMonitor(final String qualifiedMethodName, String workCategory,
			final AsynchronousLatencyMonitorFactory monitorFactory) {
		AsynchronousLatencyMonitor result;

		if (workCategory == null || workCategory.length() <= 0) workCategory = qualifiedMethodName;

		result = monitorFactory.getMonitor(workCategory);

		return result;
	}
}// end class def