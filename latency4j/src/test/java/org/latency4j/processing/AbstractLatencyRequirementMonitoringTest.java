package org.latency4j.processing;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.latency4j.alert.CountingMockAlertHandler;

@Ignore
public class AbstractLatencyRequirementMonitoringTest {
	@After
	@Before
	public void cleanUp() {
		CountingMockAlertHandler.resetAfterTest();
	}
}