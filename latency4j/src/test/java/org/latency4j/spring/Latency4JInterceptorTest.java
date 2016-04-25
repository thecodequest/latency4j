package org.latency4j.spring;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.latency4j.TestObjectFactory;
import org.latency4j.alert.CountingMockAlertHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/*
 * Tests AOP based performance monitor intercepter. 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/org/latency4j/spring/aop/aop-test-beans.xml" })
public class Latency4JInterceptorTest {
	private static final int TEST_INVOCATION_COUNT = 10;

	// an arbitrary sleep interval which allows the handler daemon
	// thread to catch up. assume thread is at most
	// half-way behind ?
	private static final long ARBTRARY_SLEEP_INTERVAL = MockService.WAIT_INTERVAL * (TEST_INVOCATION_COUNT / 2);

	private static final int EXPECTED_NUMBER_OF_HANDLER_CALLS = TEST_INVOCATION_COUNT
			- MockService.INVOCATION_SLEEP_THRESHOLD;

	private static final String CONFIGURED_CAPPED_CATEGORY = "InterceptedServiceCappedCategory";

	private static final String CONFIGURED_STATS_CATEGORY = "InterceptedServiceStatsCategory";

	@Autowired
	private MockService testService;

	@Before
	@After
	public void resetMockHandlerCounters() {
		TestObjectFactory.deleteAllEpsilonFilesFromTemp();
		CountingMockAlertHandler.resetAfterTest();
	}

	/*
	 * Tests intercept of invocations governed by capped requirement.
	 */
	@Test
	public void testMonitoringOfServiceWithCappedRequirement() throws InterruptedException {
		for (int i = 0; i < TEST_INVOCATION_COUNT; i++)
			testService.cappedTestService(i);

		// allow daemon thread to catch up
		waitForProcessingToComplete();
		CountingMockAlertHandler.assertCounts(CONFIGURED_CAPPED_CATEGORY, EXPECTED_NUMBER_OF_HANDLER_CALLS, 0, 0);
	}

	/*
	 * Tests intercept of invocations governed by stats requirement.
	 */
	@Test
	public void testMonitoringOfServiceWithStatsRequirement() throws InterruptedException {
		for (int i = 0; i < TEST_INVOCATION_COUNT; i++)
			testService.statsTestService(i);

		// allow daemon thread to catch up
		waitForProcessingToComplete();
		CountingMockAlertHandler.assertCounts(CONFIGURED_STATS_CATEGORY, 0, 0, EXPECTED_NUMBER_OF_HANDLER_CALLS);
	}

	/*
	 * Tests exception handling behaviour.
	 */
	@Test
	public void testExceptionServiceInvocation() throws InterruptedException {
		for (int i = 0; i < TEST_INVOCATION_COUNT; i++) {
			try {
				testService.exceptionService();
				fail("Interceptor should have propagated failure from " + "monitored service invocation ...");
			} catch (RuntimeException exce) {}
		}

		// allow daemon thread to catch up
		waitForProcessingToComplete();
		CountingMockAlertHandler.assertCounts(CONFIGURED_CAPPED_CATEGORY, 0, TEST_INVOCATION_COUNT, 0);
	}

	/*
	 * Make sure processing queue is empty before continuing. This ensures that
	 * the mock handler registers all notifications.
	 */
	private void waitForProcessingToComplete() throws InterruptedException {
		Thread.sleep(ARBTRARY_SLEEP_INTERVAL);
	}
}