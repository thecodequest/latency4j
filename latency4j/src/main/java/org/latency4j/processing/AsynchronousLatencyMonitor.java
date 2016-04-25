package org.latency4j.processing;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.latency4j.LatencyMonitor;
import org.latency4j.LatencyRequirement;
import org.latency4j.WorkDuration;
import org.latency4j.WorkDurationId;

/**
 * <p>
 * The default {@link LatencyMonitor latency monitor} shipped with the Epsilon
 * toolkit. As its name implies, it processes all {@link WorkDuration durations}
 * , generated from monitored {@link LatencyRequirement requirements}, in an
 * asynchronous fashion. Put differently, processing takes place outside the
 * thread of execution. As such, it does not inadvertently skew the latency
 * figures for monitored {@link LatencyRequirement#getWorkCategory()
 * operations/tasks}.
 * </p>
 */
public class AsynchronousLatencyMonitor implements LatencyMonitor {

	/**
	 * <p>
	 * Internal constant for method or class names that cannot be determined.
	 * </p>
	 */
	private static final String MONITOR_UNDETERMINED_METHOD_OR_CLASS_NAME = "UNDETERMINED";

	/**
	 * <p>
	 * {@link Map Map} which is used to store {@link WorkDuration durations}.
	 * Each entry consists of a {@link WorkDurationId durationId} and a
	 * {@link Stack}, where the entries in the {@link Stack stack} relate to the
	 * method calls falling within the monitored
	 * {@link LatencyRequirement#getWorkCategory() category} for a given thread
	 * of execution.
	 * </p>
	 */
	private final Map<WorkDurationId, Stack<WorkDuration>> durationsMap;

	/**
	 * <p>
	 * The {@link LatencyRequirement requirement} being monitored.
	 * </p>
	 */
	private final LatencyRequirement latencyRequirement;

	/**
	 * <p>
	 * Reference to {@link LatencyProcessor latency processor}. The processor is
	 * {@link #AsynchronousLatencyMonitor(LatencyRequirement, LatencyProcessor)
	 * provided} at construction by the {@link AsynchronousLatencyMonitorFactory
	 * monitor factory}.
	 * </p>
	 */
	private final LatencyProcessor asyncProcessor;

	/**
	 * <p>
	 * Constructs an instance to monitor the specified {@link LatencyRequirement
	 * requirement}.
	 * </p>
	 * 
	 * @param requirement
	 *            The {@link LatencyRequirement requirement} to monitor.
	 * @param asyncProcessor
	 *            The {@link LatencyProcessor processor} which will be used to
	 *            process {@link WorkDuration durations} relating to the
	 *            monitored requirement.
	 */
	public AsynchronousLatencyMonitor(final LatencyRequirement requirement, final LatencyProcessor asyncProcessor) {
		this.durationsMap = new HashMap<WorkDurationId, Stack<WorkDuration>>();
		this.latencyRequirement = requirement;
		this.asyncProcessor = asyncProcessor;
	}

	@Override
	public WorkDuration taskCompleted() {
		return processTaskCompletion(false, null);
	}

	@Override
	public WorkDuration taskErrored(final Throwable cause) {
		WorkDuration result = processTaskCompletion(true, cause);
		return result;
	}

	@Override
	public void taskStarted() {
		String methodName = resolveTargetMethodName();
		createTaskEntry(methodName);
	}

	/**
	 * <p>
	 * Creates an entry in the internal {@link WorkDurationId durationId}->
	 * {@link WorkDuration} duration {@link Map map} for the current execution
	 * point i.e. the current thread and method.
	 * </p>
	 * 
	 * @param methodName
	 *            The name of the method for which the entry is to be created.
	 */
	public void createTaskEntry(final String methodName) {
		WorkDuration taskDuration = WorkDuration.start(latencyRequirement.getWorkCategory(), methodName);
		WorkDurationId durationId = taskDuration.getIdentifier();

		Stack<WorkDuration> durations = durationsMap.get(durationId);
		if (durations == null) {
			durations = new Stack<WorkDuration>();
			durationsMap.put(durationId, durations);
			taskDuration.setRoot(true);
		}
		durations.push(taskDuration);
	}

	/**
	 * <p>
	 * Delegate method which processes a completed task. It creates a
	 * {@link WorkDuration duration} instance to hold the information about the
	 * current execution point, and
	 * {@link LatencyProcessor#processTaskCompletion(LatencyRequirement, WorkDuration)
	 * places} this on the processing queue of the {@link LatencyProcessor
	 * latency processor}.
	 * </p>
	 * 
	 * @param errored
	 *            <code>True</code> if an exception occurred at the current
	 *            execution point and <code>False</code> otherwise.
	 * 
	 * @param cause
	 *            Where the parameter <code>errored</code> is set to true, this
	 *            parameter should contain a reference to the exception that
	 *            occurred during execution.
	 * 
	 * @return A {@link WorkDuration duration} instance generated for the
	 *         execution point.
	 */
	private WorkDuration processTaskCompletion(final boolean errored, final Throwable cause) {
		WorkDuration result = null;

		Thread currentThread = Thread.currentThread();
		WorkDurationId taskDurationKey = new WorkDurationId(latencyRequirement.getWorkCategory(),
				currentThread.getName());

		Stack<WorkDuration> durations;
		if (durationsMap.containsKey(taskDurationKey)) {
			durations = durationsMap.get(taskDurationKey);
			result = durations.pop();
			result.markFinished();
			result.setError(cause);
			result.setErrored(errored);

			asyncProcessor.processTaskCompletion(latencyRequirement, result);

			// if no more entries, then remove stack
			if (durations.isEmpty()) durationsMap.remove(taskDurationKey);
		}
		return result;
	}

	/**
	 * <p>
	 * Resolves the name of the currently executing method in the
	 * {@link LatencyRequirement#getWorkCategory() category} thread. This is
	 * achieved by discounting all calls related to the monitor instance from
	 * the call stack. These are assumed to be the last two elements in the
	 * thread's call stack.
	 * </p>
	 * 
	 * @return The name of the currently executing method in the
	 *         {@link LatencyRequirement#getWorkCategory() work/task} thread.
	 */
	private String resolveTargetMethodName() {
		String result;
		Thread t = Thread.currentThread();
		StackTraceElement[] stackTraceElements = t.getStackTrace();

		int callStackSize = stackTraceElements.length;
		StackTraceElement targetMethodElement;

		// getStackTrace, taskStarted, and resolveTargetMethodName
		int NUMBER_OF_METHOD_CALLS_TO_DISCOUNT = 3;

		// finds the first method that is not from this class
		String methodName = MONITOR_UNDETERMINED_METHOD_OR_CLASS_NAME;
		String className = MONITOR_UNDETERMINED_METHOD_OR_CLASS_NAME;
		for (int i = callStackSize - 1; i >= NUMBER_OF_METHOD_CALLS_TO_DISCOUNT; i--) {
			targetMethodElement = stackTraceElements[i];
			methodName = targetMethodElement.getMethodName();
			className = targetMethodElement.getClassName();
		}

		result = className + "." + methodName;
		return result;
	}
}// end class def
