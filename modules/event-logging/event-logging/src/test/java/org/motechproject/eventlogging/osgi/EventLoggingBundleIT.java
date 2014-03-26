package org.motechproject.eventlogging.osgi;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.eventlogging.service.EventLoggingServiceManager;
import org.motechproject.testing.osgi.BaseOsgiIT;

public class EventLoggingBundleIT extends BaseOsgiIT {

    public static final String TEST_EVENT_SUBJECT = "EventLoggingBundleIT";

    public void testEventLoggingServiceManager() throws InterruptedException {
        EventLoggingServiceManager eventLoggingServiceManager = getService(EventLoggingServiceManager.class);
        EventRelay eventRelay = getService(EventRelay.class);

        TestEventLoggingService eventLoggingService = new TestEventLoggingService();
        eventLoggingServiceManager.registerEventLoggingService(eventLoggingService);
        eventRelay.sendEventMessage(new MotechEvent(TEST_EVENT_SUBJECT));

        synchronized (eventLoggingService) {
            eventLoggingService.wait(5000);
        }
        assertTrue("Event not logged.", eventLoggingService.isLogged());
    }
}
