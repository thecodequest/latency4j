package org.latency4j.util;

import static org.latency4j.util.ObixNetAPIConstants.APPLICATION_OCTET_STREAM_MIME_CONTENT_TYPE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

/**
 * <p>
 * An implementation of {@link DataSource} backed by a byte array. This
 * implementation simplifies the manipulation of email component data by
 * reducing it to its binary form. A good example of where this is convenient is
 * in the handling of email attachment data.
 * 
 * @see ObixEmailSender#addAttachment(java.io.File)
 * @see ObixEmailSender#addAttachment(String, byte[])
 * @see ObixEmailSender#addAttachment(String, String)
 *      </p>
 */
class EmailDataSource implements DataSource {
	/**
	 * <p>
	 * The default content-type assumed for instances of this {@link DataSource}
	 * where non is specified. It has a value of
	 * {@link ObixNetAPIConstants#APPLICATION_OCTET_STREAM_MIME_CONTENT_TYPE}.
	 * </p>
	 */
	private static final String DEFAULT_CONTENT_TYPE = APPLICATION_OCTET_STREAM_MIME_CONTENT_TYPE;

	/**
	 * <p>
	 * The internal byte array which encapsulates the message component's data.
	 * </p>
	 */
	private byte[] data;

	/**
	 * <p>
	 * The stream which is used by external components/clients to write data to
	 * the {@link DataSource}. Note that {@link ByteArrayOutputStream} is used
	 * here as it is a dummy stream which collects written data in an internal
	 * byte array.
	 * </p>
	 */
	private final ByteArrayOutputStream dataStream;

	/**
	 * <p>
	 * Describes the type of data encapsulated by an instance. The value of this
	 * field is generally used by recipient applications to determine how best
	 * to interpret the data. Where not specified, it defaults to
	 * {@value #DEFAULT_CONTENT_TYPE}.
	 * </p>
	 */
	private final String contentType;

	/**
	 * <p>
	 * The name of this instance. When an instance of this type is used to
	 * create an email attachment, the value of this field forms the name of the
	 * attachment.
	 * </p>
	 */
	private final String dataSourceName;

	/**
	 * <p>
	 * Creates an instance with the specified name and binary data, and a
	 * content-type of {@value #DEFAULT_CONTENT_TYPE}.
	 * </p>
	 * 
	 * @param dataSourceData
	 *            The instance's payload. This is the data that is to be
	 *            encapsulated by the instance.
	 * @param dataSourceName
	 *            The name of the {@link DataSource}, as returned by
	 *            {@link #getName()}.
	 */
	public EmailDataSource(final byte[] dataSourceData, final String dataSourceName) {
		this(dataSourceData, dataSourceName, DEFAULT_CONTENT_TYPE);
	}

	/**
	 * <p>
	 * Creates an instance with the specified name, binary data, and
	 * content-type.
	 * </p>
	 * 
	 * @param data
	 *            The data to be encapsulated by the instance,
	 * @param dataSourceName
	 *            The name used to identify this instance. This is the value
	 *            that will be returned by the method {@link #getName()}.
	 * @param contentType
	 *            Describes the nature of the data encapsulated by this
	 *            instance. For example
	 *            {@link ObixNetAPIConstants#APPLICATION_OCTET_STREAM_MIME_CONTENT_TYPE}
	 *            .
	 */
	public EmailDataSource(final byte[] data, final String dataSourceName, final String contentType) {
		this.data = data;
		this.dataSourceName = dataSourceName;
		this.contentType = contentType;
		this.dataStream = new ByteArrayOutputStream();
	}

	/**
	 * <p>
	 * Identifies the type of content encapsulated by the instance. This is the
	 * value that is specified via the constructor
	 * {@link #EmailDataSource(byte[], String, String)}. If the instance was
	 * initialised using the constructor
	 * {@link #EmailDataSource(byte[], String)}, this method will return the
	 * value {@value #DEFAULT_CONTENT_TYPE}.
	 * </p>
	 * 
	 * @return The content-type of the instance.
	 */
	@Override
	public String getContentType() {
		return this.contentType;
	}

	/**
	 * <p>
	 * Returns the name of the instance. The name is useful for a variety of
	 * reasons, for example, if the instance is used to specify an email
	 * attachment, the name is used as the attachment id or name.
	 * </p>
	 * 
	 * @return The name of the instance.
	 */
	@Override
	public String getName() {
		return this.dataSourceName;
	}

	/**
	 * <p>
	 * Returns an {@link InputStream} for reading the data encapsulated by the
	 * instance. Internally this method simply returns a
	 * {@link ByteArrayInputStream} initialised with the instance's
	 * payload/data.
	 * </p>
	 * 
	 * @return An {@link InputStream} for reading the datasource's contents.
	 * @see #getOutputStream()
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		// if any valid data has been written to this output stream
		if (this.dataStream.size() != 0) this.data = this.dataStream.toByteArray();

		return new ByteArrayInputStream(this.data);
	}

	/**
	 * <p>
	 * Returns a handle to an {@link OutputStream} which can be used to
	 * overwrite the instance's data. Internally, this class uses a
	 * {@link ByteArrayOutputStream} instance as the {@link OutputStream}. This
	 * makes it possible for the input data to be collated in a byte array until
	 * it is required.
	 * </p>
	 * <p>
	 * <b>Note</b> however that the internal {@link OutputStream} is reset with
	 * each call to this method. As such, callers should not repeatedly invoke
	 * this method, except they intend to reset the internal stream on each
	 * invocation.
	 * </p>
	 * 
	 * @return A reference to the internal field {@link #dataStream}.
	 * @see #getInputStream()
	 */
	@Override
	public OutputStream getOutputStream() throws IOException {
		// rest any contents in the dataStream
		this.dataStream.reset();
		return this.dataStream;
	}

	/**
	 * <p>
	 * Ensures that all internal streams are closed prior to garbage collection.
	 * </p>
	 */
	@Override
	protected void finalize() throws Throwable {
		IOResourceCloser.close(this.dataStream);
		super.finalize();
	}
}