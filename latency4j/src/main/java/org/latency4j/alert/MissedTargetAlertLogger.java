package org.latency4j.alert;

import static org.latency4j.Latency4JConstants.EPSILON_LOGGER_NAME;

import org.latency4j.CappedLatencyRequirement;
import org.latency4j.AlertHandler;
import org.latency4j.Latency4JConstants;
import org.latency4j.Latency4JException;
import org.latency4j.LatencyRequirement;
import org.latency4j.StatisticalLatencyRequirement;
import org.latency4j.WorkDuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * An {@link AlertHandler alert handler} implementation which logs alerts
 * using the <a href="http://www.slf4j.org/">SLF4J</a> logger abstraction API.
 * The log-level and category with which messages are logged can be configured
 * via the parameter list as specified via the
 * {@link AlertHandler#setParameters(java.util.Map)} method.
 * </p>
 * <p>
 * The log-level is specified via the parameter
 * {@value #LOG_LEVEL_CONFIG_PARAM_KEY}. The parameter value must translate to
 * one of the values of the Enum {@link MissedTargetLogLevel}. Where no
 * log-level is specified, the default {@link MissedTargetLogLevel#INFO} is
 * used.
 * </p>
 * <p>
 * The logger category is specified via the parameter
 * {@value #LOG_CATEGORY_CONFIG_PARAM_KEY}. Where no value is specified for this
 * parameter, the internal category {@link Latency4JConstants#EPSILON_LOGGER_NAME}
 * is used.
 * </p>
 * <p>
 * In addition to the logger specific parameters described above, this class
 * also supports the parameters defined by the parent class
 * {@link AbstractAlertHandler}.
 * </p>
 * <p>
 * <b>Note:</b>Instances of this class must be {@link #init() initialised}
 * before use.
 * </p>
 */
public class MissedTargetAlertLogger extends AbstractAlertHandler {

	/**
	 * <p>
	 * The key (
	 * <q><code>{@value #LOG_LEVEL_CONFIG_PARAM_KEY}</q></code>) of the
	 * parameter which specifies the {@link MissedTargetLogLevel log-level} with
	 * which an instance logs alerts.
	 * </p>
	 */
	public static final String LOG_LEVEL_CONFIG_PARAM_KEY = "logLevel";

	/**
	 * <p>
	 * The key (
	 * <q><code>{@value #LOG_CATEGORY_CONFIG_PARAM_KEY}</code></q>) of the
	 * parameter which specifies the logger category to which an instance logs
	 * alerts.
	 * </p>
	 */
	public static final String LOG_CATEGORY_CONFIG_PARAM_KEY = "logger.category";

	/**
	 * <p>
	 * Internal logger which is used for logging alerts.
	 * </p>
	 */
	private Logger logger;

	/**
	 * <p>
	 * The {@link MissedTargetLogLevel log-level} at which alerts are written.
	 * </p>
	 */
	private MissedTargetLogLevel internalLogLevel;

	/**
	 * <p>
	 * Default constructor.
	 * </p>
	 */
	public MissedTargetAlertLogger() {
		super();
	}

	@Override
	public void init() {
		super.init();

		if (parameters.containsKey(LOG_CATEGORY_CONFIG_PARAM_KEY))
			logger = LoggerFactory.getLogger(parameters.get(LOG_CATEGORY_CONFIG_PARAM_KEY));
		else logger = LoggerFactory.getLogger(EPSILON_LOGGER_NAME);

		if (!parameters.containsKey(LOG_LEVEL_CONFIG_PARAM_KEY)) internalLogLevel = MissedTargetLogLevel.INFO;
		else {
			internalLogLevel = MissedTargetLogLevel.resolve(parameters.get(LOG_LEVEL_CONFIG_PARAM_KEY));
			if (internalLogLevel == null) throw new Latency4JException(
					"Unable to resolve specified log-level '" + parameters.get(LOG_LEVEL_CONFIG_PARAM_KEY));
		}

		this.initialized.set(true);
	}

	@Override
	public void latencyDeviationExceededTolerance(final StatisticalLatencyRequirement requirement,
			final WorkDuration duration, final double deviationFromMean, final double mean) {

		String message = prepareDeviationExceededToleranceMsg(requirement, duration, deviationFromMean, mean);
		internalLogMessage(message);
	}

	@Override
	public void latencyExceededCap(final CappedLatencyRequirement requirement, final WorkDuration duration) {
		String message = prepareLatencyExceededCap(requirement, duration);
		internalLogMessage(message);
	}

	@Override
	public void workCategoryFailed(final LatencyRequirement requirement, final WorkDuration duration) {
		String message = prepareWorkCategoryFailedMsg(requirement, duration);
		internalLogMessage(message);
	}

	/**
	 * <p>
	 * Returns a reference to the internal logger used by this instance for
	 * logging alerts.
	 * </p>
	 * 
	 * @return A reference to the internal alerts logger.
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * Internal utility method which logs a message using the internal
	 * {@link #logger logger} and the pre-configured {@link #internalLogLevel
	 * log-level}.
	 * 
	 * @param message
	 *            The message to log.
	 */
	private void internalLogMessage(final String message) {
		if (logger != null) {
			switch (internalLogLevel) {
				case DEBUG:
					logger.debug(message);
					break;
				case INFO:
					logger.info(message);
					break;
				case WARN:
					logger.warn(message);
					break;
				case ERROR:
					logger.error(message);
					break;
				case TRACE:
					logger.trace(message);
					break;
				default:
					break;
			}
		}
	}// end method def
}