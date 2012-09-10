package org.motechproject.event;

public class SampleEventListener implements EventListener {

    @Override
    public void handle(MotechEvent event) {
    }

    @Override
    public String getIdentifier() {
        return "TestEventListener";
    }
}
