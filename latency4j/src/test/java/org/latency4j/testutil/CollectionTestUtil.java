package org.latency4j.testutil;

import java.util.Collection;

import org.junit.Ignore;

/*
 * <p>
 * Utility class which provides helper methods
 * to simplify the use of collections in tests.
 * </p>
 */
@Ignore
public final class CollectionTestUtil {
	/*
	 * <p> Private constructor to prevent accidental initialisation of utility
	 * class. </p>
	 */
	private CollectionTestUtil() {}

	/*
	 * Tests that the given collections are equal. Two collections are
	 * considered equal if both are the same size and either of them can be
	 * shown to be a subset of the other.
	 * 
	 * @param lhs The left-side of the comparison.
	 * 
	 * @param rhs The right-side of the comparison.
	 * 
	 * @return <code>True</code> if the given collections are equal and
	 * <code>False</code> otherwise.
	 */
	public static boolean collectionsEqual(final Collection<?> lhs, final Collection<?> rhs) {
		boolean result = false;

		if (lhs.size() == rhs.size() && lhs.containsAll(rhs)) result = true;

		return result;
	}
}
