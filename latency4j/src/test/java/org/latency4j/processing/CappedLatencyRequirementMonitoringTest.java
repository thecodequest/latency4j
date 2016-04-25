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
 * Test that capped latency requirement triggers expected number of 
 * alerts.
 * 
 * NOTE: This test is unpredictable across VMs and may fail occasionally 
 * due to unexpected slowness/latency.  
 */
public class CappedLatencyRequirementMonitoringTest extends AbstractLatencyRequirementMonitoringTest {
	private static final Logger logger = LoggerFactory.getLogger(StatisticalLatencyRequirementTest.class);

	private static final String TEST_CONFIGURATION_URL = "CLASSPATH:/org/latency4j/processing/capped-req-test-config.xml";
	private static final int NUMBER_OF_ITERATIONS = 10;
	private static final long CONFIGURED_CAP = 400;
	private static final long WAIT_INTERVAL = 100;

	private static final String TEST_CATEGORY_WITH_ERRORS_DISABLED = "cappedRequirementTestTask";

	private AsynchronousLatencyMonitorFactory monitorFactory;
	private int expectedNumberOfAlertHandlerCalls;

	@Before
	public void setUp() {
		monitorFactory = new AsynchronousLatencyMonitorFactory();
		monitorFactory.setConfigurationPath(TEST_CONFIGURATION_URL);
		monitorFactory.init();

		// e.g. ((10 * 100) - 400) = 600. Then divide by 100 to get 6
		// so we expect 6 calls to be above the cap
		expectedNumberOfAlertHandlerCalls = (int) (((NUMBER_OF_ITERATIONS * WAIT_INTERVAL) - CONFIGURED_CAP)
				/ WAIT_INTERVAL);
	}

	/*
	 * Test the correct number of alerts are issued for successful completions
	 */
	@Test
	public void testNumberOfAlertsIssuedForSuccessfullCompletion() throws InterruptedException {
		// get a monitor from the factory
		LatencyMonitor monitor = monitorFactory.getMonitor(TEST_CATEGORY_WITH_ERRORS_DISABLED);

		long waitTime = WAIT_INTERVAL;
		WorkDuration duration;
		for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
			monitor.taskStarted();

			if (i < (CONFIGURED_CAP / WAIT_INTERVAL)) Thread.sleep(1);
			else Thread.sleep(i * waitTime + (i * i));

			duration = monitor.taskCompleted();
			logger.info("Duration " + i + " : " + duration.toStringTimeOnly());
		}

		// arbitrary sleep to allow processing thread to catch up
		// assume that at most daemon thread is half way behind?
		Thread.sleep(WAIT_INTERVAL * (NUMBER_OF_ITERATIONS / 2));

		assertEquals("Incorrect number of notifications issued for requirement.", expectedNumberOfAlertHandlerCalls,
				CountingMockAlertHandler.getLatencyExceededCapCount(TEST_CATEGORY_WITH_ERRORS_DISABLED));

		assertEquals("No statistical notifications should have been issued " + "in relation to this requirement.", 0,
				CountingMockAlertHandler.getDeviationExceededToleranceCount(TEST_CATEGORY_WITH_ERRORS_DISABLED));

		assertEquals("No operations errored. 0 alerts should have been " + "issued for errors.", 0,
				CountingMockAlertHandler.getWorkFailureCount(TEST_CATEGORY_WITH_ERRORS_DISABLED));
	}

	/*
	 * Test that errors are ignored when ignoreErrors is not overriden.
	 */
	@Test
	public void testThatErrorsAreIgnoredIfIgnoreErrorsFalse() throws InterruptedException {
		// get a monitor from the factory
		LatencyMonitor monitor = monitorFactory.getMonitor(TEST_CATEGORY_WITH_ERRORS_DISABLED);

		long waitTime = WAIT_INTERVAL;
		WorkDuration duration;
		for (int i = 0; i < NUMBER_OF_ITERATIONS; i++) {
			monitor.taskStarted();

			if (i <= expectedNumberOfAlertHandlerCalls) Thread.sleep(1);
			else Thread.sleep(i * waitTime + (i * i));

			duration = monitor.taskErrored(null);
			logger.info("Duration " + i + " : " + duration);
		}

		// arbitrary sleep to allow processing thread to catch up
		// assume that at most daemon thread is half way behind?
		Thread.sleep(WAIT_INTERVAL * (NUMBER_OF_ITERATIONS / 2));

		assertEquals("No sucessfull completions for this requirement " + "should have been recorded.", 0,
				CountingMockAlertHandler.getLatencyExceededCapCount(TEST_CATEGORY_WITH_ERRORS_DISABLED));

		assertEquals("No statistical notifications should have been issued " + "in relation to this requirement.", 0,
				CountingMockAlertHandler.getDeviationExceededToleranceCount(TEST_CATEGORY_WITH_ERRORS_DISABLED));

		assertEquals("Requirement set to ignore errors. No error notifications should have been " + "issued.", 0,
				CountingMockAlertHandler.getWorkFailureCount(TEST_CATEGORY_WITH_ERRORS_DISABLED));

	}
}
