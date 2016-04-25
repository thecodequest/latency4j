package org.latency4j.configuration;

import org.junit.Test;
import org.latency4j.configuration.CappedRequirementConfiguration;
import org.latency4j.testutil.BeanTestUtil;

/*
 * Test for class CappedRequirementConfiguration
 */
public class CappedRequirementConfigurationTest {
	/*
	 * Test initial field values after construction/initialisation.
	 */
	@Test
	public void testInitialState() throws Exception {
		CappedRequirementConfiguration target = new CappedRequirementConfiguration();
		BeanTestUtil.testInitialPropertyValue(target, null, "expectedLatency");
	}

	/*
	 * Test field mutators and accessors.
	 */
	@Test
	public void testMutatorsAndAccessors() throws Exception {
		CappedRequirementConfiguration target = new CappedRequirementConfiguration();
		BeanTestUtil.testMutatorsAndAccessors(target, System.currentTimeMillis(), "expectedLatency");
	}
}