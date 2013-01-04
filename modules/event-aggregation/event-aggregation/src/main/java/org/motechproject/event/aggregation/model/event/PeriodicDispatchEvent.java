package org.motechproject.event.aggregation.model.event;

import org.motechproject.event.MotechEvent;

import java.util.HashMap;
import java.util.Map;

public class PeriodicDispatchEvent {

    private MotechEvent event;

    public PeriodicDispatchEvent(String aggregationRuleName) {
        Map<String, Object> params = new HashMap<>();
        params.put(EventStrings.AGGREGATION_RULE_NAME, aggregationRuleName);
        event = new MotechEvent(EventStrings.PERIODIC_DISPATCH_EVENT, params);
    }

    public PeriodicDispatchEvent(MotechEvent event) {
        this.event = event;
    }

    public String getAggregationRuleName() {
        return (String) event.getParameters().get(EventStrings.AGGREGATION_RULE_NAME);
    }

    public MotechEvent toMotechEvent() {
        return event;
    }
}
