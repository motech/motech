package org.motechproject.event;

import org.motechproject.event.listener.EventListener;
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
