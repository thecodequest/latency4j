package org.latency4j.testutil;

import java.util.Random;

import org.latency4j.Latency4JException;

/**
 * <p>
 * A {@link CharacterSource character source} implementation which returns
 * randomly selected characters from the ISO-Latin character set.
 * </p>
 */
public class RandomCharacterGenerator {
	/**
	 * <p>
	 * Prime used to seed internal random index generator.
	 * </p>
	 */
	private static final int LARGE_PRIME = 19341;

	/**
	 * <p>
	 * Internal code {@value #TYPE_UPPERCASE_CHAR} which is used when randomly
	 * selecting {@link #nextChar() which type of character to generate}. An
	 * {@link #random internal random value} of this value will result in the
	 * {@link #nextChar()} method generating an upper-case character.
	 * </p>
	 */
	private static final int TYPE_UPPERCASE_CHAR = 0;

	/**
	 * <p>
	 * Internal code {@value #TYPE_LOWERCASE_CHAR} which is used when randomly
	 * selecting {@link #nextChar() which type of character to generate}. An
	 * {@link #random internal random value} of this value will result in the
	 * {@link #nextChar()} method generating a lower-case character.
	 * </p>
	 */
	private static final int TYPE_LOWERCASE_CHAR = 1;

	/**
	 * <p>
	 * Internal code {@value #TYPE_ALPHA_CHAR} which is used when randomly
	 * selecting {@link #nextChar() which type of character to generate}. An
	 * {@link #random internal random value} of this value will result in the
	 * {@link #nextChar()} method generating an alphabet between
	 * <code>a/A and z/Z</code>.
	 * </p>
	 */
	private static final int TYPE_ALPHA_CHAR = 2;

	/**
	 * <p>
	 * Internal code {@value #TYPE_NUMERIC_CHAR} which is used when randomly
	 * selecting {@link #nextChar() which type of character to generate}. An
	 * {@link #random internal random value} of this value will result in the
	 * {@link #nextChar()} method generating a number.
	 * </p>
	 */
	private static final int TYPE_NUMERIC_CHAR = 3;

	/**
	 * <p>
	 * Internal code {@value #TYPE_ALPHA_NUMERIC_CHAR} which is used when
	 * randomly selecting {@link #nextChar() which type of character to
	 * generate}. An {@link #random internal random value} of this value will
	 * result in the {@link #nextChar()} method generating an alpha-numeric
	 * character.
	 * </p>
	 */
	private static final int TYPE_ALPHA_NUMERIC_CHAR = 4;

	/**
	 * <p>
	 * Internal code {@value #TYPE_NON_ALPHANUMERIC_CHAR} which is used when
	 * randomly selecting {@link #nextChar() which type of character to
	 * generate}. An {@link #random internal random value} of this value will
	 * result in the {@link #nextChar()} method generating a non-alpha-numeric
	 * character.
	 * </p>
	 */
	private static final int TYPE_NON_ALPHANUMERIC_CHAR = 5;

	/**
	 * <p>
	 * Internal array which holds all the upper-case characters in the English
	 * alphabet.
	 * </p>
	 */
	private static final char[] UPPER_CASE_CHARS = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
			'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

	/**
	 * <p>
	 * Internal array which holds all the lower-case characters in the English
	 * alphabet.
	 * </p>
	 */
	private static final char[] LOWER_CASE_CHARS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
			'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

	/**
	 * <p>
	 * Internal array which holds the digits from 0 to 9.
	 * </p>
	 */
	private static final char[] NUMERIC_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

	/**
	 * <p>
	 * Internal array which holds the punctuation symbols in the English
	 * language.
	 * </p>
	 */
	private static final char[] SPECIAL_CHARS = { '`', '!', '\"', '$', '%', '^', '&', '*', '(', ')', '-', '_', '+', '=',
			'{', '}', '[', ']', ':', ';', '@', '\'', '~', '#', '<', '>', ',', '.', '?', '/', '|' };

	/**
	 * <p>
	 * Internal random number generator.
	 * </p>
	 */
	private final Random random;

	/**
	 * <p>
	 * Creates an instance using the given random number generator.
	 * </p>
	 * 
	 * @param random
	 *            The random number generator with which to initialise the
	 *            instance.
	 */
	public RandomCharacterGenerator(final Random random) {
		this.random = random;
	}

	/**
	 * <p>
	 * Creates an instance, initialising the internal random index generator
	 * with the given seed value.
	 * </p>
	 * 
	 * @param seed
	 *            The seed with which to initialise the internal index
	 *            generator.
	 */
	public RandomCharacterGenerator(final int seed) {
		this(new Random(seed));
	}

	/**
	 * <p>
	 * Default constructor. This initialises the internal generator with the
	 * modulus of the system time and a suitably large prime number
	 * {@value #LARGE_PRIME}.
	 * </p>
	 */
	public RandomCharacterGenerator() {
		this((int) (System.currentTimeMillis() % LARGE_PRIME));
	}

	/**
	 * <p>
	 * Generates a string, which is no longer than the specified length, and
	 * consists entirely of upper-case characters.
	 * </p>
	 * 
	 * @param maxLength
	 *            The maximum length of the string to generate.
	 * 
	 * @return A string, which is no longer than the specified length and
	 *         consists entirely of upper-case characters.
	 */
	public synchronized String upperCaseString(final int maxLength) {
		return internalRandomString(maxLength, TYPE_UPPERCASE_CHAR);
	}

	/**
	 * <p>
	 * Generates a string, which is no longer than the specified length, and
	 * consists entirely of lower-case characters.
	 * </p>
	 * 
	 * @param maxLength
	 *            The maximum length of the string to generate.
	 * 
	 * @return A string, which is no longer than the specified length and
	 *         consists entirely of lower-case characters.
	 */
	public synchronized String lowerCaseString(final int maxLength) {
		return internalRandomString(maxLength, TYPE_LOWERCASE_CHAR);
	}

	/**
	 * <p>
	 * Generates a string, which is no longer than the specified length, and
	 * consists entirely of characters from the English alphabet.
	 * </p>
	 * 
	 * @param maxLength
	 *            The maximum length of the string to generate.
	 * 
	 * @return A string, which is no longer than the specified length and
	 *         consists entirely of characters from the English alphabet.
	 */
	public synchronized String alphaString(final int maxLength) {
		return internalRandomString(maxLength, TYPE_ALPHA_CHAR);
	}

	/**
	 * <p>
	 * Generates a string, which is no longer than the specified length, and
	 * consists entirely of numbers.
	 * </p>
	 * 
	 * @param maxLength
	 *            The maximum length of the string to generate.
	 * 
	 * @return A string, which is no longer than the specified length and
	 *         consists entirely of numbers.
	 */
	public synchronized String numericString(final int maxLength) {
		return internalRandomString(maxLength, TYPE_NUMERIC_CHAR);
	}

	/**
	 * <p>
	 * Generates a string, which is no longer than the specified length, and
	 * consists entirely of characters from the English alphabet and/or numbers.
	 * </p>
	 * 
	 * @param maxLength
	 *            The maximum length of the string to generate.
	 * 
	 * @return A string, which is no longer than the specified length and
	 *         consists entirely of characters from the English alphabet and/or
	 *         numbers.
	 */
	public synchronized String alphaNumericString(final int maxLength) {
		return internalRandomString(maxLength, TYPE_ALPHA_NUMERIC_CHAR);
	}

	/**
	 * <p>
	 * Generates a string, which is no longer than the specified length, and
	 * does not contain any letters or digits.
	 * </p>
	 * 
	 * @param maxLength
	 *            The maximum length of the string to generate.
	 * 
	 * @return A string, which is no longer than the specified length and does
	 *         not contain any letters or digits.
	 */
	public synchronized String nonAlphaNumericString(final int maxLength) {
		return internalRandomString(maxLength, TYPE_NON_ALPHANUMERIC_CHAR);
	}

	/**
	 * <p>
	 * Generates a string, which is no longer than the specified length.
	 * </p>
	 * 
	 * @param maxLength
	 *            The maximum length of the string to generate.
	 * 
	 * @return A string, which is no longer than the specified length.
	 */
	public synchronized String string(final int maxLength) {
		return internalRandomString(maxLength, TYPE_NON_ALPHANUMERIC_CHAR + 1);
	}

	public synchronized char nextUpperCaseChar() {
		char result;
		int resultIndex = random.nextInt(UPPER_CASE_CHARS.length);
		result = UPPER_CASE_CHARS[resultIndex];
		return result;
	}

	public synchronized char nextLowerCaseChar() {
		char result;
		int resultIndex = random.nextInt(LOWER_CASE_CHARS.length);
		result = LOWER_CASE_CHARS[resultIndex];
		return result;
	}

	public synchronized char nextNonAlphaNumericChar() {
		char result;
		int resultIndex = random.nextInt(SPECIAL_CHARS.length);
		result = SPECIAL_CHARS[resultIndex];
		return result;
	}

	public synchronized char nextNumericChar() {
		char result;
		int resultIndex = random.nextInt(NUMERIC_CHARS.length);
		result = NUMERIC_CHARS[resultIndex];
		return result;
	}

	public synchronized char nextAlphaChar() {
		char result;
		int typeOfCharToGenerate = random.nextInt(TYPE_LOWERCASE_CHAR + 1);
		switch (typeOfCharToGenerate) {
			case TYPE_UPPERCASE_CHAR:
				result = nextUpperCaseChar();
				break;
			case TYPE_LOWERCASE_CHAR:
				result = nextLowerCaseChar();
				break;
			default:
				throw new Latency4JException("Application coding error. Invalid case detected in call to get "
						+ "nextAlphaNumeric char! Possible random number error!");
		}
		return result;
	}

	public synchronized char nextAlphaNumericChar() {
		char result;
		int typeOfCharToGenerate = random.nextInt(TYPE_NUMERIC_CHAR + 1);
		switch (typeOfCharToGenerate) {
			case TYPE_UPPERCASE_CHAR:
				result = nextUpperCaseChar();
				break;
			case TYPE_LOWERCASE_CHAR:
				result = nextLowerCaseChar();
				break;
			case TYPE_ALPHA_CHAR:
				result = nextAlphaChar();
				break;
			case TYPE_NUMERIC_CHAR:
				result = nextNumericChar();
				break;
			default:
				throw new Latency4JException("Application coding error. Invalid case detected in call "
						+ "to get nextAlphaNumeric char! Possible random number error!");
		}
		return result;
	}

	public synchronized char nextChar() {
		char result;
		int typeOfCharToGenerate = random.nextInt(TYPE_NON_ALPHANUMERIC_CHAR + 1);

		switch (typeOfCharToGenerate) {
			case TYPE_UPPERCASE_CHAR:
				result = nextUpperCaseChar();
				break;
			case TYPE_LOWERCASE_CHAR:
				result = nextLowerCaseChar();
				break;
			case TYPE_ALPHA_CHAR:
				result = nextAlphaChar();
				break;
			case TYPE_NUMERIC_CHAR:
				result = nextNumericChar();
				break;
			case TYPE_ALPHA_NUMERIC_CHAR:
				result = nextAlphaNumericChar();
				break;
			case TYPE_NON_ALPHANUMERIC_CHAR:
				result = nextNonAlphaNumericChar();
				break;
			default:
				throw new Latency4JException("Application coding error. Invalid case (" + typeOfCharToGenerate
						+ ") detected! Possible random number error!");
		}
		return result;
	}

	/**
	 * <p>
	 * Generates a random string of the given type.
	 * </p>
	 * 
	 * @param maxLength
	 *            The maximum length of the string to generate.
	 * 
	 * @param typeOfCharToGenerate
	 *            The type of character with which to populate the returned
	 *            string. The value of this parameter must be one of
	 *            {@link #LARGE_PRIME}, {@link #TYPE_UPPERCASE_CHAR},
	 *            {@link #TYPE_LOWERCASE_CHAR}, {@link #TYPE_ALPHA_CHAR},
	 *            {@link #TYPE_NUMERIC_CHAR}, {@link #TYPE_ALPHA_NUMERIC_CHAR},
	 *            {@link #TYPE_NON_ALPHANUMERIC_CHAR}.
	 * 
	 * @return A string of the given type no longer than the given length.
	 */
	private String internalRandomString(final int maxLength, final int typeOfCharToGenerate) {
		StringBuffer result = new StringBuffer();

		int stringLength = random.nextInt(maxLength + 1);
		if (stringLength <= 0) stringLength = maxLength;

		switch (typeOfCharToGenerate) {
			case TYPE_UPPERCASE_CHAR:
				for (int i = 0; i < stringLength; i++)
					result.append(nextUpperCaseChar());
				break;
			case TYPE_LOWERCASE_CHAR:
				for (int i = 0; i < stringLength; i++)
					result.append(nextLowerCaseChar());
				break;
			case TYPE_ALPHA_CHAR:
				for (int i = 0; i < stringLength; i++)
					result.append(nextAlphaChar());
				break;
			case TYPE_NUMERIC_CHAR:
				for (int i = 0; i < stringLength; i++)
					result.append(nextNumericChar());
				break;
			case TYPE_ALPHA_NUMERIC_CHAR:
				for (int i = 0; i < stringLength; i++)
					result.append(nextAlphaNumericChar());
				break;
			case TYPE_NON_ALPHANUMERIC_CHAR:
				for (int i = 0; i < stringLength; i++)
					result.append(nextNonAlphaNumericChar());
				break;
			default:
				for (int i = 0; i < stringLength; i++)
					result.append(nextChar());
		} // end switch

		return result.toString();
	}
}