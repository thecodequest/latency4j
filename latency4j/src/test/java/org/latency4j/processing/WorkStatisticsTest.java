package org.latency4j.processing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;
import org.latency4j.processing.WorkStatistics;

/*
 * Unit test for class WorkStatistics.
 */
public class WorkStatisticsTest {

	// test target
	private WorkStatistics target;

	// test field values
	private int significanceBarrier;

	@Before
	public void setUp() throws Exception {
		significanceBarrier = 30;
		target = new WorkStatistics(significanceBarrier);
	}

	/*
	 * Assert that test target is initialise with default field values.
	 */
	@Test
	public void testDefaultValues() {
		assertEquals(0.0d, target.getTotalTimeToDate(), 0d);
		assertEquals(0, target.getNumberOfObservations(), 0d);
		assertEquals(0.0d, target.getRunningAverage(), 0d);

		assertFalse(target.isSignificanceBarrierBreached());
	}

	/*
	 * Test update to statistics.
	 */
	@Test
	public void testUpdate() {
		int loopUpperBound = significanceBarrier + 2;
		for (int i = 0; i < loopUpperBound; i++)
			target.update(1);

		assertEquals(loopUpperBound, target.getTotalTimeToDate(), 0d);
		assertEquals(loopUpperBound, target.getNumberOfObservations());
		assertEquals(1.0, target.getRunningAverage(), 0d);
	}
}
