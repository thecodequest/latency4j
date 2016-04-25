package org.latency4j.processing;

import static org.latency4j.Latency4JConstants.CONFIG_FILE_VM_PROP;
import static org.latency4j.Latency4JConstants.DEFAULT_CONFIGURATION_RESOURCE_NAME;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.latency4j.AlertHandler;
import org.latency4j.Latency4JConstants;
import org.latency4j.Latency4JException;
import org.latency4j.LatencyRequirement;
import org.latency4j.MonitorFactory;
import org.latency4j.MonitorFactoryStaticHandle;
import org.latency4j.StatisticalLatencyRequirement;
import org.latency4j.WorkDuration;
import org.latency4j.alert.MissedTargetAlertLogger;
import org.latency4j.configuration.Latency4JConfiguration;
import org.latency4j.configuration.ConfigurationReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * {@link MonitorFactory} implementation which creates and manages
 * {@link AsynchronousLatencyMonitor asynchronous monitors}.
 * </p>
 */
public class AsynchronousLatencyMonitorFactory implements MonitorFactory {
	/**
	 * <p>
	 * Internal logger reference.
	 * </p>
	 */
	private static final Logger logger = LoggerFactory.getLogger(AsynchronousLatencyMonitorFactory.class);

	/**
	 * <p>
	 * A reference to the {@link Latency4JResourceManager Epsilon resource
	 * manager}. The {@link Latency4JResourceManager resource manager} is
	 * responsible for storing and providing access to {@link LatencyRequirement
	 * requirements} and their associated {@link AlertHandler alert
	 * handlers}.
	 * </p>
	 */
	private final Latency4JResourceManager epsilonResourceManager;

	/**
	 * <p>
	 * {@link LatencyProcessor Asynchronous latency processing thread}. It is
	 * started when the factory is created and runs continually in the
	 * background; thus allowing execution threads to proceed unimpeded. Put
	 * differently, {@link AsynchronousLatencyMonitor monitors} delegate
	 * {@link WorkDuration duration} processing to {@link LatencyProcessor this
	 * object}, which performs said processing as a background task. Thus
	 * ensuring that {@link AsynchronousLatencyMonitor monitors} do not block
	 * the execution thread.
	 * </p>
	 */
	private final LatencyProcessor asyncProcessor;

	/**
	 * <p>
	 * An internal {@link Map} holding {@link AsynchronousLatencyMonitor
	 * monitor} references.
	 * </p>
	 */
	private final Map<String, AsynchronousLatencyMonitor> monitorMap;

	/**
	 * <p>
	 * The path from which to load the {@link Latency4JConfiguration epsilon
	 * configuration}.
	 * </p>
	 */
	private String configurationPath;

	/**
	 * <p>
	 * Synchronisation lock.
	 * </p>
	 */
	private final Lock writeLock;

	/**
	 * <p>
	 * Default constructor.
	 * </p>
	 */
	public AsynchronousLatencyMonitorFactory() {
		this.epsilonResourceManager = new Latency4JResourceManager();
		this.monitorMap = new HashMap<String, AsynchronousLatencyMonitor>();
		this.writeLock = new ReentrantLock();
		this.asyncProcessor = new LatencyProcessor();
		this.asyncProcessor.start();
	}

	@Override
	public AsynchronousLatencyMonitor getMonitor(final String workCategory) {
		AsynchronousLatencyMonitor result = this.monitorMap.get(workCategory);
		if (result == null) {
			this.writeLock.lock();
			try {
				// if not already created, then create
				if (!this.monitorMap.containsKey(workCategory)) {
					LatencyRequirement requirement = getEpsilonResourceManager().getLatencyRequirement(workCategory);

					// if no requirement, then create an implicit statistical
					// requirement
					if (requirement == null) {
						requirement = new StatisticalLatencyRequirement();
						requirement.setWorkCategory(workCategory);
						requirement.getAlertHandlers().add(createHandlerForImplicitRequirement(workCategory));

						requirement.init();
					}

					result = new AsynchronousLatencyMonitor(requirement, this.asyncProcessor);
					this.monitorMap.put(workCategory, result);
				} else result = this.monitorMap.get(workCategory);
				// someone beat this thread to it
			}
			finally {
				this.writeLock.unlock();
			}
		}
		return result;
	}

	/**
	 * <p>
	 * Returns the path from which the factory will read its
	 * {@link Latency4JConfiguration configuration} at {@link #init()
	 * initialisation}.
	 * </p>
	 * 
	 * @return The path from which this factory is {@link Latency4JConfiguration
	 *         configured}.
	 */
	public String getConfigurationPath() {
		return this.configurationPath;
	}

	/**
	 * <p>
	 * Sets the path from which the factory will read its
	 * {@link Latency4JConfiguration configuration} at {@link #init()
	 * initialisation}.
	 * </p>
	 * 
	 * @param configurationPath
	 *            The path to the {@link Latency4JConfiguration Epsilon
	 *            configuration}.
	 */
	public void setConfigurationPath(final String configurationPath) {
		this.configurationPath = configurationPath;
	}

	/**
	 * <p>
	 * Initialises this factory. In doing so, this method will attempt to read
	 * the {@link Latency4JConfiguration Epsilon configuration} from the first
	 * available of the following three choices:
	 * 
	 * <ul>
	 * <li>The {@link #setConfigurationPath(String) configuration path}
	 * specified for this instance.</li>
	 * <li>Or the value of the vm property
	 * <q>{@link Latency4JConstants#CONFIG_FILE_VM_PROP}</q>.</li>
	 * <li>If the above two options are not available, it will search for the
	 * file
	 * <q>{@link Latency4JConstants#DEFAULT_CONFIGURATION_RESOURCE_NAME}</q> in
	 * the application classpath.</li>
	 * </ul>
	 * </p>
	 */
	public void init() {
		String configurationResourceName = this.configurationPath;

		boolean useDefault = false;

		if (configurationResourceName == null || configurationResourceName.length() == 0) {
			if (logger.isDebugEnabled())
				logger.debug("No epsilon configuration resource specified. Will attempt to read from vm property '{}'.",
						CONFIG_FILE_VM_PROP);

			configurationResourceName = System.getProperty(CONFIG_FILE_VM_PROP);
		}

		// if still null, then
		if (configurationResourceName == null || configurationResourceName.length() == 0) {
			if (logger.isDebugEnabled()) logger.debug("No value specified for vm property '{}', will default to '{}'.",
					CONFIG_FILE_VM_PROP, DEFAULT_CONFIGURATION_RESOURCE_NAME);
			configurationResourceName = DEFAULT_CONFIGURATION_RESOURCE_NAME;
			useDefault = true;
		}

		try {
			ConfigurationReader.readConfiguration(getEpsilonResourceManager(), configurationResourceName);
		} catch (Throwable exception) {
			if (useDefault) {
				if (logger.isDebugEnabled()) logger.debug(
						"Failed to read configuration from classpath using default config-name. Probably not available.",
						exception);

				logger.warn("Epsilon not initialized! No configuration set found at this time");
			} else Latency4JException.wrapException(exception);
		}
	}

	/**
	 * <p>
	 * Forces initialisation from the {@link Latency4JConfiguration configuration}
	 * which is read from the given {@link InputStream stream}. Provided as an
	 * alternative to the {@link #init()} method, and for situations where it is
	 * necessary to override the configuration from a specific
	 * {@link InputStream stream}.
	 * 
	 * </p>
	 * 
	 * @param stream
	 *            The {@link InputStream stream} from which to read the
	 *            {@link Latency4JConfiguration Epsilon configuration} and
	 *            initialise this instance.
	 * 
	 * @see MonitorFactoryStaticHandle#initializeFrom(InputStream)
	 */
	public void init(final InputStream stream) {
		ConfigurationReader.readConfiguration(getEpsilonResourceManager(), stream);
	}

	/**
	 * <p>
	 * Returns a reference to the {@link LatencyProcessor asynchronous latency
	 * processor} which processes {@link WorkDuration durations} generated from
	 * the {@link AsynchronousLatencyMonitor monitors} created by the factory.
	 * </p>
	 * 
	 * @return A reference to the {@link LatencyProcessor latency processor}
	 *         shared by the {@link AsynchronousLatencyMonitor monitors} created
	 *         by the factory.
	 */
	public LatencyProcessor getAsyncProcessor() {
		return this.asyncProcessor;
	}

	/**
	 * <p>
	 * Returns a reference to the {@link Latency4JResourceManager resource
	 * manager} for this instance.
	 * </p>
	 * 
	 * @return A reference to the {@link Latency4JResourceManager resource
	 *         manager} for this instance.
	 */
	protected Latency4JResourceManager getEpsilonResourceManager() {
		return this.epsilonResourceManager;
	}

	/**
	 * <p>
	 * Creates an {@link AlertHandler alert handler} for an implicit
	 * {@link StatisticalLatencyRequirement statistical requirement}. It returns
	 * an {@link MissedTargetAlertLogger alert logger} initialised with the
	 * default log-level, and with the logger category set as to the specified
	 * category.
	 * </p>
	 * 
	 * @param workCategory
	 *            The category covered by the implicit {@link LatencyRequirement
	 *            requirement} for which the {@link MissedTargetAlertLogger
	 *            alert handler} is being created.
	 * 
	 * @return A {@link MissedTargetAlertLogger alert logger} initialised to the
	 *         default log-level and the given category.
	 */
	private MissedTargetAlertLogger createHandlerForImplicitRequirement(final String workCategory) {
		MissedTargetAlertLogger result = new MissedTargetAlertLogger();

		result.setAlertHandlerId(workCategory + String.valueOf(System.currentTimeMillis()));

		Map<String, String> handlerParams = new HashMap<String, String>();
		handlerParams.put(MissedTargetAlertLogger.LOG_CATEGORY_CONFIG_PARAM_KEY, workCategory);

		result.setParameters(handlerParams);

		// initialise
		result.init();

		return result;
	}
}// end class def