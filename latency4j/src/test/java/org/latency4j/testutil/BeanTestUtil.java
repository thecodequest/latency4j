package org.latency4j.testutil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.latency4j.util.IOResourceCloser.close;

import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.springframework.beans.BeanUtils;

/*
 * Utility class for testing default behaviour 
 * and functionality of POJO Beans. 
 */
public class BeanTestUtil {

	private static final Random randomNumberGenerator = new Random(System.currentTimeMillis());
	private static final RandomCharacterGenerator textGenerator = new RandomCharacterGenerator(randomNumberGenerator);

	/*
	 * Create collection of test strings.
	 * 
	 * @param size The size of the collection to create i.e. the number of test
	 * elements in the collection.
	 * 
	 * @param maxStringLength The maximum size of the generated strings.
	 * 
	 * @return A collection of test strings of the given size.
	 */
	public synchronized static List<String> createTestStringCollection(final int size, final int maxStringLength) {
		List<String> result = new ArrayList<String>();
		int maxTestStringSize = randomInt(maxStringLength);
		for (int i = 0; i < size; i++)
			result.add(createRandomAlphaNumericString(maxTestStringSize));
		return result;
	}

	/*
	 * Utility method which creates a random string no longer than the given
	 * length. This is intended as a convenience method for generating test
	 * data.
	 * 
	 * @param maxLength The maximum length of the string to create.
	 */
	public synchronized static String createRandomString(final int maxLength) {
		String result;
		result = textGenerator.string(maxLength);
		return result;
	}

	/*
	 * Utility method which creates a random alpha-numeric string no longer than
	 * the given length. This is intended as a convenience method for generating
	 * test data.
	 * 
	 * @param maxLength The maximum length of the string to create.
	 */
	public synchronized static String createRandomAlphaNumericString(final int maxLength) {
		String result;
		result = textGenerator.alphaNumericString(maxLength);
		return result;
	}

	/*
	 * Utility method which generates a positive random number no greater than
	 * the given value.
	 * 
	 * @param maxValue The maximum value of the random number to generate.
	 */
	public synchronized static int randomInt(final int maxValue) {
		int result;
		result = randomNumberGenerator.nextInt(maxValue);
		if (result <= 0) result = maxValue;
		return result;
	}

	/*
	 * Utility method which generates a positive fraction between 0 and .9.
	 */
	public synchronized static double randomFraction() {
		return randomInt(99) / 100;
	}

	/*
	 * Tests that the value of a given property on a bean matches an expected
	 * value. This method can be used to test the effectiveness of bean mutator
	 * methods.
	 * 
	 * @param bean The bean being tested.
	 * 
	 * @param expectedValue The expected value of the property.
	 * 
	 * @param propertyName The name of the property whose value is to be
	 * validated.
	 * 
	 * @throws IllegalAccessException
	 * 
	 * @throws InvocationTargetException
	 * 
	 * @throws NoSuchMethodException
	 */
	public static void testInitialPropertyValue(final Object bean, final Object expectedValue,
			final String propertyName) throws Exception {
		assertPropertyValue(bean, expectedValue, propertyName);
	}

	private static Object getPropertyValue(final Object bean, final String propertyName) throws Exception {
		Object result;
		Class<?> clazz = bean.getClass();
		PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(clazz, propertyName);
		Method accessor = descriptor.getReadMethod();
		result = accessor.invoke(bean);
		return result;
	}

	private static void setPropertyValue(final Object bean, final String propertyName, final Object propertyValue)
			throws Exception {
		Class<?> clazz = bean.getClass();
		PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(clazz, propertyName);
		Method mutator = descriptor.getWriteMethod();
		mutator.invoke(bean, propertyValue);
	}

	/*
	 * The same as {@link #testMutatorsAndAccessors(Object, Object, String,
	 * boolean)} except that it does not test acceptance of null property
	 * values.
	 * 
	 * @param bean The bean being tested.
	 * 
	 * @param value The value with which to test the bean property's set and get
	 * methods.
	 * 
	 * @param propertyName The name of the property whose get and set methods
	 * are being tested.
	 * 
	 * @throws IllegalAccessException
	 * 
	 * @throws InvocationTargetException
	 * 
	 * @throws NoSuchMethodException
	 */
	public static void testMutatorsAndAccessors(final Object bean, final Object value, final String propertyName)
			throws Exception {
		testMutatorsAndAccessors(bean, value, propertyName, false);
	}

	/*
	 * Tests a bean property's get and set methods with a given value. It can
	 * also tests that these methods accept and return null values.
	 * 
	 * @param bean The bean whose property get and set methods are to be tested.
	 * 
	 * @param value The value with which to test the get and set methods.
	 * 
	 * @param propertyName The name of the bean property whose get and set
	 * methods are to be tested.
	 * 
	 * @param testNull If true, it will also test that the property mutator
	 * accepts a null value, and, that when invoked in that way, the
	 * corresponding getter method will return null thereafter.
	 * 
	 * @throws IllegalAccessException
	 * 
	 * @throws InvocationTargetException
	 * 
	 * @throws NoSuchMethodException
	 */
	public static void testMutatorsAndAccessors(final Object bean, final Object value, final String propertyName,
			final boolean testNull) throws Exception {
		setPropertyValue(bean, propertyName, value);
		assertPropertyValue(bean, value, propertyName);
		if (testNull) {
			setPropertyValue(bean, propertyName, null);
			assertPropertyValue(bean, null, propertyName);
		}
	}

	private static void assertPropertyValue(final Object bean, final Object expectedValue, final String propertyName)
			throws Exception {
		Object valueAfterSet = getPropertyValue(bean, propertyName);
		if (expectedValue != null) assertEquals("Field " + propertyName + " on class '" + bean.getClass().getName()
				+ "' does not match expected value.", expectedValue, valueAfterSet);
		else assertNull("Field " + propertyName + " on class '" + bean.getClass().getName() + "' should be null.",
				valueAfterSet);
	}

	/*
	 * Tests if two collections are identical in structure. This is achieved by
	 * comparing the size of both collections, and where both are equal in size,
	 * then one of them must also contain all the elements of the other. The
	 * validity of this test can be shown with a relatively simple proof by
	 * contradiction.
	 * 
	 * @param expected The base of the comparison i.e. the expected collection.
	 * 
	 * @param actual The collection to compare to the expected collection.
	 * 
	 * @return True of both collections are the same size and the expected
	 * collection contains all the elements in the collection which is being
	 * compared to it.
	 */
	public static boolean collectionsSame(final Collection<? extends Object> expected,
			final Collection<? extends Object> actual) {
		boolean result = expected.size() == actual.size() && expected.containsAll(actual);
		return result;
	}

	/*
	 * Tests that the given object is serializable, and that object state is not
	 * affected by serialization. Note that this method relies on the Object's
	 * equals(...) method for comparison.
	 */
	public static void testSerialization(final Object fixture) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream outputByteStream = null;
		ObjectOutputStream objectOutputStream = null;
		ByteArrayInputStream inputByteStream = null;
		ObjectInputStream objectInputStream = null;

		try {
			// write object
			outputByteStream = new ByteArrayOutputStream();
			objectOutputStream = new ObjectOutputStream(outputByteStream);
			objectOutputStream.writeObject(fixture);
			objectOutputStream.flush();
			objectOutputStream.close();

			// read the object again
			byte[] objectRep = outputByteStream.toByteArray();
			inputByteStream = new ByteArrayInputStream(objectRep);
			objectInputStream = new ObjectInputStream(inputByteStream);
			Object readFixture = objectInputStream.readObject();
			objectInputStream.close();

			assertEquals("De-serialised object is not the same as serialised object.", fixture, readFixture);
		}
		finally {
			close(outputByteStream);
			close(objectOutputStream);
			close(inputByteStream);
			close(objectInputStream);
		}
	}
}