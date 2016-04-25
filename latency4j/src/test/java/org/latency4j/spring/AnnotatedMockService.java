package org.latency4j.spring;

import org.latency4j.MonitoredByLatency4J;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotatedMockService {
	private static Logger logger = LoggerFactory.getLogger(AnnotatedMockService.class);

	public static int CONFIGURED_STATS_THRESHOLD = 5;

	@MonitoredByLatency4J("StatsServiceRequirement")
	public void methodWithStatisticalRequirement(final int i) {
		try {
			logger.info("Monitred service without category invoked ...");
			if (i >= CONFIGURED_STATS_THRESHOLD) Thread.sleep(i * 100L);
		} catch (Throwable t) {
			logger.error("error occured", t);
		}
	}

	@MonitoredByLatency4J("CappedServiceRequirement")
	public void methodWithCappedRequirement(final int i) {
		try {
			logger.info("monitored service with category invoked ...");
			Thread.sleep((i * 100L) + i);
		} catch (Throwable t) {
			logger.error("error occured", t);
		}
	}

	@MonitoredByLatency4J("CappedServiceRequirement")
	public void exceptionMethod() {
		logger.info("exception service without category " + "invoked ... will throw an exception");
		throw new RuntimeException("no error occured!");
	}
}
