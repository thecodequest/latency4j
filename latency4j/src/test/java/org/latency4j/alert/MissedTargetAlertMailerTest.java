package org.latency4j.alert;

import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.latency4j.alert.MissedTargetAlertMailer;

/*
 * Template test unit for class MissedTargetAlertMailer.
 */
@Ignore
public class MissedTargetAlertMailerTest extends AbstractAlertHandlerTest {
	/*
	 * Test target
	 */
	private MissedTargetAlertMailer alertMailer;

	@Override
	protected Map<String, String> getAlertHandlerParams() {
		Map<String, String> result = super.getAlertHandlerParams();

		// enter your mail logon details here
		// BUT REMEMBER TO REMOVE THEM IF
		// RE-DISTRIBUTING YOUR CODE!!
		result.put("mail.transport.protocol", "");
		result.put("mail.host", "");
		result.put("mail.user", "");
		result.put("mail.password", "");
		// result.put("mail.from", "");

		result.put("subject", "Test Message to see if this works");
		result.put("to.addresses", "");
		result.put("cc.addresses", "");
		result.put("bcc.addresses", "");

		return result;
	}

	/*
	 * creates an instance with the test data set.
	 */
	@Before
	public void setUp() throws Exception {
		alertMailer = new MissedTargetAlertMailer();
		alertMailer.setAlertHandlerId("Alert-Logger");
		alertMailer.setParameters(getAlertHandlerParams());
		alertMailer.init();
		super.setUp(alertMailer);
	}

	/*
	 * Uncomment the following test methods only after you have provided all the
	 * paramaters in the method getAlertHandlerParams().
	 */
	@Test
	public void testLatencyExceededTolerance() {
		alertMailer.latencyDeviationExceededTolerance(statsRequirement, duration, 2.0d, 0.5d);
	}

	@Test
	public void testTargetMissed() {
		alertMailer.latencyExceededCap(fixedRequirement, duration);
	}

	@Test
	public void testWorkCategoryFailed() {
		duration.setError(exception);
		alertMailer.workCategoryFailed(fixedRequirement, duration);
	}
}
