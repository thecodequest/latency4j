package org.latency4j.persistence;

import static org.latency4j.Latency4JConstants.JAVA_TMP_FILE_FOLDER;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.latency4j.Latency4JConstants;
import org.latency4j.Latency4JException;
import org.latency4j.LatencyRequirement;
import org.latency4j.WorkDuration;
import org.latency4j.WorkDurationId;
import org.latency4j.configuration.LatencyRequirementConfiguration;
import org.latency4j.util.FileUtilities;
import org.latency4j.util.LockUtil;
import org.latency4j.util.PrimitiveTypeUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Default {@link WorkDurationPersistenceManager persistence manager} shipped
 * with the Epsilon toolkit. This implementation is file-based in the sense that
 * it reads and writes {@link WorkDuration durations} to a specified location on
 * the filesystem.
 * </p>
 */
public class DefaultWorkDurationPersistenceManager implements WorkDurationPersistenceManager {
	private static final Logger logger = LoggerFactory.getLogger(DefaultWorkDurationPersistenceManager.class);

	/**
	 * <p>
	 * Key ({@value #DATA_DIRECTORY_CONFIG_PARAM}) to the
	 * {@link LatencyRequirementConfiguration#getPersistenceManagerParameters()
	 * configuration parameter} that specifies the directory to which
	 * {@link WorkDuration duration} files should be persisted. Note that if the
	 * specified directory does not currently exist, this manager will attempt
	 * to create it.
	 * </p>
	 * <p>
	 * Where not specified, this parameter defaults to the system temp folder as
	 * identified from the java runtime property
	 * <q>{@link Latency4JConstants#JAVA_TMP_FILE_FOLDER}</q>.
	 * </p>
	 */
	public static final String DATA_DIRECTORY_CONFIG_PARAM = "data.directory";

	/**
	 * <p>
	 * Key ({@value #MAX_HISTORICAL_DATA_FILESIZE_CONFIG_PARAM}) to the
	 * {@link LatencyRequirementConfiguration#getPersistenceManagerParameters()
	 * configuration parameter} which specifies the maximum size of
	 * {@link WorkDuration duration} files.
	 * </p>
	 * <p>
	 * In order to prevent stale statistics on system startup, it is advisable
	 * to delete the duration files which have exceeded, reached or nearing this
	 * size so that the toolkit can replace them with more up-to-date records.
	 * </p>
	 * <p>
	 * The default value of this parameter is
	 * <q>{@link #DEFAULT_MAX_FILESIZE}</q>.
	 * </p>
	 */
	public static final String MAX_HISTORICAL_DATA_FILESIZE_CONFIG_PARAM = "max.file.bytes";

	/**
	 * <p>
	 * The default value of the configuration parameter (
	 * {@value #MAX_HISTORICAL_DATA_FILESIZE_CONFIG_PARAM}kb).
	 * </p>
	 */
	public static final long DEFAULT_MAX_FILESIZE = new Long(4194304); // 4mb

	/**
	 * <p>
	 * The map of configuration parameters with which the instance is
	 * initialised.
	 * </p>
	 */
	private Map<String, String> parameters;

	private long maxFileSize;

	/**
	 * <p>
	 * A map of file {@link DurationFileHandle output-stream handles}, to which
	 * {@link WorkDuration duration} instances are persisted. There is a 1:1
	 * mapping between the {@link LatencyRequirement requirements} to which the
	 * {@link WorkDurationPersistenceManager persistence manager} relates, and
	 * the {@link DurationFileHandle output-stream handles}. Put differently,
	 * there should be an {@link DurationFileHandle output-stream handle} for
	 * each {@link LatencyRequirement requirement} to which the
	 * {@link WorkDurationPersistenceManager persistence-manager} relates.
	 * </p>
	 */
	private volatile Map<String, DurationFileHandle> outputFileHandles;

	private File outputDirectory;

	/**
	 * <p>
	 * Lock which is used to control insertion into the
	 * {@link #outputFileHandles output-stream handles} map.
	 * </p>
	 */
	private final Lock outputStreamsLock;

	/**
	 * <p>
	 * An internal field which specifies whether or not the instance has been
	 * initialised. Requests to this instance will fail if the value of this
	 * field is <code>false</code>.
	 * </p>
	 */
	private final AtomicBoolean initialized;

	/**
	 * <p>
	 * Default constructor.
	 * </p>
	 */
	public DefaultWorkDurationPersistenceManager() {
		initialized = new AtomicBoolean(false);
		maxFileSize = DEFAULT_MAX_FILESIZE;
		outputFileHandles = new HashMap<String, DurationFileHandle>();
		outputStreamsLock = new ReentrantLock();
	}

	@Override
	public synchronized void init() {
		if (this.parameters == null) this.parameters = new HashMap<String, String>();

		String outputFolderName = System.getProperty(JAVA_TMP_FILE_FOLDER);
		if (parameters != null && parameters.containsKey(DATA_DIRECTORY_CONFIG_PARAM))
			outputFolderName = parameters.get(DATA_DIRECTORY_CONFIG_PARAM);

		if (parameters != null && parameters.containsKey(MAX_HISTORICAL_DATA_FILESIZE_CONFIG_PARAM)) {
			String maxFileSizeStr = parameters.get(MAX_HISTORICAL_DATA_FILESIZE_CONFIG_PARAM);
			if (!PrimitiveTypeUtilities.isValidLong(maxFileSizeStr))
				logger.warn("PersistenceManager parameter '{}', is not a valid long! Saw value '{}'",
						MAX_HISTORICAL_DATA_FILESIZE_CONFIG_PARAM, maxFileSizeStr);
			else maxFileSize = PrimitiveTypeUtilities.toLongValue(maxFileSizeStr);
		}

		outputDirectory = prepareOutputDirectoryForWritting(outputFolderName);

		initialized.set(true);
	}

	@Override
	public void setParameters(final Map<String, String> parameters) {
		this.parameters = parameters;
	}

	@Override
	public Map<String, String> getParameters() {
		return parameters;
	}

	@Override
	public synchronized void save(final WorkDuration taskDuration) {
		assertInitialized();
		persistDuration(taskDuration);
	}

	@Override
	public synchronized List<WorkDuration> loadHistoricalData(final String workCategory) {
		assertInitialized();
		List<WorkDuration> result = new ArrayList<WorkDuration>();
		File dataFileHandle;
		try {
			dataFileHandle = new File(this.outputDirectory, DurationFileHandle.getDataFileName(workCategory));

			List<String> linesFromFile = FileUtilities.readLinesFromFile(dataFileHandle,
					DurationFileHandle.DATA_FILE_LINE_DELIMITER);

			WorkDuration duration;
			int lineNumber = 0;
			for (String durationLine : linesFromFile) {
				duration = parseDurationFromLine(durationLine, dataFileHandle, lineNumber);

				lineNumber++;
				result.add(duration);
			}
		} catch (Throwable error) {
			logger.warn("No historical data file for latency-requirement: " + workCategory + ". " + error.getMessage());
		}

		return result;
	}

	public File getOutputDirectory() {
		return outputDirectory;
	}

	/**
	 * <p>
	 * Saves the specified {@link WorkDuration duration} to a data-file
	 * corresponding to the {@link WorkDurationId#getWorkCategory() category} to
	 * which it relates and as determined by the method
	 * {@link #getDataFileName(String)}. The
	 * {@link WorkDurationId#getWorkCategory() category} is obtained from the
	 * {@link WorkDuration#getIdentifier() identifier} of the specified
	 * {@link WorkDuration duration}.
	 * </p>
	 * 
	 * @param duration
	 *            The data to save.
	 */
	private void persistDuration(final WorkDuration duration) {
		try {
			DurationFileHandle dataFileHandle = getFileHandle(duration.getIdentifier().getWorkCategory());
			dataFileHandle.saveDuration(duration);
		} catch (Throwable error) {
			logger.warn("Error persisting task-duration '" + duration.getIdentifier() + "': " + error.getMessage());
		}
	}

	private DurationFileHandle getFileHandle(final String category) throws IOException {
		DurationFileHandle result = outputFileHandles.get(category);
		if (result == null) {
			try {
				LockUtil.acquireLock(this.outputStreamsLock);
				result = this.outputFileHandles.get(category);
				if (result == null) {
					result = new DurationFileHandle(outputDirectory, category, maxFileSize);
					outputFileHandles.put(category, result);
				}
			}
			finally {
				LockUtil.releaseLock(outputStreamsLock);
			}
		}
		return result;
	}

	/**
	 * <p>
	 * Parses a {@link WorkDuration duration} record from the specified line
	 * taken from an input data-file. This method utilises the delegate method
	 * {@link #createDuration(String, String, String, long, long, boolean, boolean)}
	 * to create the object representation of the record.
	 * </p>
	 * 
	 * @param line
	 *            The line from which the data is to be parsed.
	 * @param dataFile
	 *            The file from which the record was read. This is specified for
	 *            error reporting only.
	 * @param lineNumber
	 *            The line-number in the input file from which the record was
	 *            read. This parameter is also specified for error reporting
	 *            purposes only.
	 * 
	 * @return An object/parsed representation of the input line.
	 */
	private WorkDuration parseDurationFromLine(final String line, final File dataFile, final int lineNumber) {
		WorkDuration result = null;

		StringTokenizer tokenizer = new StringTokenizer(line, DurationFileHandle.DATA_DELIMETER, false);

		String workCategory = null, threadId = null, methodName = null, startTimeText = null, endTimeText = null,
				rootText = null, erroredText = null;

		long start, end;
		boolean root, errored;

		if (tokenizer.hasMoreElements()) workCategory = tokenizer.nextToken();
		else {
			logger.warn("Error reading data file from directory '" + dataFile.getAbsolutePath()
					+ "'. Missing category information. Line# " + lineNumber);
			return null;
		}

		if (tokenizer.hasMoreElements()) threadId = tokenizer.nextToken();
		else {
			logger.warn("Error reading data file from directory '" + dataFile.getAbsolutePath()
					+ "'. Missing threadId . Line# " + lineNumber);
			return null;
		}

		if (tokenizer.hasMoreElements()) methodName = tokenizer.nextToken();
		else {
			logger.warn("Error reading data file from directory '" + dataFile.getAbsolutePath()
					+ "'. Missing method-name. Line# " + lineNumber);
			return null;
		}

		if (tokenizer.hasMoreElements()) startTimeText = tokenizer.nextToken();
		else throw new Latency4JException("Error reading data file from directory '" + dataFile.getAbsolutePath()
				+ "'. Missing start-time. Line# " + lineNumber);

		if (tokenizer.hasMoreElements()) endTimeText = tokenizer.nextToken();
		else throw new Latency4JException("Error reading data file from directory '" + dataFile.getAbsolutePath()
				+ "'. Missing end-time. Line# " + lineNumber);

		if (tokenizer.hasMoreElements()) rootText = tokenizer.nextToken();
		else throw new Latency4JException("Error reading data file from directory '" + dataFile.getAbsolutePath()
				+ "'. Missing thread-stack root marker. Line# " + lineNumber);

		if (tokenizer.hasMoreElements()) erroredText = tokenizer.nextToken();
		else throw new Latency4JException("Error reading data file from directory '" + dataFile.getAbsolutePath()
				+ "'. Missing termination-code. Line# " + lineNumber);

		if (!PrimitiveTypeUtilities.isValidLong(startTimeText) || !PrimitiveTypeUtilities.isValidLong(endTimeText))
			throw new Latency4JException("Failure to read line due to invalid start/end time formats. "
					+ "Error reading historical data from: " + dataFile.getAbsolutePath() + ". Line#" + lineNumber);

		if (!PrimitiveTypeUtilities.isValidBoolean(rootText) || !PrimitiveTypeUtilities.isValidBoolean(erroredText))
			throw new Latency4JException("Failure to read line due to invalid boolean fields. "
					+ "Error reading historical data from: " + dataFile.getAbsolutePath() + ". Line#" + lineNumber);

		{
			start = PrimitiveTypeUtilities.toLongValue(startTimeText);
			end = PrimitiveTypeUtilities.toLongValue(endTimeText);
			root = Boolean.parseBoolean(rootText);
			errored = Boolean.parseBoolean(erroredText);

			result = createDuration(workCategory, threadId, methodName, start, end, root, errored);
		}

		return result;
	}

	/**
	 * <p>
	 * Delegate method which encapsulates the logic for assembling a
	 * {@link WorkDuration duration} instance from its primitive constituents.
	 * This method acts as a utility delegated for the method
	 * {@link #parseDurationFromLine(String, File, int)}
	 * </p>
	 * 
	 * @param workContext
	 *            The {@link WorkDurationId#getWorkCategory() context or
	 *            category} to which the duration belongs.
	 * 
	 * @param threadId
	 *            The {@link WorkDurationId#getThreadId() id of the thread} in
	 *            which the {@link WorkDuration duration} was generated.
	 * 
	 * @param methodName
	 *            The {@link WorkDuration#getMethodName() name of the method} to
	 *            which the duration relates.
	 * 
	 * @param start
	 *            The {@link WorkDuration#getStartTime() start time} of the task
	 *            segment to which this record relates.
	 * 
	 * @param end
	 *            The {@link WorkDuration#getEndTime() end time} of the task to
	 *            which this record relates.
	 * 
	 * @param root
	 *            Indicates if the record is for the
	 *            {@link WorkDuration#isRoot() root} of the task call stack.
	 * 
	 * @param errored
	 *            Indicates if the record relates to a
	 *            {@link WorkDuration#isErrored() failed} task.
	 * 
	 * @return Returns the {@link WorkDuration duration} instance created from
	 *         the specified field values.
	 */
	private WorkDuration createDuration(final String workContext, final String threadId, final String methodName,
			final long start, final long end, final boolean root, final boolean errored) {
		WorkDurationId identifier = new WorkDurationId(workContext, threadId);
		return new WorkDuration(identifier, methodName, start, end, root, errored);
	}

	/**
	 * <p>
	 * Ensures that the directory to which this manager writes has been created
	 * and is writable. Note that where the directory does not already exist,
	 * this method will attempt to create it.
	 * </p>
	 * 
	 * @param dataDirectory
	 *            The directory to which this manager has been initialised.
	 * @return The directory to which this manager should write its output.
	 */
	private File prepareOutputDirectoryForWritting(final String dataDirectory) {
		File resullt = new File(dataDirectory);
		if (!resullt.exists() && !resullt.isDirectory()) {
			if (!resullt.mkdirs()) throw new Latency4JException("Unable to open data output directory:" + dataDirectory);
		}
		return resullt;
	}

	/**
	 * Internal assertion which verifies that this manager has been initialised
	 * and is ready for use.
	 * 
	 * @throws Latency4JException
	 *             If the manager has not been initialised.
	 */
	private void assertInitialized() {
		if (!initialized.get()) throw new Latency4JException("Epsilon PersistenceManager not initialised.");
	}
}