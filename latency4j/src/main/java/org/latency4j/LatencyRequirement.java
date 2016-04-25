package org.latency4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.latency4j.persistence.DefaultWorkDurationPersistenceManager;
import org.latency4j.persistence.WorkDurationPersistenceManager;

/**
 * </p>
 * Abstract representation of a latency requirement. Please note that instances
 * of this class must be {@link #init() initialised} before use.
 * </p>
 */
public abstract class LatencyRequirement {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1104379557397618003L;

	/**
	 * <p>
	 * The category/operation-identifier to which the requirement applies.
	 * </p>
	 */
	private String workCategory;

	/**
	 * <p>
	 * When set to <code>true</code>, {@link LatencyMonitor monitors} of this
	 * requirement will not issue notifications for exceptions.
	 * </p>
	 */
	private boolean ignoreErrors;

	/**
	 * <p>
	 * A list of {@link AlertHandler alert handlers} which are registered
	 * to receive notifications relating to this requirement.
	 * </p>
	 */
	private List<AlertHandler> alertHandlers;

	/**
	 * <p>
	 * The {@link WorkDurationPersistenceManager duration persistence manager}
	 * which is used to persist and read the {@link WorkDuration durations} of
	 * tasks which fall under the scope of this requirement.
	 * </p>
	 * <p>
	 * <b>Note:</b> The {@link DefaultWorkDurationPersistenceManager default
	 * manager} is used, where a {@link WorkDurationPersistenceManager
	 * persistence manager} is not specified. This will, by default, save
	 * {@link WorkDuration durations} to the system's temporary folder as
	 * identified by the java property
	 * {@link Latency4JConstants#JAVA_TMP_FILE_FOLDER}.
	 * </p>
	 */
	private WorkDurationPersistenceManager persistenceManager;

	/**
	 * <p>
	 * Default constructor which initialises internal fields to their default
	 * values.
	 * </p>
	 */
	public LatencyRequirement() {
		alertHandlers = new ArrayList<AlertHandler>();
		setIgnoreErrors(true);
	}

	/**
	 * <p>
	 * Initialises the instance. This involves performing the following actions:
	 * </p>
	 * <p>
	 * <ul>
	 * <li>{@link #assertCompulsoryFieldsSpecified() Verify that all fields have
	 * been assigned valid values}.</li>
	 * <li>Test for an override of the {@link WorkDurationPersistenceManager
	 * persistence manager}. If none is provided, the
	 * {@link DefaultWorkDurationPersistenceManager default persistence manager}
	 * is used. This, by default, writes and reads durations from the system's
	 * temporary folder as identified by the java property
	 * {@link Latency4JConstants#JAVA_TMP_FILE_FOLDER}.</li>
	 * </ul>
	 * </p>
	 */
	public void init() {
		assertCompulsoryFieldsSpecified();
		if (persistenceManager == null) initWithDefaultPersistenceManager();
	}

	/**
	 * <p>
	 * Returns the category of work (identifier of the operations) to which this
	 * requirement is associated.
	 * </p>
	 * 
	 * @return The category with which this requirement is associated.
	 */
	public String getWorkCategory() {
		return workCategory;
	}

	/**
	 * <p>
	 * Sets the category of work (identifier of the operations) to which this
	 * requirement is associated.
	 * </p>
	 * 
	 * @param workCategory
	 *            The category with which this requirement is to be associated.
	 */
	public void setWorkCategory(final String workCategory) {
		this.workCategory = workCategory;
	}

	/**
	 * <p>
	 * Specifies if {@link LatencyMonitor monitors} of this requirement should
	 * report abnormal task terminations brought about by {@link Throwable
	 * exceptions and errors}.
	 * </p>
	 * 
	 * @param ignoreErrors
	 *            <code>True</code> if {@link LatencyMonitor monitors} of this
	 *            requirement should ignore {@link Throwable errors}, and
	 *            <code>False</code> otherwise.
	 * 
	 * @see #isIgnoreErrors()
	 */
	public void setIgnoreErrors(final boolean ignoreErrors) {
		this.ignoreErrors = ignoreErrors;
	}

	/**
	 * <p>
	 * Indicates if monitors of this requirement should report abnormal task
	 * terminations due to {@link Throwable exceptions and errors}. Except
	 * explicitly {@link #setIgnoreErrors(boolean) enabled}, this method will
	 * return <code>True</code>. This is to ensure that
	 * {@link AlertHandler#workCategoryFailed(LatencyRequirement, WorkDuration)
	 * error reporting} only occurs if requested explicitly.
	 * </p>
	 * 
	 * @return <code>True</code> if {@link LatencyMonitor monitors} of this
	 *         requirement should ignore errors, and <code>False</code>
	 *         otherwise.
	 * 
	 * @see #setIgnoreErrors(boolean)
	 */
	public boolean isIgnoreErrors() {
		return ignoreErrors;
	}

	/**
	 * <p>
	 * Provides access to the {@link List list} of {@link AlertHandler
	 * alert handlers} associated to the instance. These are the
	 * {@link AlertHandler handlers} which have been designated to
	 * receive notifications related to this requirement.
	 * </p>
	 * 
	 * @return The {@link Collection collection} of {@link AlertHandler
	 *         alert handlers} responsible for processing alerts related to this
	 *         requirement.
	 */
	public List<AlertHandler> getAlertHandlers() {
		return alertHandlers;
	}

	/**
	 * <p>
	 * Associates a {@link List list} of {@link AlertHandler alert
	 * handlers} to this instance. These are the {@link AlertHandler
	 * handlers} which will process notifications related to this requirement.
	 * </p>
	 * 
	 * @param missedTargetHandlers
	 *            The {@link AlertHandler alert handlers} to be
	 *            associated to this requirement.
	 */
	public void setAlertHandlers(final List<AlertHandler> missedTargetHandlers) {
		this.alertHandlers = missedTargetHandlers;
	}

	/**
	 * <p>
	 * Specifies a {@link WorkDurationPersistenceManager persistence manager}
	 * for persisting and reading {@link WorkDuration durations} of
	 * {@link #getWorkCategory() tasks} falling under the remit of this
	 * requirement. This method can be used to override the use of the
	 * {@link WorkDurationPersistenceManager default (file-based) persistence
	 * manager}.
	 * </p>
	 * 
	 * @param persistenceManager
	 *            The {@link WorkDurationPersistenceManager persistence manager}
	 *            that is to be used to store and read {@link WorkDuration
	 *            durations} of {@link #getWorkCategory() tasks} falling under
	 *            the remit of this requirement.
	 * 
	 * @see #getPersistenceManager()
	 */
	public void setPersistenceManager(final WorkDurationPersistenceManager persistenceManager) {
		this.persistenceManager = persistenceManager;
	}

	/**
	 * <p>
	 * Returns a reference to the {@link WorkDurationPersistenceManager
	 * persistence manager} for this requirement.
	 * </p>
	 * 
	 * @return The {@link WorkDurationPersistenceManager persistence manager}
	 *         that is to be used to store and read {@link WorkDuration
	 *         durations} of tasks falling under the remit of this requirement.
	 * 
	 * @see #setPersistenceManager(WorkDurationPersistenceManager)
	 */
	public WorkDurationPersistenceManager getPersistenceManager() {
		return persistenceManager;
	}

	/**
	 * <p>
	 * Produces a textual representation of the requirement.
	 * </p>
	 * 
	 * @return A {@link String string} representation of this requirement.
	 */
	@Override
	public String toString() {
		return getWorkCategory();
	}

	/**
	 * <p>
	 * Internal method which asserts that all compulsory fields have been
	 * specified and have valid values.
	 * </p>
	 */
	protected void assertCompulsoryFieldsSpecified() {
		if (workCategory == null || workCategory.length() == 0)
			throw new Latency4JException("Error creating latency-requirement. " + "WorkCategory must be specifed!");
	}

	/**
	 * <p>
	 * Initialises this requirement with the
	 * {@link DefaultWorkDurationPersistenceManager default (file system)
	 * persistence manager}.
	 * </p>
	 */
	private void initWithDefaultPersistenceManager() {
		DefaultWorkDurationPersistenceManager defaultPersistenceManager = new DefaultWorkDurationPersistenceManager();
		defaultPersistenceManager.init();
		setPersistenceManager(defaultPersistenceManager);
	}

}