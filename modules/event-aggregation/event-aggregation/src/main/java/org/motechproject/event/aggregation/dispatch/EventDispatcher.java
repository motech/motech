package org.motechproject.event.aggregation.dispatch;

import org.motechproject.event.aggregation.model.Aggregation;
import org.motechproject.event.aggregation.model.event.AggregationEvent;
import org.motechproject.event.aggregation.repository.AllAggregatedEvents;
import org.motechproject.event.aggregation.service.AggregationRule;
import org.motechproject.event.listener.EventRelay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventDispatcher {

    private final AllAggregatedEvents allAggregatedEvents;
    private final EventRelay eventRelay;

    @Autowired
    public EventDispatcher(AllAggregatedEvents allAggregatedEvents, EventRelay eventRelay) {
        this.allAggregatedEvents = allAggregatedEvents;
        this.eventRelay = eventRelay;
    }

    public void dispatch(AggregationRule aggregationRule) {
        List<Aggregation> aggregations = allAggregatedEvents.findAllAggregations(aggregationRule.getName());
        for (Aggregation aggregation : aggregations) {
            eventRelay.sendEventMessage(new AggregationEvent(aggregationRule, aggregation).toMotechEvent());
        }
        allAggregatedEvents.removeByAggregationRule(aggregationRule.getName());
    }
}
