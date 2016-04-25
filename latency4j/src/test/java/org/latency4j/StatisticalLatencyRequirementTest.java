package org.latency4j;

import org.junit.Test;
import org.latency4j.StatisticalLatencyRequirement;
import org.latency4j.testutil.BeanTestUtil;

/*
 * Test for StatisticalLatencyRequirement.
 */
public class StatisticalLatencyRequirementTest {
	/*
	 * Test initial (post-construction) state.
	 */
	@Test
	public void testInitialState() throws Exception {
		StatisticalLatencyRequirement target = new StatisticalLatencyRequirement();
		BeanTestUtil.testInitialPropertyValue(target, StatisticalLatencyRequirement.DEFAULT_TOLERANCE,
				"toleranceLevel");
		BeanTestUtil.testInitialPropertyValue(target, StatisticalLatencyRequirement.DEFAULT_SIGNIFICANCE_BARRIER,
				"observationsSignificanceBarrier");
	}

	/*
	 * Test field mutator and accessors.
	 */
	@Test
	public void testSetPropertyValues() throws Exception {
		StatisticalLatencyRequirement target = new StatisticalLatencyRequirement();

		// tolerance level
		BeanTestUtil.testMutatorsAndAccessors(target, 0.99, "toleranceLevel");
		BeanTestUtil.testMutatorsAndAccessors(target, 0.0d, "toleranceLevel");
		BeanTestUtil.testMutatorsAndAccessors(target, -1.0d, "toleranceLevel");

		// significance barrier
		BeanTestUtil.testMutatorsAndAccessors(target, 0L, "observationsSignificanceBarrier");
		BeanTestUtil.testMutatorsAndAccessors(target, -1L, "observationsSignificanceBarrier");
		BeanTestUtil.testMutatorsAndAccessors(target, 6789L, "observationsSignificanceBarrier");
	}
}// end class def