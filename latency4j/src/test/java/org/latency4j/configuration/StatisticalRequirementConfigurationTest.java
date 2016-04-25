package org.latency4j.configuration;

import org.junit.Test;
import org.latency4j.configuration.StatisticalRequirementConfiguration;
import org.latency4j.testutil.BeanTestUtil;

/*
 * Test case for StatisticalRequirementConfiguration
 */
public class StatisticalRequirementConfigurationTest {
	/*
	 * Test initial state after construction.
	 */
	@Test
	public void testInitialState() throws Exception {
		StatisticalRequirementConfiguration target = new StatisticalRequirementConfiguration();
		BeanTestUtil.testInitialPropertyValue(target, null, "observationsSignificanceBarrier");
		BeanTestUtil.testInitialPropertyValue(target, null, "toleranceLevel");
	}

	/*
	 * Test class mutators and accessors.
	 */
	@Test
	public void testMutatorsAndAccessors() throws Exception {
		StatisticalRequirementConfiguration target = new StatisticalRequirementConfiguration();
		BeanTestUtil.testMutatorsAndAccessors(target, System.currentTimeMillis(), "observationsSignificanceBarrier");
		BeanTestUtil.testMutatorsAndAccessors(target, (double) System.currentTimeMillis(), "toleranceLevel");
	}
}