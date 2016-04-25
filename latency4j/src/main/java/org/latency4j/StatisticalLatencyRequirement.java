package org.latency4j;

/**
 * <p>
 * A statistical {@link LatencyRequirement requirement} can be used in
 * situations where there is no specific upper bound for
 * {@link LatencyRequirement#getWorkCategory() operation} {@link WorkDuration
 * latency}, but monitoring is required to identify
 * {@link LatencyRequirement#getWorkCategory() operation} {@link WorkDuration
 * latencies} which deviate from an observed pattern. Put differently, this type
 * of {@link LatencyRequirement requirement} is suited for situations where
 * {@link LatencyRequirement#getWorkCategory() operation} {@link WorkDuration
 * latency} should not deviate by more than a given margin from an observed
 * average.
 * </p>
 */
public class StatisticalLatencyRequirement extends LatencyRequirement {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = -3940973332253617142L;

	/**
	 * <p>
	 * The default ({@value #DEFAULT_SIGNIFICANCE_BARRIER}) number of
	 * {@link #getObservationsSignificanceBarrier() observations} with which a
	 * monitor is calibrated.
	 * </p>
	 */
	public static final long DEFAULT_SIGNIFICANCE_BARRIER = 50;

	/**
	 * <p>
	 * The default ({@value #DEFAULT_TOLERANCE}) {@link #getToleranceLevel()
	 * tolerance level}.
	 * </p>
	 */
	public static final double DEFAULT_TOLERANCE = 0.10d;

	/**
	 * <p>
	 * The number of observations with which a {@link LatencyMonitor monitor} of
	 * the statistical {@link LatencyRequirement requirement} is calibrated,
	 * before it can draw conclusions about further observations. Put
	 * differently, this is the number of observations from which the
	 * {@link LatencyMonitor monitor} will initially calculate the avarage
	 * {@link WorkDuration execution latency}, against which further executions
	 * can be compared. Thus, no notifications will be issued by a
	 * {@link LatencyMonitor monitor} of this {@link LatencyRequirement
	 * requirement} until this barrier is reached.
	 * </p>
	 * <p>
	 * This property defaults to {@link #DEFAULT_TOLERANCE}.
	 * </p>
	 */
	private long observationsSignificanceBarrier;

	/**
	 * <p>
	 * The maximum margin (in percentage points) by which the
	 * {@link WorkDuration duration} of a given
	 * {@link LatencyRequirement#getWorkCategory() operation} can exceed the
	 * observed average {@link WorkDuration duration} for the
	 * {@link LatencyRequirement requirement}. Thus any
	 * {@link LatencyRequirement#getWorkCategory() operation} which exceeds the
	 * observed average by a figure greater than this margin will be considered
	 * in breach of the {@link LatencyRequirement requirement} and,
	 * consequently, will trigger an {@link AlertHandler alert}.
	 * </p>
	 */
	private double toleranceLevel;

	/**
	 * <p>
	 * Default constructor.
	 * </p>
	 */
	public StatisticalLatencyRequirement() {
		setObservationsSignificanceBarrier(DEFAULT_SIGNIFICANCE_BARRIER);
		setToleranceLevel(DEFAULT_TOLERANCE);
	}

	/**
	 * <p>
	 * Returns the {@link #observationsSignificanceBarrier number of
	 * observations} with which {@link LatencyMonitor monitors} of the
	 * requirement are to be calibrated initially.
	 * </p>
	 * <p>
	 * {@link LatencyMonitor Monitors} will use this number of executions to
	 * observe the behaviour of the application and determine parameters such as
	 * average {@link WorkDuration execution latency}. As such, observance of
	 * the requirement will not be checked until this barrier is reached.
	 * </p>
	 * <p>
	 * For example, if a value of 10 is returned by this method, it means that
	 * the first 10 invocations falling within this requirement are used purely
	 * for calibration. All subsequent requests will then be judged based on
	 * figures calculated from these first ten.
	 * </p>
	 * <p>
	 * <b>Note:</b> {@link LatencyMonitor Monitors} continuously re-calibrate
	 * during the lifetime of an application. As such, calibration does not stop
	 * entirely after this number of requests is observed. Instead, these
	 * requests are used as a starting point.
	 * </p>
	 * 
	 * @return The {@link #observationsSignificanceBarrier number of
	 *         observations} with which monitors of this requirement are
	 *         initially calibrated.
	 * 
	 * @see AlertHandler#latencyDeviationExceededTolerance(StatisticalLatencyRequirement,
	 *      WorkDuration, double, double)
	 */
	public long getObservationsSignificanceBarrier() {
		return observationsSignificanceBarrier;
	}

	/**
	 * <p>
	 * Sets the {@link #observationsSignificanceBarrier observations
	 * significance barrier}.
	 * </p>
	 * 
	 * @param observationsSignificanceBarrier
	 *            The {@link #observationsSignificanceBarrier number of
	 *            observations} with which {@link LatencyMonitor monitors} of
	 *            this {@link LatencyRequirement requirement} are calibrated.
	 * 
	 * @see #getObservationsSignificanceBarrier()
	 */
	public void setObservationsSignificanceBarrier(final long observationsSignificanceBarrier) {
		this.observationsSignificanceBarrier = observationsSignificanceBarrier;
	}

	/**
	 * <p>
	 * Returns the {@link #toleranceLevel tolerance level} within which
	 * {@link LatencyRequirement#getWorkCategory() operation}
	 * {@link WorkDuration execution times} must fall in order not to be in
	 * breach of the {@link LatencyRequirement requirement}.
	 * </p>
	 * 
	 * <p>
	 * A {@link LatencyRequirement#getWorkCategory() task} invocation with a
	 * {@link WorkDuration duration} of <code>x</code> milliseconds is
	 * considered to have breached its {@link LatencyRequirement requirement} if
	 * the following inequality holds: <br>
	 * <code> (x - averageLatency) </code> &gt; <code>averageLatency * 
	 * {@link #getToleranceLevel() getToleranceLevel()}</code>. <br>
	 * In other words, the deviation from the average {@link WorkDuration
	 * latency} must be below a given percentage of said average. Else the
	 * {@link LatencyRequirement#getWorkCategory() operation} is treated as
	 * having breached the {@link LatencyRequirement requirement}, thus
	 * triggering an {@link AlertHandler alert}.
	 * </p>
	 * 
	 * @return The {@link WorkDuration latency/duration} {@link #toleranceLevel
	 *         tolerance level} to be applied to latency observations governed
	 *         by the {@link LatencyRequirement requirement}.
	 * 
	 * @see AlertHandler#latencyDeviationExceededTolerance(StatisticalLatencyRequirement,
	 *      WorkDuration, double, double)
	 */
	public double getToleranceLevel() {
		return toleranceLevel;
	}

	/**
	 * <p>
	 * Sets the {@link #toleranceLevel tolerance} to be applied to the
	 * {@link WorkDuration latency} of
	 * {@link LatencyRequirement#getWorkCategory() operations} falling within
	 * the scope of the {@link LatencyRequirement requirement}.
	 * </p>
	 * 
	 * @param toleranceLevel
	 *            The {@link #toleranceLevel tolerance level} for
	 *            {@link LatencyRequirement#getWorkCategory() operation
	 *            invocations} which fall within the scope of the
	 *            {@link LatencyRequirement requirement}.
	 * 
	 * @see #getToleranceLevel()
	 * @see WorkDuration
	 */
	public void setToleranceLevel(final double toleranceLevel) {
		this.toleranceLevel = toleranceLevel;
	}
}