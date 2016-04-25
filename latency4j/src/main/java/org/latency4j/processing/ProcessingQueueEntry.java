package org.latency4j.processing;

import org.latency4j.LatencyRequirement;
import org.latency4j.WorkDuration;

/**
 * <p>
 * Encapsulates an entry in the {@link LatencyProcessor latency processor's}
 * queue. It not only holds a reference to the {@link WorkDuration duration} to
 * be processed, but also the {@link LatencyRequirement requirement} to which
 * the {@link WorkDuration duration} relates.
 * </p>
 */
class ProcessingQueueEntry {
	/**
	 * <p>
	 * The {@link WorkDuration duration} to be processed.
	 * </p>
	 */
	private final WorkDuration duration;

	/**
	 * <p>
	 * The {@link LatencyRequirement requirement} to which the
	 * {@link WorkDuration duration} relates.
	 * </p>
	 */
	private final LatencyRequirement requirement;

	/**
	 * <p>
	 * Constructor
	 * </p>
	 * 
	 * @param duration
	 *            The {@link WorkDuration duration} to be processed.
	 * @param requirement
	 *            The {@link LatencyRequirement requirement} to which the
	 *            {@link WorkDuration duration} relates.
	 * 
	 */
	ProcessingQueueEntry(final WorkDuration duration, final LatencyRequirement requirement) {
		this.duration = duration;
		this.requirement = requirement;
	}

	/**
	 * <p>
	 * Returns the {@link WorkDuration duration} to be processed.
	 * </p>
	 * 
	 * @return The {@link WorkDuration duration} to be processed.
	 */
	WorkDuration getDuration() {
		return duration;
	}

	/**
	 * <p>
	 * Returns the {@link LatencyRequirement requirement} to which the
	 * {@link #getDuration() duration} to process relates.
	 * </p>
	 * 
	 * @return The {@link LatencyRequirement requirement} to which the
	 *         {@link WorkDuration duration} to process relates.
	 */
	LatencyRequirement getRequirement() {
		return requirement;
	}
}
