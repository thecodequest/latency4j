package org.latency4j.processing;

import java.util.HashMap;
import java.util.List;

import org.latency4j.LatencyRequirement;
import org.latency4j.StatisticalLatencyRequirement;
import org.latency4j.WorkDuration;

/**
 * <p>
 * Map which stores {@link WorkStatistics statistics} generated from
 * {@link WorkDuration durations} resulting from a specific {@link #requirement
 * latency requirement}.
 * </p>
 */
class RequirementStatisticsMap extends HashMap<String, WorkStatistics> {
	private static final long serialVersionUID = -4508963052524857774L;

	/**
	 * <p>
	 * Specialist key used to retrieve the {@link WorkStatistics statistics}
	 * related to the root of the call stack for a given
	 * {@link LatencyRequirement#getWorkCategory() category}.
	 * </p>
	 */
	private static final String ROOT_METHOD_MARKER = "<CALL.ROOT>";

	/**
	 * <p>
	 * The {@link StatisticalLatencyRequirement requirement} to which the
	 * statistics relate.
	 * </p>
	 */
	private final StatisticalLatencyRequirement requirement;

	/**
	 * <p>
	 * Constructor
	 * </p>
	 * 
	 * @param requirement
	 *            The {@link LatencyRequirement requirement} to which the
	 *            instance relates.
	 */
	RequirementStatisticsMap(final StatisticalLatencyRequirement requirement) {
		this.requirement = requirement;
		calculateStatsFromHistory();
	}

	/**
	 * <p>
	 * Returns the {@link WorkStatistics statistics} observed to date for the
	 * given {@link LatencyRequirement#getWorkCategory() category} and
	 * {@link WorkDuration#getMethodName() method}. Where none exists, it
	 * creates and stores a new {@link WorkStatistics entry} and returns a
	 * reference to it.
	 * </p>
	 * 
	 * @param duration
	 *            The {@link WorkDuration duration} for which relevant/related
	 *            {@link WorkStatistics statistics} are to be returned.
	 * @return A reference to the {@link WorkStatistics statistics} relevant to
	 *         the specified {@link WorkDuration duration}.
	 */
	WorkStatistics getOrCreateStatistics(final WorkDuration duration) {
		WorkStatistics result;
		String statisticsMapKey = getStatisticsMapKey(duration);

		result = get(statisticsMapKey);
		if (result == null) {
			result = new WorkStatistics(requirement.getObservationsSignificanceBarrier());
			put(statisticsMapKey, result);
		}
		return result;
	}

	/**
	 * <p>
	 * Build {@link WorkStatistics statistics} from historical/saved
	 * {@link WorkDuration durations} pertaining to the {@link #requirement
	 * requirement} for this instance.
	 * </p>
	 */
	private void calculateStatsFromHistory() {
		List<WorkDuration> historicalData = requirement.getPersistenceManager()
				.loadHistoricalData(requirement.getWorkCategory());
		if (historicalData != null) {
			for (WorkDuration taskDuration : historicalData)
				adjustStatisticsToHistoricalDuration(taskDuration);
		}
	}

	/**
	 * <p>
	 * Adjusts the {@link WorkStatistics statistics} for the {@link #requirement
	 * requirement} based on the information contained in the given
	 * {@link WorkDuration duration}.
	 * </p>
	 * 
	 * @param duration
	 *            The {@link WorkDuration duration} with which to adjust the
	 *            observed {@link WorkStatistics statistics}.
	 * 
	 * @see WorkStatistics#update(long)
	 */
	private void adjustStatisticsToHistoricalDuration(final WorkDuration duration) {
		if (!duration.isErrored()) {
			WorkStatistics statistics = getOrCreateStatistics(duration);
			statistics.update(duration.getElapsedTime());
		}
	}

	/**
	 * <p>
	 * Returns the key into the internal {@link WorkStatistics statistics} map
	 * with which the {@link WorkStatistics statistics} relevant to the
	 * specified {@link WorkDuration duration} can be retrieved.
	 * </p>
	 * 
	 * @param duration
	 *            The {@link WorkDuration duration} whose {@link WorkStatistics
	 *            statistics} key is being sought.
	 * 
	 * @return {@value #ROOT_METHOD_MARKER} If the duration refers to a method
	 *         at the root of a call stack for the monitored task, or the
	 *         {@link WorkDuration#getMethodName() name of the method from which
	 *         the duration originates} otherwise.
	 */
	private String getStatisticsMapKey(final WorkDuration duration) {
		String result;
		if (duration.isRoot()) result = ROOT_METHOD_MARKER;
		else result = duration.getMethodName();
		return result;
	}
}