package org.latency4j.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.latency4j.CappedLatencyRequirement;
import org.latency4j.AlertHandler;
import org.latency4j.Latency4JException;
import org.latency4j.LatencyRequirement;
import org.latency4j.StatisticalLatencyRequirement;
import org.latency4j.persistence.WorkDurationPersistenceManager;
import org.latency4j.processing.Latency4JResourceManager;
import org.latency4j.util.ClasspathResourceUtils;
import org.latency4j.util.IOResourceCloser;
import org.latency4j.util.NetworkUtils;
import org.latency4j.util.PrimitiveTypeUtilities;
import org.latency4j.util.StreamReadWriteUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Reads the {@link Latency4JConfiguration Epsilon configuration} from an XML
 * source, and initialises an {@link Latency4JResourceManager Epsilon resource
 * manager} to manage the configured {@link LatencyRequirement requirements} and
 * {@link AlertHandler alert handlers}.
 * </p>
 */
public class ConfigurationReader {
	/**
	 * <p>
	 * Internal logger.
	 * </p>
	 */
	private static Logger logger = LoggerFactory.getLogger(ConfigurationReader.class);

	/**
	 * <p>
	 * Loads an {@link Latency4JConfiguration Epsilon configuration} from the
	 * specified {@link InputStream stream}, and populates the given
	 * {@link Latency4JResourceManager resource manager} with the configured
	 * {@link LatencyRequirement requirements} and {@link AlertHandler
	 * alert handlers}.
	 * </p>
	 * 
	 * @param resourceManager
	 *            The {@link Latency4JResourceManager resource manager} into which
	 *            the configured {@link LatencyRequirement requirements} and
	 *            {@link AlertHandler alert handlers} will be loaded.
	 * 
	 * @param inputStream
	 *            The {@link InputStream stream} from which the
	 *            {@link Latency4JConfiguration Epsilon configuration} XML will be
	 *            read.
	 */
	public static void readConfiguration(final Latency4JResourceManager resourceManager, final InputStream inputStream) {
		Latency4JConfiguration configuration;
		try {
			// reset resource manager
			resourceManager.reset();

			JAXBContext context = JAXBContext.newInstance(Latency4JConfiguration.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			JAXBElement<Latency4JConfiguration> rootElement = unmarshaller.unmarshal(new StreamSource(inputStream),
					Latency4JConfiguration.class);
			configuration = rootElement.getValue();

			loadAlertHandlers(configuration, resourceManager);
			loadLatencyRequirements(configuration, resourceManager);
		} catch (Throwable loadException) {
			if (logger.isDebugEnabled())
				logger.debug("Unexpected Exception caught unmarshalling epsilon configuration from stream.");
			logger.error("Error unmarshalling epsilon configuration.");
			throw Latency4JException.wrapException(loadException);
		}
	}

	/**
	 * <p>
	 * Loads an {@link Latency4JConfiguration Epsilon configuration} from the
	 * specified resource {@link URI}, and populates the given
	 * {@link Latency4JResourceManager resource manager} with the configured
	 * {@link LatencyRequirement requirements} and {@link AlertHandler
	 * alert handlers}.
	 * </p>
	 * 
	 * <p>
	 * <b>Note</b> that for a classpath resource, the URI should be prefixed
	 * with
	 * <q>CLASSPATH:</q>.
	 * </p>
	 * 
	 * @param resourceManager
	 *            The {@link Latency4JResourceManager resource manager} into which
	 *            the configured {@link LatencyRequirement requirements} and
	 *            {@link AlertHandler alert handlers} will be loaded.
	 * 
	 * @param configurationResourceURI
	 *            The URI from which the {@link Latency4JConfiguration
	 *            configuration XML} will be read.
	 */
	public static void readConfiguration(final Latency4JResourceManager resourceManager,
			final String configurationResourceURI) {
		InputStream resourceStream = null;
		try {
			resourceStream = resolveStreamFromSource(configurationResourceURI, resourceManager);
			if (resourceStream == null) {
				String error = "Could not read Epsilon configuration from location '" + configurationResourceURI
						+ "'. Null resource stream.";
				throw new Latency4JException(error);
			} else {
				logger.info("Reading Epsilon configuration file from: " + configurationResourceURI);
				readConfiguration(resourceManager, resourceStream);
			}
		}
		finally {
			IOResourceCloser.close(resourceStream);
		}
	}

	/**
	 * <p>
	 * Opens an {@link InputStream input-stream} to the specified location.
	 * </p>
	 * 
	 * @param source
	 *            The location from which to open an {@link InputStream
	 *            input-stream}. This location can either be a classpath
	 *            resource, URL or file.
	 * 
	 * @param resourceManager
	 *            The {@link Latency4JResourceManager resource manager} which is
	 *            being populated. This is required for classpath resources
	 *            only, as it is assumed that the resource is accessible to the
	 *            {@link ClassLoader class-loader} with which the
	 *            {@link Latency4JResourceManager resource manager} was
	 *            loaded/created.
	 * 
	 * @return An {@link InputStream input stream} from the specified location.
	 * 
	 * @throws ObixException
	 *             If the source specification is malformed, or an error occurs
	 *             trying to open a FileInputStream from the location.
	 */
	private static InputStream resolveStreamFromSource(final String source,
			final Latency4JResourceManager resourceManager) {
		InputStream result;

		try {
			if (source == null || source.length() == 0) result = null;
			else if (ClasspathResourceUtils.isClasspathURL(source))
				result = ClasspathResourceUtils.openStreamToResource(source, resourceManager);
			else if (NetworkUtils.isValidURL(source)) {
				URL url = new URL(source);
				result = StreamReadWriteUtilities.bufferStream(url.openStream());
			} else result = new FileInputStream(source);
		} catch (IOException ioexce) {
			throw Latency4JException.wrapException(ioexce);
		}

		return result;
	}

	/**
	 * <p>
	 * Internal method which initialises the {@link AlertHandler alert
	 * handlers} specified in the {@link AlertHandlerGroupConfig alert handlers
	 * element} of the {@link Latency4JConfiguration configuration instance}.
	 * </p>
	 * 
	 * @param configuration
	 *            The {@link Latency4JConfiguration configuration instance} which
	 *            contains the {@link AlertHandlerConfiguration
	 *            configuration(s)} for the {@link AlertHandler alert
	 *            handler(s)} to load.
	 *
	 * @param epsilonResourceManager
	 *            The resource manager into which the {@link AlertHandler
	 *            alert handler} instances will be loaded.
	 * 
	 * @throws Exception
	 *             If an exception occurs processing the
	 *             {@link Latency4JConfiguration configuration}.
	 */
	private static void loadAlertHandlers(final Latency4JConfiguration configuration,
			final Latency4JResourceManager epsilonResourceManager) {
		if (configuration.getAlertHandlersConfiguration() != null) {
			List<AlertHandlerConfiguration> alertHandlerConfiguraionList = configuration.getAlertHandlersConfiguration()
					.getAlertHandlers();
			if (logger.isDebugEnabled()) logger.debug(alertHandlerConfiguraionList.size() + " alert handlers defined");

			AlertHandler alertHandler;
			for (AlertHandlerConfiguration alertHandlerConfiguration : alertHandlerConfiguraionList) {
				alertHandler = createAlertHandler(alertHandlerConfiguration);
				epsilonResourceManager.registerHandler(alertHandler);
			}
		} else if (logger.isDebugEnabled()) {
			logger.debug("No alert-handlers specified.");
		}
	}

	/**
	 * <p>
	 * Initialises the {@link LatencyRequirement requirements} encapsulated in
	 * the specified {@link Latency4JConfiguration configuration}, and loads them
	 * into the given {@link Latency4JResourceManager resource manager}.
	 * </p>
	 * 
	 * @param configuration
	 *            The {@link Latency4JConfiguration configuration set} from which
	 *            the {@link LatencyRequirement latency requirements} are to be
	 *            loaded.
	 * 
	 * @param epsilonResourceManager
	 *            The {@link Latency4JResourceManager resource manager} which
	 *            holds the {@link LatencyRequirementGroupConfig requirement
	 *            configuration elements}.
	 */
	private static void loadLatencyRequirements(final Latency4JConfiguration configuration,
			final Latency4JResourceManager epsilonResourceManager) throws Exception {
		LatencyRequirementGroupConfig latencyRequirementGroupConfig = configuration
				.getLatencyRequirementsConfiguration();
		if (configuration.getLatencyRequirementsConfiguration() != null) {
			if (logger.isDebugEnabled()) logger.debug("Loading capped requirements.");

			loadRequirements(latencyRequirementGroupConfig.getCappedRequirements(), epsilonResourceManager);

			if (logger.isDebugEnabled()) logger.debug("Loading stats requirements.");

			loadRequirements(latencyRequirementGroupConfig.getStatisticalRequirements(), epsilonResourceManager);

		} else if (logger.isDebugEnabled()) logger.debug("No latency requirements specified.");
	}

	private static void loadRequirements(final List<? extends LatencyRequirementConfiguration> latencyRequirements,
			final Latency4JResourceManager epsilonResourceManager) throws Exception {
		if (latencyRequirements != null) {
			if (logger.isDebugEnabled()) logger.debug(latencyRequirements.size() + " requirements defined.");

			LatencyRequirement latencyRequirement;
			AlertHandler alertHandler;
			for (LatencyRequirementConfiguration latencyRequirementConfiguration : latencyRequirements) {
				latencyRequirement = createLatencyRequirement(latencyRequirementConfiguration);

				if (latencyRequirementConfiguration.getAlertHandlerIds() != null) {
					for (String handlerName : latencyRequirementConfiguration.getAlertHandlerIds()) {
						alertHandler = epsilonResourceManager.getAlertHandler(handlerName);
						if (alertHandler == null)
							throw new Latency4JException("Unable to find handler with id " + handlerName);
						latencyRequirement.getAlertHandlers().add(alertHandler);
					}
				}

				epsilonResourceManager.registerLatencyRequirement(latencyRequirement);
			}
		} else if (logger.isDebugEnabled()) logger.debug("No matching requirements specified.");
	}

	/**
	 * <p>
	 * Creates a {@link LatencyRequirement latency requirement} from a
	 * {@link LatencyRequirementConfiguration requirement configuration}.
	 * </p>
	 * 
	 * @param configuration
	 *            The {@link LatencyRequirementConfiguration configuration} from
	 *            which to create the {@link LatencyRequirement requirement}.
	 * @return A {@link LatencyRequirement requirement} initialised from the
	 *         given {@link LatencyRequirementConfiguration configuration}.
	 * @throws Exception
	 *             If an error occurs initialising the {@link LatencyRequirement
	 *             requirement}.
	 */
	@SuppressWarnings("unchecked")
	private static LatencyRequirement createLatencyRequirement(final LatencyRequirementConfiguration configuration)
			throws Exception {
		LatencyRequirement result;
		if (configuration instanceof CappedRequirementConfiguration) {
			CappedRequirementConfiguration cappedRequirementConfig = (CappedRequirementConfiguration) configuration;
			if (cappedRequirementConfig.getExpectedLatency() == null
					|| cappedRequirementConfig.getExpectedLatency() <= 0) { throw new Latency4JException(
							"A non-zero value must be specified for expected latency for requirement '"
									+ cappedRequirementConfig.getWorkCategory() + "'."); }
			result = new CappedLatencyRequirement();
			((CappedLatencyRequirement) result).setExpectedLatency(cappedRequirementConfig.getExpectedLatency());
		} else {
			StatisticalLatencyRequirement statisticalRequirement = new StatisticalLatencyRequirement();
			StatisticalRequirementConfiguration statsRequirementConfig = (StatisticalRequirementConfiguration) configuration;
			if (statsRequirementConfig.getObservationsSignificanceBarrier() != null
					&& statsRequirementConfig.getObservationsSignificanceBarrier() > 0) {
				statisticalRequirement.setObservationsSignificanceBarrier(
						statsRequirementConfig.getObservationsSignificanceBarrier());
			}
			if (statsRequirementConfig.getToleranceLevel() != null)
				statisticalRequirement.setToleranceLevel(statsRequirementConfig.getToleranceLevel());
			result = statisticalRequirement;
		}
		result.setWorkCategory(configuration.getWorkCategory());
		if (configuration.getIgnoreErrors() != null) result.setIgnoreErrors(configuration.getIgnoreErrors());

		if (configuration.getPersistenceManagerClass() != null
				&& configuration.getPersistenceManagerClass().length() > 0) {
			WorkDurationPersistenceManager persistenceManager;
			Class<WorkDurationPersistenceManager> persistenceManagerClass = (Class<WorkDurationPersistenceManager>) Class
					.forName(configuration.getPersistenceManagerClass());

			if (logger.isDebugEnabled()) logger.debug("Requirement for '" + configuration.getWorkCategory()
					+ "' specified custom persistence-manager '" + configuration.getPersistenceManagerClass() + "'.");
			persistenceManager = persistenceManagerClass.newInstance();
			persistenceManager.setParameters(configuration.getPersistenceManagerParameters());
			persistenceManager.init();
			result.setPersistenceManager(persistenceManager);
		}
		result.init();
		return result;
	}

	/**
	 * <p>
	 * Creates/initialises an {@link AlertHandler alert handler} from a
	 * {@link AlertHandlerConfiguration configuration} instance. In essence, it
	 * turns the specification of an {@link AlertHandler alert handler}
	 * into an instance.
	 * </p>
	 * 
	 * @param alertHandlerConfiguration
	 *            The {@link AlertHandlerConfiguration configuration} from which
	 *            to create an {@link AlertHandler alert handler}.
	 * @return An {@link AlertHandler alert handler} instance created
	 *         from the given {@link AlertHandlerConfiguration configuration}.
	 * 
	 */
	private static AlertHandler createAlertHandler(final AlertHandlerConfiguration alertHandlerConfiguration) {
		AlertHandler result = PrimitiveTypeUtilities
				.createObjectFromClass(alertHandlerConfiguration.getClassName());
		result.setAlertHandlerId(alertHandlerConfiguration.getAlertHandlerId());
		result.setParameters(alertHandlerConfiguration.getParameters());
		result.init();
		return result;
	}
}