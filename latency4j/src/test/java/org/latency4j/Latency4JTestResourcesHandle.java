package org.latency4j;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.latency4j.Latency4JConstants;
import org.latency4j.util.ClasspathResourceUtils;
import org.latency4j.util.FileUtilities;

/*
 * Utility handle for obtaining test resources
 */
@Ignore
public class Latency4JTestResourcesHandle {
	public static final String CUSTOM_CONFIG_RESOURCE_NAME = "CLASSPATH:/customconfig/epsilon-config.xml";

	public static final String TEST_AOP_MONITOR_CONFIG = "CLASSPATH:/org/latency4j/spring/aop/epsilon-config.xml";

	private static final String[] TEST_RESOURCES = { CUSTOM_CONFIG_RESOURCE_NAME, TEST_AOP_MONITOR_CONFIG,
			Latency4JConstants.DEFAULT_CONFIGURATION_RESOURCE_NAME };

	private final Map<String, File> resourceToFileNameMappings;

	public Latency4JTestResourcesHandle() {
		resourceToFileNameMappings = new HashMap<String, File>();

		for (String resourceName : TEST_RESOURCES) {
			resourceToFileNameMappings.put(resourceName, processResource(resourceName));
		}
	}

	public String getResourceFilename(final String resourceName) {
		String result;
		File resourceFile = getResourceFile(resourceName);
		if (resourceFile != null) result = resourceFile.getAbsolutePath();
		else result = null;
		return result;
	}

	public File getResourceFile(final String resourceName) {
		File result;
		if (resourceToFileNameMappings.containsKey(resourceName)) result = resourceToFileNameMappings.get(resourceName);
		else result = null;
		return result;
	}

	public void cleanUp() {
		for (File resourceFile : resourceToFileNameMappings.values())
			resourceFile.delete();
	}

	private File processResource(final String resourceName) {
		File result;

		File tempFolder = new File(System.getProperty("java.io.tmpdir"));

		byte[] resourceData = ClasspathResourceUtils.getClasspathResource(resourceName,
				Latency4JTestResourcesHandle.class);

		result = new File(tempFolder.getAbsolutePath() + ClasspathResourceUtils.extractPathFromURL(resourceName));

		FileUtilities.writeBytesToFile(result, resourceData);

		return result;
	}

}
