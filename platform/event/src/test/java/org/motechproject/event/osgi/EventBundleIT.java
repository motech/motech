package org.motechproject.event.osgi;


import org.motechproject.event.MotechEvent;
import org.motechproject.event.domain.TestEventPayload;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Manifest;

public class EventBundleIT extends BaseOsgiIT {

    public void testEventListener() throws Exception {
        final String subject = "OSGi IT - 001";
        final Object waitLock = new Object();
        final ArrayList<String> receivedEvents = new ArrayList<>();

        EventListenerRegistryService registry = getService(EventListenerRegistryService.class);

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

        EventRelay eventRelay = getService(EventRelay.class);

        eventRelay.sendEventMessage(new MotechEvent(subject));
        synchronized (waitLock) {
            waitLock.wait(2000);
        }
        assertEquals(1, receivedEvents.size());
        assertEquals(subject, receivedEvents.get(0));
    }

    public void testEventListener_WithAnnotation() throws Exception {
        final TestEventListnerOsgi testEventListenerOsgi = (TestEventListnerOsgi) getApplicationContext().getBean("testEventListenerOsgi");

        EventRelay eventRelay = getService(EventRelay.class);

        eventRelay.sendEventMessage(new MotechEvent(TestEventListnerOsgi.TEST_SUBJECT_OSGI));

        final List<String> receivedEvents = testEventListenerOsgi.getReceivedEvents();
        synchronized (receivedEvents) {
            receivedEvents.wait(2000);
        }
        assertEquals(1, receivedEvents.size());
        assertEquals(TestEventListnerOsgi.TEST_SUBJECT_OSGI, receivedEvents.get(0));
    }

    public void testEventWithTypedPayload() throws Exception {
        final Object waitLock = new Object();
        final ArrayList<MotechEvent> receivedEvents = new ArrayList<>();

        EventListenerRegistryService registry = getService(EventListenerRegistryService.class);

        registry.registerListener(new EventListener() {

            @Override
            public void handle(MotechEvent event) {
                receivedEvents.add(event);
                synchronized (waitLock) {
                    waitLock.notify();
                }
            }

            @Override
            public String getIdentifier() {
                return "event";
            }
        }, "event");

        EventRelay eventRelay = getService(EventRelay.class);

        Map<String, Object> params = new HashMap<>();
        params.put("foo", new TestEventPayload());
        eventRelay.sendEventMessage(new MotechEvent("event", params));

        synchronized (waitLock) {
            waitLock.wait(2000);
        }
        assertEquals(1, receivedEvents.size());
        assertTrue(receivedEvents.get(0).getParameters().get("foo") instanceof TestEventPayload);
    }

    @Override
    protected List<String> getImports() {
        return Arrays.asList("org.motechproject.event",
                "org.motechproject.event.listener",
                "org.motechproject.event.listener.annotations",
                "org.motechproject.config.service");
    }

    @Override
    protected Manifest getManifest() {
        Manifest manifest = super.getManifest();

        String originalExports = manifest.getMainAttributes().getValue(Constants.EXPORT_PACKAGE);
        String exports = "org.motechproject.event.domain," + ((originalExports == null)? "" : originalExports);
        manifest.getMainAttributes().putValue(Constants.EXPORT_PACKAGE, exports);

        return manifest;
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/osgi/testEventBundleContext.xml"};
    }
}
