package org.motechproject.event.osgi;


import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistry;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.Manifest;

public class EventBundleIT extends BaseOsgiIT {

    public void testEventListener() throws Exception {
        final String subject = "OSGi IT - 001";
        final Object waitLock = new Object();
        final ArrayList<String> receivedEvents = new ArrayList<>();

        ServiceReference registryReference = bundleContext.getServiceReference(EventListenerRegistryService.class.getName());
        assertNotNull(registryReference);
        EventListenerRegistry registry = (EventListenerRegistry) bundleContext.getService(registryReference);
        assertNotNull(registry);
        registry.registerListener(new EventListener() {
            @Override
            public void handle(MotechEvent event) {
                receivedEvents.add(event.getSubject());
                synchronized (waitLock) {
                    waitLock.notify();
                }
            }

            @Override
            public String getIdentifier() {
                return subject;
            }
        }, subject);

        ServiceReference relayReference = bundleContext.getServiceReference(EventRelay.class.getName());
        assertNotNull(relayReference);
        EventRelay eventRelay = (EventRelay) bundleContext.getService(relayReference);
        assertNotNull(eventRelay);
        eventRelay.sendEventMessage(new MotechEvent(subject));
        synchronized (waitLock) {
            waitLock.wait(2000);
        }
        assertEquals(1, receivedEvents.size());
        assertEquals(subject, receivedEvents.get(0));
    }

    public void testEventListener_WithAnnotation() throws Exception {
        final TestEventListnerOsgi testEventListenerOsgi = (TestEventListnerOsgi) getApplicationContext().getBean("testEventListenerOsgi");

        ServiceReference relayReference = bundleContext.getServiceReference(EventRelay.class.getName());
        assertNotNull(relayReference);
        EventRelay eventRelay = (EventRelay) bundleContext.getService(relayReference);
        assertNotNull(eventRelay);
        eventRelay.sendEventMessage(new MotechEvent(TestEventListnerOsgi.TEST_SUBJECT_OSGI));
        final List<String> receivedEvents = testEventListenerOsgi.getReceivedEvents();
        synchronized (receivedEvents) {
            receivedEvents.wait(2000);
        }
        assertEquals(1, receivedEvents.size());
        assertEquals(TestEventListnerOsgi.TEST_SUBJECT_OSGI, receivedEvents.get(0));
    }

    @Override
    protected List<String> getImports() {
        return Arrays.asList("org.motechproject.event",
                "org.motechproject.event.listener",
                "org.motechproject.event.listener.annotations");
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/osgi/testEventBundleContext.xml"};
    }
}