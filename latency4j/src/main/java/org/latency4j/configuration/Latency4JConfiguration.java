package org.latency4j.configuration;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.latency4j.AlertHandler;
import org.latency4j.LatencyRequirement;

/*
 * Schemagen options
 * schemagen -cp target\classes src\main\java\com\obixlabs\epsilon\configuration\Latency4JConfiguration.java -d src\main\resources\doctype
 */
/**
 * <p>
 * Bean-implementation which represents an Epsilon runtime configuration. It
 * consists of one or more {@link LatencyRequirementConfiguration latency
 * requirement templates}, and one or more {@link AlertHandlerConfiguration
 * alert handler templates}.
 * </p>
 * <p>
 * Put differently, a configuration consists of one
 * {@link AlertHandlerGroupConfig} and a {@link LatencyRequirementGroupConfig},
 * where the {@link AlertHandlerGroupConfig} instance contains configuration for
 * the {@link AlertHandler receptors of the alerts} generated in relation
 * to the {@link LatencyRequirement requirements} configured via the
 * {@link LatencyRequirementGroupConfig} instance.
 * </p>
 */
@XmlRootElement(name = "latency4j", namespace = "www.github.com/latency4j")
public class Latency4JConfiguration {
	/**
	 * <p>
	 * The {@link AlertHandlerConfiguration alert handler configurations}.
	 * </p>
	 */
	private AlertHandlerGroupConfig alertHandlersConfiguration;

	/**
	 * <p>
	 * The {@link LatencyRequirementConfiguration latency requirement
	 * configurations}.
	 * </p>
	 */
	private LatencyRequirementGroupConfig latencyRequirementsConfiguration;

	/**
	 * <p>
	 * Default constructor.
	 * </p>
	 */
	public Latency4JConfiguration() {}

	/**
	 * <p>
	 * Accessor to the {@link AlertHandlerConfiguration alert handler
	 * configurations/templates}.
	 * </p>
	 * 
	 * @return The {@link AlertHandlerConfiguration alert handler
	 *         configurations}.
	 */
	@XmlElement(name = "alertHandlers")
	public AlertHandlerGroupConfig getAlertHandlersConfiguration() {
		return alertHandlersConfiguration;
	}

	/**
	 * <p>
	 * Mutator for the {@link AlertHandlerConfiguration alert handler
	 * configurations/templates}.
	 * </p>
	 * 
	 * @param handlers
	 *            The {@link AlertHandlerConfiguration alert handler
	 *            configurations}.
	 */
	public void setAlertHandlersConfiguration(final AlertHandlerGroupConfig handlers) {
		this.alertHandlersConfiguration = handlers;
	}

	/**
	 * <p>
	 * Returns a reference to the {@link LatencyRequirementConfiguration latency
	 * requirement configuration entries}.
	 * </p>
	 * 
	 * @return A reference to the {@link LatencyRequirementConfiguration latency
	 *         requirement configuration entries}.
	 */
	@XmlElement(name = "latencyRequirements")
	public LatencyRequirementGroupConfig getLatencyRequirementsConfiguration() {
		return latencyRequirementsConfiguration;
	}

	/**
	 * <p>
	 * Mutator for {@link LatencyRequirementConfiguration latency requirement
	 * configurations}.
	 * </p>
	 * 
	 * @param latencyRequirements
	 *            The {@link LatencyRequirementConfiguration latency requirement
	 *            configuration entries}.
	 */
	public void setLatencyRequirementsConfiguration(final LatencyRequirementGroupConfig latencyRequirements) {
		this.latencyRequirementsConfiguration = latencyRequirements;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("Epsilon Configuration: \n");
		if (alertHandlersConfiguration != null) result.append(alertHandlersConfiguration.toString());
		if (latencyRequirementsConfiguration != null) result.append(latencyRequirementsConfiguration.toString());
		return result.toString();
	}
}// end class def