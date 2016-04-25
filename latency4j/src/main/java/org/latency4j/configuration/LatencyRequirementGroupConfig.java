package org.latency4j.configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

/**
 * <p>
 * Encapsulates the {@link LatencyRequirementConfiguration latency requirement
 * configurations} as included in an {@link Latency4JConfiguration Epsilon
 * configuration}.
 * </p>
 */
public class LatencyRequirementGroupConfig implements Serializable {
	private static final long serialVersionUID = -6493602067513014823L;

	/**
	 * <p>
	 * The {@link CappedRequirementConfiguration capped requirement
	 * configurations}.
	 * </p>
	 */
	private List<CappedRequirementConfiguration> cappedRequirements;

	/**
	 * <p>
	 * The {@link StatisticalRequirementConfiguration statistical requirement
	 * configuration}.
	 * </p>
	 */
	private List<StatisticalRequirementConfiguration> statisticalRequirements;

	/**
	 * <p>
	 * Default constructor.
	 * </p>
	 */
	public LatencyRequirementGroupConfig() {
		cappedRequirements = new ArrayList<CappedRequirementConfiguration>();
		statisticalRequirements = new ArrayList<StatisticalRequirementConfiguration>();
	}

	/**
	 * <p>
	 * Returns the {@link CappedRequirementConfiguration capped requirement
	 * configurations} encapsulated by the instance.
	 * </p>
	 * 
	 * @return The encapsulated {@link CappedRequirementConfiguration capped
	 *         requirement configurations}.
	 */
	@XmlElement(name = "cappedRequirement", required = false)
	public List<CappedRequirementConfiguration> getCappedRequirements() {
		return cappedRequirements;
	}

	/**
	 * <p>
	 * Sets the {@link CappedRequirementConfiguration capped requirement
	 * configurations} encapsulated by the instance.
	 * </p>
	 * 
	 * @param latencyRequirements
	 *            The {@link CappedRequirementConfiguration capped requirement
	 *            configurations} encapsulated by this instance.
	 */
	public void setCappedRequirements(final List<CappedRequirementConfiguration> latencyRequirements) {
		this.cappedRequirements = latencyRequirements;
	}

	/**
	 * <p>
	 * Returns the {@link StatisticalRequirementConfiguration statistical
	 * requirement configurations} encapsulated by the instance.
	 * </p>
	 * 
	 * @return The encapsulated {@link StatisticalRequirementConfiguration
	 *         statistical requirement configurations}.
	 */
	@XmlElement(name = "statisticalRequirement", required = false)
	public List<StatisticalRequirementConfiguration> getStatisticalRequirements() {
		return statisticalRequirements;
	}

	/**
	 * <p>
	 * Sets the {@link StatisticalRequirementConfiguration statistical
	 * requirement configurations} encapsulated by the instance.
	 * </p>
	 * 
	 * @param statisticalRequirements
	 *            The {@link StatisticalRequirementConfiguration statistical
	 *            requirement configurations} encapsulated by this instance.
	 */
	public void setStatisticalRequirements(final List<StatisticalRequirementConfiguration> statisticalRequirements) {
		this.statisticalRequirements = statisticalRequirements;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();

		if (cappedRequirements != null) {
			result.append("Latency Requirements: \n");
			for (LatencyRequirementConfiguration req : cappedRequirements)
				result.append(req + "\n");
		}

		return result.toString();
	}
}