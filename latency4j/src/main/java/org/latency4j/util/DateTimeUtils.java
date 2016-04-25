package org.latency4j.util;

import java.util.Calendar;

/**
 * <p>
 * Date & Time utility functions.
 * </p>
 */
public class DateTimeUtils {
	/**
	 * <p>
	 * Private constructor to prevent instantiation of utility class.
	 * </p>
	 */
	private DateTimeUtils() {}

	/**
	 * <p>
	 * Returns the current year from the system {@link Calendar calendar}.
	 * </p>
	 * 
	 * @return The current year as indicated by the system {@link Calendar
	 *         calendar}.
	 */
	public static int getCurrentYear() {
		int returnValue;

		returnValue = Calendar.getInstance().get(Calendar.YEAR);

		return returnValue;
	}
}