package org.motechproject.event.listener;

import org.motechproject.event.MotechEvent;

public class SampleEventListener implements EventListener {

    private String identifier;

    public SampleEventListener() {
        this("TestEventListener");
    }

    public SampleEventListener(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public void handle(MotechEvent event) {
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }
}
