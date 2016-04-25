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
 * Tests annotations based performance monitor intercepter. 
 */
/*
 * Tests AOP based performance monitor intercepter. 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:org/latency4j/spring/annotations/annotations-test-beans.xml" })
public class Latency4JProxyTest {

	private static final int TEST_INVOCATION_COUNT = 10;

	// an arbitrary sleep interval which allows the handler daemon
	// thread to catch up
	private static final long ARBITRARY_SLEEP_INTERVAL = 500;

	private static final int EXPECTED_NUMBER_OF_HANDLER_CALLS = 5;

	private static final String CONFIGURED_CAPPED_CATEGORY = "CappedServiceRequirement";

	private static final String CONFIGURED_STATS_CATEGORY = "StatsServiceRequirement";

	@Autowired
	private AnnotatedMockService annotatedMockService;

	@Before
	@After
	public void resetMockHandlerCounters() {
		TestObjectFactory.deleteAllEpsilonFilesFromTemp();
		CountingMockAlertHandler.resetAfterTest();
	}

	/*
	 * Tests intercept of invocations targeting the monitored operation.
	 */
	@Test
	public void testMonitoredServiceWithCategoryInvocation() throws InterruptedException {
		for (int i = 0; i < TEST_INVOCATION_COUNT; i++)
			this.annotatedMockService.methodWithCappedRequirement(i);

		// allow daemon thread to catch up
		waitForProcessingToComplete();

		waitForProcessingToComplete();
		CountingMockAlertHandler.assertCounts(CONFIGURED_CAPPED_CATEGORY, EXPECTED_NUMBER_OF_HANDLER_CALLS, 0, 0);
	}

	@Test
	public void testMonitoredServiceWithStatsRequirement() throws InterruptedException {
		int invocationCount = AnnotatedMockService.CONFIGURED_STATS_THRESHOLD * 2;

		for (int i = 0; i < invocationCount; i++)
			this.annotatedMockService.methodWithStatisticalRequirement(i);

		// allow daemon thread to catch up
		waitForProcessingToComplete();
		CountingMockAlertHandler.assertCounts(CONFIGURED_STATS_CATEGORY, 0, 0,
				AnnotatedMockService.CONFIGURED_STATS_THRESHOLD);
	}

	@Test
	public void testExceptionService() throws InterruptedException {
		for (int i = 0; i < TEST_INVOCATION_COUNT; i++) {
			try {
				this.annotatedMockService.exceptionMethod();
				fail("Underlying exception should be propagated by EpsilonProxy.");
			} catch (RuntimeException exce) { /* ignore */ }
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
		Thread.sleep(ARBITRARY_SLEEP_INTERVAL);
	}
}