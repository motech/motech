package org.motechproject.scheduler.context;

import org.motechproject.scheduler.event.EventRelay;
import org.motechproject.scheduler.domain.MotechEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventContext {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EventRelay eventRelay;

    public EventRelay getEventRelay() {
        log.info("EventContext: " + this + " GetEventRelay: " + eventRelay);
        return eventRelay;
    }

    public void setEventRelay(EventRelay eventRelay) {
        log.info("EventContext: " + this + " SetEventRelay: " + eventRelay);
        this.eventRelay = eventRelay;
    }

    /**
     * Responsible for packing parameters in order with keys: 0,1,2 ... n
     * parameters can be unpacked in handlers using
     * {@code
     *
     * @MotechListener(subjects={"destination"}, type=MotechListenerType.ORDERED_PARAMETERS)
     * public void handler(Type1 a, Type2 b ...) {}
     * }
     * @param destination
     * @param objs
     */
    public void send(String destination, Object... objs) {
        MotechEvent event = new MotechEvent(destination);
        int i = 0;
        for (Object o : objs) {
            event.getParameters().put(Integer.toString(i++), o);
        }
        eventRelay.sendEventMessage(event);
    }

    public static EventContext getInstance() {
        return instance;
    }

    private static EventContext instance = new EventContext();

    private EventContext() {
    }
}
