package org.latency4j;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.latency4j.WorkDurationId;

/*
 * Tests the WorkDurationId object. 
 */
public class WorkDurationIdTest {

	/*
	 * Test field values.
	 */
	private final String workCategory = "TestCategory";
	private final String threadId = "main";

	/*
	 * The test target
	 */
	private WorkDurationId workCategoryId;

	/*
	 * Sets up an instance with the test field values.
	 */
	@Before
	public void setUp() {
		workCategoryId = new WorkDurationId(workCategory, threadId);
	}

	/*
	 * Asserts that the values returned by the target's accessors are the same
	 * as those specified for the test field values.
	 */
	@Test
	public void testAccessors() {
		assertEquals(threadId, workCategoryId.getThreadId());
		assertEquals(workCategory, workCategoryId.getWorkCategory());
	}

	/*
	 * Tests the implementation of the equals method.
	 */
	@Test
	public void testEqualsObject() {
		WorkDurationId secondDuration = new WorkDurationId(workCategory, threadId);
		assertEquals(workCategoryId, secondDuration);
	}

}
