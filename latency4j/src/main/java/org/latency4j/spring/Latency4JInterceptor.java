package org.latency4j.spring;

import static org.latency4j.spring.AOPInterceptorUtil.getLatencyMonitor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.latency4j.Latency4JException;
import org.latency4j.LatencyMonitor;
import org.latency4j.LatencyRequirement;
import org.latency4j.WorkDuration;
import org.latency4j.processing.AsynchronousLatencyMonitor;
import org.latency4j.processing.AsynchronousLatencyMonitorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * An AOP/AspectJ intercepter which can be used to create a
 * {@link LatencyMonitor monitoring} aspect.
 * </p>
 * <p>
 * A {@link AsynchronousLatencyMonitorFactory factory} for obtaining the
 * {@link AsynchronousLatencyMonitor monitor}, for the methods forming part of
 * the aspect, must be
 * {@link #setMonitorFactory(AsynchronousLatencyMonitorFactory) injected as a
 * dependency} to instances of this class.
 * </p>
 * <p>
 * For more information, please see the documentation from the
 * <a href="http://www.obix-labs.com">obix-labs website</a>.
 * </p>
 * 
 * @see AsynchronousLatencyMonitor
 */
@Aspect
public class Latency4JInterceptor {
	/**
	 * <p>
	 * Logger.
	 * </p>
	 */
	private static final Logger logger = LoggerFactory.getLogger(Latency4JInterceptor.class);

	/**
	 * <p>
	 * A {@link AsynchronousLatencyMonitorFactory monitor factory} for obtaining
	 * {@link AsynchronousLatencyMonitor monitors} for the methods forming part
	 * of the {@link LatencyMonitor monitoring} aspect.
	 * </p>
	 */
	private AsynchronousLatencyMonitorFactory monitorFactory;

	/**
	 * <p>
	 * The {@link LatencyRequirement#getWorkCategory() category of operations}
	 * which constitute the aspect.
	 * </p>
	 */
	private String workCategory;

	/**
	 * <p>
	 * Default constructor.
	 * </p>
	 */
	public Latency4JInterceptor() {}

	/**
	 * <p>
	 * Serves as a proxy to the specified call. It records its
	 * {@link LatencyMonitor#taskStarted() start} and
	 * {@link LatencyMonitor#taskCompleted() end time}, and triggers the
	 * processing of the resulting {@link WorkDuration duration}.
	 * </p>
	 * 
	 * @param call
	 *            The method call to proxy.
	 * @return The return value from the invocation of <code>call</code>.
	 * 
	 * @throws Throwable
	 *             Propagated from <code>call</code> where relevant.
	 */
	public Object monitor(final ProceedingJoinPoint call) throws Throwable {
		Object result;

		assertInitialized();

		Signature callSignature = call.getSignature();
		String qualifiedMethodName = callSignature.getDeclaringTypeName() + "." + callSignature.getName();

		AsynchronousLatencyMonitor monitor = getLatencyMonitor(qualifiedMethodName, workCategory, monitorFactory);

		try {
			monitor.createTaskEntry(qualifiedMethodName);
			result = call.proceed();
			monitor.taskCompleted();
		} catch (Throwable exce) {
			monitor.taskErrored(exce);
			throw exce;
		}

		return result;
	}

	/**
	 * <p>
	 * Returns a {@link AsynchronousLatencyMonitorFactory factory} for obtaining
	 * {@link AsynchronousLatencyMonitor the monitors} for the methods that form
	 * part of the {@link LatencyMonitor monitoring} aspect.
	 * </p>
	 * 
	 * @return The {@link AsynchronousLatencyMonitorFactory factory} used by the
	 *         instance for obtaining {@link AsynchronousLatencyMonitor
	 *         monitors}.
	 */
	public AsynchronousLatencyMonitorFactory getMonitorFactory() {
		return monitorFactory;
	}

	/**
	 * <p>
	 * Specifies the {@link AsynchronousLatencyMonitorFactory factory} for
	 * obtaining {@link AsynchronousLatencyMonitor monitors} for the methods
	 * which constitute the {@link LatencyMonitor monitoring} aspect.
	 * </p>
	 * 
	 * @param monitorFactory
	 *            The {@link AsynchronousLatencyMonitorFactory factory} to be
	 *            used by the instance for obtaining
	 *            {@link AsynchronousLatencyMonitor monitors}.
	 */
	public void setMonitorFactory(final AsynchronousLatencyMonitorFactory monitorFactory) {
		this.monitorFactory = monitorFactory;
	}

	/**
	 * <p>
	 * Specifies the {@link LatencyRequirement#getWorkCategory() latency
	 * requirement category} which the aspect encapsulates. Thus all methods
	 * forming part of this aspect will be monitored under this
	 * {@link LatencyRequirement requirement category}.
	 * </p>
	 * 
	 * @param workCategory
	 *            The {@link LatencyRequirement#getWorkCategory() requirement
	 *            category} which the aspect encapsulates. Note that where this
	 *            value is null, the fully qualified name of the intercepted
	 *            method is used to create an implicit {@link LatencyRequirement
	 *            requirement}.
	 */
	public void setWorkCategory(final String workCategory) {
		this.workCategory = workCategory;
	}

	/**
	 * <p>
	 * Gets the {@link LatencyRequirement#getWorkCategory() latency requirement
	 * category} which the aspect encapsulates.
	 * </p>
	 * 
	 * @return The {@link LatencyRequirement#getWorkCategory() requirement
	 *         category} which the aspect encapsulates or <code>Null</code> if
	 *         none was
	 *         {@link #setMonitorFactory(AsynchronousLatencyMonitorFactory)
	 *         specified}.
	 */
	public String getWorkCategory() {
		return workCategory;
	}

	/**
	 * <p>
	 * Internal assertion which checks that all fields have been correctly
	 * initialised.
	 * </p>
	 */
	private void assertInitialized() {
		if (monitorFactory == null) throw new Latency4JException(
				"Epsilon interceptor not initialized correctly, property " + "'monitorFactory' not set!");

		if (workCategory == null || workCategory.length() <= 0)
			logger.warn("Epsilon interceptor not initialized correctly, property "
					+ "'workCategory' not set! Will default to method name!");
	}

}// end class def