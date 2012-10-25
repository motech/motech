package org.motechproject.event.aggregation.dispatch;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.aggregation.model.event.PeriodicDispatchEvent;
import org.motechproject.event.aggregation.model.event.EventStrings;
import org.motechproject.event.listener.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PeriodicDispatcher {

    private EventDispatcher eventDispatcher;

    @Autowired
    public PeriodicDispatcher(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    @MotechListener(subjects = EventStrings.PERIODIC_DISPATCH_EVENT)
    public void handle(MotechEvent event) {
        eventDispatcher.dispatch(new PeriodicDispatchEvent(event).getAggregationRule());
    }
}
