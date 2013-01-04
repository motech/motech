package org.motechproject.event.aggregation.dispatch;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.aggregation.model.event.EventStrings;
import org.motechproject.event.aggregation.model.event.SporadicDispatchEvent;
import org.motechproject.event.aggregation.repository.AllAggregatedEvents;
import org.motechproject.event.aggregation.rule.RuleAgent;
import org.motechproject.event.aggregation.service.AggregatedEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SporadicDispatcher {

    private EventDispatcher eventDispatcher;
    private AllAggregatedEvents allAggregatedEvents;

    @Autowired
    public SporadicDispatcher(EventDispatcher eventDispatcher, AllAggregatedEvents allAggregatedEvents) {
        this.eventDispatcher = eventDispatcher;
        this.allAggregatedEvents = allAggregatedEvents;
    }

    @MotechListener(subjects = EventStrings.SPORADIC_DISPATCH_EVENT)
    public void handle(MotechEvent event) {
        SporadicDispatchEvent dispatchEvent = new SporadicDispatchEvent(event);
        String aggregationRuleName = dispatchEvent.getAggregationRuleName();
        List<? extends AggregatedEvent> aggregatedEvents = allAggregatedEvents.findByAggregationRule(aggregationRuleName);

        if (new RuleAgent(dispatchEvent.getExpression(), aggregatedEvents).execute()) {
            eventDispatcher.dispatch(aggregationRuleName);
        }
    }
}
