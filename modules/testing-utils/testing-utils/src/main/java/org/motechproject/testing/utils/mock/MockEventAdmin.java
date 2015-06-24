package org.motechproject.testing.utils.mock;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 * A mock implementation of the {@link EventAdmin} class, that can be used
 * in the Spring context integration tests, where there are no OSGi classes available.
 * The methods of this mock do nothing.
 */
public class MockEventAdmin implements EventAdmin {

    @Override
    public void postEvent(Event event) {
    }

    @Override
    public void sendEvent(Event event) {
    }
}
