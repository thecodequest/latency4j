package org.latency4j.configuration;

import javax.xml.bind.annotation.XmlAttribute;

import org.latency4j.StatisticalLatencyRequirement;

/**
 * <p>
 * Encapsulates the configuration for a {@link StatisticalLatencyRequirement
 * statistical latency requirement}.
 * </p>
 */
public class StatisticalRequirementConfiguration extends LatencyRequirementConfiguration {
	/**
	 * <p>
	 * Encapsulates the
	 * {@link StatisticalLatencyRequirement#getObservationsSignificanceBarrier()
	 * the significance barrier of the associated requirement}.
	 * </p>
	 */
	private Long observationsSignificanceBarrier;

	/**
	 * <p>
	 * Encapsulates the {@link StatisticalLatencyRequirement#getToleranceLevel()
	 * the tolerance level of the associated requirement}.
	 * </p>
	 */
	private Double toleranceLevel;

	/**
	 * <p>
	 * Accessor for the field {@link #observationsSignificanceBarrier}.
	 * </p>
	 * 
	 * @return The value of the field {@link #observationsSignificanceBarrier}.
	 * @see StatisticalLatencyRequirement#getObservationsSignificanceBarrier()
	 */
	@XmlAttribute(name = "observationsSignificanceBarrier", required = false)
	public Long getObservationsSignificanceBarrier() {
		return observationsSignificanceBarrier;
	}

	/**
	 * <p>
	 * Mutator for the field {@link #observationsSignificanceBarrier}.
	 * </p>
	 * 
	 * @param observationsSignificanceBarrier
	 *            The value to which the field
	 *            {@link #observationsSignificanceBarrier} should be set.
	 * @see StatisticalLatencyRequirement#setObservationsSignificanceBarrier(long)
	 */
	public void setObservationsSignificanceBarrier(final Long observationsSignificanceBarrier) {
		this.observationsSignificanceBarrier = observationsSignificanceBarrier;
	}

	/**
	 * <p>
	 * Accessor for the field {@link #toleranceLevel}.
	 * </p>
	 * 
	 * @return The value of the field {@link #toleranceLevel}.
	 * @see StatisticalLatencyRequirement#getToleranceLevel()
	 */
	@XmlAttribute(name = "toleranceLevel", required = false)
	public Double getToleranceLevel() {
		return toleranceLevel;
	}

	/**
	 * <p>
	 * Mutator for the field {@link #toleranceLevel}.
	 * </p>
	 * 
	 * @param toleranceLevel
	 *            The value to which the field {@link #toleranceLevel} should be
	 *            set.
	 * @see StatisticalLatencyRequirement#setToleranceLevel(double)
	 */
	public void setToleranceLevel(final Double toleranceLevel) {
		this.toleranceLevel = toleranceLevel;
	}
}// end class def