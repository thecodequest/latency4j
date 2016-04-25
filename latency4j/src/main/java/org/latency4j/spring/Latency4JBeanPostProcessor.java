package org.latency4j.spring;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;

import org.latency4j.MonitoredByLatency4J;
import org.latency4j.processing.AsynchronousLatencyMonitor;
import org.latency4j.processing.AsynchronousLatencyMonitorFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * <p>
 * A <a href=http://www.springsource.org/>Spring bean post processor</a>
 * implementation which, {@link Latency4JDynamicProxy proxies}
 * {@link MonitoredByLatency4J Epsilon annotated} classes. Calls to the
 * {@link MonitoredByLatency4J annotated} methods are intercepted and monitored
 * by an {@link Latency4JDynamicProxy Epsilon dynamic proxy}.
 * </p>
 */
public class Latency4JBeanPostProcessor implements BeanPostProcessor {
	/**
	 * <p>
	 * The {@link AsynchronousLatencyMonitorFactory monitor factory} from which
	 * {@link AsynchronousLatencyMonitor monitors} are created.
	 * </p>
	 */
	private AsynchronousLatencyMonitorFactory monitorFactory;

	/**
	 * <p>
	 * Performs no additional action/processing.
	 * </p>
	 *
	 * @param bean
	 *            The bean to process.
	 * @param beanName
	 *            The name of the bean to process.
	 * 
	 * @return The unmodified bean reference as specified in the argument list.
	 */
	@Override
	public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
		return bean;
	}

	/**
	 * <p>
	 * If the given bean contains {@link MonitoredByLatency4J Epsilon annotated
	 * methods}, this method will create and return a
	 * {@link Latency4JDynamicProxy dynamic Epsilon proxy} to it.
	 * </p>
	 * <p>
	 * If the bean is not {@link MonitoredByLatency4J annotated}, this method
	 * will return the unmodified bean reference as supplied in the parameter
	 * list.
	 * </p>
	 * 
	 * @param bean
	 *            The bean to process.
	 * 
	 * @param beanName
	 *            The name of the bean to process.
	 * 
	 * @return An {@link Latency4JDynamicProxy Epsilon proxy} to the specified
	 *         bean if, and only if, the bean is {@link MonitoredByLatency4J
	 *         annotated}. Else it will return the bean reference as supplied in
	 *         the argument list.
	 */
	@Override
	public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
		Object result;

		if (shouldProxy(bean)) {
			Latency4JDynamicProxy proxy = new Latency4JDynamicProxy(monitorFactory);
			result = Enhancer.create(bean.getClass(), proxy);
		} else result = bean;

		return result;
	}

	/**
	 * <p>
	 * Accessor for the {@link AsynchronousLatencyMonitorFactory factory} from
	 * which the {@link AsynchronousLatencyMonitor monitors} used by the
	 * {@link Latency4JDynamicProxy dynamic proxies} are created.
	 * </p>
	 * 
	 * @return The {@link AsynchronousLatencyMonitorFactory factory} used by the
	 *         processor to create {@link AsynchronousLatencyMonitor monitors}
	 *         used by the {@link Latency4JDynamicProxy dynamic proxies} also
	 *         {@link #postProcessAfterInitialization(Object, String) created by
	 *         the processor}.
	 */
	public AsynchronousLatencyMonitorFactory getMonitorFactory() {
		return monitorFactory;
	}

	/**
	 * <p>
	 * Sets the {@link AsynchronousLatencyMonitorFactory factory} from which the
	 * {@link AsynchronousLatencyMonitor monitors} used by the
	 * {@link Latency4JDynamicProxy dynamic proxies} are created.
	 * </p>
	 * 
	 * @param monitorFactory
	 *            The {@link AsynchronousLatencyMonitorFactory factory} to be
	 *            used by the processor to create
	 *            {@link AsynchronousLatencyMonitor monitors} used by the
	 *            {@link Latency4JDynamicProxy dynamic proxies} also
	 *            {@link #postProcessAfterInitialization(Object, String) created
	 *            by the processor}.
	 */
	public void setMonitorFactory(final AsynchronousLatencyMonitorFactory monitorFactory) {
		this.monitorFactory = monitorFactory;
	}

	/**
	 * <p>
	 * Determines if the specified bean should be {@link Latency4JDynamicProxy
	 * proxied}.
	 * </p>
	 * 
	 * @param bean
	 *            The bean to examine.
	 * 
	 * @return <code>True</code> if the specified bean contains at least one
	 *         {@link MonitoredByLatency4J Epsilon annotation}; and
	 *         <code>False</code> otherwise.
	 */
	private boolean shouldProxy(final Object bean) {
		boolean result = false;

		Class<?> beanClass = bean.getClass();

		Method[] methods = beanClass.getDeclaredMethods();

		if (methods != null) {
			for (Method method : methods) {
				if (method.isAnnotationPresent(MonitoredByLatency4J.class)) {
					result = true;
					break;
				}
			}
		}

		return result;
	}

}// end class def