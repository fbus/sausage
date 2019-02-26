package org.sausage.grinder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.sausage.model.Asset;
import org.sausage.model.misc.Trigger;
import org.sausage.model.misc.Trigger.Destination;
import org.sausage.model.misc.Trigger.RoutingRule;

import com.wm.app.b2b.server.jms.consumer.JMSTrigger;
import com.wm.lang.ns.NSTrigger;
import com.wm.msg.JMSDestinationFilterPair;
import com.wm.msg.JMSRoutingRule;

public class TriggerGrinder {
	
	public static Asset convert(NSTrigger node) {
		Trigger result = new Trigger();
		
		if (!(node instanceof JMSTrigger)) {
			throw new IllegalArgumentException("unhandled trigger type : " + node.getClass());
		}
		JMSTrigger jmsTrig = (JMSTrigger) node;
		
		List<JMSRoutingRule> rules = jmsTrig.getRoutingRules();
		List<JMSDestinationFilterPair> filters = jmsTrig.getDestinationFilterPairs();
		
		if (filters != null && !filters.isEmpty()) {
			result.destinations = new ArrayList<Destination>();
			
			for (JMSDestinationFilterPair filter : filters) {
				Destination d = new Destination();
				d.messageSelector = StringUtils.trimToNull(filter.getMesageSelector());
				d.name = filter.getDestinationName();
				d.durableSubscriberName = filter.getDurableSubscriberName();
				result.destinations.add(d);
			}
		}
		
		if (rules != null && !rules.isEmpty()) {
			result.routingRules = new ArrayList<RoutingRule>(rules.size());
			for (JMSRoutingRule rule : rules) {
				RoutingRule rul = new RoutingRule();
				if (rule.getFilter().getSource() != null && !rule.getFilter().getSource().trim().isEmpty()) {
					rul.condition = rule.getFilter().getSource();
				}
				rul.serviceName = rule.getServiceName().getFullName();
				result.routingRules.add(rul);
			}
		}
		
		if(jmsTrig.allowsConcurrent()) {
			result.concurrent = true;
			result.maxConcurrentThreads = jmsTrig.getMaxConcurrentThreads();
			result.maxBatchMessages = jmsTrig.getBatchSize();
		} 
		
		result.suspendOnError = jmsTrig.getSerialSuspendOnError();
		if(jmsTrig.getRedeliveryFailureAction() == NSTrigger.FAILURE_ACTION_SUSPEND_AND_NACK) {
			result.suspendAndRetryLater = true;
			result.retryIntervalMs = jmsTrig.getRedeliveryDelay();
		}
		if(jmsTrig.getResourceMonitoringSvcName() != null) {
			result.resourceMonitoringService = jmsTrig.getResourceMonitoringSvcName().getFullName();
		}
		return result;
	}
	
}
