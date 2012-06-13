package org.motechproject.server.event;

import org.motechproject.model.MotechEvent;

public class OtherSampleEventListener implements EventListener {

    @Override
    public void handle(MotechEvent event) {

    }

    @Override
    public String getIdentifier() {
        return "TestEventListener2";
    }

}
