package org.latency4j.alert;

import org.latency4j.AlertHandler;

/**
 * <p>
 * Enumeration of the log-levels supported by the
 * {@link MissedTargetAlertLogger} {@link AlertHandler alert handler}. It
 * serves as a shorthand for those supported by the
 * <a href="http://www.slf4j.org/">SLF4J</a> logging API.
 * </p>
 */
public enum MissedTargetLogLevel {
	/**
	 * <q>DEBUG</q>.
	 */
	DEBUG("DEBUG"),

	/**
	 * <q>INFO</q>.
	 */
	INFO("INFO"),

	/**
	 * <q>WARN</q>.
	 */
	WARN("WARN"),

	/**
	 * <q>ERROR</q>.
	 */
	ERROR("ERROR"),

	/**
	 * <q>TRACE</q>.
	 */
	TRACE("TRACE");

	/**
	 * <p>
	 * The text equivalent of an enum value.
	 * </p>
	 */
	private String description;

	/**
	 * <p>
	 * Private constructor which builds an enum value from a string value.
	 * </p>
	 * 
	 * @param description
	 *            The text value of the enum.
	 */
	private MissedTargetLogLevel(final String description) {
		this.description = description;
	}

	/**
	 * <p>
	 * Resolves the value of an enum from a string representation of the enum.
	 * <b>Note</b> that the argument is not case sensitive. The following table
	 * provides details of the resolution scheme.
	 * </p>
	 * 
	 * <tbody>
	 * <th>
	 * <td>Text value</td>
	 * <td>Enum value</td></th>
	 * <tr>
	 * <td>
	 * <q>DEBUG</q></td>
	 * <td>DEBUG</td>
	 * </tr>
	 * <tr>
	 * <td>
	 * <q>WARN</q></td>
	 * <td>WARN</td>
	 * </tr>
	 * <tr>
	 * <td>
	 * <q>ERROR</q></td>
	 * <td>ERROR</td>
	 * </tr>
	 * <tr>
	 * <td>
	 * <q>TRACE</q></td>
	 * <td>TRACE</td>
	 * </tr>
	 * <tr>
	 * <td>
	 * <q>INFO</q></td>
	 * <td>INFO</td>
	 * </tr>
	 * </tbody>
	 * 
	 * @param description
	 *            The text value from which the enum is to be built.
	 * @return An enum equivalent of the argument <code>description</code>.
	 */
	static MissedTargetLogLevel resolve(final String description) {
		MissedTargetLogLevel result;
		if (description.equalsIgnoreCase("DEBUG")) result = DEBUG;
		else if (description.equalsIgnoreCase("WARN")) result = WARN;
		else if (description.equalsIgnoreCase("ERROR")) result = ERROR;
		else if (description.equalsIgnoreCase("TRACE")) result = TRACE;
		else if (description.equalsIgnoreCase("INFO")) result = INFO;
		else result = null;

		return result;
	}

	@Override
	public String toString() {
		return description;
	}
}
