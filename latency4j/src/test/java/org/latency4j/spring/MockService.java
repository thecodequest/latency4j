package org.latency4j.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Dummy service used for testing Epsilon AOP functionality 
 */
public class MockService {
	private static Logger logger = LoggerFactory.getLogger(MockService.class);

	public static final long WAIT_INTERVAL = 100L;

	public static int INVOCATION_SLEEP_THRESHOLD = 5;

	/*
	 * Test service operation. Simply sleeps for a hardcoded interval.
	 */
	public void cappedTestService(final int i) {
		internalServiceMock(i);
	}

	/*
	 * Test service operation. Simply sleeps for a hardcoded interval.
	 */
	public void statsTestService(final int i) {
		internalServiceMock(i);
	}

	/*
	 * Method which throws an exception.
	 */
	public void exceptionService() {
		logger.info("service which throws exception invoked ...");
		throw new RuntimeException("test error!");
	}

	/*
	 * Internal test invocation.
	 */
	private void internalServiceMock(final int i) {
		try {
			logger.info("service invoked ...");
			if (i >= INVOCATION_SLEEP_THRESHOLD) Thread.sleep((i * WAIT_INTERVAL) + i);
			else Thread.sleep(i);
		} catch (Throwable t) {
			logger.error("error occured", t);
		}
	}
}