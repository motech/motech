package org.motechproject.event.aggregation.aggregate;

import org.apache.log4j.Logger;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.aggregation.model.event.AggregatedEventRecord;
import org.motechproject.event.aggregation.model.rule.AggregationRuleRecord;
import org.motechproject.event.aggregation.model.rule.AggregationState;
import org.motechproject.event.aggregation.repository.AllAggregatedEvents;
import org.motechproject.event.aggregation.repository.AllAggregationRules;
import org.motechproject.event.listener.EventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class EventAggregator implements EventListener {

    private AllAggregatedEvents allAggregatedEvents;

    private String aggregationRuleName;
    private List<String> fields;
    private AllAggregationRules allAggregationRules;

    private Logger logger = Logger.getLogger(EventAggregator.class);

    public EventAggregator(String aggregationRuleName, List<String> fields, AllAggregatedEvents allAggregatedEvents, AllAggregationRules allAggregationRules) {
        this.aggregationRuleName = aggregationRuleName;
        this.fields = fields;
        this.allAggregatedEvents = allAggregatedEvents;
        this.allAggregationRules = allAggregationRules;
    }

    @Override
    public String getIdentifier() {
        return aggregationRuleName;
    }

    @Override
    public void handle(MotechEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("aggregating: " + event);
        }
        AggregationRuleRecord rule = allAggregationRules.findByName(aggregationRuleName);
        if (rule.getState().equals(AggregationState.Paused)) {
            if (logger.isDebugEnabled()) {
                logger.debug("aggregation[" + aggregationRuleName + "] is paused, ignoring events.");
            }
            return;
        }

        Map<String, Object> aggregationParameters = new TreeMap<>();
        Map<String, Object> nonAggregationParameters = new HashMap<>();
        for (String key : event.getParameters().keySet()) {
            if (fields.contains(key)) {
                aggregationParameters.put(key, event.getParameters().get(key));
            } else {
                nonAggregationParameters.put(key, event.getParameters().get(key));
            }
        }
        AggregatedEventRecord aggregatedEvent = new AggregatedEventRecord(aggregationRuleName, aggregationParameters, nonAggregationParameters, hasFieldMissing(event));

        allAggregatedEvents.add(aggregatedEvent);
    }

    private boolean hasFieldMissing(MotechEvent event) {
        for (String field : fields) {
            if (!event.getParameters().containsKey(field)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EventAggregator that = (EventAggregator) o;

        if (!aggregationRuleName.equals(that.aggregationRuleName)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return aggregationRuleName.hashCode();
    }
}
