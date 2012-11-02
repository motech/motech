package org.motechproject.event.osgi;


import org.eclipse.gemini.blueprint.test.platform.Platforms;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistry;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

public class EventBundleIT extends BaseOsgiIT {
    @Override
    protected String getPlatformName() {
        return Platforms.FELIX;
    }

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
        synchronized (testEventListenerOsgi) {
            testEventListenerOsgi.wait(2000);
        }
        final List<String> receivedEvents = testEventListenerOsgi.getReceivedEvents();
        assertEquals(1, receivedEvents.size());
        assertEquals(TestEventListnerOsgi.TEST_SUBJECT_OSGI, receivedEvents.get(0));
    }

    @Override
    protected Manifest getManifest() {
        StringBuilder builder = new StringBuilder();
        builder.append("org.motechproject.event").append(",")
                .append("org.motechproject.event.listener").append(",")
                .append("org.motechproject.event.listener.annotations");


        Manifest mf = super.getManifest();    //TODO : PULL THIS UP TO MOTECH BASE OSGI IT.
        String imports = (String) mf.getMainAttributes().getValue(Constants.IMPORT_PACKAGE);
        mf.getMainAttributes().putValue(Constants.IMPORT_PACKAGE, builder.append(",").append(imports).toString());
        return mf;
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/osgi/testEventBundleContext.xml"};
    }
}