package org.motechproject.server.mock;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

public class MockEventAdmin implements EventAdmin {

    @Override
    public void postEvent(Event event) {
    }

    @Override
    public void sendEvent(Event event) {
    }
}
