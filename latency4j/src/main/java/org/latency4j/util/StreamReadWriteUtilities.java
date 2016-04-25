package org.latency4j.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.latency4j.Latency4JException;

/**
 * <p>
 * A utility class for performing common stream-based I/O operations.
 * </p>
 */
public final class StreamReadWriteUtilities {
	
	private static final int DEFAULT_BLOCK_SIZE = 512;

	/**
	 * <p>
	 * Private default constructor to prevent accidental initialisation.
	 * </p>
	 */
	private StreamReadWriteUtilities() {}

	/**
	 * <p>
	 * Reads the contents of the specified {@link InputStream} into a list of
	 * strings, where each string represents a line taken from the stream. The
	 * contents of the stream are tokenized into lines using the specified
	 * delimiter.
	 * </p>
	 * 
	 * @param inputStream
	 *            The {@link InputStream} to be read and tokenized.
	 * @param delimiter
	 *            The end-of-line character to be applied to the
	 *            {@link InputStream}.
	 * @param size
	 *            The maximum quantity of data to be read from the
	 *            {@link InputStream}.
	 * 
	 * @return A list of strings, where each string represents a line in the
	 *         {@link InputStream}.
	 * 
	 * @throws ObixException
	 *             If an error occurred reading the {@link InputStream}.
	 * 
	 * @see FileUtilities#readFileContentsAsBytes(File)
	 */
	public static List<String> readLinesFromStream(final InputStream inputStream, final String delimiter,
			final int size) {
		List<String> results = new ArrayList<String>();

		StringTokenizer lineTokenizer;
		String streamContents;

		byte[] streamContentsAsBytes = readStreamContentsAsBytes(inputStream, size);

		streamContents = new String(streamContentsAsBytes);
		lineTokenizer = new StringTokenizer(streamContents, delimiter);

		while (lineTokenizer.hasMoreElements())
			results.add((String) lineTokenizer.nextElement());

		return results;
	}

	/**
	 * <p>
	 * Reads the contents of the specified {@link InputStream} into a byte
	 * array. This method is similar to
	 * {@link #readStreamContentsAsBytes(InputStream, int)}, except that it does
	 * not allow the caller to specify the maximum number of bytes to be read.
	 * In essence, this method will attempt to read the entire contents of the
	 * stream.
	 * </p>
	 * 
	 * @param inputStream
	 *            The {@link InputStream} to read.
	 * @return A byte array containing the data read from the specified stream.
	 * @throws ObixException
	 *             If an error occurs while reading the contents of the
	 *             specified stream.
	 * 
	 * @see #readStreamContentsAsBytes(InputStream, int)
	 * @see #readStreamContentsAsBytes(InputStream, boolean)
	 */
	public static byte[] readStreamContentsAsBytes(final InputStream inputStream) {
		return readStreamContentsAsBytes(inputStream, true);
	}

	/**
	 * <p>
	 * Performs the same function as
	 * {@link #readStreamContentsAsBytes(InputStream)}, but allows the caller to
	 * indicate if the stream should be closed after its contents have been
	 * read.
	 * </p>
	 * 
	 * @param inputStream
	 *            The {@link InputStream} to read.
	 * @param closeStream
	 *            A value of <code>true</code> will cause the stream to be
	 *            closed after it is read, and <code>false</code> will achieve
	 *            the opposite.
	 * @return A byte array containing the data read from the specified stream.
	 * @throws ObixException
	 *             If an error occurs while reading the contents of the
	 *             specified stream.
	 * 
	 * @see #readStreamContentsAsBytes(InputStream)
	 * @see #readStreamContentsAsBytes(InputStream, int)
	 */
	public static byte[] readStreamContentsAsBytes(final InputStream inputStream, final boolean closeStream) {
		byte[] result;

		BufferedInputStream bufferedStream = null;

		try {
			bufferedStream = bufferStream(inputStream);

			ArrayList<Byte> readResults = new ArrayList<Byte>();
			int streamSizeEstimate;
			byte[] readBuffer;
			int numberOfBytesRead;

			do {
				streamSizeEstimate = bufferedStream.available();

				if (streamSizeEstimate == 0) streamSizeEstimate = DEFAULT_BLOCK_SIZE;

				readBuffer = new byte[streamSizeEstimate];

				numberOfBytesRead = bufferedStream.read(readBuffer);

				if (numberOfBytesRead != -1) {
					for (int i = 0; i < numberOfBytesRead; i++)
						readResults.add(readBuffer[i]);
				}

			}
			while (numberOfBytesRead != -1);

			result = new byte[readResults.size()];
			for (int i = 0; i < readResults.size(); i++)
				result[i] = readResults.get(i);

		} catch (IOException exce) {
			throw new Latency4JException(exce);
		}
		finally {
			if (closeStream) IOResourceCloser.close(bufferedStream);
		}

		return result;
	}

	/**
	 * <p>
	 * Reads the contents, up to the specified number of bytes, of an
	 * {@link InputStream} and returns the result as a byte array.
	 * </p>
	 * 
	 * <p>
	 * Unlike {@link #readStreamContentsAsBytes(InputStream)} this method allows
	 * the caller to specify the maximum number of bytes to be read from the
	 * stream. As such, the quantity of data returned by this method never
	 * exceeds the maximum read size.
	 * </p>
	 * 
	 * @param inputStream
	 *            The stream from which the data is to be read
	 * @param resourceSize
	 *            The amount of data to be read from the stream.
	 * @return A byte array containing the data read from the
	 *         {@link InputStream}.
	 * @throws ObixException
	 *             If an error occurs while reading data from the specified
	 *             {@link InputStream}
	 * 
	 * @see #readStreamContentsAsBytes(InputStream)
	 */
	public static byte[] readStreamContentsAsBytes(final InputStream inputStream, final int resourceSize) {
		return readStreamContentsAsBytes(inputStream, resourceSize, true);
	}

	/**
	 * <p>
	 * Identical to {@link #readStreamContentsAsBytes(InputStream, int)} other
	 * than it allows the caller to indicate if the stream should be closed
	 * after its contents have been read.
	 * </p>
	 * 
	 * @param inputStream
	 *            The stream from which the data is to be read.
	 * @param resourceSize
	 *            The amount of data to be read from the stream.
	 * @param closeStream
	 *            A value of <code>true</code> will cause the stream to be
	 *            closed after it is read, and <code>false</code> will achieve
	 *            the opposite.
	 * @return A byte array containing the data read from the
	 *         {@link InputStream}.
	 * @throws ObixException
	 *             If an error occurs while reading data from the specified
	 *             {@link InputStream}.
	 * 
	 * @see #readStreamContentsAsBytes(InputStream, int)
	 * @see #readStreamContentsAsBytes(InputStream)
	 * @see #readStreamContentsAsBytes(InputStream, boolean)
	 * 
	 */
	public static byte[] readStreamContentsAsBytes(final InputStream inputStream, final int resourceSize,
			final boolean closeStream) {
		byte[] result;

		BufferedInputStream bufferedStream = null;
		try {
			bufferedStream = bufferStream(inputStream);

			byte[] readBuffer = new byte[resourceSize];
			int readCount = 0;
			int totalBytesRead = 0;

			while (readCount != -1 && totalBytesRead < resourceSize) {
				readCount = bufferedStream.read(readBuffer);
				if (readCount > 0) totalBytesRead += readCount;
			}

			int totalBytesToReturn = totalBytesRead < resourceSize ? totalBytesRead : resourceSize;

			// copy read buffer into the results array
			result = new byte[totalBytesToReturn];
			System.arraycopy(readBuffer, 0, result, 0, totalBytesToReturn);
		} catch (IOException exce) {
			throw new Latency4JException(exce);
		}
		finally {
			if (closeStream) IOResourceCloser.close(bufferedStream);
		}
		return result;
	}

	/**
	 * <p>
	 * Writes a data array to an output stream. <b>Note </b>that this method
	 * also closes and flushes the stream after writing to it.
	 * </p>
	 * 
	 * @param outputStream
	 *            The destination {@link OutputStream}.
	 * @param data
	 *            The array to be written to the {@link OutputStream}.
	 * @throws ObixException
	 *             If an error occurs performing I/O operations against the
	 *             stream.
	 * 
	 * @see FileUtilities#writeBytesToFile(File, byte[])
	 * @see FileUtilities#writeBytesToFile(File, byte[], boolean)
	 * @see FileUtilities#writeLinesToFile(File, List, String)
	 * @see IOResourceCloser#close(OutputStream)
	 */
	public static void writeBytesToStream(final OutputStream outputStream, final byte[] data) {
		writeBytesToStream(outputStream, data, true);
	}

	/**
	 * <p>
	 * Performs the same function as
	 * {@link #writeBytesToStream(OutputStream, byte[])} but allows the caller
	 * to control if the stream is closed after it is written to.
	 * </p>
	 * 
	 * @param outputStream
	 *            The destination {@link OutputStream}.
	 * @param data
	 *            The array to be written to the {@link OutputStream}.
	 * @param closeStream
	 *            A value of <code>true</code> will cause the stream to be
	 *            closed after the data is written to it, and <code>false</code>
	 *            will achieve the opposite.
	 * @throws ObixException
	 *             If an error occurs performing I/O operations against the
	 *             stream.
	 */
	public static void writeBytesToStream(final OutputStream outputStream, final byte[] data,
			final boolean closeStream) {
		BufferedOutputStream bufferedStream = null;
		try {

			if (outputStream instanceof BufferedOutputStream) bufferedStream = (BufferedOutputStream) outputStream;
			else bufferedStream = new BufferedOutputStream(outputStream);

			bufferedStream.write(data, 0, data.length);
		} catch (IOException exce) {
			throw new Latency4JException(exce);
		}
		finally {
			if (closeStream) IOResourceCloser.close(bufferedStream);
		}
	}

	/**
	 * <p>
	 * Wraps the given stream with a BufferedInputStream if it is not already a
	 * BufferedInputStream.
	 * </p>
	 * 
	 * @param inputStream
	 *            The stream to be wrapped with a BufferedInputStream.
	 * 
	 * @return The given stream wrapped with a BufferedInputStream if the stream
	 *         is not already an instance of that type. If the stream is a
	 *         BufferedInputStream, this method just returns a reference to it.
	 */
	public static BufferedInputStream bufferStream(final InputStream inputStream) {
		BufferedInputStream result;
		if (inputStream instanceof BufferedInputStream) result = (BufferedInputStream) inputStream;
		else result = new BufferedInputStream(inputStream);
		return result;
	}

}