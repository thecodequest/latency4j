package org.latency4j.persistence;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.latency4j.LatencyRequirement;
import org.latency4j.WorkDuration;
import org.latency4j.util.IOResourceCloser;

public class DurationFileHandle {

	/**
	 * <p>
	 * Extension
	 * <q>{@value #HISTORICAL_DATA_FILE_EXTENSION}</q> for {@link WorkDuration
	 * duration} files.
	 * </p>
	 */
	public static final String HISTORICAL_DATA_FILE_EXTENSION = ".eps";

	/**
	 * <p>
	 * Internal line/record delimiter for {@link WorkDuration duration} records.
	 * </p>
	 */
	protected static final String DATA_FILE_LINE_DELIMITER = "\n";

	/**
	 * <p>
	 * Field delimiter for {@link WorkDuration duration} fields. This is the
	 * delimiter that is used to separate different fields of a given
	 * {@link WorkDuration duration} record.
	 * </p>
	 */
	protected static final String DATA_DELIMETER = ",";

	/**
	 * <p>
	 * The maximum permissible size for {@link WorkDuration duration} data
	 * files. The data files are rolled over once they exceed this size.
	 * </p>
	 */
	private final long maxFileSize;

	private volatile boolean notFull;

	private final File outputFile;
	private final OutputStream outputStream;

	protected DurationFileHandle(final File outputDirectory, final String category, final long maxFileSize)
			throws IOException {
		this.notFull = true;
		this.outputFile = new File(outputDirectory, getDataFileName(category));
		this.outputStream = new BufferedOutputStream(new FileOutputStream(this.outputFile));
		this.maxFileSize = maxFileSize;
	}

	protected void saveDuration(final WorkDuration duration) throws IOException {
		if (this.notFull) {
			if (this.isFileExceededAllowedSize()) {
				this.notFull = false;
				IOResourceCloser.close(this.outputStream);
			} else {
				StringBuffer outputBuffer = new StringBuffer();

				outputBuffer.append(escapeDilimiter(duration.getIdentifier().getWorkCategory()) + DATA_DELIMETER);
				outputBuffer.append(escapeDilimiter(duration.getIdentifier().getThreadId()) + DATA_DELIMETER);
				outputBuffer.append(duration.getMethodName() + DATA_DELIMETER);
				outputBuffer.append(duration.getStartTime() + DATA_DELIMETER);
				outputBuffer.append(duration.getEndTime() + DATA_DELIMETER);
				outputBuffer.append(duration.isRoot() + DATA_DELIMETER);
				outputBuffer.append(duration.isErrored() + DATA_FILE_LINE_DELIMITER);

				this.outputStream.write(outputBuffer.toString().getBytes());
				this.outputStream.flush();
			}
		}
	}

	/**
	 * <p>
	 * Resolves the name of the file to which data for the specified
	 * {@link LatencyRequirement#getWorkCategory() category} is to be saved. The
	 * file is generally a concatenation of the
	 * {@link LatencyRequirement#getWorkCategory() category} and the Epsilon
	 * file extension
	 * <q>{@value #HISTORICAL_DATA_FILE_EXTENSION}</q>.
	 * </p>
	 * 
	 * @param category
	 *            The {@link LatencyRequirement#getWorkCategory() category} to
	 *            which the output data file relates.
	 * @return The name of the file to which data for the specified category is
	 *         to be saved.
	 */
	public static String getDataFileName(final String category) {
		return category + HISTORICAL_DATA_FILE_EXTENSION;
	}

	@Override
	protected void finalize() throws Throwable {
		IOResourceCloser.close(this.outputStream);
		super.finalize();
	}

	/**
	 * <p>
	 * Processes all text cells/fields to be written to file, so as to ensure
	 * that any occurrences of the reserved field delimited
	 * <q>{@value #DATA_DELIMETER}</q> are escaped.
	 * </p>
	 * 
	 * @param arg
	 *            The string in which occurrences of the delimiter are to be
	 *            escaped.
	 * @return The parameter string, but with all occurrences of the delimiter
	 *         <q>{@value #DATA_DELIMETER}</q> escaped.
	 */
	private String escapeDilimiter(final String arg) {
		return arg.replaceAll(DATA_DELIMETER, "\\" + DATA_DELIMETER);
	}

	/**
	 * <p>
	 * Indicates if the specified file has exceeded the maximum permissible size
	 * as determined from the {@link #setParameters(Map) configuration
	 * parameter}
	 * <q>{@value #MAX_HISTORICAL_DATA_FILESIZE_CONFIG_PARAM}</q>.
	 * </p>
	 * 
	 * @param dataFile
	 *            The file whose size is to be examined.
	 * 
	 * @return <code>True</code> if the file has exceeded the maximum specified
	 *         size, and <code>false</code> otherwise.
	 */
	private boolean isFileExceededAllowedSize() {
		return outputFile.length() > getMaxFileSize();
	}

	/**
	 * <p>
	 * Returns the maximum permissible size for {@link WorkDuration duration}
	 * data files. The data files are rolled over once they exceed this size.
	 * </p>
	 * 
	 * @return The maximum permissible size of {@link WorkDuration duration}
	 *         data files.
	 */
	private long getMaxFileSize() {
		return maxFileSize;
	}
}
