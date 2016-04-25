package org.latency4j;

import java.io.Serializable;

/**
 * <p>
 * A {@link WorkDuration duration} primary key.
 * </p>
 */
public class WorkDurationId implements Serializable {
	private static final long serialVersionUID = -4887858866762134439L;

	/**
	 * <p>
	 * The {@link LatencyRequirement#getWorkCategory() category of the task} to
	 * which the {@link WorkDuration duration} instance relates. Put
	 * differently, this is the {@link LatencyRequirement#getWorkCategory()
	 * requirement category} which covers the
	 * {@link LatencyRequirement#getWorkCategory() operation or task} for which
	 * the {@link WorkDuration duration} instance was generated.
	 * </p>
	 */
	private String workCategory;

	/**
	 * <p>
	 * The id/name of the {@link Thread thread} in which {@link WorkDuration
	 * duration} is generated. This is {@link Thread thread} in which the
	 * {@link LatencyRequirement#getWorkCategory() monitored operation} is
	 * performed or executed, thus leading to a {@link WorkDuration duration}.
	 * </p>
	 */
	private String threadId;

	/**
	 * Private default constructor. Provided for the convenience of API that
	 * rely on {@link Serializable serialization}.
	 */
	private WorkDurationId() {}

	/**
	 * <p>
	 * Constructor.
	 * </p>
	 * 
	 * @param workCategory
	 *            The {@link LatencyRequirement#getWorkCategory() category/id of
	 *            the operation/task} to which the {@link WorkDuration duration}
	 *            relates.
	 * @param threadId
	 *            The identifier of the {@link Thread thread} from which the
	 *            instance results.
	 */
	public WorkDurationId(final String workCategory, final String threadId) {
		this();
		setWorkCategory(workCategory);
		setThreadId(threadId);
	}

	/**
	 * <p>
	 * Returns the identifier of the {@link Thread thread} of execution within
	 * which the monitored task is was performed.
	 * </p>
	 * 
	 * @return The id of the thread within which the monitored task was
	 *         initiated.
	 */
	public String getThreadId() {
		return threadId;
	}

	/**
	 * <p>
	 * Sets the identifier of the {@link Thread thread} of execution within
	 * which the monitored task was performed.
	 * </p>
	 * 
	 * @param threadId
	 *            The id of the {@link Thread thread} of execution to which the
	 *            {@link WorkDuration duration} instance is related.
	 */
	private void setThreadId(final String threadId) {
		this.threadId = threadId;
	}

	/**
	 * <p>
	 * Returns the {@link #workCategory category of the task/operation} being
	 * monitored, and to which the {@link WorkDuration duration} instance is
	 * related.
	 * </p>
	 * 
	 * @return The {@link #workCategory identifier of the task/operation} to
	 *         which the {@link WorkDuration duration} instance relates.
	 */
	public String getWorkCategory() {
		return workCategory;
	}

	/**
	 * <p>
	 * Sets the {@link #workCategory category}.
	 * </p>
	 * 
	 * @param workCategory
	 *            The value to which the {@link #workCategory category} is to be
	 *            initialised.
	 */
	private void setWorkCategory(final String workCategory) {
		this.workCategory = workCategory;
	}

	@Override
	public boolean equals(final Object obj) {
		boolean result = false;
		if (obj instanceof WorkDurationId) {
			WorkDurationId candidate = (WorkDurationId) obj;
			if (workCategory.equals(candidate.workCategory) && threadId.equals(candidate.threadId)) result = true;
		}
		return result;
	}

	@Override
	public int hashCode() {
		String keyString = workCategory + "." + threadId;
		return keyString.hashCode();
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("workCategory-" + getWorkCategory());
		result.append(", threadId-" + getThreadId());
		return result.toString();
	}
}