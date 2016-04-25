package org.latency4j.processing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.latency4j.Latency4JTestResourcesHandle;
import org.latency4j.LatencyMonitor;
import org.latency4j.processing.AsynchronousLatencyMonitor;
import org.latency4j.processing.AsynchronousLatencyMonitorFactory;

public class AsynchronousLatencyMonitorFactoryTest {

	private Latency4JTestResourcesHandle testResourcesHandle;

	@Before
	public void setUp() {
		testResourcesHandle = new Latency4JTestResourcesHandle();
	}

	@After
	public void tearDown() {
		testResourcesHandle.cleanUp();
	}

	@Test
	public void testConstructorAndFieldAccess() {
		AsynchronousLatencyMonitorFactory target = new AsynchronousLatencyMonitorFactory();

		assertNull("configurationPath should be initialized to null.", target.getConfigurationPath());

		String testPath = "test-path";
		target.setConfigurationPath(testPath);
		assertEquals("Configuration path value not set correctly!", testPath, target.getConfigurationPath());
	}

	@Test
	public void testDefaultBehaviour() {
		AsynchronousLatencyMonitorFactory target = new AsynchronousLatencyMonitorFactory();

		LatencyMonitor uncategorizedMonitor = target.getMonitor("UNCATEGORIZED....");

		assertNotNull("Should still be able to create a " + "monitor without initiatilization!", uncategorizedMonitor);
		assertTrue(
				"Returned type does not match implementation of " + "default factory. Expected Asynchronous monitor!",
				uncategorizedMonitor instanceof AsynchronousLatencyMonitor);
	}

	@Test
	public void testInitializationFromDefaultConfig() {
		AsynchronousLatencyMonitorFactory target = new AsynchronousLatencyMonitorFactory();

		target.init();

		// find one known requirement
		String testResourceName = "statsRequirement0";
		boolean containsResource = target.getEpsilonResourceManager().containsRequirement("statsRequirement0");

		assertTrue("Could not find test requirement '" + testResourceName + "' as specified in the "
				+ "default configuration!", containsResource);

		// find one known alert handler
		testResourceName = "alertLogger0";
		containsResource = target.getEpsilonResourceManager().containsHandler("alertLogger0");

		assertTrue("Could not find configured handler '" + testResourceName + "' as specified in the "
				+ "default configuration!", containsResource);
	}

	@Test
	public void testOverridenConfig() {
		AsynchronousLatencyMonitorFactory target = new AsynchronousLatencyMonitorFactory();

		target.setConfigurationPath(
				testResourcesHandle.getResourceFilename(Latency4JTestResourcesHandle.CUSTOM_CONFIG_RESOURCE_NAME));

		assertEquals("ConfigurationPath not set correctly.",
				testResourcesHandle.getResourceFilename(Latency4JTestResourcesHandle.CUSTOM_CONFIG_RESOURCE_NAME),
				target.getConfigurationPath());

		target.init();
		internalValidateFactoryAfterInitFromOverrideConfig(target);
	}

	@Test
	public void testOverrideInitFromStream() throws FileNotFoundException {
		AsynchronousLatencyMonitorFactory target = new AsynchronousLatencyMonitorFactory();

		File testFile = testResourcesHandle.getResourceFile(Latency4JTestResourcesHandle.CUSTOM_CONFIG_RESOURCE_NAME);

		FileInputStream fstream = new FileInputStream(testFile);

		target.init(fstream);
		internalValidateFactoryAfterInitFromOverrideConfig(target);
	}

	private void internalValidateFactoryAfterInitFromOverrideConfig(final AsynchronousLatencyMonitorFactory target) {
		String testResourceName = "statsRequirement";

		// check for the first requirement
		boolean containsResource = target.getEpsilonResourceManager().containsRequirement(testResourceName);
		assertTrue("Could not find test requirement '" + testResourceName + "' as specified in the " + "configuration '"
				+ target.getConfigurationPath() + "'!", containsResource);

		// check for the second requirement
		testResourceName = "fixedRequirement";
		containsResource = target.getEpsilonResourceManager().containsRequirement(testResourceName);
		assertTrue("Could not find test requirement '" + testResourceName + "' as specified in the " + "configuration '"
				+ target.getConfigurationPath() + "'!", containsResource);

		// find first alert handler
		testResourceName = "alertLogger";
		containsResource = target.getEpsilonResourceManager().containsHandler(testResourceName);
		assertTrue("Could not find configured handler '" + testResourceName + "' as specified in the "
				+ "default configuration!", containsResource);

		// find second alert handler
		testResourceName = "countingAlertHandler";
		containsResource = target.getEpsilonResourceManager().containsHandler(testResourceName);
		assertTrue("Could not find configured handler '" + testResourceName + "' as specified in the "
				+ "default configuration!", containsResource);

	}

}// end class def