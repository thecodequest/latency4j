package org.latency4j.processing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.latency4j.TestObjectFactory.createFixedRequirement;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.latency4j.TestObjectFactory;
import org.latency4j.WorkDuration;
import org.latency4j.processing.AsynchronousLatencyMonitor;
import org.latency4j.processing.LatencyProcessor;

/*
 * Test unit for asynchronous latency monitor. 
 */
public class AsynchronousLatencyMonitorTest {

	/*
	 * Test target.
	 */
	private AsynchronousLatencyMonitor target;

	@Before
	public void setUp() throws Exception {
		LatencyProcessor latencyProcessor = new LatencyProcessor();
		latencyProcessor.start();
		target = new AsynchronousLatencyMonitor(createFixedRequirement(1), latencyProcessor);
	}

	/*
	 * Test handling of task termination due to errors.
	 */
	@Test
	public void testAbnormalCompletion() {
		target.taskStarted();
		WorkDuration duration = target.taskCompleted();
		assertFalse(duration.isErrored());

		target.taskStarted();
		duration = target.taskErrored(new Throwable("Test Exception message."));
		assertTrue(duration.isErrored());
	}

	/*
	 * Test that normal completion does not trigger error handling.
	 */
	@Test
	public void testElapsedNormalCompletion() {
		target.taskStarted();
		WorkDuration duration = target.taskCompleted();
		assertFalse(duration.isErrored());
	}

	/*
	 * Tests that task root-methods (i.e. the first method call in method
	 * stack). Also test that elapsed times are recorded.
	 */
	@Test
	public void testRootTaskStati() throws InterruptedException {
		int i = 0;
		WorkDuration duration;
		do {
			target.taskStarted();
			Thread.sleep(i * 2);
			i++;
		}
		while (i <= 5);

		do {
			duration = target.taskCompleted();
			if (i == 1) assertTrue(duration.isRoot());
			else assertFalse(duration.isRoot());

			assertFalse(duration.isErrored());

			assertTrue(duration.getElapsedTime() >= 0);

			i--;
		}
		while (i >= 1);
	}

	/*
	 * Delete all files from fileSystem
	 */
	@AfterClass
	public static void cleanUp() {
		TestObjectFactory.deleteAllEpsilonFilesFromTemp();
	}

}
