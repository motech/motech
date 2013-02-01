package org.motechproject.testing.utils;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;

import java.util.ArrayList;
import java.util.List;

public class TestEventListener implements EventListener {

    private String id;
    private List<MotechEvent> receivedEvents = new ArrayList<>();

    public TestEventListener(String id) {
        this.id = id;
    }

    @Override
    public void handle(MotechEvent event) {
        synchronized (receivedEvents) {
            receivedEvents.add(event);
            receivedEvents.notifyAll();
        }
    }

    @Override
    public String getIdentifier() {
        return id;
    }

    public List<MotechEvent> getReceivedEvents() {
        return receivedEvents;
    }
}
