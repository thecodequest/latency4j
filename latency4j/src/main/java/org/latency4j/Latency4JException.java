package org.latency4j;

import org.latency4j.util.ExceptionUtil;

/**
 * <p>
 * Root class of all Epsilon exceptions.
 * </p>
 */
public class Latency4JException extends RuntimeException {
	private static final long serialVersionUID = 4958445697761705625L;

	/**
	 * <p>
	 * Default constructor.
	 * </p>
	 */
	public Latency4JException() {}

	/**
	 * <p>
	 * Constructs an instance with the specified description.
	 * </p>
	 * 
	 * @param message
	 *            A description of the error condition.
	 */
	public Latency4JException(final String message) {
		super(message);
	}

	/**
	 * <p>
	 * Wraps the specified exception with an instance of this class. Generally
	 * used to encapsulate an error triggered or detected by an underlying API.
	 * </p>
	 * 
	 * @param cause
	 *            The exception to be wrapped.
	 * @see ExceptionUtil#getStackTrace(Throwable)
	 */
	public Latency4JException(final Throwable cause) {
		super(cause);
	}

	/**
	 * <p>
	 * Wraps an underlying exception as an instance of this type, but also masks
	 * the message with that specified in the constructor arguments.
	 * </p>
	 * <p>
	 * Although constructor {@link #Latency4JException(Throwable)} can be used to
	 * wrap an underlying exception, it does not mask the underlying's message.
	 * So a call to the method {@link #getMessage()} will still return the
	 * message of the underlying exception.
	 * </p>
	 * <p>
	 * This constructor allows for a substitute message (or mask) to be defined,
	 * which is the returned by {@link #getMessage()} instead of that of the
	 * wrapped exception.
	 * </p>
	 * 
	 * @param message
	 *            The message mask, also referred to as the description of the
	 *            exception.
	 * @param cause
	 *            The exception to be wrapped by this instance.
	 */
	public Latency4JException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * <p>
	 * Convenience method for transforming the instance's stack trace to a
	 * {@link String}.
	 * </p>
	 * 
	 * @return A {@link String} representation of the instance's stack trace.
	 */
	public String getStackTraceAsString() {
		return ExceptionUtil.getStackTrace(this);
	}

	/**
	 * <p>
	 * Wraps the specified exception with an {@link Latency4JException} if it is
	 * not already one.
	 * </p>
	 * 
	 * @param exce
	 *            The Exception to wrap.
	 * 
	 * @return If the specified Exception is an {@link Latency4JException}, this
	 *         method will simply return a reference to it. Else, it will return
	 *         a new {@link Latency4JException}
	 *         {@link Latency4JException#EpsilonException(Throwable) initialised}
	 *         with the specified parameter.
	 */
	public static Latency4JException wrapException(final Throwable exce) {
		if (exce instanceof Latency4JException) return (Latency4JException) exce;
		else return new Latency4JException(exce);
	}

}
