package org.latency4j.configuration;

import static org.junit.Assert.*;
import static org.latency4j.testutil.BeanTestUtil.testInitialPropertyValue;
import static org.latency4j.testutil.BeanTestUtil.testMutatorsAndAccessors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.latency4j.configuration.LatencyRequirementConfiguration;

/*
 * Unit test for latency requirement configuration bean. 
 */
public class LatencyRequirementConfigurationTest {

	// test/field data
	private String workCategoryId;
	private String testPersistenceManagerClass;
	private Map<String, String> testPersistenceManagerParameters;
	private List<String> testHandlerIds;

	@Before
	public void setUp() {
		workCategoryId = "Salif Keita";
		testPersistenceManagerClass = "Latency Requirement Test Persister";
		testPersistenceManagerParameters = new HashMap<String, String>();
		testPersistenceManagerParameters.put("data.directory", "/usr/tmp");
		testHandlerIds = new ArrayList<String>();
		testHandlerIds.add("Papa Wemba");
		testHandlerIds.add("Rokia Traore");
		testHandlerIds.add("Onyeka Onwenu");
	}

	/*
	 * Validates constructor logic.
	 */
	@Test
	public void testInitialState() throws Exception {
		LatencyRequirementConfiguration target = new LatencyRequirementConfiguration() {};

		assertTrue(target.getAlertHandlerIds().isEmpty());
		testInitialPropertyValue(target, true, "ignoreErrors");
		testInitialPropertyValue(target, null, "persistenceManagerClass");
		testInitialPropertyValue(target, null, "persistenceManagerParameters");
		testInitialPropertyValue(target, null, "workCategory");
	}

	/*
	 * Test logic for mutators and accessors.
	 */
	@Test
	public void testFieldAccess() throws Exception {
		LatencyRequirementConfiguration target = new LatencyRequirementConfiguration() {};

		testMutatorsAndAccessors(target, workCategoryId, "workCategory");
		testMutatorsAndAccessors(target, testPersistenceManagerClass, "persistenceManagerClass");
		testMutatorsAndAccessors(target, testPersistenceManagerParameters, "persistenceManagerParameters");
		testMutatorsAndAccessors(target, testHandlerIds, "alertHandlerIds");
		testMutatorsAndAccessors(target, false, "ignoreErrors", false);
	}
}