package org.motechproject.event.aggregation.model.event;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.aggregation.model.AggregationRuleRecord;
import org.motechproject.event.aggregation.service.AggregationRule;

import java.util.HashMap;
import java.util.Map;

public class SporadicDispatchEvent {

    private MotechEvent event;

    public SporadicDispatchEvent(AggregationRuleRecord aggregationRule, String expression) {
        Map<String, Object> params = new HashMap<>();
        params.put(EventStrings.AGGREGATION_RULE, aggregationRule);
        params.put(EventStrings.DISPATCH_RULE_EXPRESSION, expression);
        event = new MotechEvent(EventStrings.SPORADIC_DISPATCH_EVENT, params);
    }

    public SporadicDispatchEvent(MotechEvent event) {
        this.event = event;
    }

    public AggregationRule getAggregationRule() {
        return (AggregationRule) event.getParameters().get(EventStrings.AGGREGATION_RULE);
    }

    public String getExpression() {
        return (String) event.getParameters().get(EventStrings.DISPATCH_RULE_EXPRESSION);
    }

    public MotechEvent toMotechEvent() {
        return event;
    }
}
