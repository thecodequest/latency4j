package org.latency4j;

import java.io.File;

import org.junit.Ignore;
import org.latency4j.CappedLatencyRequirement;
import org.latency4j.Latency4JConstants;
import org.latency4j.LatencyMonitor;
import org.latency4j.MonitorFactoryStaticHandle;
import org.latency4j.StatisticalLatencyRequirement;
import org.latency4j.WorkDuration;
import org.latency4j.persistence.DurationFileHandle;

/*
 * Utility class which centralises the logic for 
 * creating template test objects.
 */
@Ignore
public abstract class TestObjectFactory {
	public static final String TEST_CATEGORY_NAME = "TestTask";

	public static WorkDuration createTaskDuration() {
		WorkDuration result;

		LatencyMonitor monitor = MonitorFactoryStaticHandle.getMonitor("TestTask");
		monitor.taskStarted();

		result = monitor.taskCompleted();
		return result;
	}

	public static WorkDuration createDummyDurationRecord() {
		WorkDuration result = WorkDuration.start(TEST_CATEGORY_NAME, "testMethod");
		result.markFinished();
		return result;
	}

	public static WorkDuration createErrorDuration() {
		WorkDuration result = WorkDuration.start("TestTask", "testMethod");
		result.setErrored(true);
		result.setError(new Exception("Terminal error!!"));

		return result;
	}

	public static StatisticalLatencyRequirement createStatsBasedRequirement(final int singnifanceBarrier,
			final double toleranceLevel) {
		StatisticalLatencyRequirement result = new StatisticalLatencyRequirement();
		result.setWorkCategory("TestTask");
		result.setObservationsSignificanceBarrier(singnifanceBarrier);
		result.setToleranceLevel(toleranceLevel);
		result.init();
		return result;
	}

	public static CappedLatencyRequirement createFixedRequirement(final long expectedLatency) {
		CappedLatencyRequirement result = new CappedLatencyRequirement();
		result.setExpectedLatency(expectedLatency);
		result.setWorkCategory("TestTask");
		result.init();
		return result;
	}

	/*
	 * Deletes all Epsilon files from temp folder
	 */
	public static void deleteAllEpsilonFilesFromTemp() {
		String tempDirectory = System.getProperty(Latency4JConstants.JAVA_TMP_FILE_FOLDER);

		String epsilonExtension = DurationFileHandle.HISTORICAL_DATA_FILE_EXTENSION;

		File tempFolder = new File(tempDirectory);

		if (tempFolder.exists()) {
			File[] files = tempFolder.listFiles();

			if (files != null) {
				for (File file : files) {
					if (file.getName().contains(epsilonExtension)) file.delete();
				}
			}
		}
	}
}