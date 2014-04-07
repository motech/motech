package org.motechproject.event.osgi;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.domain.TestEventPayload;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.helper.ServiceRetriever;
import org.ops4j.pax.exam.ProbeBuilder;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.BundleContext;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class EventBundleIT extends BasePaxIT {

    @Inject
    private EventListenerRegistryService registry;
    @Inject
    private EventRelay eventRelay;
    @Inject
    private BundleContext bundleContext;

    private final Object waitLock = new Object();

    @ProbeBuilder
    public TestProbeBuilder build(TestProbeBuilder builder) {
        return builder.setHeader("Export-Package", "org.motechproject.event.domain");
    }

    @Test
    public void testEventListener() throws Exception {
        final String subject = "OSGi IT - 001";
        final ArrayList<String> receivedEvents = new ArrayList<>();

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

        wait2s();
        eventRelay.sendEventMessage(new MotechEvent(subject));
        wait2s();

        assertEquals(1, receivedEvents.size());
        assertEquals(subject, receivedEvents.get(0));
    }

    @Test
    public void testEventListener_WithAnnotation() throws Exception {
        final TestEventListnerOsgi testEventListenerOsgi = (TestEventListnerOsgi)
                ServiceRetriever.getWebAppContext(bundleContext, bundleContext.getBundle().getSymbolicName())
                        .getBean("testEventListenerOsgi");

        eventRelay.sendEventMessage(new MotechEvent(TestEventListnerOsgi.TEST_SUBJECT_OSGI));

        final List<String> receivedEvents = testEventListenerOsgi.getReceivedEvents();
        synchronized (receivedEvents) {
            receivedEvents.wait(2000);
        }
        assertEquals(1, receivedEvents.size());
        assertEquals(TestEventListnerOsgi.TEST_SUBJECT_OSGI, receivedEvents.get(0));
    }

    @Test
    public void testEventWithTypedPayload() throws Exception {
        final ArrayList<MotechEvent> receivedEvents = new ArrayList<>();

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

        Map<String, Object> params = new HashMap<>();
        params.put("foo", new TestEventPayload());

        wait2s();
        eventRelay.sendEventMessage(new MotechEvent("event", params));
        wait2s();

        assertEquals(1, receivedEvents.size());
        assertTrue(receivedEvents.get(0).getParameters().get("foo") instanceof TestEventPayload);
    }

    private void wait2s() throws InterruptedException {
        synchronized (waitLock) {
            waitLock.wait(2000);
        }
    }
}
