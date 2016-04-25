package org.latency4j.configuration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.latency4j.testutil.BeanTestUtil.testMutatorsAndAccessors;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.latency4j.configuration.CappedRequirementConfiguration;
import org.latency4j.configuration.LatencyRequirementGroupConfig;
import org.latency4j.configuration.StatisticalRequirementConfiguration;

/*
 * Unit test for latency requirement group specification
 * bean
 */
public class LatencyRequirementGroupConfigTest {

	// capped requirements
	private ArrayList<CappedRequirementConfiguration> cappedRequirements;
	private List<StatisticalRequirementConfiguration> statisticalRequirements;

	@Before
	public void setUp() throws Exception {
		this.cappedRequirements = new ArrayList<CappedRequirementConfiguration>();
		for (int i = 0; i < 5; i++)
			this.cappedRequirements.add(createCappedRequirementConfig(i));

		this.statisticalRequirements = new ArrayList<StatisticalRequirementConfiguration>();
		for (int i = 0; i < 5; i++)
			this.statisticalRequirements.add(createStatisticalRequirementConfig(i));
	}

	/*
	 * Validates constructor logic.
	 */
	@Test
	public void testInitialState() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		LatencyRequirementGroupConfig target = new LatencyRequirementGroupConfig();
		assertNotNull(target.getCappedRequirements());
		assertTrue(target.getCappedRequirements().isEmpty());
		assertNotNull(target.getStatisticalRequirements());
		assertTrue(target.getStatisticalRequirements().isEmpty());
	}

	/*
	 * Test property mutators and accessors.
	 */
	@Test
	public void testPropertyMutatorsAndAccessors() throws Exception {
		LatencyRequirementGroupConfig target = new LatencyRequirementGroupConfig();
		testMutatorsAndAccessors(target, this.cappedRequirements, "cappedRequirements");
		testMutatorsAndAccessors(target, this.statisticalRequirements, "statisticalRequirements");
	}

	// utility method for creating dummy requirements
	private CappedRequirementConfiguration createCappedRequirementConfig(final int requirementIndex) {
		CappedRequirementConfiguration result = new CappedRequirementConfiguration();
		result.setPersistenceManagerClass("Amadou and Miriam");
		result.setWorkCategory("Fast Food Senegal " + requirementIndex);
		return result;
	}

	private StatisticalRequirementConfiguration createStatisticalRequirementConfig(final int requirementIndex) {
		StatisticalRequirementConfiguration result = new StatisticalRequirementConfiguration();
		result.setPersistenceManagerClass("Bright Chimezie");
		result.setWorkCategory("Zigima All Stars " + requirementIndex);
		return result;
	}
}// end class def
