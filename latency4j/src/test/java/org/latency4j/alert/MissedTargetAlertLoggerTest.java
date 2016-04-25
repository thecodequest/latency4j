package org.latency4j.alert;

import org.junit.Before;
import org.junit.Test;
import org.latency4j.alert.MissedTargetAlertLogger;

/*
 * Test unit for class  MissedTargetAlertLogger
 */
public class MissedTargetAlertLoggerTest extends AbstractAlertHandlerTest {
	// test target
	private MissedTargetAlertLogger targetLogger;

	/*
	 * initialises test target
	 */
	@Before
	public void setUp() throws Exception {
		targetLogger = new MissedTargetAlertLogger();
		targetLogger.setAlertHandlerId("Alert-Logger");
		targetLogger.setParameters(getAlertHandlerParams());
		targetLogger.init();
		super.setUp(targetLogger);
	}

	/*
	 * Message print tests
	 */
	@Test
	public void testLatencyExceededTolerance() {
		targetLogger.latencyDeviationExceededTolerance(statsRequirement, duration, 2.0d, 0.5d);
	}

	@Test
	public void testTargetMissed() {
		targetLogger.latencyExceededCap(fixedRequirement, duration);
	}

	@Test
	public void testWorkCategoryFailed() {
		duration.setError(exception);
		targetLogger.workCategoryFailed(fixedRequirement, duration);
	}
}
