package org.latency4j.processing;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.latency4j.AlertHandler;
import org.latency4j.LatencyRequirement;
import org.latency4j.configuration.Latency4JConfiguration;

/**
 * <p>
 * Can be used to manage the {@link AlertHandler handlers} and
 * {@link LatencyRequirement requirements} declared within a given
 * {@link Latency4JConfiguration Epsilon configuration}.
 * </p>
 */
public class Latency4JResourceManager implements Serializable {
	private static final long serialVersionUID = 7512839076269152341L;

	/**
	 * <p>
	 * {@link Map Map} of {@link AlertHandler alert handlers} keyed by
	 * their {@link AlertHandler#getAlertHandlerId() identifiers}.
	 * </p>
	 */
	private final Map<String, AlertHandler> alertHandlers;

	/**
	 * <p>
	 * {@link Map Map} of {@link LatencyRequirement latency requirements} keyed
	 * by their {@link LatencyRequirement#getWorkCategory() category names}.
	 * </p>
	 */
	private final Map<String, LatencyRequirement> latencyRequirements;

	/**
	 * <p>
	 * Constructor.
	 * </p>
	 */
	public Latency4JResourceManager() {
		alertHandlers = new HashMap<String, AlertHandler>();
		latencyRequirements = new HashMap<String, LatencyRequirement>();
	}

	/**
	 * <p>
	 * Indicates if this instance holds a reference to an
	 * {@link AlertHandler alert handler} with the specified
	 * {@link AlertHandler#getAlertHandlerId() id}.
	 * </p>
	 * 
	 * @param id
	 *            The {@link AlertHandler#getAlertHandlerId() identifier}
	 *            of the {@link AlertHandler alert handler} to check for.
	 * 
	 * @return <code>True</code> if this instance contains a reference to the
	 *         {@link AlertHandler handler}, and <code>False</code>
	 *         otherwise.
	 */
	public boolean containsHandler(final String id) {
		return alertHandlers.containsKey(id);
	}

	/**
	 * <p>
	 * Resets this instance by deleting all registered {@link LatencyRequirement
	 * requirements} and {@link AlertHandler alert handlers}.
	 * </p>
	 */
	public void reset() {
		alertHandlers.clear();
		latencyRequirements.clear();
	}

	/**
	 * <p>
	 * Registers (stores) the specified {@link AlertHandler alert
	 * handler}, thus making it possible to retrieve it
	 * {@link #getAlertHandler(String) later} using its
	 * {@link AlertHandler#getAlertHandlerId() id}.
	 * </p>
	 * 
	 * @param alertHandler
	 *            The alert handler to store.
	 */
	public void registerHandler(final AlertHandler alertHandler) {
		alertHandlers.put(alertHandler.getAlertHandlerId(), alertHandler);
	}

	/**
	 * <p>
	 * Returns a reference to a {@link #registerHandler(AlertHandler)
	 * stored} {@link AlertHandler alert handler} with the given
	 * {@link AlertHandler#getAlertHandlerId() id}.
	 * </p>
	 * 
	 * @param id
	 *            The identifier of the {@link AlertHandler alert
	 *            handler} to retrieve.
	 * 
	 * @return The {@link AlertHandler alert handler} with the specified
	 *         {@link AlertHandler#getAlertHandlerId() id} if the manager
	 *         holds a reference to it and <code>NULL</code> otherwise.
	 */
	public AlertHandler getAlertHandler(final String id) {
		AlertHandler result = alertHandlers.get(id);
		return result;
	}

	/**
	 * <p>
	 * Indicates if the manager contains a reference to the
	 * {@link LatencyRequirement latency requirement} for the given
	 * {@link LatencyRequirement#getWorkCategory() category}.
	 * </p>
	 * 
	 * @param workCategory
	 *            The {@link LatencyRequirement#getWorkCategory() category} of
	 *            the {@link LatencyRequirement requirement} to search for.
	 * 
	 * @return <code>True</code> if this manager contains a
	 *         {@link LatencyRequirement requirement} for the given category,
	 *         and <code>False</code> otherwise.
	 * 
	 * @see #getLatencyRequirement(String)
	 * @see #registerLatencyRequirement(LatencyRequirement)
	 */
	public boolean containsRequirement(final String workCategory) {
		return latencyRequirements.containsKey(workCategory);
	}

	/**
	 * <p>
	 * Returns a reference to the {@link LatencyRequirement requirement}
	 * corresponding to the given {@link LatencyRequirement#getWorkCategory()
	 * category} where such a {@link LatencyRequirement requirement} had been
	 * previously {@link #registerLatencyRequirement(LatencyRequirement)
	 * registered} with the manager.
	 * </p>
	 * 
	 * @param workCategory
	 *            The {@link LatencyRequirement#getWorkCategory() category} of
	 *            the {@link LatencyRequirement requirement} to search for.
	 * 
	 * @return The {@link LatencyRequirement requirement} for the specified
	 *         {@link LatencyRequirement#getWorkCategory() category} if one has
	 *         been {@link #registerLatencyRequirement(LatencyRequirement)
	 *         registered} and <code>Null</code> otherwise.
	 * 
	 * @see #registerLatencyRequirement(LatencyRequirement)
	 * @see #containsRequirement(String)
	 */
	public LatencyRequirement getLatencyRequirement(final String workCategory) {
		LatencyRequirement result = latencyRequirements.get(workCategory);
		return result;
	}

	/**
	 * <p>
	 * Stores a reference to the specified {@link LatencyRequirement
	 * requirement} in the internal storage {@link Map map}. The stored
	 * {@link LatencyRequirement requirement} can be
	 * {@link #getLatencyRequirement(String) retrieved} later using the
	 * {@link LatencyRequirement#getWorkCategory() requirement category}.
	 * </p>
	 * 
	 * @param requirement
	 *            The {@link LatencyRequirement requirement} to store.
	 * @see #containsRequirement(String)
	 * @see AsynchronousLatencyMonitorFactory#init()
	 */
	public void registerLatencyRequirement(final LatencyRequirement requirement) {
		latencyRequirements.put(requirement.getWorkCategory(), requirement);
	}
}