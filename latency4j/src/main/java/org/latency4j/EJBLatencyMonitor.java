package org.latency4j;

import static org.latency4j.util.PrimitiveTypeUtilities.isEmptyString;

import java.lang.reflect.Method;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

/**
 * <p>
 * EJB around-method interceptor, which is used to monitor EJB methods. This
 * class is dependent on the {@link MonitorFactoryStaticHandle static monitor
 * factory} for obtaining {@link LatencyMonitor monitors}. Consequently, this
 * class is dependent on the
 * {@link Latency4JConstants#DEFAULT_CONFIGURATION_RESOURCE_NAME default epsilon
 * configuration}.
 * </p>
 */
public class EJBLatencyMonitor {

	/**
	 * <p>
	 * Interceptor method which monitors the latency of
	 * {@link MonitoredByLatency4J annotated} EJB methods.
	 * </p>
	 * 
	 * @param context
	 *            The context of the intercepted call.
	 * @return The return value of the intercepted method.
	 * @throws Exception
	 *             Propagated from intercepted method.
	 */
	@AroundInvoke
	public Object monitor(final InvocationContext context) throws Exception {
		Object result;
		Method interceptedMethod = context.getMethod();
		if (isMethodAnnotated(interceptedMethod)) {
			MonitoredByLatency4J annotation = interceptedMethod.getAnnotation(MonitoredByLatency4J.class);
			String category = annotation.value();
			if (isEmptyString(category))
				category = interceptedMethod.getClass().getName() + "." + interceptedMethod.getName();

			LatencyMonitor monitor = MonitorFactoryStaticHandle.getMonitor(category);

			monitor.taskStarted();
			try {
				result = context.proceed();
				monitor.taskCompleted();
			} catch (Exception exce) {
				monitor.taskErrored(exce);
				throw exce;
			}
		} else result = context.proceed();

		return result;
	}

	/**
	 * <p>
	 * Determines if a method is {@link MonitoredByLatency4J annotated with the
	 * MonitoredByLatency4J annotation}.
	 * </p>
	 * 
	 * @param interceptedMethod
	 *            The method on which to verify that the
	 *            {@link MonitoredByLatency4J epsilon annotation} exists.
	 * 
	 * @return <code>True</code> if the specified method is
	 *         {@link MonitoredByLatency4J annotated with the MonitoredByLatency4J
	 *         annotation}, and <code>false</code> otherwise.
	 */
	private boolean isMethodAnnotated(final Method interceptedMethod) {
		return interceptedMethod.isAnnotationPresent(MonitoredByLatency4J.class);
	}
}