package org.motechproject.event.listener;

import org.motechproject.event.MotechEvent;

public class SampleEventListener implements EventListener {

    @Override
    public void handle(MotechEvent event) {
    }

    @Override
    public String getIdentifier() {
        return "TestEventListener";
    }
}
