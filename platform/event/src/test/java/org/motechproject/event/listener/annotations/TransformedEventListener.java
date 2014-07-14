package org.motechproject.event.listener.annotations;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;

public class TransformedEventListener implements EventListener {

    private static final String EVENT_SUBJECT = "EVENT_UNIQUE_ID_TEST";

    private boolean eventHandled;
    private MotechEvent event;

    @Override
    public String getIdentifier() {
        return EVENT_SUBJECT;
    }

    @MotechListener(subjects = EVENT_SUBJECT)
    public synchronized void handle(MotechEvent motechEvent) {
        eventHandled = true;
        event = motechEvent;
        notifyAll();
    }

    public MotechEvent getEvent() {
        return event;
    }

    public boolean isEventHandled() {
        return eventHandled;
    }
}