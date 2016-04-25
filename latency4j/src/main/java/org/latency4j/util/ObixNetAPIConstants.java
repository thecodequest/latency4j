package org.latency4j.util;

import javax.mail.BodyPart;
import javax.mail.internet.MimeBodyPart;

/**
 * <p>
 * Definitions of constants which are applicable within the obix network
 * utilities package.
 * </p>
 */
interface ObixNetAPIConstants {

	/**
	 * <p>
	 * A separator that can be used to delineate multiple email addresses,
	 * specified as a single contiguous string. For an example of its
	 * application see {@link NetworkUtils#parseEmailAddresses(String)}.
	 * </p>
	 */
	String EMAIL_ADDRESS_SEPERATOR_CHAR = ";";

	/**
	 * <p>
	 * An encoding type that can be applied to email attachments. Popular
	 * desktop software should generally attempt to open up attachments, with
	 * this encoding type, using the appropriate software for the file type--as
	 * defined in the operating system file-to-application mappings.
	 * </p>
	 * 
	 * @see Latency4JEmailSender#addAttachment(java.io.File)
	 * @see Latency4JEmailSender#addAttachment(String, byte[])
	 * @see Latency4JEmailSender#addAttachment(String, String)
	 */
	String APPLICATION_OCTET_STREAM_MIME_CONTENT_TYPE = "application/octet-stream";

	/**
	 * <p>
	 * The content type to be applied to mail messages to indicate that the
	 * message can consist of multiple {@link BodyPart}s (e.g. plain-text/HTML),
	 * and attachments.
	 * </p>
	 */
	String MIME_MIXED_CONTENT_HEADER = "mixed";

	/**
	 * <p>
	 * The content-type that is applied to an email {@link MimeBodyPart} to
	 * indicate that the email consists of a plain text portion, and also
	 * (possibly) a HTML part. Where the {@link MimeBodyPart} consists of a
	 * {@link #MIME_CONTENT_TYPE_TEXT_HTML} part, it would be used as the
	 * preferred format by all mail readers capable of interpreting HTML.
	 * </p>
	 */
	String MIME_MULTIPART_ALTERNATIVE_HEADER = "alternative";

	/**
	 * <p>
	 * Content type which is applied to HTML message {@link BodyPart}s.
	 * </p>
	 * 
	 * @see #MIME_MIXED_CONTENT_HEADER
	 */
	String MIME_CONTENT_TYPE_TEXT_HTML = "text/html";

	/**
	 * <p>
	 * Content type which is applied to plain-text message {@link BodyPart}s.
	 * </p>
	 * 
	 * @see #MIME_MIXED_CONTENT_HEADER
	 */
	String MIME_CONTENT_TYPE_TEXT = "text/plain";
}