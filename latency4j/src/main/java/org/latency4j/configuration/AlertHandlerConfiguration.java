package org.latency4j.configuration;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.latency4j.AlertHandler;

/**
 * <p>
 * A bean which holds configuration information for an
 * {@link AlertHandler alert handler}. Instances of this class are used
 * by the {@link ConfigurationReader} as templates for creating
 * {@link AlertHandler alert handler} instances.
 * </p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class AlertHandlerConfiguration implements Serializable {

	private static final long serialVersionUID = 3962303711344639874L;

	/**
	 * The {@link AlertHandler alert handler} type. Thus, by implication,
	 * the value of this field must be the name of a class that implements the
	 * {@link AlertHandler} interface.
	 */
	@XmlAttribute(required = true)
	private String className;

	/**
	 * <p>
	 * The {@link AlertHandler#getAlertHandlerId() handler id}.
	 * </p>
	 */
	@XmlAttribute(required = true)
	private String alertHandlerId;

	/**
	 * <p>
	 * The {@link AlertHandler#setParameters(Map) parameters} with which
	 * the handler should be initialised.
	 * </p>
	 */
	private Map<String, String> parameters;

	/**
	 * <p>
	 * Default constructor.
	 * </p>
	 */
	public AlertHandlerConfiguration() {}

	/**
	 * <p>
	 * Returns the value that would be obtained by invoking
	 * {@link AlertHandler#getAlertHandlerId()} on an
	 * {@link AlertHandler alert handler} created from this template.
	 * </p>
	 * 
	 * @return The identifier for the handler being configured.
	 * @see #getAlertHandlerId()
	 */
	public String getAlertHandlerId() {
		return this.alertHandlerId;
	}

	/**
	 * <p>
	 * Sets the identifier for any {@link AlertHandler alert handler}
	 * created from this template.
	 * </p>
	 * 
	 * @param id
	 *            The {@link AlertHandler#setAlertHandlerId(String)
	 *            identifier} to assign to the configured handler.
	 * 
	 * @see #getAlertHandlerId()
	 */
	public void setAlertHandlerId(final String id) {
		this.alertHandlerId = id;
	}

	/**
	 * Returns the {@link AlertHandler alert handler} class name. Put
	 * differently, this method is called to obtain the name of the class which
	 * implements the {@link AlertHandler} interface. It is an instance
	 * of the returned class that is created by the
	 * {@link ConfigurationReader}, and initialised with the values from
	 * the bean against which this method is invoked.
	 * 
	 * @return The type of the {@link AlertHandler alert handler} that
	 *         will be created from this template.
	 * 
	 *         {@link #setClassName(String)}
	 */
	public String getClassName() {
		return this.className;
	}

	/**
	 * Sets the type/implementation of {@link AlertHandler alert handler}
	 * to create.
	 * 
	 * @param className
	 *            The type of the {@link AlertHandler handler}.
	 * 
	 * @see #getClassName()
	 */
	public void setClassName(final String className) {
		this.className = className;
	}

	/**
	 * Accessor to the configured {@link AlertHandler#setParameters(Map)
	 * handler parameters}. These are the values with which the method
	 * {@link AlertHandler#setParameters(Map)} is invoked prior to the
	 * {@link AlertHandler#init() initialisation} of the handler.
	 * 
	 * @return The handler configuration parameters.
	 * @see #setParameters(Map)
	 */
	public Map<String, String> getParameters() {
		return this.parameters;
	}

	/**
	 * Used to specify {@link AlertHandler#setParameters(Map) handler
	 * parameters}. Put differently, it sets the parameters with which the
	 * configured {@link AlertHandler handler} will be initialised.
	 * 
	 * @param handlerParameters
	 *            The parameters for the configured {@link AlertHandler
	 *            handler}.
	 * 
	 * @see #getParameters()
	 */
	public void setParameters(final Map<String, String> handlerParameters) {
		this.parameters = handlerParameters;
	}

	/**
	 * Returns a String representation of this configuration.
	 * 
	 * @return A string representation of the listener configuration.
	 */
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("AlertHandler '" + getAlertHandlerId());
		result.append("'\n\t. Classname='" + getClassName());

		if (this.parameters != null) {
			result.append("'\n\t. Properties:'");
			Set<String> keys = this.parameters.keySet();
			for (Object key : keys)
				result.append("\n\t\t" + key.toString() + "=" + this.parameters.get(key));
		}
		return result.toString();
	}

}// end class def