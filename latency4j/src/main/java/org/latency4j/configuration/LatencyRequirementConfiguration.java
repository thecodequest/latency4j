package org.latency4j.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.latency4j.AlertHandler;
import org.latency4j.LatencyRequirement;
import org.latency4j.WorkDuration;
import org.latency4j.persistence.WorkDurationPersistenceManager;

/**
 * <p>
 * Encapsulates the specification of a {@link LatencyRequirement latency
 * requirement}.
 * </p>
 * <p>
 * In addition to the fields with which {@link LatencyRequirement requirements}
 * are initialised, this class also models two additional concepts that are
 * crucial to latency monitoring:
 * 
 * <ul>
 * <li><b>{@link WorkDurationPersistenceManager A Persistence Manager}:</b> Each
 * {@link LatencyRequirement latency requirement} is associated with a
 * {@link WorkDurationPersistenceManager persistence manager} which is used to
 * persist and load {@link WorkDuration durations} generated in relation to said
 * {@link LatencyRequirement requirement}.</li>
 * 
 * <li><b>{@link AlertHandler Zero or more alert handlers}:</b> Ideally a
 * {@link LatencyRequirement latency requirement} should be associated with at
 * least one {@link AlertHandler alert handler} that will handle
 * notifications generated in the course of monitoring the
 * {@link LatencyRequirement requirement}.</li>
 * </ul>
 * </p>
 */
public abstract class LatencyRequirementConfiguration {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = -8165432662912077929L;

	/**
	 * <p>
	 * The {@link LatencyRequirement#getWorkCategory() operation/category} for
	 * the requirement.
	 * </p>
	 */
	private String workCategory;

	/**
	 * <p>
	 * The name of the class which implements the
	 * {@link WorkDurationPersistenceManager} interface, and which will be used
	 * to persist {@link WorkDuration duration} instances generated as a result
	 * of monitoring the associated {@link LatencyRequirement requirement}.
	 * </p>
	 */
	private String persistenceManagerClass;

	/**
	 * <p>
	 * Used to specify if the associated
	 * {@link LatencyRequirement#isIgnoreErrors() requirement should ignore
	 * execution errors}.
	 * </p>
	 */
	private Boolean ignoreErrors;

	/**
	 * <p>
	 * The parameters with which the {@link WorkDurationPersistenceManager
	 * persistence manager} will be initialised.
	 * </p>
	 */
	private Map<String, String> persistenceManagerParameters;

	/**
	 * <p>
	 * The {@link AlertHandler#getAlertHandlerId() identifiers} of the
	 * {@link AlertHandler alert handlers} which will handle
	 * notifications generated from monitoring the configured
	 * {@link LatencyRequirement requirement}. The identifiers must map to the
	 * values specified in the
	 * {@link Latency4JConfiguration#getAlertHandlersConfiguration() Epsilon
	 * configuration}.
	 * </p>
	 */
	private List<String> alertHandlerIds;

	/**
	 * <p>
	 * Default constructor.
	 * </p>
	 */
	public LatencyRequirementConfiguration() {
		alertHandlerIds = new ArrayList<String>();
		setIgnoreErrors(true);
	}

	/**
	 * <p>
	 * Returns the name of the {@link WorkDurationPersistenceManager persistence
	 * manager} implementation. Instances of this type will be used to persist
	 * {@link WorkDuration durations} generated as a result of monitoring the
	 * configured {@link LatencyRequirement requirement}.
	 * </p>
	 * 
	 * @return The name of the {@link WorkDurationPersistenceManager persistence
	 *         manager} implementation.
	 * 
	 * @see #setPersistenceManagerClass(String)
	 */
	@XmlAttribute(required = false)
	public String getPersistenceManagerClass() {
		return persistenceManagerClass;
	}

	/**
	 * <p>
	 * Accessor for the field {@link #workCategory}. This is the value to which
	 * the {@link LatencyRequirement#setWorkCategory(String) requirement
	 * category is set}.
	 * </p>
	 * 
	 * @return The value of {@link #workCategory} field.
	 */
	@XmlAttribute(name = "workCategory", required = true)
	public String getWorkCategory() {
		return workCategory;
	}

	/**
	 * <p>
	 * Mutator for the field {@link #workCategory}. This is the value to which
	 * the {@link LatencyRequirement#setWorkCategory(String) requirement
	 * category is set}.
	 * </p>
	 * 
	 * @param workCategory
	 *            The {@link LatencyRequirement#setWorkCategory(String)
	 *            category} for the associated {@link LatencyRequirement
	 *            requirement}.
	 * @see #getWorkCategory()
	 */
	public void setWorkCategory(final String workCategory) {
		this.workCategory = workCategory;
	}

	/**
	 * <p>
	 * Sets the name of the {@link WorkDurationPersistenceManager persistence
	 * manager} implementation; which will be used for persisting
	 * {@link WorkDuration durations} generated from monitoring the configured
	 * {@link LatencyRequirement requirement}.
	 * </p>
	 * 
	 * @param persistenceManagerClass
	 *            The name of the {@link WorkDurationPersistenceManager
	 *            persistence manager} implementation.
	 */
	public void setPersistenceManagerClass(final String persistenceManagerClass) {
		this.persistenceManagerClass = persistenceManagerClass;
	}

	/**
	 * <p>
	 * Accessor for the field {@link #ignoreErrors}. The value of this field
	 * indicates if execution errors {@link LatencyRequirement#isIgnoreErrors()
	 * should be ignored when monitoring the associated latency requirement}.
	 * </p>
	 * 
	 * @return The value of the field {@link #ignoreErrors}.
	 */
	@XmlAttribute(name = "ignoreErrors", required = false)
	public Boolean getIgnoreErrors() {
		return ignoreErrors;
	}

	/**
	 * <p>
	 * Mutator for the field {@link #ignoreErrors}. The value of this field
	 * indicates if execution errors {@link LatencyRequirement#isIgnoreErrors()
	 * should be ignored when monitoring the associated latency requirement}.
	 * </p>
	 * 
	 * @param ignoreErrors
	 *            The value to which the field should be set.
	 * @see #getIgnoreErrors()
	 */
	public void setIgnoreErrors(final Boolean ignoreErrors) {
		this.ignoreErrors = ignoreErrors;
	}

	/**
	 * <p>
	 * Returns the parameters with which the
	 * {@link WorkDurationPersistenceManager persistence manager} implementation
	 * [specified by the invocation {@link #setPersistenceManagerClass(String)}]
	 * will be initialised.
	 * </p>
	 * 
	 * @return A reference to the {@link WorkDurationPersistenceManager
	 *         persistence manager} initialisation parameters.
	 * @see #setPersistenceManagerParameters(Map)
	 */
	public Map<String, String> getPersistenceManagerParameters() {
		return persistenceManagerParameters;
	}

	/**
	 * <p>
	 * Sets the parameters with which the {@link WorkDurationPersistenceManager
	 * persistence manager} implementation [as specified by the invocation
	 * {@link #setPersistenceManagerClass(String)}] will be initialised.
	 * </p>
	 * 
	 * @param parameters
	 *            The {@link WorkDurationPersistenceManager persistence manager}
	 *            parameters.
	 * @see LatencyRequirementConfiguration#getPersistenceManagerParameters()
	 */
	public void setPersistenceManagerParameters(final Map<String, String> parameters) {
		this.persistenceManagerParameters = parameters;
	}

	/**
	 * <p>
	 * Returns a list containing the
	 * {@link AlertHandler#getAlertHandlerId() identifiers} of the
	 * {@link AlertHandler alert handlers} which will process alerts
	 * generated as a result of monitoring the {@link LatencyRequirement
	 * requirement} encapsulated by an instance of this type.
	 * </p>
	 * 
	 * @return A list of identifiers for {@link AlertHandler alert
	 *         handlers} to which alerts concerning this requirement will be
	 *         sent.
	 * 
	 * @see #setAlertHandlerIds(List)
	 */
	@XmlElement(name = "alertHandlerId", required = false)
	public List<String> getAlertHandlerIds() {
		return this.alertHandlerIds;
	}

	/**
	 * <p>
	 * Sets the {@link AlertHandler#getAlertHandlerId() identifiers} of
	 * the {@link AlertHandler alert handlers}, via which all alerts
	 * generated in relation to the configured {@link LatencyRequirement
	 * requirement} will be processed.
	 * </p>
	 * <p>
	 * Note that the contents of the list must be a subset of the values
	 * returned by invoking
	 * {@link AlertHandlerConfiguration#getAlertHandlerId()} against the
	 * contents of the {@link AlertHandlerGroupConfig alert handler
	 * configuration}. Put differently, all referenced
	 * {@link AlertHandler handlers} must be declared in the
	 * {@link Latency4JConfiguration Epsilon configuration}.
	 * </p>
	 * 
	 * @param identfiers
	 *            The {@link AlertHandler alert handler} identifiers.
	 */
	public void setAlertHandlerIds(final List<String> identfiers) {
		this.alertHandlerIds = identfiers;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("WorkCategoryId: " + getWorkCategory());
		result.append("\n\tHandlers:" + getAlertHandlerIds());
		return result.toString();
	}
}