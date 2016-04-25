package org.latency4j.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * <p>
 * A utility class for manipulating exceptions.
 * </p>
 */
public final class ExceptionUtil {
	/**
	 * <p>
	 * Default private constructor to prevent accidental initialisation.
	 * </p>
	 */
	private ExceptionUtil() {}

	/**
	 * <p>
	 * Returns the stack trace of the given exception as a string.
	 * </p>
	 * 
	 * @param t
	 *            The exception whose stack trace is to be unwrapped and
	 *            returned as a string.
	 * @return A string representation of the exception's stack trace
	 */
	public static String getStackTrace(final Throwable t) {
		StringBuffer result = new StringBuffer();

		try {
			StringWriter writeBuffer = new StringWriter();
			PrintWriter printBuffer = new PrintWriter(writeBuffer);

			// write the stack buffer into the StringWriter
			t.printStackTrace(printBuffer);

			printBuffer.flush();
			printBuffer.close();

			result.append(writeBuffer.toString());
		} catch (Throwable t2) {
			result.append("Unexpected error getting stack trace: " + t2.getMessage());
		}
		return result.toString();
	}
}// end class def