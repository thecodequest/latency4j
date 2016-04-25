package org.latency4j.configuration;

import org.latency4j.persistence.DefaultWorkDurationPersistenceManager;

/*
 * Test persistence manager which is used just for testing epsilon xml configuration.
 */
public class ConfigurationTestPersister extends DefaultWorkDurationPersistenceManager {
	public ConfigurationTestPersister() {
		super();
	}
}
