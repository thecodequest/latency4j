package org.latency4j.alert;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.latency4j.alert.MissedTargetLogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Tests enum MissedTargetLogLevel
 */
public class MissedTargetLevelTest {
	private final Logger logger = LoggerFactory.getLogger(MissedTargetLevelTest.class);

	/*
	 * Test enum resolution from string value. Includes some random tests for
	 * case insensitivity.
	 */
	@Test
	public void testResolve() {
		MissedTargetLogLevel debugLevel = MissedTargetLogLevel.resolve("DEBUG");
		assertEquals("DEBUG level resolution failed.", MissedTargetLogLevel.DEBUG, debugLevel);
		debugLevel = MissedTargetLogLevel.resolve("debug");
		assertEquals("debug level resolution failed.", MissedTargetLogLevel.DEBUG, debugLevel);

		MissedTargetLogLevel infoLevel = MissedTargetLogLevel.resolve("INFO");
		assertEquals("INFO level resolution failed.", MissedTargetLogLevel.INFO, infoLevel);
		infoLevel = MissedTargetLogLevel.resolve("iNfo");
		assertEquals("iNfo level resolution failed.", MissedTargetLogLevel.INFO, infoLevel);

		MissedTargetLogLevel warnLevel = MissedTargetLogLevel.resolve("WARN");
		assertEquals("WARN level resolution failed.", MissedTargetLogLevel.WARN, warnLevel);
		warnLevel = MissedTargetLogLevel.resolve("waRN");
		assertEquals("waRN level resolution failed.", MissedTargetLogLevel.WARN, warnLevel);

		MissedTargetLogLevel errorLevel = MissedTargetLogLevel.resolve("ERROR");
		assertEquals("ERROR level resolution failed.", MissedTargetLogLevel.ERROR, errorLevel);
		errorLevel = MissedTargetLogLevel.resolve("ERRor");
		assertEquals("ERRor level resolution failed.", MissedTargetLogLevel.ERROR, errorLevel);

		MissedTargetLogLevel traceLevel = MissedTargetLogLevel.resolve("TRACE");
		assertEquals("TRACE level resolution failed.", MissedTargetLogLevel.TRACE, traceLevel);
		traceLevel = MissedTargetLogLevel.resolve("trACe");
		assertEquals("trACe level resolution failed.", MissedTargetLogLevel.TRACE, traceLevel);

		logger.info("Completed MissedTargetLogLevel resolution");
	}
}