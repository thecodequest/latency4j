package org.latency4j.util;

import java.io.InputStream;

import org.latency4j.Latency4JException;

/**
 * <p>
 * Utility class for accessing resources on an application's classpath.
 * </p>
 */
public final class ClasspathResourceUtils {
	public static final String CLASSPATH_RESOURCE_PREFIX = "CLASSPATH:";

	/**
	 * <p>
	 * Private default constructor to prevent accidental instantiation.
	 * </p>
	 */
	private ClasspathResourceUtils() {}

	/**
	 * <p>
	 * Determines if the specified string is a valid classpath URL of the form
	 * <i> {@value #CLASSPATH_RESOURCE_PREFIX}resource-path</i>. Note that this
	 * method does not test if the specified does indeed point to a valid
	 * resource or if it can be read. It simply tests that the string meets the
	 * specifications for a classpath URL.
	 * </p>
	 * 
	 * @param candidateString
	 *            The string to test.
	 * @return <code>True</code> if the specified string meets the
	 *         specifications for a classpath URL, and <code>False</code>
	 *         otherwise.
	 */
	public static boolean isClasspathURL(final String candidateString) {
		boolean result = false;
		String specification = candidateString.toUpperCase();

		if (specification != null && specification.startsWith(CLASSPATH_RESOURCE_PREFIX)) result = true;

		return result;
	}

	/**
	 * <p>
	 * Extracts the path portion of the given classpath URL. The path is
	 * everything that comes after the {@value #CLASSPATH_RESOURCE_PREFIX}
	 * portion of the URL.
	 * </p>
	 * 
	 * @param classpathURL
	 *            The URL whose path portion is to be extracted.
	 * @return In the case where the URL begins with the prefix,
	 *         {@value #CLASSPATH_RESOURCE_PREFIX}, then everything after the
	 *         prefix is returned. However, where the URL does not start with
	 *         the prefix, then the entire URL is returned.
	 */
	public static String extractPathFromURL(final String classpathURL) {
		String result;

		int prefixLength = CLASSPATH_RESOURCE_PREFIX.length();

		if (classpathURL == null) throw new IllegalArgumentException("'" + classpathURL + "' is not a valid URL.");
		else {
			if (classpathURL.toUpperCase().startsWith(CLASSPATH_RESOURCE_PREFIX))
				result = classpathURL.substring(prefixLength, classpathURL.length());
			else result = classpathURL;
		}

		return result;
	}

	/**
	 * <p>
	 * Opens an {@link InputStream} which can then be used to read the
	 * identified classpath resource. This method works on the assumption that
	 * the resource is accessible via the {@link ClassLoader} of the specified
	 * loader object.
	 * </p>
	 * 
	 * @param resourceName
	 *            The classpath resource to which to open a stream.
	 * @param loader
	 *            The loader object i.e. the object requesting an
	 *            {@link InputStream input stream} for reading the resource. It
	 *            is also possible to simply specify the {@link Class
	 *            type/class} of the object as the value of this parameter.
	 * 
	 * @return An {@link InputStream} to the identified classpath resource.
	 * @throws ObixException
	 *             If an error occurs creating the resource stream.
	 */
	public static InputStream openStreamToResource(final String resourceName, final Object loader) {
		InputStream result = null;
		String resourcePath;

		if (isClasspathURL(resourceName)) resourcePath = extractPathFromURL(resourceName);
		else resourcePath = resourceName;

		if (loader != null) {
			if (loader instanceof Class<?>) result = ((Class<?>) loader).getResourceAsStream(resourcePath);
			else result = loader.getClass().getResourceAsStream(resourcePath);
		}

		// if not found, then try system ClassLoader i.e.
		// try one level up
		if (result == null) result = ClassLoader.getSystemResourceAsStream(resourceName);

		if (result == null)
			throw new Latency4JException("The identified resource '" + resourceName + "' is not accessible to the "
					+ "class-loader of the requesting object, or " + "the system classloader.");
		return result;
	}

	/**
	 * <p>
	 * Returns the contents of the specified resource as a fixed-size byte
	 * array. This method differs from
	 * {@link #getClasspathResource(String, Object)} by the fact that the caller
	 * can, in effect, restrict the maximum number of bytes to be read from the
	 * resource. Thus if the resource is larger than the specified number of
	 * bytes, the results are truncated.
	 * </p>
	 * 
	 * <p>
	 * This method works on the principle that the specified resource is
	 * accessible via the {@link ClassLoader} of the loading object i.e. the
	 * object on whose behalf behalf the resource is being read.
	 * 
	 * </p>
	 * 
	 * @param resourceName
	 *            The name of the resource to be loaded.
	 * @param loader
	 *            The object requesting the resource. This object's
	 *            {@link ClassLoader} will be used to locate the resource.
	 * @param resourceSize
	 *            The maximum number of bytes to be read from the resource
	 *            stream.
	 * 
	 * @return A byte array containing the resource data.
	 * 
	 * @throws ObixException
	 *             If an error occurs accessing the resource.
	 * 
	 * @see #getClasspathResource(String, Object)
	 */
	public static byte[] getClasspathResource(final String resourceName, final Object loader, final int resourceSize) {
		byte[] result;
		InputStream stream = null;
		try {
			stream = openStreamToResource(resourceName, loader);
			result = StreamReadWriteUtilities.readStreamContentsAsBytes(stream, resourceSize);
		}
		finally {
			IOResourceCloser.close(stream);
		}
		return result;
	}

	/**
	 * <p>
	 * Reads the specified resource from the application's classpath. This
	 * method is similar in functionality to
	 * {@link #getClasspathResource(String, Object, int)}, except that it will
	 * attempt to load the entire resource without regard as to its size.
	 * </p>
	 * 
	 * @param resourceName
	 *            The name of the resource to be loaded.
	 * @param loader
	 *            The object requesting the resource. This object's
	 *            {@link ClassLoader} will be used to locate the resource.
	 * @return A byte array containing the resource data.
	 * @throws ObixException
	 *             If an error occurs accessing the resource.
	 * @see #getClasspathResource(String, Object, int)
	 */
	public static byte[] getClasspathResource(final String resourceName, final Object loader) {
		byte[] result;
		InputStream stream = null;
		try {
			stream = openStreamToResource(resourceName, loader);
			result = StreamReadWriteUtilities.readStreamContentsAsBytes(stream);
		}
		finally {
			IOResourceCloser.close(stream);
		}
		return result;
	}
}
