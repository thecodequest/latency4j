package org.latency4j.persistence;

import java.util.ArrayList;
import java.util.List;

import org.latency4j.WorkDuration;
import org.latency4j.persistence.DefaultWorkDurationPersistenceManager;

/*
 * PersistenceManager which does not save or load historical
 * durations. This is used for testing only so as to prevent 
 * historical data skewing test results.
 */
public class NullPersistenceManager extends DefaultWorkDurationPersistenceManager {
	@Override
	public synchronized void save(final WorkDuration taskDuration) {}

	@Override
	public synchronized List<WorkDuration> loadHistoricalData(final String workCategory) {
		return new ArrayList<WorkDuration>();
	}
}
