package org.latency4j.configuration;

import javax.xml.bind.annotation.XmlAttribute;

import org.latency4j.CappedLatencyRequirement;

/**
 * <p>
 * Configuration bean-implementation encapsulating information required to
 * instantiate a {@link CappedLatencyRequirement capped latency requirement}.
 * </p>
 * 
 * @see CappedLatencyRequirement
 */
public class CappedRequirementConfiguration extends LatencyRequirementConfiguration {
	/**
	 * <p>
	 * Used to initialise the field
	 * {@link CappedLatencyRequirement#setExpectedLatency(Long)}.
	 * </p>
	 */
	private Long expectedLatency;

	/**
	 * <p>
	 * Accessor for field {@link #expectedLatency}.
	 * </p>
	 * 
	 * @return The value of the field {@link #expectedLatency}.
	 */
	@XmlAttribute(name = "expectedLatency", required = false)
	public Long getExpectedLatency() {
		return expectedLatency;
	}

	/**
	 * <p>
	 * Mutator for field {@link #expectedLatency}.
	 * </p>
	 * 
	 * @param expectedLatency
	 *            The value to which the field {@link #expectedLatency} should
	 *            be set.
	 */
	public void setExpectedLatency(final Long expectedLatency) {
		this.expectedLatency = expectedLatency;
	}
}