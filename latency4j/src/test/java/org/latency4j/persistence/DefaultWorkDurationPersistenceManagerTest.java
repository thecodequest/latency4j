package org.latency4j.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.latency4j.TestObjectFactory.TEST_CATEGORY_NAME;
import static org.latency4j.TestObjectFactory.createDummyDurationRecord;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.latency4j.TestObjectFactory;
import org.latency4j.WorkDuration;
import org.latency4j.persistence.DefaultWorkDurationPersistenceManager;
import org.latency4j.persistence.DurationFileHandle;

/*
 * Unit test for default duration persistence manager 
 */
public class DefaultWorkDurationPersistenceManagerTest {
	// test target
	private DefaultWorkDurationPersistenceManager target;

	// dummy test data/durations
	private List<WorkDuration> testDurations;

	@Before
	public void setUp() throws Exception {
		target = new DefaultWorkDurationPersistenceManager();
		testDurations = new ArrayList<WorkDuration>();
		for (int i = 0; i < 20; i++)
			testDurations.add(createDummyDurationRecord());

		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("data.directory", System.getProperty("java.io.tmpdir"));

		target.setParameters(parameters);

		// delete old files
		TestObjectFactory.deleteAllEpsilonFilesFromTemp();
	}

	/*
	 * Test to ensure that manager will fail if its logic is accessed before it
	 * has been initialised by calling the init method.
	 */
	@Test
	public void testPreInitFailure() {
		try {
			target.save(null);
			fail("Manager allowed call despite not being initialised!");
		} catch (RuntimeException re_exce) {
			// passed
		}
	}

	/*
	 * Test the functionality for saving durations to secondary storage.
	 */
	@Test
	public void testSave() {
		target.init();

		for (WorkDuration duration : testDurations)
			target.save(duration);

		// check that file has been created
		String dataFileName = DurationFileHandle.getDataFileName(TEST_CATEGORY_NAME);
		File dataFile = new File(target.getOutputDirectory(), dataFileName);

		assertTrue("Data file should have been created by write.", dataFile.exists());

	}

	/*
	 * Test logic for loading previously saved duration instances.
	 */
	@Test
	public void testLoadHistoricalData() {
		target.init();

		// save first
		for (WorkDuration duration : testDurations)
			target.save(duration);

		List<WorkDuration> loadedDurations = target.loadHistoricalData("TestTask");

		assertEquals(testDurations.size(), loadedDurations.size());
		assertEquals(testDurations.get(0), loadedDurations.get(0));
	}

}
