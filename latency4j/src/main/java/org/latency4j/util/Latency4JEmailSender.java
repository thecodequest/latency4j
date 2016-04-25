package org.latency4j.util;

import static org.latency4j.util.ObixNetAPIConstants.MIME_CONTENT_TYPE_TEXT;
import static org.latency4j.util.ObixNetAPIConstants.MIME_CONTENT_TYPE_TEXT_HTML;
import static org.latency4j.util.ObixNetAPIConstants.MIME_MIXED_CONTENT_HEADER;
import static org.latency4j.util.ObixNetAPIConstants.MIME_MULTIPART_ALTERNATIVE_HEADER;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.latency4j.Latency4JException;
import org.latency4j.util.FileUtilities;

/**
 * <p>
 * Utility class for packaging and sending email via the Java Mail API.
 * </p>
 * 
 * <p>
 * <b>Note</b> that this class provides a 1-shot approach to sending a single
 * email. An instance should either be reset by calling the {@link #reset()}
 * method or discarded once the {@link #sendMail()} method has been invoked.
 * Prior to such an invocation however, the message state can be altered by
 * invoking any of the message mutators.
 * </p>
 * <p>
 * Put differently, instances of this class maintain conversational state, which
 * has to be reset after each message is sent.
 * </p>
 * 
 * <p>
 * <b>Please note that this class is not thread-safe.</b>
 * </p>
 */
public class Latency4JEmailSender {
	/**
	 * <p>
	 * The mail session via which the message will be sent.
	 * </p>
	 */
	private final Session mailServerSession;

	/**
	 * <p>
	 * The message to which the instance relates. This field is initialised in
	 * the constructor, and its state is fleshed-out by the invocation of
	 * subsequent methods.
	 * </p>
	 */
	private MimeMessage currentMessage;

	/**
	 * <p>
	 * The constituent parts of the message being built. Where the instance is
	 * constructed to send email in HTML format, this field will hold both the
	 * HTML and alternative plain text representations of the email. It will
	 * also hold the message attachments where applicable.
	 * </p>
	 */
	private Multipart messageParts;

	/**
	 * <p>
	 * Field to indicate if message has been sent. When true, subsequent
	 * invocations of this classes' mail altering methods (e.g
	 * {@link #setSubject(String)}, {@link #setFrom(String)}) will result in an
	 * exception.
	 * </p>
	 */
	private boolean messageSent;

	/**
	 * <p>
	 * Indicates if the email should be prepared in HTML or plain text format.
	 * Note that even when HTML format is used, a fallback text representation
	 * is also included in the message. This ensures that clients unable to show
	 * HTML can still display the message.
	 * </p>
	 */
	private final boolean sendInHTMLFormat;

	/**
	 * <p>
	 * Constructs an instance which builds and sends messages in HTML format.
	 * <b>Note</b> that even when HTML format is used, a fallback text
	 * representation is always included as part of the message. This ensures
	 * that clients unable to show HTML can still display the message.
	 * </p>
	 * 
	 * @param mailSession
	 *            The {@link Session} via which the message will be sent.
	 */
	public Latency4JEmailSender(final Session mailSession) {
		this(mailSession, true);
	}

	/**
	 * <p>
	 * Constructor which provides a means of turning off the default HTML
	 * format. This is in contrast to {@link #ObixEmailSender(Session)}, which
	 * results in the instance formatting all messages as HTML.
	 * </p>
	 * 
	 * @param mailSession
	 *            The {@link Session} for which the message is being
	 *            constructed, and via which the message will be sent.
	 * @param sendInHTMLFormat
	 *            A value of <code>True</code> is equivalent to invoking the
	 *            constructor {@link #ObixEmailSender(Session)} instead, and a
	 *            value of <coe>False</code> will result in all messages being
	 *            sent as plain text.
	 * 
	 * @see #ObixEmailSender(Session)
	 */
	public Latency4JEmailSender(final Session mailSession, final boolean sendInHTMLFormat) {
		this.mailServerSession = mailSession;
		this.sendInHTMLFormat = sendInHTMLFormat;
		reset();
	}

	/**
	 * <p>
	 * Resets an instance's conversational state. Where a single instance of
	 * this type is re-used multiple times to send several emails, then this
	 * method must be called after each invocation of the {@link #sendMail()}
	 * method. It, in effect, allows the caller to begin creating another email
	 * anew.
	 * </p>
	 */
	public void reset() {
		this.currentMessage = new MimeMessage(this.mailServerSession);
		this.messageSent = false;
		this.messageParts = new MimeMultipart(MIME_MIXED_CONTENT_HEADER);
	}

	/**
	 * <p>
	 * Specifies the subject of the message to be sent.
	 * </p>
	 * 
	 * @param subject
	 *            The email's subject.
	 * @throws Latency4JException
	 *             If an error occurs communicating with the mail
	 *             {@link Session}, or if the {{@link #sendMail()} has already
	 *             been invoked.
	 */
	public void setSubject(final String subject) {
		try {
			currentMessage.setSubject(subject);
		} catch (MessagingException exception) {
			throw new Latency4JException("An error occured setting " + "the message subject to '" + subject + "'");
		}
	}

	/**
	 * <p>
	 * Specifies the address from which the email is to be sent. Note that this
	 * does not necessarily have to be consistent with the {@link Session}; it
	 * is simply the value to which the "From" field of the email will be set.
	 * This also means that it will serve as the default 'reply-to' address.
	 * </p>
	 * 
	 * @param address
	 *            The value of the 'From' email field.
	 * @throws Latency4JException
	 *             If an error occurs communicating with the mail
	 *             {@link Session}, or if the {{@link #sendMail()} has already
	 *             been invoked.
	 * 
	 * @see #setReplyTo(String)
	 */
	public void setFrom(final String address) {
		try {
			InternetAddress fromAddress = new InternetAddress(address);
			currentMessage.setFrom(fromAddress);
		} catch (MessagingException exception) {
			String errorMessageText = "Unable to set 'from' email address to {" + address
					+ "}, a messaging exception (embedded) occured!";
			throw new Latency4JException(errorMessageText, exception);
		}
	}

	/**
	 * <p>
	 * Sets the address to which replies to this message will be directed. This
	 * is simply the address to which the "Reply-To" field of the message will
	 * be set. Where not specified, this will default to the "From" address as
	 * specified via the {@link #setFrom(String)}.
	 * </p>
	 * 
	 * @param address
	 *            The address to which replies to the message will be directed.
	 * @throws Latency4JException
	 *             If an error occurs communicating with the mail
	 *             {@link Session}, or if the {{@link #sendMail()} has already
	 *             been invoked.
	 * 
	 * @see #setFrom(String)
	 */
	public void setReplyTo(final String address) {
		try {
			InternetAddress replyToAddress = new InternetAddress(address);
			currentMessage.setReplyTo(new Address[] { replyToAddress });
		} catch (MessagingException exception) {
			String errorMessageText = "Unable to set 'replyTo' email address to {" + address
					+ "}, a messaging exception (embedded) occured!";
			throw new Latency4JException(errorMessageText, exception);
		}
	}

	/**
	 * <p>
	 * Specifies the text of the email. The text can be supplied as a single
	 * string or a list of strings.
	 * </p>
	 * 
	 * <p>
	 * If the text is specified as a multi-element array, each element is
	 * treated as a separate paragraph. Thus, where the email format is HTML,
	 * the elements are enclosed with HTML &lt;p&gt; tags. In the plain-text
	 * equivalent, the elements are simply separated by newline characters.
	 * </p>
	 * 
	 * @param messageText
	 *            The message text, specified as a variable length string array.
	 * @throws Latency4JException
	 *             If an error occurs communicating with the mail session, or if
	 *             the {{@link #sendMail()} has already been invoked.
	 */
	public void setText(final String... messageText) {
		if (messageText != null) {
			ArrayList<String> paragraphs = new ArrayList<String>();
			for (String paragraph : messageText)
				paragraphs.add(paragraph);

			setText(paragraphs);
		}
	}

	/**
	 * <p>
	 * Specifies the text of the email. This method is equivalent to
	 * {@link #setText(String...)}, except for the use of a {@link List} for
	 * specifying the message paragraphs.
	 * </p>
	 * 
	 * @param paragraphs
	 *            The message text, specified as a {@link List}.
	 * @throws Latency4JException
	 *             If an error occurs communicating with the mail session, or if
	 *             the {{@link #sendMail()} has already been invoked.
	 */
	public void setText(final List<String> paragraphs) {
		assertMessageNotSent();
		try {
			if (messageParts.getCount() != 0)
				throw new Latency4JException("Message body has already been specified. Illegal operation!");
			else {
				Multipart alternativeMessageParts = new MimeMultipart(MIME_MULTIPART_ALTERNATIVE_HEADER);

				// if we are sending in HTML, then add HTML
				// as the preferred format
				if (sendInHTMLFormat) {
					String htmlMessageText = amalgamateTextParagraphsAsHtml(paragraphs);
					EmailDataSource htmlPartDataSource = new EmailDataSource(htmlMessageText.getBytes(),
							"Message Body (HTML)", MIME_CONTENT_TYPE_TEXT_HTML);
					BodyPart htmlBodyPart = new MimeBodyPart();
					htmlBodyPart.setDataHandler(new DataHandler(htmlPartDataSource));
					alternativeMessageParts.addBodyPart(htmlBodyPart);
				}

				// set a plain/text body part.
				// even when sending in HTML, this should also
				// be included in the offchance that the client cannot display
				// HTML
				String messageText = amalgamateTextParagraphs(paragraphs);
				EmailDataSource textPartDataSource = new EmailDataSource(messageText.getBytes(),
						"Message Body (Plain Text)", MIME_CONTENT_TYPE_TEXT);
				BodyPart textBodyPart = new MimeBodyPart();
				textBodyPart.setDataHandler(new DataHandler(textPartDataSource));
				alternativeMessageParts.addBodyPart(textBodyPart); // plain text
																	// is the
																	// backup

				// add it as another part of the message
				MimeBodyPart alternativeMessageBodyPart = new MimeBodyPart();
				alternativeMessageBodyPart.setContent(alternativeMessageParts);
				messageParts.addBodyPart(alternativeMessageBodyPart);
			}
		} catch (MessagingException messagingException) {
			String errorMessage = "Unable to create message body due to a Java Mail exception (embedded)!";
			throw new Latency4JException(errorMessage, messagingException);
		}
	}// end method def

	/**
	 * <p>
	 * Sends the underlying message being packaged by this instance. Put
	 * differently, it sends the message which represents the conversation
	 * state, to date, of the instance. An invocation of this method thus marks
	 * the end of the conversation between an instance of this class and its
	 * callers. Any subsequent invocation of the message construction methods
	 * will result in an exception. However, the {@link #reset()} method can be
	 * used to reset the conversational state of the instance again thus
	 * allowing for the creation of a new message.
	 * </p>
	 * 
	 * @throws Latency4JException
	 *             If an error occurs communicating with the mail session, or if
	 *             this method has already been invoked on this instance and the
	 *             {@link #reset()} method has not been used to reset the
	 *             conversational state.
	 * 
	 * @see #reset()
	 */
	public void sendMail() {
		assertMessageNotSent();
		try {
			if (messageParts.getCount() == 0) throw new Latency4JException("Message content not set, Illegal call");

			// if message parts already set, then simply set the messageParts
			currentMessage.setContent(messageParts);
			Transport.send(currentMessage);

			// set the property to true to indicate that we are done with this
			// service instance
			messageSent = true;
		} catch (MessagingException messageException) {
			String errorText = "Unable to send the current message due to a Java Mail exception (embedded)!";
			throw new Latency4JException(errorText, messageException);
		}
	}// end method def

	/**
	 * <p>
	 * Attaches a file to the email. This method is equivalent to reading the
	 * file data into a <code>byte</code> array and invoking the
	 * {@link #addAttachment(String, byte[])} method, with the filename used as
	 * the attachment name.
	 * </p>
	 * 
	 * @param dataFile
	 *            The file to be attached to the email.
	 * @throws Latency4JException
	 *             If an error occurs communicating with the mail session; if
	 *             the {{@link #sendMail()} has already been invoked; or if
	 *             unable to read the specified file.
	 * 
	 * @see #addAttachment(String, byte[])
	 * @see #addAttachment(String, String)
	 * @see EmailDataSource
	 */
	public void addAttachment(final File dataFile) {
		byte[] attachmentData = FileUtilities.readFileContentsAsBytes(dataFile);
		addAttachment(dataFile.getName(), attachmentData);

	}// end method

	/**
	 * <p>
	 * Creates an attachment to the email from the specified text. Note that
	 * this method is equivalent to invoking the
	 * {@link #addAttachment(String, byte[])} where the second argument is
	 * obtained by invoking the {@link String#getBytes()} method on the
	 * attachment text.
	 * </p>
	 * 
	 * @param attachmentName
	 *            The name to assign to the attachment. This is typically used
	 *            by the mail client as the identifier for the attachment.
	 * @param attachmentText
	 *            The text from which to create the email attachment.
	 * @throws Latency4JException
	 *             If an error occurs communicating with the mail session, or if
	 *             the {{@link #sendMail()} has already been invoked.
	 * @see EmailDataSource
	 * @see #addAttachment(File)
	 * @see #addAttachment(String, byte[])
	 */
	public void addAttachment(final String attachmentName, final String attachmentText) {
		byte[] attachmentData = attachmentText.getBytes();
		addAttachment(attachmentName, attachmentData);
	}// end method

	/**
	 * <p>
	 * Creates an email attachment from native data. This method is agnostic
	 * regarding the type of data content in the code>byte</code> array. Typical
	 * uses for this method would be the attachment of documents, images, audio
	 * and files e.t.c as plain byte data.
	 * </p>
	 * 
	 * @param attachmentName
	 *            The name to be applied to the attachment. This is typically
	 *            used by mail client as the identifier for the attachment.
	 * 
	 * @param data
	 *            The attachment data.
	 * @throws Latency4JException
	 *             If an error occurs communicating with the mail session, or if
	 *             the {{@link #sendMail()} has already been invoked.
	 * @see EmailDataSource
	 * @see #addAttachment(File)
	 * @see #addAttachment(String, byte[])
	 */
	public void addAttachment(final String attachmentName, final byte[] data) {
		try {
			if (messageParts.getCount() == 0)
				throw new Latency4JException("You must specify the message body before adding any attachments. "
						+ "Unable to add email attachment: " + attachmentName);
			else {
				EmailDataSource attachmentDataSource = new EmailDataSource(data, attachmentName);
				BodyPart emailBodyPart = new MimeBodyPart();
				emailBodyPart.setFileName(attachmentName);
				emailBodyPart.setDataHandler(new DataHandler(attachmentDataSource));
				messageParts.addBodyPart(emailBodyPart);
			} // end else
		} catch (MessagingException messagingException) {
			throw new Latency4JException(messagingException);
		}
	}

	/**
	 * <p>
	 * Specifies a {@link Collection} of addresses to which the email should be
	 * primarily addressed. The values in the {@link Collection} will be added
	 * to the 'To:' recipient list of the email.
	 * </p>
	 * 
	 * @param emailAddresses
	 *            A {@link Collection} of email addresses to which the email is
	 *            to be addressed.
	 * @throws Latency4JException
	 *             If an error occurs communicating with the mail session, or if
	 *             the {@link #sendMail()} has already been invoked.
	 */
	public void addressTo(final Collection<String> emailAddresses) {
		addRecipientList(emailAddresses, Message.RecipientType.TO);
	}

	/**
	 * <p>
	 * Provides an address to which the email should be sent. The argument value
	 * will be used to populate the 'To:' field of the mail message. This is a
	 * convenience method for scenarios where the email is intended for a single
	 * recipient only.
	 * </p>
	 * 
	 * @param emailAddress
	 *            The recipient email address.
	 * @throws Latency4JException
	 *             If an error occurs communicating with the mail session, or if
	 *             the {@link #sendMail()} has already been invoked.
	 */
	public void addressTo(final String emailAddress) {
		List<String> recipientAddressList = new ArrayList<String>();
		recipientAddressList.add(emailAddress);
		addressTo(recipientAddressList);
	}

	/**
	 * <p>
	 * Specifies a {@link Collection} of addresses to which the email should be
	 * copied. The values in the {@link Collection} will be used to populate the
	 * 'Cc:' recipient list of the email.
	 * </p>
	 * 
	 * @param emailAddresses
	 *            A {@link Collection} of email address to which the email is to
	 *            be copied.
	 * @throws Latency4JException
	 *             If an error occurs communicating with the mail session, or if
	 *             the {@link #sendMail()} has already been invoked.
	 */
	public void ccTo(final Collection<String> emailAddresses) {
		addRecipientList(emailAddresses, Message.RecipientType.CC);
	}

	/**
	 * Specifies addresses to which the email should be blind copied. The values
	 * in the address {@link Collection} will be used to populate the 'Bcc:'
	 * recipient list of the email.
	 * 
	 * @param emailAddresses
	 *            A {@link Collection} of email address to which the email is to
	 *            be blind copied.
	 * @throws Latency4JException
	 *             If an error occurs communicating with the mail session, or if
	 *             the {@link #sendMail()} has already been invoked.
	 */
	public void bccTo(final Collection<String> emailAddresses) {
		addRecipientList(emailAddresses, Message.RecipientType.BCC);
	}

	/**
	 * <p>
	 * Internal utility method to add a list of addresses to the email's
	 * recipient list.
	 * </p>
	 * 
	 * @param addresses
	 *            The list of recipient addresses.
	 * @param recipientType
	 *            The recipient type e.g. 'To:', 'Cc:' or 'Bcc:'.
	 * @throws Latency4JException
	 *             If an error occurs communicating with the mail session, or if
	 *             the {{@link #sendMail()} has already been invoked.
	 */
	private void addRecipientList(final Collection<String> addresses, final Message.RecipientType recipientType) {
		try {
			InternetAddress address;
			for (String addressAsString : addresses) {
				if (addressAsString != null) {
					address = new InternetAddress(addressAsString.trim());
					currentMessage.addRecipient(recipientType, address);
				}
			}
		} catch (MessagingException messagingException) {
			String errorText = "Unable to address mail message to {" + addresses
					+ "}, a messaging exception (embedded) occured!";
			throw new Latency4JException(errorText, messagingException);
		}
	}

	/**
	 * <p>
	 * Internal utility method to build a text representation of the message's
	 * constituent paragraphs.
	 * </p>
	 * 
	 * @param paragraphs
	 *            The message text specified as a list, where each element in
	 *            the list represents a distinct paragraph.
	 * 
	 * @return An amalgamation of the paragraphs which together make up the
	 *         email's body, where each paragraph is delimited by a simple
	 *         newline character.
	 * @see #amalgamateTextParagraphsAsHtml(List)
	 */
	private String amalgamateTextParagraphs(final List<String> paragraphs) {
		StringBuffer result = new StringBuffer();
		for (String paragraph : paragraphs) {
			result.append(paragraph);
			result.append("\n");
		}
		return result.toString();
	}

	/**
	 * <p>
	 * Internal utility method to build a HTML representation of the message's
	 * constituent paragraphs.
	 * </p>
	 * 
	 * @param paragraphs
	 *            The message text specified as a {@link List}, where each
	 *            element represents a distinct paragraph.
	 * 
	 * @return An amalgamation of the paragraphs which together make up the
	 *         email's body, where each paragraph is delimited by a HTML
	 *         &lt;p&gt; tag.
	 * @see #amalgamateTextParagraphs(List)
	 */
	private String amalgamateTextParagraphsAsHtml(final List<String> paragraphs) {
		StringBuffer result = new StringBuffer("<HTML><HEAD></HEAD><BODY>");
		for (String paragraph : paragraphs) {
			result.append("<p>");
			result.append(paragraph);
			result.append("</p>");
		}
		result.append("</BODY></HTML>");
		return result.toString();
	}

	/**
	 * <p>
	 * Asserts that the method {@link #sendMail()} has not been sent. In
	 * essence, it ensures that the email being packaged by the instance has not
	 * been sent. In effect, this method ensures that the conversational state
	 * of the instance is still valid.
	 * </p>
	 */
	private void assertMessageNotSent() {
		if (messageSent) throw new Latency4JException(
				"The message has already been sent, " + "Illegal call, possible application logic error!!");
	}
}