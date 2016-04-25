package org.latency4j.spring;

import static org.latency4j.spring.AOPInterceptorUtil.getLatencyMonitor;

import java.lang.reflect.Method;

import org.latency4j.LatencyMonitor;
import org.latency4j.LatencyRequirement;
import org.latency4j.MonitoredByLatency4J;
import org.latency4j.processing.AsynchronousLatencyMonitor;
import org.latency4j.processing.AsynchronousLatencyMonitorFactory;

import net.sf.cglib.asm.$Type;
import net.sf.cglib.core.Signature;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * <p>
 * A dynamic proxy implementation, which {@link AsynchronousLatencyMonitor
 * monitors} the latency of proxied methods.
 * </p>
 */
public class Latency4JDynamicProxy implements MethodInterceptor {
	/**
	 * <p>
	 * The {@link AsynchronousLatencyMonitorFactory monitor factory} from which
	 * {@link AsynchronousLatencyMonitor monitors} are created to monitor the
	 * latency of {@link MonitoredByLatency4J annotated} methods.
	 * </p>
	 */
	private final AsynchronousLatencyMonitorFactory monitorFactory;

	/**
	 * <p>
	 * Constructor
	 * </p>
	 * 
	 * @param monitorFactory
	 *            The {@link AsynchronousLatencyMonitorFactory factory} from
	 *            which {@link AsynchronousLatencyMonitor monitors} are created
	 *            for the proxied methods.
	 */
	public Latency4JDynamicProxy(final AsynchronousLatencyMonitorFactory monitorFactory) {
		this.monitorFactory = monitorFactory;
	}

	/**
	 * <p>
	 * Method interceptor, which in the case of {@link MonitoredByLatency4J
	 * Epsilon annotated methods} will:
	 * <ul>
	 * <li>Create a {@link AsynchronousLatencyMonitor latency monitor} for
	 * either the {@link LatencyRequirement#getWorkCategory() category}
	 * specified in the {@link MonitoredByLatency4J annotation}, or an implicit
	 * category built from the name of the intercepted method.</li>
	 * <li>Invoke {@link LatencyMonitor#taskStarted()} on the
	 * {@link AsynchronousLatencyMonitor monitor} instance.</li>
	 * <li>Call the proxied method.</li>
	 * <li>Invoke either {@link LatencyMonitor#taskCompleted()} or
	 * {@link LatencyMonitor#taskErrored(Throwable)} on the
	 * {@link AsynchronousLatencyMonitor monitor} instance post completion of
	 * the proxied method.</li>
	 * </ul>
	 * </p>
	 * <p>
	 * Where the proxied method is not {@link MonitoredByLatency4J annotated},
	 * this method simply invokes the proxied method without any monitoring.
	 * </p>
	 * 
	 * @param targetObject
	 *            The instance on which to invoke th e proxied method.
	 * @param targetMethod
	 *            The proxied method.
	 * @param params
	 *            The parameters with which to invoke the proxied method.
	 * @param proxy
	 *            CGLIB method proxy instance.
	 * 
	 * @return The result of the proxied method.
	 */
	@Override
	public Object intercept(final Object targetObject, final Method targetMethod, final Object[] params,
			final MethodProxy proxy) throws Throwable {
		Object result;

		if (targetMethod.isAnnotationPresent(MonitoredByLatency4J.class))
			result = wrapCallWithMonitor(targetObject, targetMethod, params, proxy);
		else result = straightThroughCall(targetObject, targetMethod, params, proxy);

		return result;
	}

	/**
	 * <p>
	 * Internal helper method which wraps the proxied method with a
	 * {@link AsynchronousLatencyMonitor monitor} so as to capture its latency.
	 * This method should only apply {@link MonitoredByLatency4J annotated}
	 * methods.
	 * </p>
	 * 
	 * @param targetObject
	 *            The instance on which to invoke the proxied method.
	 * @param targetMethod
	 *            The proxied method.
	 * @param params
	 *            The parameters with which to invoke the proxied method.
	 * @param proxy
	 *            CGLIB method proxy instance.
	 * 
	 * @return The result of the proxied method.
	 * 
	 * @throws Throwable
	 *             Propagated from the proxied call or thrown in the case of
	 *             unexpected errors.
	 */
	private Object wrapCallWithMonitor(final Object targetObject, final Method targetMethod, final Object[] params,
			final MethodProxy proxy) throws Throwable {
		Object result = null;

		String qualifiedMethodName = getQualifiedMethodName(targetMethod, proxy.getSignature());

		MonitoredByLatency4J monitorAnnotation = targetMethod.getAnnotation(MonitoredByLatency4J.class);

		AsynchronousLatencyMonitor monitor = getLatencyMonitor(qualifiedMethodName, monitorAnnotation.value(),
				monitorFactory);

		monitor.createTaskEntry(qualifiedMethodName);

		try {
			if (proxy.getSignature().getReturnType() != $Type.VOID_TYPE)
				result = proxy.invokeSuper(targetObject, params);
			else proxy.invokeSuper(targetObject, params);

			monitor.taskCompleted();
		} catch (Throwable exce) {
			monitor.taskErrored(exce);
			throw exce;
		}

		return result;
	}

	/**
	 * <p>
	 * Internal delegate proxy for un-annotated bean methods. This method simply
	 * forwards the call directly to the proxied method without any
	 * {@link AsynchronousLatencyMonitor monitoring}.
	 * </p>
	 * 
	 * @param targetObject
	 *            The instance on which to invoke the proxied method.
	 * @param targetMethod
	 *            The proxied method.
	 * @param params
	 *            The parameters with which to invoke the proxied method.
	 * @param proxy
	 *            CGLIB method proxy instance.
	 * 
	 * @return The result of the proxied method.
	 * 
	 * @throws Throwable
	 *             Propagated from the proxied call or thrown in the case of
	 *             unexpected errors.
	 */
	private Object straightThroughCall(final Object targetObject, final Method targetMethod, final Object[] params,
			final MethodProxy proxy) throws Throwable {
		Object result = null;
		if (proxy.getSignature().getReturnType() != $Type.VOID_TYPE) result = proxy.invokeSuper(targetObject, params);
		else proxy.invokeSuper(targetObject, params);
		return result;
	}

	/**
	 * <p>
	 * Resolves the fully qualified method name, including the class, for a
	 * proxied method. For a method <code>c</code> on class <code>a.b</code>,
	 * this method will return the string <code>a.b.c</code>
	 * </p>
	 * 
	 * @param targetMethod
	 *            The method to proxy.
	 * @param signature
	 *            The signature of the proxied method.
	 * @return The fully qualified name of the proxied method.
	 */
	private String getQualifiedMethodName(final Method targetMethod, final Signature signature) {
		StringBuffer result = new StringBuffer(targetMethod.getDeclaringClass().getName());
		result.append("." + signature.getName());
		return result.toString();
	}
}
