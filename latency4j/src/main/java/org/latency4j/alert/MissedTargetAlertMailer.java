package org.latency4j.alert;

import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.mail.Session;

import org.latency4j.CappedLatencyRequirement;
import org.latency4j.Latency4JException;
import org.latency4j.LatencyRequirement;
import org.latency4j.StatisticalLatencyRequirement;
import org.latency4j.WorkDuration;
import org.latency4j.util.Latency4JEmailSender;
import org.latency4j.util.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Alert handler implementation which mails alert messages to a set of
 * pre-configured email addresses. This handler creates a JavaMail session by
 * extracting all parameters with a <i>'mail.'</i> prefix. For example a
 * property with the name <i>'mail.store.protocol'</i> is automatically assumed
 * to be mail specific due to its <i>'mail.'</i> prefix.
 * </p>
 * <p>
 * For details on the parameters required to initialise a mail session, please
 * see the Environment properties section of the
 * <a href='http://java.sun.com/products/javamail/JavaMail-1.4.pdf'>JavaMail
 * 1.4</a> specification.
 * </p>
 * <p>
 * Other parameters required by this handler include:
 * <ul>
 * <li>{@value #MAIL_TO_ADDR_PARAM_KEY}: A ';' separated list of email addresses
 * to which the alert is to be addressed.</li>
 * <li>{@value #MAIL_CC_ADDR_PARAM_KEY}: A ';' separated list of email addresses
 * to which the alert is to be copied (i.e. cc'ed).</li>
 * <li>{@value #MAIL_BCC_ADDR_PARAM_KEY}: A ';' separated list of email
 * addresses to which the alert is to be blind copied (i.e. bcc'ed).</li>
 * <li>{@value #MAIL_SUBJECT_PARAM_KEY}: The value to be used in the subject
 * line of notifications.</li>
 * </ul>
 * </p>
 * <p>
 * <b>Note:</b>Instances of this class must not be used prior to {@link #init()
 * initialisation}.
 * </p>
 */
public class MissedTargetAlertMailer extends AbstractAlertHandler {
	/**
	 * <p>
	 * Internal error/trace logger.
	 * </p>
	 */
	private static final Logger logger = LoggerFactory.getLogger(MissedTargetAlertMailer.class);

	/**
	 * <p>
	 * The prefix of JavaMail environment parameters. All parameters with this
	 * prefix are ignored and simply passed wholesale to the JavaMail Session
	 * factory.
	 * </p>
	 */
	private static final String MAIL_PARAMS_PREFIX = "mail.";

	/**
	 * <p>
	 * The key (
	 * <q><code>{@value #MAIL_TO_ADDR_PARAM_KEY}</code></q>) for the parameter
	 * which specifies the addresses to which the alerts will be sent. These are
	 * the addresses that go into the 'To:' address field of the email.
	 * </p>
	 */
	public static final String MAIL_TO_ADDR_PARAM_KEY = "to.addresses";

	/**
	 * <p>
	 * The key (
	 * <q><code>{@value #MAIL_CC_ADDR_PARAM_KEY}</code></q>) for the parameter
	 * which specifies the addresses to which the alerts will be copied. These
	 * are the addresses that go into the 'Cc:' address field of the email.
	 * </p>
	 */
	public static final String MAIL_CC_ADDR_PARAM_KEY = "cc.addresses";

	/**
	 * <p>
	 * The key (
	 * <q><code>{@value #MAIL_BCC_ADDR_PARAM_KEY}</code></q>) for the parameter
	 * which specifies the addresses to which the alerts will be blind copied.
	 * These are the addresses that go into the 'Bcc:' address field of the
	 * email.
	 * </p>
	 */
	public static final String MAIL_BCC_ADDR_PARAM_KEY = "bcc.addresses";

	/**
	 * <p>
	 * The key
	 * <q><code>{@value #MAIL_SUBJECT_PARAM_KEY}</code></q> for the parameter
	 * which specifies the subject line of alert emails.
	 * </p>
	 */
	public static final String MAIL_SUBJECT_PARAM_KEY = "subject";

	/**
	 * <p>
	 * The JavaMail session used for sending mail alerts.
	 * </p>
	 */
	private Session session;

	/**
	 * <p>
	 * Default constructor.
	 * </p>
	 */
	public MissedTargetAlertMailer() {
		super();
	}

	@Override
	public void latencyDeviationExceededTolerance(final StatisticalLatencyRequirement requirement,
			final WorkDuration duration, final double deviationFromMean, final double mean) {
		String messageText = prepareDeviationExceededToleranceMsg(requirement, duration, deviationFromMean, mean);
		sendEmail(messageText);
	}

	@Override
	public void latencyExceededCap(final CappedLatencyRequirement requirement, final WorkDuration duration) {
		String messageText = prepareLatencyExceededCap(requirement, duration);
		sendEmail(messageText);
	}

	@Override
	public void workCategoryFailed(final LatencyRequirement requirement, final WorkDuration duration) {
		String messageText = prepareWorkCategoryFailedMsg(requirement, duration);
		sendEmail(messageText);
	}

	@Override
	public void init() {
		logger.info("Initializing alert-mailer...");

		super.init();

		if (!parameters.containsKey(MAIL_TO_ADDR_PARAM_KEY) && !parameters.containsKey(MAIL_CC_ADDR_PARAM_KEY)
				&& !parameters.containsKey(MAIL_BCC_ADDR_PARAM_KEY)) {
			logger.warn("No recipient addresses specified for handler! Cannot proceed");
			throw new Latency4JException("At least one to, cc or bcc address must be specified for mail alert handler!");
		}

		Properties mailProperties = extractMailPropertiesFromParameters();
		session = Session.getInstance(mailProperties);

		this.initialized.set(true);
		logger.info("Initialized alert-mailer...");
	}

	/**
	 * <p>
	 * Internal delegate method which actually does the heavy lifting in sending
	 * a formated alert.
	 * </p>
	 * 
	 * @param messageText
	 *            A formated alert to be sent via JavaMail.
	 */
	private void sendEmail(final String messageText) {
		try {
			Latency4JEmailSender mailSender = new Latency4JEmailSender(session);

			String subject = parameters.get(MAIL_SUBJECT_PARAM_KEY);
			if (subject != null) mailSender.setSubject(subject);

			addAddressFromParamList(mailSender, MAIL_TO_ADDR_PARAM_KEY);
			addAddressFromParamList(mailSender, MAIL_CC_ADDR_PARAM_KEY);
			addAddressFromParamList(mailSender, MAIL_BCC_ADDR_PARAM_KEY);

			mailSender.setText(messageText);

			mailSender.sendMail();

		} catch (Throwable exception) {
			logger.error("Problem sending epsilon notification message, details: \n" + messageText);
		}
	}

	/**
	 * <p>
	 * Internal delegate method which decomposes a string containing multiple
	 * <q>;</q> separated email addresses into a list of email addresses and
	 * registers them with the {@link Latency4JEmailSender mail-sender} instance.
	 * </p>
	 * 
	 * @param mailSender
	 *            The {@link Latency4JEmailSender mail-sender} with which the
	 *            target addresses are to be registered.
	 * @param mailAddressParamKey
	 *            The key of the parameter containing the mail addresses.
	 * @throws ObixException
	 *             If an exception occurs interacting with the underlying mail
	 *             session.
	 */
	private void addAddressFromParamList(final Latency4JEmailSender mailSender, final String mailAddressParamKey) {
		String toAddress = parameters.get(mailAddressParamKey);
		if (toAddress != null) {
			List<String> toAddressList = NetworkUtils.parseEmailAddresses(toAddress);
			if (MAIL_TO_ADDR_PARAM_KEY.equalsIgnoreCase(mailAddressParamKey)) mailSender.addressTo(toAddressList);
			else if (MAIL_CC_ADDR_PARAM_KEY.equalsIgnoreCase(mailAddressParamKey)) mailSender.ccTo(toAddressList);
			else mailSender.bccTo(toAddressList);
		}
	}

	/**
	 * <p>
	 * Internal delegate method which extracts the JavaMail specific parameters
	 * from the parameter list with which the instance is initialised.
	 * </p>
	 * 
	 * @return {@link Properties} which are specific to the JavaMail API.
	 */
	private Properties extractMailPropertiesFromParameters() {
		Properties result = new Properties();

		Set<String> parameterKeys = parameters.keySet();

		for (String parameterKey : parameterKeys) {
			if (parameterKey.startsWith(MAIL_PARAMS_PREFIX)) result.put(parameterKey, parameters.get(parameterKey));
		}

		return result;
	}// end class def
}// end class def