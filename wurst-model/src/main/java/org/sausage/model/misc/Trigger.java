package org.sausage.model.misc;

import java.util.List;

import org.sausage.model.AbstractAsset;

public class Trigger extends AbstractAsset {

	public List<Destination> destinations;
	public List<RoutingRule> routingRules;
	public int maxConcurrentThreads;
	public int maxBatchMessages;
	public boolean suspendOnError;
	public String resourceMonitoringService;
	public boolean concurrent; // serial or concurrent
	public boolean suspendAndRetryLater;
	public long retryIntervalMs;
	
	
	public static class RoutingRule {

		public String condition;
		public String serviceName;
	}
	
	public static class Destination {
		
		public String name;
		public String messageSelector;
		public String durableSubscriberName;
	}
}
