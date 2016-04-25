package org.latency4j.persistence;

import java.util.List;
import java.util.Map;

import org.latency4j.LatencyRequirement;
import org.latency4j.WorkDuration;
import org.latency4j.configuration.LatencyRequirementConfiguration;

/**
 * <p>
 * This interface defines the contract to which Epsilon persistence managers
 * have to adhere. An instance of this type is responsible for persisting, and
 * loading, {@link WorkDuration durations} to, and from, persistent storage.
 * </p>
 * <p>
 * Each {@link LatencyRequirement requirement} is
 * {@link LatencyRequirement#setPersistenceManager(WorkDurationPersistenceManager)
 * associated} with a persistence manager, either explicitly through
 * {@link LatencyRequirementConfiguration configuration} or
 * {@link DefaultWorkDurationPersistenceManager implicitly at runtime}.
 * </p>
 * 
 * <p>
 * <b>Note:</b>Instances of this class must be {@link #init() initialised} prior
 * to use.
 * </p>
 */
public interface WorkDurationPersistenceManager {
	/**
	 * <p>
	 * Initialises the instance. This method should not be called until all
	 * relevant/mandatory {@link #setParameters(Map) parameters} have been
	 * specified.
	 * </p>
	 */
	void init();

	/**
	 * <p>
	 * Sets the parameters with which the instance should be {@link #init()
	 * initialised}.
	 * </p>
	 * <p>
	 * <b>Note:</b> All mandatory parameters must be specified before the
	 * instance can be initialised.
	 * </p>
	 * 
	 * @param parameters
	 *            A '<code>key=value</code>' of parameters with which to
	 *            initialise the manager.
	 * 
	 * @see #init()
	 */
	void setParameters(Map<String, String> parameters);

	/**
	 * <p>
	 * Returns the {@link #init() initialisation} parameters as specified via
	 * {@link #setParameters(Map)}.
	 * </p>
	 * 
	 * @return The {@link #init() initialisation} parameters for the instance.
	 */
	Map<String, String> getParameters();

	/**
	 * <p>
	 * Persists the supplied {@link WorkDuration duration}.
	 * </p>
	 * 
	 * @param taskDuration
	 *            The {@link WorkDuration duration} to persist.
	 */
	void save(WorkDuration taskDuration);

	/**
	 * <p>
	 * Loads all previously persisted {@link WorkDuration duration} instances
	 * for the given {@link LatencyRequirement#getWorkCategory() category},
	 * where such {@link WorkDuration instances} exist in the backing persistent
	 * storage.
	 * </p>
	 * 
	 * @param workCategory
	 *            The {@link LatencyRequirement#getWorkCategory() category of
	 *            the requirement} whose associated {@link WorkDuration
	 *            durations} should be loaded.
	 * 
	 * @return A list of previously saved {@link WorkDuration duration}
	 *         instances for the given
	 *         {@link LatencyRequirement#getWorkCategory() category}, where they
	 *         exist in the backing persistent store.
	 */
	List<WorkDuration> loadHistoricalData(String workCategory);
}// end class def