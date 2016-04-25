package org.latency4j.alert;

import static org.latency4j.TestObjectFactory.createFixedRequirement;
import static org.latency4j.TestObjectFactory.createStatsBasedRequirement;
import static org.latency4j.TestObjectFactory.createTaskDuration;
import static org.latency4j.alert.StandardHandlerConstants.CAP_EXCEEDED_MSG_PARAM_KEY;
import static org.latency4j.alert.StandardHandlerConstants.EXCEEDED_TOLERANCE_MSG_PARAM_KEY;

import java.util.HashMap;
import java.util.Map;

import org.latency4j.CappedLatencyRequirement;
import org.latency4j.AlertHandler;
import org.latency4j.StatisticalLatencyRequirement;
import org.latency4j.WorkDuration;

/*
 * Base class for testing alert handler implementations.
 */
public abstract class AbstractAlertHandlerTest {
	protected StatisticalLatencyRequirement statsRequirement;
	protected CappedLatencyRequirement fixedRequirement;
	protected WorkDuration duration;
	protected Exception exception;

	protected Map<String, String> getAlertHandlerParams() {
		Map<String, String> result = new HashMap<String, String>();

		String message = "@threadId@: Target for task @taskId@ exceeded. "
				+ "Deviation from mean @mean@ is @deviation@.\n\t" + "Tolerance is: @tolerance@";
		result.put(EXCEEDED_TOLERANCE_MSG_PARAM_KEY, message);

		message = "@threadId@: Target for task @taskId@ exceeded. " + "Expected tolerance @expected.latency@.";
		result.put(CAP_EXCEEDED_MSG_PARAM_KEY, message);

		result.put("logLevel", "INFO");
		result.put("logger.category", "TargetLoggerTest");

		return result;
	}

	public void setUp(final AlertHandler alertHandler) throws Exception {
		statsRequirement = createStatsBasedRequirement(5, 0.1d);
		fixedRequirement = createFixedRequirement(10);
		duration = createTaskDuration();

		statsRequirement.getAlertHandlers().add(alertHandler);
		fixedRequirement.getAlertHandlers().add(alertHandler);

		Map<String, String> parameters = new HashMap<String, String>();

		String message = "@threadId@: Target for task @taskId@ exceeded. "
				+ "Deviation from mean @mean@ is @deviation@.\n\t" + "Tolerance is: @tolerance@";
		parameters.put(EXCEEDED_TOLERANCE_MSG_PARAM_KEY, message);

		message = "@threadId@: Target for task @taskId@ exceeded. " + "Expected tolerance @expected.latency@.";
		parameters.put(CAP_EXCEEDED_MSG_PARAM_KEY, message);

		parameters.put("logLevel", "INFO");
		parameters.put("logger.category", "TargetLoggerTest");

		alertHandler.setAlertHandlerId("Alert-Logger");
		alertHandler.init();

		exception = new Exception("Test exception message!");

	}
}
