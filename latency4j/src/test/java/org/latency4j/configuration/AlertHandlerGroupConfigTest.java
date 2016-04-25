package org.latency4j.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.latency4j.configuration.AlertHandlerConfiguration;
import org.latency4j.configuration.AlertHandlerGroupConfig;

/*
 * Test unit for alert handler group config bean
 */
public class AlertHandlerGroupConfigTest {
	private List<AlertHandlerConfiguration> handlerConfigSets;
	private AlertHandlerGroupConfig target;

	@Before
	public void setUp() {
		handlerConfigSets = new ArrayList<AlertHandlerConfiguration>();
		AlertHandlerConfiguration config;
		for (int i = 0; i < 20; i++) {
			config = AlertHandlerGroupConfigTest.createMockAlertHandlerConfig(i);
			handlerConfigSets.add(config);
		}

		target = new AlertHandlerGroupConfig();
	}

	/*
	 * Ensure that handler collection is not null by default Also ensure that
	 * mutators and accessor work
	 */
	@Test
	public void testGetAlertHandlers() {
		assertNotNull(target.getAlertHandlers());
		target.setAlertHandlers(handlerConfigSets);
		assertEquals(handlerConfigSets, target.getAlertHandlers());
	}

	/*
	 * test for toString method. simply check that the output is not null.
	 */
	@Test
	public void testToString() {
		String toStringValue = target.toString();
		assertNotNull(toStringValue);
	}

	// shared method for creating test/template data.
	public static AlertHandlerConfiguration createMockAlertHandlerConfig(final int handlerIndex) {
		AlertHandlerConfiguration result = new AlertHandlerConfiguration();
		result.setClassName("Test Class");
		result.setAlertHandlerId("Test Alert Handler Id " + handlerIndex);

		Map<String, String> testHandlerParameters = new HashMap<String, String>();
		testHandlerParameters.put("paramKey1", "paramValue1");
		testHandlerParameters.put("paramKey2", "paramValue2");

		result.setParameters(testHandlerParameters);
		return result;
	}

}
