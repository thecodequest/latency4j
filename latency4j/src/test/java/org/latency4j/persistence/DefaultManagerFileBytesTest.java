package org.latency4j.persistence;

import static org.junit.Assert.assertEquals;
import static org.latency4j.TestObjectFactory.TEST_CATEGORY_NAME;
import static org.latency4j.TestObjectFactory.createDummyDurationRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.latency4j.TestObjectFactory;
import org.latency4j.WorkDuration;
import org.latency4j.persistence.DefaultWorkDurationPersistenceManager;

/*
 * Test case which ensures that 
 * default manager observes specified maximum 
 * file sizes.
 */
public class DefaultManagerFileBytesTest {
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
		parameters.put("max.file.bytes", "1");

		target.setParameters(parameters);

		target.init();

		// delete old files
		TestObjectFactory.deleteAllEpsilonFilesFromTemp();
	}

	/*
	 * Test logic for not exceeding file size
	 */
	@Test
	public void testLowMaxFileSize() {
		for (WorkDuration duration : testDurations)
			target.save(duration);

		List<WorkDuration> savedDurations = target.loadHistoricalData(TEST_CATEGORY_NAME);

		assertEquals("Only one duration record should exist " + "in historical file.", 1, savedDurations.size());

	}

}
