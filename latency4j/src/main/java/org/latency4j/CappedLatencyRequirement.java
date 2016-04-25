package org.latency4j;

/**
 * <p>
 * Representation of a capped {@link LatencyRequirement latency requirement}.
 * </p>
 * <p>
 * A capped {@link LatencyRequirement requirement} is one where a fixed upper
 * bound is specified for {@link LatencyRequirement#getWorkCategory() task}
 * latency.
 * </p>
 */
public class CappedLatencyRequirement extends LatencyRequirement {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = -4224077898670538121L;

	/**
	 * <p>
	 * The default value for field {@link #expectedLatency}.
	 * </p>
	 */
	protected static final Long DEFAULT_EXPECTED_LATENCY = 100L;

	/**
	 * <p>
	 * Indicates the maximum tolerable latency for work which falls within the
	 * monitored {@link #getWorkCategory() category of work}.
	 * </p>
	 */
	private Long expectedLatency;

	/**
	 * <p>
	 * Default constructor.
	 * </p>
	 */
	public CappedLatencyRequirement() {
		super();
		expectedLatency = DEFAULT_EXPECTED_LATENCY;
	}

	/**
	 * <p>
	 * Returns the maximum amount of time (in milliseconds) that a unit of
	 * execution falling within the {@link #getWorkCategory() work category} is
	 * expected to take. Where the {@link WorkDuration duration} of an execution
	 * exceeds this limit, all {@link LatencyMonitor monitors} watching this
	 * requirement are required to
	 * {@link AlertHandler#latencyExceededCap(CappedLatencyRequirement, WorkDuration)
	 * issue a notification to indicate so}.
	 * </p>
	 * 
	 * @return The {@link #expectedLatency latency limit} for
	 *         {@link #getWorkCategory() work} which is covered by this
	 *         requirement. Note that if this {@link #setExpectedLatency(Long)
	 *         value was not explicitly set}, it defaults to
	 *         {@link #DEFAULT_EXPECTED_LATENCY}.
	 * 
	 * @see AlertHandler#latencyExceededCap(CappedLatencyRequirement,
	 *      WorkDuration)
	 */
	public Long getExpectedLatency() {
		return expectedLatency;
	}

	/**
	 * <p>
	 * Sets the maximum amount of time (in milliseconds) that a unit of
	 * execution falling within the {@link #getWorkCategory() work category}
	 * covered by this requirement is expected to take.
	 * </p>
	 * 
	 * @param expectedLatency
	 *            The maximum expected/allowed/tolerable latency for execution
	 *            which falls within the {@link #getWorkCategory() category of
	 *            work} covered by this requirement. This value must be greater
	 *            than zero.
	 * 
	 * @throws IllegalArgumentException
	 *             If the specified {@link #expectedLatency expected latency} is
	 *             less than or equal to 0.
	 * 
	 * @see #getExpectedLatency()
	 */
	public void setExpectedLatency(Long expectedLatency) {
		validateExpectedLatency(expectedLatency);
		this.expectedLatency = Math.abs(expectedLatency);
	}

	@Override
	protected void assertCompulsoryFieldsSpecified() {
		super.assertCompulsoryFieldsSpecified();
		validateExpectedLatency(expectedLatency);
	}

	/**
	 * <p>
	 * Validates that a value for the field {@link #expectedLatency} is above
	 * zero and is non-null.
	 * </p>
	 * 
	 * @param expectedLatency
	 *            The candidate {@link #expectedLatency value} to validate.
	 * 
	 * @throws Latency4JException
	 *             If the specified value is less than or equal to zero or is
	 *             invalid.
	 */
	private void validateExpectedLatency(Long expectedLatency) {
		if (expectedLatency == null || expectedLatency <= 0)
			throw new Latency4JException("Expected latency must be greater than 0.");
	}
}