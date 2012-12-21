package org.motechproject.eventlogging.osgi;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.eventlogging.service.EventLoggingServiceManager;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

public class EventLoggingBundleIT extends BaseOsgiIT {

    public static final String TEST_EVENT_SUBJECT = "EventLoggingBundleIT";

    public void testEventLoggingServiceManager() throws InterruptedException {
        ServiceReference serviceReference = bundleContext.getServiceReference(EventLoggingServiceManager.class.getName());
        assertNotNull(serviceReference);
        EventLoggingServiceManager eventLoggingServiceManager = (EventLoggingServiceManager) bundleContext.getService(serviceReference);
        assertNotNull(eventLoggingServiceManager);

        serviceReference = bundleContext.getServiceReference(EventRelay.class.getName());
        assertNotNull(serviceReference);
        EventRelay eventRelay = (EventRelay) bundleContext.getService(serviceReference);
        assertNotNull(eventRelay);

        TestEventLoggingService eventLoggingService = new TestEventLoggingService();
        eventLoggingServiceManager.registerEventLoggingService(eventLoggingService);
        eventRelay.sendEventMessage(new MotechEvent(TEST_EVENT_SUBJECT));

        synchronized (eventLoggingService) {
            eventLoggingService.wait(5000);
        }
        assertTrue("Event not logged.", eventLoggingService.isLogged());
    }
}
