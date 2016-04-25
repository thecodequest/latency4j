package org.latency4j.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.latency4j.configuration.AlertHandlerConfiguration;

/*
 * Test for alert handler configuration bean.
 */
public class AlertHandlerConfigurationTest {
	// test target
	private AlertHandlerConfiguration alertHandlerConfiguration;

	// test data. i.e. field values.
	private String testClassName;
	private String testAlertHandlerId;
	private Map<String, String> testHandlerParameters;

	@Before
	public void setUp() {
		testClassName = "Test Class";
		testAlertHandlerId = "Alert Handler Id";

		testHandlerParameters = new HashMap<String, String>();
		testHandlerParameters.put("paramKey1", "paramValue1");
		testHandlerParameters.put("paramKey2", "paramValue2");

		alertHandlerConfiguration = new AlertHandlerConfiguration();
	}

	// accessor and mutator tests
	@Test
	public void testMutatorsAndAccessors() {
		alertHandlerConfiguration.setAlertHandlerId(testAlertHandlerId);
		assertEquals(testAlertHandlerId, alertHandlerConfiguration.getAlertHandlerId());

		alertHandlerConfiguration.setClassName(testClassName);
		assertEquals(testClassName, alertHandlerConfiguration.getClassName());

		alertHandlerConfiguration.setParameters(testHandlerParameters);
		assertEquals(testHandlerParameters, alertHandlerConfiguration.getParameters());

	}

	// test of tostring method.
	@Test
	public void testToString() {
		String stringValue = alertHandlerConfiguration.toString();
		assertNotNull(stringValue);
	}

}
