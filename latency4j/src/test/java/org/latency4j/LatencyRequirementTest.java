package org.latency4j;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.latency4j.testutil.BeanTestUtil.testInitialPropertyValue;
import static org.latency4j.testutil.BeanTestUtil.testMutatorsAndAccessors;

import org.junit.Test;
import org.latency4j.Latency4JException;
import org.latency4j.LatencyRequirement;
import org.latency4j.alert.MissedTargetAlertLogger;
import org.latency4j.persistence.DefaultWorkDurationPersistenceManager;

/*
 * Test unit for class LatencyRequirement. 
 */
public class LatencyRequirementTest {
	/*
	 * Test state after construction
	 */
	@Test
	public void testInitialState() throws Exception {
		LatencyRequirement testRequirement = new LatencyRequirement() {};
		testInitialPropertyValue(testRequirement, null, "persistenceManager");
		testInitialPropertyValue(testRequirement, null, "workCategory");
		testInitialPropertyValue(testRequirement, true, "ignoreErrors");
	}

	/*
	 * Test property mutators and accessors.
	 */
	@Test
	public void testPropertyMutatorsAndAccessors() throws Exception {
		LatencyRequirement testRequirement = new LatencyRequirement() {};
		testMutatorsAndAccessors(testRequirement, new DefaultWorkDurationPersistenceManager(), "persistenceManager");
		testMutatorsAndAccessors(testRequirement, "TestWorkCategory", "workCategory");
		testMutatorsAndAccessors(testRequirement, false, "ignoreErrors", false);
	}

	/*
	 * Test behaviour of init method, and also to ensure that default
	 * persistence managers are enabled without configuration.
	 */
	@Test
	public void testInitMethod() throws Exception {
		LatencyRequirement testRequirement = new LatencyRequirement() {};
		testInitialPropertyValue(testRequirement, null, "persistenceManager");
		// after init method, should fail
		// because no category specified
		try {
			testRequirement.init();
			fail("Requirement init should fail without non-null category.");
		} catch (Latency4JException exce) {}

		// specify work category and then retry init
		testRequirement.setWorkCategory("TestCategory");
		testRequirement.init();

		// verify that persistence manager has been set to default after test
		assertNotNull(testRequirement.getPersistenceManager());
		assertTrue(testRequirement.getPersistenceManager() instanceof DefaultWorkDurationPersistenceManager);
	}

	/*
	 * Test that alert handlers can be modified
	 */
	@Test
	public void testAddAlertHandler() {
		LatencyRequirement testRequirement = new LatencyRequirement() {};
		assertTrue(testRequirement.getAlertHandlers().size() == 0);

		testRequirement.getAlertHandlers().add(new MissedTargetAlertLogger());
		assertTrue(testRequirement.getAlertHandlers().size() == 1);
	}
}
