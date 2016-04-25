package org.latency4j.processing;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.latency4j.LatencyMonitor;
import org.latency4j.StatisticalLatencyRequirementTest;
import org.latency4j.WorkDuration;
import org.latency4j.alert.CountingMockAlertHandler;
import org.latency4j.processing.AsynchronousLatencyMonitorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Test case to validate the monitoring of statistical latency requirements.
 * Validates that alerts are issued for all latencies above the given 
 * average.
 * 
 * NOTE: This test is unpredictable across VMs and may fail occasionally 
 * due to unexpected slowness/latency.
 */
public class StatisticalRequirementMonitoringTest extends AbstractLatencyRequirementMonitoringTest {
	private static final Logger logger = LoggerFactory.getLogger(StatisticalLatencyRequirementTest.class);

	private static final String TEST_CONFIGURATION_URL = "CLASSPATH:/org/latency4j/processing/stats-req-test-config.xml";
	private static final int CONFIGURED_OBSERVATIONS_BARRIER = 5;
	private static final int NUMBER_OF_ITERATIONS = 10;
	public static final long WAIT_INTERVAL = 100;
	private static final String TEST_CATEGORY_FOR_IGNORE_ERRORS = "statsRequirementTestTask";
	private static final String TEST_CATEGORY_WITH_IGNORE_ERRORS_DISABLED = "statsRequirementTestTaskWithIgnoreErrorsDisabled";

	private AsynchronousLatencyMonitorFactory monitorFactory;
	private int expectedNumberOfAlertHandlerCalls;

	@Before
	public void setUp() {
		monitorFactory = new AsynchronousLatencyMonitorFactory();
		monitorFactory.setConfigurationPath(TEST_CONFIGURATION_URL);
		monitorFactory.init();

		expectedNumberOfAlertHandlerCalls = NUMBER_OF_ITERATIONS - CONFIGURED_OBSERVATIONS_BARRIER;
	}

	/*
	 * Test that the number of alerts issued. If we seed the requirement stats
	 * with 5 observations. Then the average after iterations will be about 200
	 * milliseconds. So by the 5th observation, the latency is 500ms. Which
	 * should be in excess of 200ms (and not within 10% boundary). So every
	 * observation after the 5th iteration should issue an alert.
	 */
	@Test
	public void testNumberOfAlertsForSuccessfullCompletion() throws InterruptedException {
		// get a monitor from the factory
		LatencyMonitor monitor = monitorFactory.getMonitor(TEST_CATEGORY_FOR_IGNORE_ERRORS);

		WorkDuration duration;
		for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
			monitor.taskStarted();

			if (i >= CONFIGURED_OBSERVATIONS_BARRIER) Thread.sleep(i * WAIT_INTERVAL);
			else Thread.sleep(1);

			duration = monitor.taskCompleted();
			logger.info("Duration " + i + " : " + duration);
		}

		// arbitrary sleep to allow processing thread to catch up
		// assume that at most daemon thread is half way behind?
		Thread.sleep(WAIT_INTERVAL * (NUMBER_OF_ITERATIONS / 2));

		assertEquals("Incorrect number of notifications issued for requirement.", expectedNumberOfAlertHandlerCalls,
				CountingMockAlertHandler.getDeviationExceededToleranceCount(TEST_CATEGORY_FOR_IGNORE_ERRORS));

		assertEquals("No capped requirement specified. 0 alerts should have been " + "issued for capped requirements.",
				0, CountingMockAlertHandler.getLatencyExceededCapCount(TEST_CATEGORY_FOR_IGNORE_ERRORS));

		assertEquals("No operations errored. 0 alerts should have been " + "issued for capped requirements.", 0,
				CountingMockAlertHandler.getWorkFailureCount(TEST_CATEGORY_FOR_IGNORE_ERRORS));
	}

	/*
	 * Ignore errors defaults to true. For this default scenario, all error
	 * terminations should be ignored.
	 */
	@Test
	public void testNumberOfAlertsForFailuresWhenIgnoreErrorsIsTrue() throws InterruptedException {
		// get a monitor from the factory
		LatencyMonitor monitor = monitorFactory.getMonitor(TEST_CATEGORY_FOR_IGNORE_ERRORS);

		WorkDuration duration;
		for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
			monitor.taskStarted();

			if (i >= CONFIGURED_OBSERVATIONS_BARRIER) Thread.sleep(i * WAIT_INTERVAL);
			else Thread.sleep(1);

			duration = monitor.taskErrored(null);
			logger.info("Duration " + i + " : " + duration);
		}

		// arbitrary sleep to allow processing thread to catch up
		// assume that at most daemon thread is half way behind?
		Thread.sleep(WAIT_INTERVAL * (NUMBER_OF_ITERATIONS / 2));

		assertEquals("Incorrect number of notifications issued for requirement.", 0,
				CountingMockAlertHandler.getDeviationExceededToleranceCount(TEST_CATEGORY_FOR_IGNORE_ERRORS));

		assertEquals("No capped requirement specified. 0 alerts should have been " + "issued for capped requirements.",
				0, CountingMockAlertHandler.getLatencyExceededCapCount(TEST_CATEGORY_FOR_IGNORE_ERRORS));

		assertEquals("Ignore errors is enabled, so no error notifications should be issued!", 0,
				CountingMockAlertHandler.getWorkFailureCount(TEST_CATEGORY_FOR_IGNORE_ERRORS));
	}

	/*
	 * If ignore errors is set to false, then all errors should result in
	 * notifications -- even if the significance barrier has not been reached.
	 */
	@Test
	public void testNumberOfAlertsForFailuresWhenIgnoreErrorsIsFalse() throws InterruptedException {
		// get a monitor from the factory
		LatencyMonitor monitor = monitorFactory.getMonitor(TEST_CATEGORY_WITH_IGNORE_ERRORS_DISABLED);

		WorkDuration duration;
		for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
			monitor.taskStarted();

			if (i >= CONFIGURED_OBSERVATIONS_BARRIER) Thread.sleep(i * WAIT_INTERVAL);
			else Thread.sleep(1);

			duration = monitor.taskErrored(null);
			logger.info("Duration " + i + " : " + duration);
		}

		// arbitrary sleep to allow processing thread to catch up
		// assume that at most daemon thread is half way behind?
		Thread.sleep(WAIT_INTERVAL * (NUMBER_OF_ITERATIONS / 2));

		assertEquals("No successes should have been registered for this reqirement.", 0,
				CountingMockAlertHandler.getDeviationExceededToleranceCount(TEST_CATEGORY_WITH_IGNORE_ERRORS_DISABLED));

		assertEquals("No capped requirement specified. 0 alerts should have been " + "issued for capped requirements.",
				0, CountingMockAlertHandler.getLatencyExceededCapCount(TEST_CATEGORY_WITH_IGNORE_ERRORS_DISABLED));

		assertEquals("Ignore errors is disabled. We should see notifications for this requirement.",
				NUMBER_OF_ITERATIONS,
				CountingMockAlertHandler.getWorkFailureCount(TEST_CATEGORY_WITH_IGNORE_ERRORS_DISABLED));
	}

	/*
	 * If ignore errors is set to false, then errors should also be used when
	 * calculating average deviation. Also all errors should still be reported,
	 * even if significance barrier not reached.
	 */
	@Test
	public void testNumberOfAlertsForSuccessWhenIgnoreErrorsIsFalse() throws InterruptedException {
		// get a monitor from the factory
		LatencyMonitor monitor = monitorFactory.getMonitor(TEST_CATEGORY_WITH_IGNORE_ERRORS_DISABLED);

		WorkDuration duration;
		for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
			monitor.taskStarted();

			if (i >= CONFIGURED_OBSERVATIONS_BARRIER) {
				Thread.sleep(i);
				duration = monitor.taskCompleted();
			} else {
				Thread.sleep(1);
				duration = monitor.taskErrored(null);
			}

			logger.info("Duration " + i + " : " + duration);
		}

		// execute a second batch of successful operations
		// the first set of successes should have been used
		// just as seed data
		for (int i = 1; i <= expectedNumberOfAlertHandlerCalls; i++) {
			monitor.taskStarted();
			Thread.sleep(i * WAIT_INTERVAL);
			duration = monitor.taskCompleted();
			logger.info("Duration after errors: " + i + " : " + duration);
		}

		// arbitrary sleep to allow processing thread to catch up
		// assume that at most daemon thread is half way behind?
		Thread.sleep(WAIT_INTERVAL * (NUMBER_OF_ITERATIONS / 2));

		assertEquals("Errors should not be used when calculating average.", expectedNumberOfAlertHandlerCalls,
				CountingMockAlertHandler.getDeviationExceededToleranceCount(TEST_CATEGORY_WITH_IGNORE_ERRORS_DISABLED));

		assertEquals("No capped requirement specified. 0 alerts should have been " + "issued for capped requirements.",
				0, CountingMockAlertHandler.getLatencyExceededCapCount(TEST_CATEGORY_WITH_IGNORE_ERRORS_DISABLED));

		assertEquals("Ignore errors is enabled. We should see notifications for this requirement.",
				CONFIGURED_OBSERVATIONS_BARRIER,
				CountingMockAlertHandler.getWorkFailureCount(TEST_CATEGORY_WITH_IGNORE_ERRORS_DISABLED));
	}

}// end class def