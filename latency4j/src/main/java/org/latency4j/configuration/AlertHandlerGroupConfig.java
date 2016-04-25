package org.latency4j.configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import org.latency4j.AlertHandler;

/**
 * <p>
 * Configuration element which groups together zero or more
 * {@link AlertHandlerConfiguration alert handler configurations}.
 * </p>
 */
public class AlertHandlerGroupConfig implements Serializable {
	private static final long serialVersionUID = 932112427424264226L;

	/**
	 * <p>
	 * List of {@link AlertHandler alert handler}
	 * {@link AlertHandlerConfiguration templates/configurations}.
	 * </p>
	 */
	private List<AlertHandlerConfiguration> alertHandlers;

	/**
	 * <p>
	 * Default constructor.
	 * </p>
	 */
	public AlertHandlerGroupConfig() {
		alertHandlers = new ArrayList<AlertHandlerConfiguration>();
	}

	/**
	 * <p>
	 * Returns the {@link List list} of {@link AlertHandler alert
	 * handler} {@link AlertHandlerConfiguration configurations}.
	 * <p>
	 * 
	 * @return A {@link List list} of {@link AlertHandler alert handler}
	 *         {@link AlertHandlerConfiguration configurations}.
	 */
	@XmlElement(name = "alertHandler", required = true)
	public List<AlertHandlerConfiguration> getAlertHandlers() {
		return alertHandlers;
	}

	/**
	 * <p>
	 * Sets the list of {@link AlertHandler alert handler}
	 * {@link AlertHandlerConfiguration configurations} encapsulated by the
	 * given instance.
	 * </p>
	 * 
	 * @param alertHandlers
	 *            The {@link AlertHandler alert handler}
	 *            {@link AlertHandlerConfiguration configurations} to
	 *            encapsulate.
	 */
	public void setAlertHandlers(final List<AlertHandlerConfiguration> alertHandlers) {
		this.alertHandlers = alertHandlers;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		if (alertHandlers != null) {
			result.append("Alert Handlers: \n");
			for (AlertHandlerConfiguration handler : alertHandlers)
				result.append(handler + "\n");
		}
		return result.toString();
	}
}