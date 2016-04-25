package org.latency4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.latency4j.WorkDuration;
import org.latency4j.WorkDurationId;

/*
 * Test unit for class WorkDuration
 */
public class WorkDurationTest {

	/*
	 * Test field values
	 */
	private long start;
	private long end;
	private long expectedElapsedTime;
	private boolean root;
	private boolean errored;
	private String methodName;

	private String workContext;
	private String threadName;

	private WorkDurationId identifier;

	/*
	 * Test target
	 */
	private WorkDuration duration;

	/*
	 * Sets up an instance to mirror the test values
	 */
	@Before
	public void setUp() {
		start = 5;
		end = 10;
		expectedElapsedTime = end - start;
		root = true;
		errored = true;
		methodName = "main";

		workContext = "TestContext";
		threadName = "main";

		identifier = new WorkDurationId(workContext, threadName);
		duration = new WorkDuration(identifier, methodName, start, end, root, errored);
	}

	/*
	 * By default a duration is assumed to be the root, except otherwise
	 * specified. This method validates this assumption.
	 */
	@Test
	public void testIsRoot() {
		assertTrue(duration.isRoot());
		duration.setRoot(false);
		assertFalse(duration.isRoot());
	}

	/*
	 * By default a duration is not in error, and has to be explicitly set to
	 * Error.
	 */
	@Test
	public void testIsErrored() {
		assertTrue(duration.isErrored());
		duration.setErrored(false);
		assertFalse(duration.isErrored());
	}

	/*
	 * Asserts that a durations identifier is equal to a duplicate which is
	 * initialised for the same context and thread id.
	 */
	@Test
	public void testGetIdentifier() {
		WorkDurationId duplicateId = new WorkDurationId(workContext, threadName);
		assertEquals(duplicateId, duration.getIdentifier());
	}

	/*
	 * Accessor tests
	 */
	@Test
	public void testGetMethodName() {
		assertEquals(methodName, duration.getMethodName());

	}

	@Test
	public void testGetElapsedTime() {
		assertTrue(expectedElapsedTime == duration.getElapsedTime());
	}

	@Test
	public void testGetStartTime() {
		assertTrue(start == duration.getStartTime());
	}

	@Test
	public void testGetEndTime() {
		assertTrue(end == duration.getEndTime());
	}
}