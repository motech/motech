package org.motechproject.event.aggregation.model.event;

import org.motechproject.event.MotechEvent;

import java.util.HashMap;
import java.util.Map;

public class SporadicDispatchEvent {

    private MotechEvent event;

    public SporadicDispatchEvent(String aggregationRuleName, String expression) {
        Map<String, Object> params = new HashMap<>();
        params.put(EventStrings.AGGREGATION_RULE_NAME, aggregationRuleName);
        params.put(EventStrings.DISPATCH_RULE_EXPRESSION, expression);
        event = new MotechEvent(EventStrings.SPORADIC_DISPATCH_EVENT, params);
    }

    public SporadicDispatchEvent(MotechEvent event) {
        this.event = event;
    }

    public String getAggregationRuleName() {
        return (String) event.getParameters().get(EventStrings.AGGREGATION_RULE_NAME);
    }

    public String getExpression() {
        return (String) event.getParameters().get(EventStrings.DISPATCH_RULE_EXPRESSION);
    }

    public MotechEvent toMotechEvent() {
        return event;
    }
}
