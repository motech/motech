package org.motechproject.event.aggregation.dispatch;

import org.apache.log4j.Logger;
import org.motechproject.event.aggregation.model.Aggregation;
import org.motechproject.event.aggregation.model.rule.AggregationRuleRecord;
import org.motechproject.event.aggregation.model.AggregationEvent;
import org.motechproject.event.aggregation.repository.AllAggregatedEvents;
import org.motechproject.event.aggregation.repository.AllAggregationRules;
import org.motechproject.event.listener.EventRelay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.lang.String.format;

@Component
public class EventDispatcher {

    private final AllAggregatedEvents allAggregatedEvents;
    private final EventRelay eventRelay;
    private AllAggregationRules allAggregationRules;

    private Logger log = Logger.getLogger(EventDispatcher.class);

    @Autowired
    public EventDispatcher(AllAggregatedEvents allAggregatedEvents, EventRelay eventRelay, AllAggregationRules allAggregationRules) {
        this.allAggregatedEvents = allAggregatedEvents;
        this.eventRelay = eventRelay;
        this.allAggregationRules = allAggregationRules;
    }

    public void dispatch(String aggregationRuleName) {
        AggregationRuleRecord aggregationRule = allAggregationRules.findByName(aggregationRuleName);
        List<Aggregation> aggregations = allAggregatedEvents.findAllAggregations(aggregationRule.getName());
        if (log.isInfoEnabled()) {
            log.info(format("publishing aggregation for rule: %s", aggregationRuleName));
        }
        for (Aggregation aggregation : aggregations) {
            eventRelay.sendEventMessage(new AggregationEvent(aggregationRule, aggregation).toMotechEvent());
            allAggregatedEvents.removeByAggregation(aggregation);
        }
    }
}
