package org.latency4j.configuration;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.latency4j.Latency4JConstants;
import org.latency4j.Latency4JTestResourcesHandle;
import org.latency4j.configuration.ConfigurationReader;
import org.latency4j.processing.Latency4JResourceManager;
import org.latency4j.util.ClasspathResourceUtils;
import org.latency4j.util.FileUtilities;

/*
 * Tests that resource manager can read epsilon configuration 
 * xml and initialise the system accordingly. 
 */
public class Latency4JResourceManagerTest {
	
	private File configurationFile;
	private String configurationFileName;
	private String configurationURL;
	private String configurationClasspathURL;
	private String badConfigurationFileURI;
	private String badConfigurationURL;

	@Before
	public void setUp() throws Exception {
		configurationClasspathURL = Latency4JTestResourcesHandle.CUSTOM_CONFIG_RESOURCE_NAME;
		configurationFile = writeTestEpsilonConfigToFile(configurationClasspathURL);

		configurationFileName = configurationFile.getAbsolutePath();
		configurationURL = configurationFile.toURI().toURL().toString();

		badConfigurationURL = "bad:/epsilon-cfg.xml";
		badConfigurationFileURI = "foo-bar.xml";
	}

	@After
	public void tearDown() {
		configurationFile.delete();
	}

	@Test
	public void testReadConfigurationFromFile() {
		Latency4JResourceManager resourceManager = new Latency4JResourceManager();

		ConfigurationReader.readConfiguration(resourceManager, configurationFileName);

		assertNotNull(resourceManager.getLatencyRequirement("fixedRequirement"));
		assertNotNull(resourceManager.getLatencyRequirement("statsRequirement"));
		assertNotNull(resourceManager.getAlertHandler("alertLogger"));
	}

	@Test
	public void testReadConfigurationFromURL() {
		Latency4JResourceManager resourceManager = new Latency4JResourceManager();

		ConfigurationReader.readConfiguration(resourceManager, configurationURL);

		assertNotNull(resourceManager.getLatencyRequirement("fixedRequirement"));
		assertNotNull(resourceManager.getLatencyRequirement("statsRequirement"));
		assertNotNull(resourceManager.getAlertHandler("alertLogger"));
	}

	@Test
	public void testReadConfigurationFromClasspathURL() {
		Latency4JResourceManager resourceManager = new Latency4JResourceManager();

		ConfigurationReader.readConfiguration(resourceManager, configurationClasspathURL);

		assertNotNull(resourceManager.getLatencyRequirement("fixedRequirement"));
		assertNotNull(resourceManager.getLatencyRequirement("statsRequirement"));
		assertNotNull(resourceManager.getAlertHandler("alertLogger"));
	}

	@Test
	public void testReadConfigurationFromBadFileURI() throws Exception {
		Latency4JResourceManager resourceManager = new Latency4JResourceManager();
		boolean passed;
		try {
			ConfigurationReader.readConfiguration(resourceManager, badConfigurationFileURI);
			passed = false;
		} catch (Exception exce) {
			passed = true;
		}
		if (!passed) throw new Exception("Error was not raised when trying to read from a bad URL!");
	}

	@Test
	public void testReadConfigurationFromBadURL() throws Exception {
		Latency4JResourceManager resourceManager = new Latency4JResourceManager();
		boolean passed;
		try {
			ConfigurationReader.readConfiguration(resourceManager, badConfigurationURL);
			passed = false;
		} catch (Exception exce) {
			passed = true;
		}
		if (!passed) throw new Exception("Error was not raised when trying to read from a bad URL!");
	}

	@Test
	public void testReadDefaultConfiguration() {
		Latency4JResourceManager resourceManager = new Latency4JResourceManager();
		ConfigurationReader.readConfiguration(resourceManager,
				Latency4JConstants.DEFAULT_CONFIGURATION_RESOURCE_NAME);

		assertNotNull(resourceManager.getLatencyRequirement("fixedRequirement0"));
		assertNotNull(resourceManager.getLatencyRequirement("statsRequirement0"));
		assertNotNull(resourceManager.getAlertHandler("alertLogger0"));
	}

	private File writeTestEpsilonConfigToFile(final String testConfig) {
		File result;

		byte[] configData = ClasspathResourceUtils.getClasspathResource(testConfig, this);

		String tempFolder = System.getProperty(Latency4JConstants.JAVA_TMP_FILE_FOLDER);

		result = new File(tempFolder, ClasspathResourceUtils.extractPathFromURL(testConfig));

		FileUtilities.writeBytesToFile(result, configData);

		return result;
	}
}
