package org.latency4j.alert;

import static org.latency4j.TestObjectFactory.createErrorDuration;
import static org.latency4j.TestObjectFactory.createFixedRequirement;
import static org.latency4j.TestObjectFactory.createStatsBasedRequirement;
import static org.latency4j.TestObjectFactory.createTaskDuration;

import org.junit.Before;
import org.junit.Test;
import org.latency4j.CappedLatencyRequirement;
import org.latency4j.StatisticalLatencyRequirement;
import org.latency4j.WorkDuration;
import org.latency4j.alert.AlertFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Test for utility class AlertFormatter. Test results have to be visually 
 * inspected to ensure formatting accuracy.
 */
public class AlertFormatterTest {
	private static final Logger logger = LoggerFactory.getLogger(AlertFormatterTest.class);

	/*
	 * Test data
	 */
	private StatisticalLatencyRequirement statsRequirement;
	private CappedLatencyRequirement fixedRequirement;
	private WorkDuration duration;
	private WorkDuration errorDuration;

	/*
	 * Create test data
	 */
	@Before
	public void setUp() throws Exception {
		statsRequirement = createStatsBasedRequirement(5, 0.1d);
		fixedRequirement = createFixedRequirement(10);
		duration = createTaskDuration();
		errorDuration = createErrorDuration();
	}

	/*
	 * 
	 */
	@Test
	public void testFormatLatencyExceededToleranceMessageWithTaskId() {
		String message = "Target for task @taskId@ exceeded average latency.";
		String formattedMessage = AlertFormatter.formatLatencyExceededToleranceMessage(statsRequirement, duration, 2.0d,
				0.5d, message);
		logger.info(formattedMessage);
	}

	@Test
	public void testFormatLatencyExceededToleranceMessageWithThreadId() {
		String message = "@threadId@: Target for task @taskId@ exceeded.";
		String formattedMessage = AlertFormatter.formatLatencyExceededToleranceMessage(statsRequirement, duration, 2.0d,
				0.5d, message);
		logger.info(formattedMessage);
	}

	@Test
	public void testFormatLatencyExceededToleranceMessageWithDeviation() {
		String message = "@threadId@: Target for task @taskId@ exceeded. Deviation is @deviation@.";
		String formattedMessage = AlertFormatter.formatLatencyExceededToleranceMessage(statsRequirement, duration, 2.0d,
				0.5d, message);
		logger.info(formattedMessage);
	}

	@Test
	public void testFormatLatencyExceededToleranceMessageWithMean() {
		String message = "@threadId@: Target for task @taskId@ exceeded. Deviation from mean @mean@ is @deviation@.";
		String formattedMessage = AlertFormatter.formatLatencyExceededToleranceMessage(statsRequirement, duration, 2.0d,
				0.5d, message);
		logger.info(formattedMessage);
	}

	@Test
	public void testFormatLatencyExceededToleranceMessageWithTolerance() {
		String message = "@threadId@: Target for task @taskId@ exceeded. "
				+ "Deviation from mean @mean@ is @deviation@.\n\t" + "Tolerance is: @tolerance@";
		String formattedMessage = AlertFormatter.formatLatencyExceededToleranceMessage(statsRequirement, duration, 2.0d,
				0.5d, message);
		logger.info(formattedMessage);
	}

	@Test
	public void testFormatTargetMissedMessage() {
		String message = "@threadId@: Target for task @taskId@ exceeded. " + "Expected tolerance @expected.latency@.";
		String formattedMessage = AlertFormatter.formatTargetMissedMessage(fixedRequirement, duration, message);
		logger.info(formattedMessage);
	}

	@Test
	public void testWorkCategoryFailedMessage() {
		String message = "@threadId@: Target for task @work.category@ failed. " + "Exception @exception.message@.";
		String formattedMessage = AlertFormatter.formatWorkCategoryFailureMessage(fixedRequirement, errorDuration,
				message);
		logger.info(formattedMessage);
	}

	/*
	 * 
	 */
	@Test
	public void testWorkCategoryFailedMessageWithStackTrace() {
		String message = "@threadId@: Target for task @work.category@ failed. "
				+ "Exception @exception.message@. \n Stack Trace: @exception.stacktrace@";
		String formattedMessage = AlertFormatter.formatWorkCategoryFailureMessage(fixedRequirement, errorDuration,
				message);
		logger.info(formattedMessage);
	}
}
