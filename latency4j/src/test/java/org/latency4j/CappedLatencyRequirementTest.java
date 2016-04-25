package org.latency4j;

import org.junit.Test;
import org.latency4j.CappedLatencyRequirement;
import org.latency4j.Latency4JException;
import org.latency4j.testutil.BeanTestUtil;

/*
 * Tests a CappedLatencyRequirement
 */
public class CappedLatencyRequirementTest {

	/*
	 * Tests the initial state of the requirement.
	 */
	@Test
	public void testInitialState() throws Exception {
		CappedLatencyRequirement target = new CappedLatencyRequirement();
		BeanTestUtil.testInitialPropertyValue(target, CappedLatencyRequirement.DEFAULT_EXPECTED_LATENCY,
				"expectedLatency");
	}

	/*
	 * Test field mutators and accessors.
	 */
	@Test
	public void testMutatorsAndAccessors() throws Exception {
		CappedLatencyRequirement target = new CappedLatencyRequirement();
		BeanTestUtil.testMutatorsAndAccessors(target, System.currentTimeMillis(), "expectedLatency");
	}

	/*
	 * Test that expected latency cannot be null.
	 */
	@Test(expected = Latency4JException.class)
	public void testSetExpectedLatencyToNullValue() {
		CappedLatencyRequirement target = new CappedLatencyRequirement();
		target.setExpectedLatency(null);
	}

	/*
	 * Test that expected latency cannot be 0.
	 */
	@Test(expected = Latency4JException.class)
	public void testSetExpectedLatencyToZero() {
		CappedLatencyRequirement target = new CappedLatencyRequirement();
		target.setExpectedLatency(0L);
	}

	/*
	 * Test that expected latency cannot be negative.
	 */
	@Test(expected = Latency4JException.class)
	public void testSetExpectedLatencyToNegativeValue() {
		CappedLatencyRequirement target = new CappedLatencyRequirement();
		target.setExpectedLatency(-1L);
	}
}