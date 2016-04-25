package org.latency4j;

import java.io.Serializable;

/**
 * <p>
 * Encapsulates the duration of {@link LatencyRequirement#getWorkCategory() an
 * operation, task or unit of work}. A {@link LatencyMonitor monitor} returns an
 * instance of this class for each {@link LatencyMonitor#taskCompleted()
 * completion} of {@link LatencyRequirement#getWorkCategory() a unit of
 * monitored work}.
 * </p>
 * 
 * @see LatencyMonitor#taskCompleted()
 */
public class WorkDuration implements Serializable {
	private static final long serialVersionUID = 8781195826267188958L;

	/**
	 * {@value #TOTAL_DAILY_HOURS}
	 */
	private static final int TOTAL_DAILY_HOURS = 24;

	/**
	 * {@value #TOTAL_MINS_IN_HR}
	 */
	private static final int TOTAL_MINS_IN_HR = 60;

	/**
	 * {@value #TOTAL_SECS_IN_MIN}
	 */
	private static final int TOTAL_SECS_IN_MIN = 60;

	/**
	 * {@value #TOTAL_MILLISECONDS_IN_A_SEC}
	 */
	private static final long TOTAL_MILLISECONDS_IN_A_SEC = 1000;

	/**
	 * <p>
	 * Equal to {@link #TOTAL_DAILY_HOURS} <b>*</b> {@link #TOTAL_MINS_IN_HR}
	 * <b>*</b> {@link #TOTAL_SECS_IN_MIN} <b>*</b>
	 * {@link #TOTAL_MILLISECONDS_IN_A_SEC}.
	 * </p>
	 */
	private static final long TOTAL_MILLISECONDS_IN_A_DAY = TOTAL_DAILY_HOURS * TOTAL_MINS_IN_HR * TOTAL_SECS_IN_MIN
			* TOTAL_MILLISECONDS_IN_A_SEC;

	/**
	 * <p>
	 * Equal to {@link #TOTAL_MINS_IN_HR} <b>*</b> {@link #TOTAL_SECS_IN_MIN}
	 * <b>*</b> {@link #TOTAL_MILLISECONDS_IN_A_SEC}.
	 * </p>
	 */
	private static final long TOTAL_MILLISECONDS_IN_HOUR = TOTAL_MINS_IN_HR * TOTAL_SECS_IN_MIN
			* TOTAL_MILLISECONDS_IN_A_SEC;

	/**
	 * <p>
	 * Equal to {@link #TOTAL_SECS_IN_MIN} <b>*</b>
	 * {@link #TOTAL_MILLISECONDS_IN_A_SEC}.
	 * </p>
	 */
	private static final long TOTAL_MILLISECONDS_IN_A_MIN = TOTAL_SECS_IN_MIN * TOTAL_MILLISECONDS_IN_A_SEC;

	/**
	 * <p>
	 * System timestamp denoting the {@link LatencyMonitor#taskStarted() start}
	 * of the {@link LatencyRequirement#getWorkCategory() task} to which the
	 * instance relates.
	 * </p>
	 */
	private final long start;

	/**
	 * <p>
	 * System timestamp denoting the {@link LatencyMonitor#taskCompleted() end}
	 * of the {@link LatencyRequirement#getWorkCategory() task} to which the
	 * instance relates.
	 * </p>
	 */
	private long end;

	/**
	 * <p>
	 * A boolean that denotes if the the method to which the instance relates is
	 * the lowest in the call stack with respect to the sequence of
	 * {@link LatencyRequirement#getWorkCategory() operation executions} which,
	 * together, compose the {@link LatencyRequirement#getWorkCategory() task}
	 * being performed.
	 * </p>
	 */
	private boolean root;

	/**
	 * <p>
	 * Indicates if the {@link LatencyRequirement#getWorkCategory() operation}
	 * to which the instance relates failed with an exception or not.
	 * </p>
	 */
	private boolean errored;

	/**
	 * <p>
	 * The name of the method from which the instance is generated. This is the
	 * method from which the related {@link LatencyRequirement#getWorkCategory()
	 * operation} is {@link LatencyMonitor#taskStarted() initiated}.
	 * </p>
	 */
	private final String methodName;

	/**
	 * <p>
	 * Where a {@link LatencyRequirement#getWorkCategory() task} is terminated
	 * by an {@link Throwable exception}, this field holds a reference to the
	 * {@link Throwable exception}.
	 * </p>
	 */
	private Throwable error;

	/**
	 * <p>
	 * The instance identifier.
	 * </p>
	 */
	private final WorkDurationId identifier;

	/**
	 * <p>
	 * Public constructor.
	 * </p>
	 * 
	 * @param identifier
	 *            The duration's identifier (primary key). This simplifies
	 *            persistence and loading.
	 * @param methodName
	 *            The method in which the {@link LatencyMonitor monitor}, which
	 *            produced this duration, was
	 *            {@link LatencyMonitor#taskStarted() triggered}.
	 * @param start
	 *            The {@link LatencyMonitor#taskStarted() start time} of the
	 *            {@link LatencyRequirement#getWorkCategory() task}, represented
	 *            as a timestamp, to which the new instance relates.
	 * @param end
	 *            The {@link LatencyMonitor#taskCompleted() end time} of the
	 *            {@link LatencyRequirement#getWorkCategory() task}, represented
	 *            as a timestamp, to which the new instance relates.
	 * @param root
	 *            Denotes if the new instance corresponds to the root of the
	 *            method calls comprising the call stack over which the executed
	 *            {@link LatencyRequirement#getWorkCategory() operation} spans.
	 * @param errored
	 *            A value of true denotes that the task ended with an exception,
	 *            and a value of false otherwise.
	 */
	public WorkDuration(final WorkDurationId identifier, final String methodName, final long start, final long end,
			final boolean root, final boolean errored) {
		this.identifier = identifier;
		this.methodName = methodName;
		this.start = start;
		this.end = end;
		this.root = root;
		this.errored = errored;
	}

	/**
	 * <p>
	 * Private constructor which is intended to be used primarily by the
	 * {@link #start(String, String)} method.
	 * </p>
	 * 
	 * @param identifier
	 *            The instance identifier.
	 * @param methodName
	 *            The name of the method within which the
	 *            {@link #start(String, String)} method is invoked, or, in the
	 *            case of intercepted or proxied calls, the name of the target
	 *            method.
	 * @param start
	 *            The time at which the related
	 *            {@link LatencyRequirement#getWorkCategory() operation or task}
	 *            {@link LatencyMonitor#taskStarted() started}.
	 */
	private WorkDuration(final WorkDurationId identifier, final String methodName, final long start) {
		this(identifier, methodName, start, -1, false, false);
	}

	/**
	 * <p>
	 * Indicates the start of a {@link LatencyRequirement#getWorkCategory()
	 * category of work or task}. If this instance relates to a specific
	 * {@link LatencyRequirement requirement}, the category would match
	 * {@link LatencyRequirement#getWorkCategory() that specified in the
	 * requirement}.
	 * </p>
	 * 
	 * @param workCategory
	 *            The identifier for the
	 *            {@link LatencyRequirement#getWorkCategory() category} of work
	 *            being started.
	 * 
	 * @param methodName
	 *            The name of the method within which this call is made. In the
	 *            case of proxied and intercepted method calls, this would be
	 *            the name of the target method whose call was intercepted.
	 * 
	 * @return An instance encapsulating the specified information, as well as
	 *         thread and timing information.
	 */
	public static WorkDuration start(final String workCategory, final String methodName) {
		Thread t = Thread.currentThread();
		String threadName = t.getName();
		WorkDurationId identifier = new WorkDurationId(workCategory, threadName);
		return new WorkDuration(identifier, methodName, System.currentTimeMillis());
	}

	/**
	 * <p>
	 * Indicates if the {@link #getMethodName() method} is the root of the call
	 * stack relating to the {@link LatencyRequirement#getWorkCategory() work}
	 * being monitored.
	 * </p>
	 * 
	 * @return <code>True</code> if this duration relates to the root method in
	 *         the call stack representing the
	 *         {@link LatencyRequirement#getWorkCategory() work} being
	 *         monitored, and <code>False</code> otherwise.
	 * 
	 *         {@link #setRoot(boolean)}
	 */
	public boolean isRoot() {
		return root;
	}

	/**
	 * <p>
	 * Specified if the {@link #getMethodName() method}, to which the instance
	 * is associated, is the root of the call stack relating to the
	 * {@link LatencyRequirement#getWorkCategory() work} being monitored.
	 * </p>
	 * 
	 * @param root
	 *            A value of <code>True</code> if this duration was initiated
	 *            from the root method in the call stack representing the
	 *            {@link LatencyRequirement#getWorkCategory() work} being
	 *            monitored, and <code>False</code> otherwise.
	 * 
	 *            {@link #setRoot(boolean)}
	 */
	public void setRoot(final boolean root) {
		this.root = root;
	}

	/**
	 * <p>
	 * Indicates if the {@link LatencyRequirement#getWorkCategory() work} being
	 * monitored ended prematurely with an {@link Throwable exception}.
	 * </p>
	 * 
	 * @return <code>True<code> If an exception occurred within the 
	 * monitored block of the {@link #getMethodName() method},
	 * and <code>False</code> otherwise.
	 * 
	 */
	public boolean isErrored() {
		return errored;
	}

	/**
	 * <p>
	 * Sets the internal flag which indicates that the
	 * {@link LatencyRequirement#getWorkCategory() task}, to which the duration
	 * relates, terminated with an {@link Throwable exception}.
	 * </p>
	 * 
	 * @param errored
	 *            A value of <code>True</code> indicates that an error occurred,
	 *            and <code>False</code> indicates otherwise.
	 */
	public void setErrored(final boolean errored) {
		this.errored = errored;
	}

	/**
	 * <p>
	 * In the case where a {@link LatencyRequirement#getWorkCategory() task}
	 * terminates with an exception, and the call {@link #isErrored()} returns
	 * <code>True</code>, this method should return a reference to the exception
	 * object.
	 * </p>
	 * 
	 * @return The {@link Throwable exception} which caused the
	 *         {@link LatencyRequirement#getWorkCategory() task} being monitored
	 *         to fail.
	 * 
	 * @see #isErrored()
	 */
	public Throwable getError() {
		return error;
	}

	/**
	 * <p>
	 * This method should only be called to specify the details of an error
	 * which caused the termination of the
	 * {@link LatencyRequirement#getWorkCategory() task} being monitored. It
	 * should not be invoked directly by clients, as it is almost certain that
	 * any such invocation will be ignored in the best case, and, in the worst
	 * case, cause indeterminate behaviour in the Epsilon runtime.
	 * </p>
	 * 
	 * @param error
	 *            A reference to the {@link Throwable exception} object.
	 */
	public void setError(final Throwable error) {
		this.error = error;
	}

	/**
	 * <p>
	 * Returns the {@link WorkDurationId identifier/primary-key} of the
	 * instance.
	 * </p>
	 * 
	 * @return The instance's key.
	 */
	public WorkDurationId getIdentifier() {
		return identifier;
	}

	/**
	 * <p>
	 * The name of the method from which the task segment being monitored was
	 * initiated.
	 * </p>
	 * 
	 * @return The name of the method from which
	 *         {@link LatencyMonitor#taskStarted() the duration originates}.
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * <p>
	 * Sets the completion time (end) time of the duration to the current system
	 * time. This is essentially a marker method which specifies the end-time of
	 * the {@link LatencyRequirement#getWorkCategory() task} to which the
	 * duration relates.
	 * </p>
	 * 
	 * @see #getEndTime()
	 * @see #getElapsedTime()
	 */
	public void markFinished() {
		this.end = System.currentTimeMillis();
	}

	/**
	 * <p>
	 * Returns the total elapsed time of the monitored
	 * {@link LatencyRequirement#getWorkCategory() task} in milliseconds. This
	 * is equivalent to the statement {@link #getEndTime() endTime}
	 * <code>-</code>{@link #getStartTime() startTime}.
	 * </p>
	 * 
	 * @return The total milliseconds elapsed between the {@link #getEndTime()
	 *         end} and {@link #getStartTime() start} of the task being
	 *         monitored.
	 */
	public long getElapsedTime() {
		return Math.max(0, this.end - start);
	}

	/**
	 * <p>
	 * Returns the start time of the monitored task. This is equivalent to the
	 * point at which the method {@link LatencyMonitor#taskStarted()} is
	 * invoked.
	 * </p>
	 * 
	 * @return The time (in milliseconds) at which the monitored task started.
	 */
	public long getStartTime() {
		return start;
	}

	/**
	 * <p>
	 * This is equivalent to the point in time at which either the method
	 * {@link LatencyMonitor#taskCompleted()} or
	 * {@link LatencyMonitor#taskErrored(Throwable)} is invoked.
	 * </p>
	 * 
	 * @return The time (in milliseconds) the monitored task was completed.
	 */
	public long getEndTime() {
		return end;
	}

	/**
	 * <p>
	 * Override of the equals method. Two duration instances are considered
	 * equal if all of their fields are equivalent. For the purposes of
	 * comparison <code>null</code> values for the {@link #getError() error}
	 * field are treated as equivalent.
	 * </p>
	 * 
	 * @return <code>True</code> if the candidate object is a duration instance,
	 *         and its fields are equivalent to that of the instance on which
	 *         the method is invoked. Else <code>False</code>.
	 */
	@Override
	public boolean equals(final Object candidate) {
		boolean result = true;

		if (candidate instanceof WorkDuration) {
			WorkDuration duration = (WorkDuration) candidate;

			if (!duration.getIdentifier().equals(identifier)) result = false;

			if (result && !methodName.equals(duration.methodName)) result = false;

			if (result && start != duration.start) result = false;

			if (result && end != duration.end) result = false;

			if (result && root != duration.root) result = false;

			if (result && errored != duration.errored) result = false;
			else {
				if (error == null && duration.error == null) result = true;
				else if (error != null && duration.error != null) result = error.equals(duration.error);
				else result = false;
			}
		} else result = false;

		return result;
	}

	/**
	 * <p>
	 * Returns a short textual representation of the instance. This method
	 * returns {@link LatencyRequirement#getWorkCategory() category} to which
	 * the instance relates, in addition to the information that is returned by
	 * {@link #toStringTimeOnly()}.
	 * </p>
	 * 
	 * @return A {@link String string} representation of the instance,
	 *         summarising the duration in terms of days, hours, minutes,
	 *         seconds and milliseconds; as well as providing the
	 *         {@link LatencyRequirement#getWorkCategory() category} to which
	 *         this instance relates.
	 */
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer("[" + getIdentifier().getWorkCategory());
		result.append(" " + toStringTimeOnly() + "]");
		return result.toString();
	}

	/**
	 * <p>
	 * Returns a full textual representation of the instance.
	 * </p>
	 * 
	 * @return A {@link String string} representation of the instance,
	 *         summarising the duration in terms of days, hours, minutes,
	 *         seconds and milliseconds; but also providing details of the
	 *         method, thread and class to which it relates.
	 */
	public String toStringFull() {
		StringBuffer result = new StringBuffer("[" + getIdentifier().toString() + "] ");
		result.append(getMethodName() + "(" + toStringTimeOnly() + ")");

		result.append(" isRootCall?=" + root);
		result.append(", callErrored?=" + errored);

		return result.toString();
	}

	/**
	 * <p>
	 * Returns a textual representation of the 'time only' portion of this
	 * instance. Thus, this method provides no information on the
	 * {@link LatencyRequirement#getWorkCategory() requirement or category} that
	 * the instance relates to.
	 * </p>
	 * 
	 * @return A {@link String string} representation of the instance, which
	 *         only summarises the duration in terms of days, hours, minutes,
	 *         seconds and milliseconds.
	 */
	public String toStringTimeOnly() {
		StringBuffer result = new StringBuffer();

		// break down to days, hours, minutes and milliseconds
		long remainder, elapsedTimeMillis = getElapsedTime();

		long elapsedTimeDays = elapsedTimeMillis / (TOTAL_MILLISECONDS_IN_A_DAY);
		remainder = elapsedTimeMillis - (elapsedTimeDays * TOTAL_MILLISECONDS_IN_A_DAY);
		if (elapsedTimeDays > 0) result.append(elapsedTimeDays + "d.");

		long elapsedTimeHour = remainder / (TOTAL_MILLISECONDS_IN_HOUR);
		remainder = remainder - (elapsedTimeHour * TOTAL_MILLISECONDS_IN_HOUR);
		if (elapsedTimeHour > 0) result.append(elapsedTimeHour + "h.");

		long elapsedTimeMin = remainder / TOTAL_MILLISECONDS_IN_A_MIN;
		remainder = remainder - (elapsedTimeMin * TOTAL_MILLISECONDS_IN_A_MIN);
		if (elapsedTimeMin > 0) result.append(elapsedTimeMin + "m.");

		long elapsedTimeSec = remainder / TOTAL_MILLISECONDS_IN_A_SEC;
		remainder = remainder - (elapsedTimeSec * TOTAL_MILLISECONDS_IN_A_SEC);
		if (elapsedTimeSec > 0) result.append(elapsedTimeSec + "s.");

		result.append(remainder + "ms");

		return result.toString();
	}
}// end class def