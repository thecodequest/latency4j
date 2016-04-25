package org.latency4j;

import java.io.InputStream;

import org.latency4j.processing.AsynchronousLatencyMonitorFactory;
import org.latency4j.processing.Latency4JResourceManager;

/**
 * <p>
 * Provides a static handle to a {@link AsynchronousLatencyMonitorFactory
 * default monitor factory} implementation. It
 * {@link AsynchronousLatencyMonitorFactory#init() initialises} the factory via
 * the path taken from the system property
 * {@link Latency4JConstants#CONFIG_FILE_VM_PROP} if specified, or from the
 * {@link Latency4JConstants#DEFAULT_CONFIGURATION_RESOURCE_NAME default
 * configuration file} if present. However, an additional override method is
 * provided to force initialisation from an {@link InputStream}.
 * </p>
 */
public class MonitorFactoryStaticHandle {
	/**
	 * <p>
	 * Static instance.
	 * </p>
	 */
	private static final MonitorFactoryStaticHandle INSTANCE = new MonitorFactoryStaticHandle();

	/**
	 * <p>
	 * Internal {@link AsynchronousLatencyMonitorFactory monitor factory}
	 * reference.
	 * </p>
	 */
	private final AsynchronousLatencyMonitorFactory monitorFactory;

	/**
	 * <p>
	 * Private constructor. This constructor initialises the internal
	 * {@link AsynchronousLatencyMonitorFactory asynchronous monitor factory}
	 * handle and {@link AsynchronousLatencyMonitorFactory#init() initialises}
	 * it.
	 * <p>
	 * 
	 * @see Latency4JConstants
	 * @see Latency4JResourceManager
	 * @see MonitorFactory
	 * @see LatencyMonitor
	 */
	private MonitorFactoryStaticHandle() {
		monitorFactory = new AsynchronousLatencyMonitorFactory();
		monitorFactory.init();
	}

	/**
	 * <p>
	 * Returns the {@link LatencyMonitor monitor} for the given
	 * {@link LatencyRequirement#getWorkCategory() category of work}. This
	 * method is simply a convenience proxy to the method
	 * {@link MonitorFactory#getMonitor(String)}.
	 * </p>
	 * 
	 * @param workCategory
	 *            The {@link LatencyRequirement#getWorkCategory() category} of
	 *            work to be {@link LatencyMonitor monitored}.
	 * 
	 * @return An existing {@link LatencyMonitor monitor} if one exists or has
	 *         already been created for the specified
	 *         {@link LatencyRequirement#getWorkCategory() category}, else a new
	 *         {@link LatencyMonitor monitor} instance is created and returned.
	 */
	public static LatencyMonitor getMonitor(final String workCategory) {
		return INSTANCE.monitorFactory.getMonitor(workCategory);
	}

	/**
	 * <p>
	 * Forces initialisation of the internal
	 * {@link AsynchronousLatencyMonitorFactory monitor factory} from the given
	 * {@link InputStream stream}.
	 * </p>
	 * 
	 * @param stream
	 *            The {@link InputStream stream} from which the
	 *            {@link AsynchronousLatencyMonitorFactory#init(InputStream)
	 *            factory configuration} should be read.
	 * @throws Latency4JException
	 *             If an exception occurs accessing or reading from the given
	 *             {@link InputStream stream}.
	 */
	public static void initializeFrom(final InputStream stream) {
		getInstance().getMonitorFactory().init(stream);
	}

	/**
	 * <p>
	 * Accessor method for the field {@link #monitorFactory}.
	 * </p>
	 * 
	 * @return A reference to the field {@link #monitorFactory}.
	 */
	public AsynchronousLatencyMonitorFactory getMonitorFactory() {
		return monitorFactory;
	}

	/**
	 * <p>
	 * Singleton accessor.
	 * </p>
	 * 
	 * @return Factory singleton.
	 */
	private static MonitorFactoryStaticHandle getInstance() {
		return INSTANCE;
	}
}