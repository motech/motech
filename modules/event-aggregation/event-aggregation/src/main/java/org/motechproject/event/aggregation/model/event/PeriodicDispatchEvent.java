package org.motechproject.event.aggregation.model.event;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.aggregation.model.AggregationRuleRecord;
import org.motechproject.event.aggregation.service.AggregationRule;

import java.util.HashMap;
import java.util.Map;

public class PeriodicDispatchEvent {

    private MotechEvent event;

    public PeriodicDispatchEvent(AggregationRuleRecord aggregationRule) {
        Map<String, Object> params = new HashMap<>();
        params.put(EventStrings.AGGREGATION_RULE, aggregationRule);
        event = new MotechEvent(EventStrings.PERIODIC_DISPATCH_EVENT, params);
    }

    public PeriodicDispatchEvent(MotechEvent event) {
        this.event = event;
    }

    public AggregationRule getAggregationRule() {
        return (AggregationRule) event.getParameters().get(EventStrings.AGGREGATION_RULE);
    }

    public MotechEvent toMotechEvent() {
        return event;
    }
}
