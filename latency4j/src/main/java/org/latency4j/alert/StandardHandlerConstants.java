package org.latency4j.alert;

import org.latency4j.AlertHandler;

/**
 * <p>
 * Interface which holds common constants shared by the
 * {@link AlertHandler alert handlers} shipped with the epsilon library.
 * Please see below for the value of the constants.
 * </p>
 */
public interface StandardHandlerConstants {
	// parameter names
	/**
	 * {@value #EXCEEDED_TOLERANCE_MSG_PARAM_KEY}.
	 */
	String EXCEEDED_TOLERANCE_MSG_PARAM_KEY = "tolerance.breach.msg";

	/**
	 * {@value #CAP_EXCEEDED_MSG_PARAM_KEY}.
	 */
	String CAP_EXCEEDED_MSG_PARAM_KEY = "cap.breach.msg";

	/**
	 * {@value #WORK_CATEGORY_FAILED_MSG_PARAM_KEY}.
	 */
	String WORK_CATEGORY_FAILED_MSG_PARAM_KEY = "work.failure.msg";

	// token
	/**
	 * {@value #WORK_CATEGORY_TOKEN}.
	 */
	String WORK_CATEGORY_TOKEN = "@work.category@";

	/**
	 * {@value #THREAD_ID_TOKEN}.
	 */
	String THREAD_ID_TOKEN = "@threadId@";

	/**
	 * {@value #DEVIATION_TOKEN}.
	 */
	String DEVIATION_TOKEN = "@deviation@";

	/**
	 * {@value #MEAN_TOKEN}.
	 */
	String MEAN_TOKEN = "@mean@";

	/**
	 * {@value #TOLERANCE_LEVEL_TOKEN}.
	 */
	String TOLERANCE_LEVEL_TOKEN = "@tolerance@";

	/**
	 * {@value #EXPECTED_LATENCY_TOKEN}.
	 */
	String EXPECTED_LATENCY_TOKEN = "@expected.latency@";

	/**
	 * {@value #DURATION_TOKEN}.
	 */
	String DURATION_TOKEN = "@duration@";

	/**
	 * {@value #EXCEPTION_MESSAGE_TOKEN}.
	 */
	String EXCEPTION_MESSAGE_TOKEN = "@exception.message@";

	/**
	 * {@value #EXCEPTION_STACKTRACE_TOKEN}.
	 */
	String EXCEPTION_STACKTRACE_TOKEN = "@exception.stacktrace@";

	// standard messages
	/**
	 * <p>
	 * Default value for parameter {@link #CAP_EXCEEDED_MSG_PARAM_KEY}.
	 * </p>
	 * <p>
	 * The text value of this constant is as follows.
	 * </p>
	 * <p>
	 * {@value #DEFAULT_TOLERANCE_EXCEEDED_MESSAGE}
	 * </p>
	 */
	String DEFAULT_TOLERANCE_EXCEEDED_MESSAGE = THREAD_ID_TOKEN + ": WorkCategory  '" + WORK_CATEGORY_TOKEN
			+ "' exceeded allowed tolerance " + TOLERANCE_LEVEL_TOKEN + "%. \n\t\tMean " + MEAN_TOKEN
			+ ", task deviation " + DEVIATION_TOKEN + ", actual duration " + DURATION_TOKEN + ".";

	/**
	 * <p>
	 * Default value for parameter {@link #EXCEEDED_TOLERANCE_MSG_PARAM_KEY}
	 * </p>
	 * <p>
	 * The text value of this constant is as follows.
	 * </p>
	 * <p>
	 * {@value #DEFAULT_MESSAGE_FOR_MISSED_TARGET}
	 * </p>
	 */
	String DEFAULT_MESSAGE_FOR_MISSED_TARGET = THREAD_ID_TOKEN + ": WorkCategory  '" + WORK_CATEGORY_TOKEN
			+ "' exceeded specified latency " + EXPECTED_LATENCY_TOKEN + ", actual duration " + DURATION_TOKEN + ".";

	/**
	 * <p>
	 * Default value for parameter {@link #WORK_CATEGORY_FAILED_MSG_PARAM_KEY}
	 * </p>
	 * <p>
	 * The text value of this constant is as follows.
	 * </p>
	 * <p>
	 * {@value #DEFAULT_MESSAGE_FOR_WORK_FAILURE}
	 * </p>
	 */
	String DEFAULT_MESSAGE_FOR_WORK_FAILURE = THREAD_ID_TOKEN + ": WorkCategory  '" + WORK_CATEGORY_TOKEN
			+ "' failed with error: " + EXCEPTION_MESSAGE_TOKEN + ".\nStack Trace:\n" + EXCEPTION_STACKTRACE_TOKEN;
}