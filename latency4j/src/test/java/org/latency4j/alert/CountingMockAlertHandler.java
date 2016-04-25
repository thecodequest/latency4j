package org.latency4j.alert;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.latency4j.CappedLatencyRequirement;
import org.latency4j.LatencyRequirement;
import org.latency4j.StatisticalLatencyRequirement;
import org.latency4j.WorkDuration;
import org.latency4j.alert.AbstractAlertHandler;

/*
 * {@link AlertHandler} implementation which is used for testing only.
 * It maintains a static internal counter of all notifications, which can 
 * be reset in between tests.
 */
public class CountingMockAlertHandler extends AbstractAlertHandler {
	private static final Map<String, Integer> durationExceededToleranceMap = new HashMap<String, Integer>();

	private static final Map<String, Integer> latencyExceededCapCountMap = new HashMap<String, Integer>();

	private static final Map<String, Integer> workFailureCountMap = new HashMap<String, Integer>();

	private static final Lock modificationLock = new ReentrantLock();

	public static void resetAfterTest() {
		lock();
		try {
			durationExceededToleranceMap.clear();
			latencyExceededCapCountMap.clear();
			workFailureCountMap.clear();
		}
		finally {
			unlock();
		}
	}

	public static int getDeviationExceededToleranceCount(final String category) {
		lock();
		try {
			if (durationExceededToleranceMap.containsKey(category)) return durationExceededToleranceMap.get(category);
			else return 0;
		}
		finally {
			unlock();
		}
	}

	public static int getLatencyExceededCapCount(final String category) {
		lock();
		try {
			if (latencyExceededCapCountMap.containsKey(category)) return latencyExceededCapCountMap.get(category);
			else return 0;
		}
		finally {
			unlock();
		}
	}

	public static int getWorkFailureCount(final String category) {
		lock();
		try {
			if (workFailureCountMap.containsKey(category)) return workFailureCountMap.get(category);
			else return 0;
		}
		finally {
			unlock();
		}
	}

	@Override
	public void latencyDeviationExceededTolerance(final StatisticalLatencyRequirement requirement,
			final WorkDuration duration, final double deviationFromMean, final double mean) {
		lock();
		try {
			Integer value;
			String category = requirement.getWorkCategory();
			if (!durationExceededToleranceMap.containsKey(category)) value = 1;
			else value = durationExceededToleranceMap.get(category) + 1;
			durationExceededToleranceMap.put(category, value);
		}
		finally {
			unlock();
		}
	}

	@Override
	public void latencyExceededCap(final CappedLatencyRequirement requirement, final WorkDuration duration) {
		lock();
		try {
			Integer value;
			String category = requirement.getWorkCategory();
			if (!latencyExceededCapCountMap.containsKey(category)) value = 1;
			else value = latencyExceededCapCountMap.get(category) + 1;
			latencyExceededCapCountMap.put(category, value);
		}
		finally {
			unlock();
		}
	}

	@Override
	public void workCategoryFailed(final LatencyRequirement requirement, final WorkDuration duration) {
		lock();
		try {
			Integer value;
			String category = requirement.getWorkCategory();
			if (!workFailureCountMap.containsKey(category)) value = 1;
			else value = workFailureCountMap.get(category) + 1;
			workFailureCountMap.put(category, value);
		}
		finally {
			unlock();
		}
	}

	public static void assertCounts(final String category, final int expectedExceededCapCount,
			final int expectedWorkFailureCount, final int expectedDeviationExceededToleranceCount) {
		assertEquals("Number of violations of capped requirement '" + category + "' does not match expected value.",
				expectedExceededCapCount, CountingMockAlertHandler.getLatencyExceededCapCount(category));

		assertEquals("Number of failures '" + category + "' does not match expected value.", expectedWorkFailureCount,
				CountingMockAlertHandler.getWorkFailureCount(category));

		assertEquals(
				"Number of violations of statistical requirement '" + category + "' does not match expected value.",
				expectedDeviationExceededToleranceCount,
				CountingMockAlertHandler.getDeviationExceededToleranceCount(category));
	}

	private static void lock() {
		modificationLock.lock();
	}

	private static void unlock() {
		modificationLock.unlock();
	}
}