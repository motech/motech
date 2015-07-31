package org.motechproject.event.it;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.messaging.OutboundEventGateway;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.osgi.framework.BundleContext;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.util.UUID;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class MotechEventTransformerBundleIT extends BasePaxIT {

    private static final String EVENT_SUBJECT = "EVENT_UNIQUE_ID_TEST";

    private OutboundEventGateway outboundEventGateway;

    @Inject
    private BundleContext bundleContext;

    @Inject
    private EventListenerRegistryService eventListenerRegistry;

    @Before
    public void setUp() {
        outboundEventGateway = (OutboundEventGateway) getBeanFromBundleContext(bundleContext,
                "org.motechproject.motech-platform-event", "outboundEventGateway");
    }

    @Test
    public void verifyTransformedQueueEventHasValidId() throws InterruptedException {
        TransformedEventListener eventListener = new TransformedEventListener();
        eventListenerRegistry.registerListener(eventListener, EVENT_SUBJECT);
        MotechEvent event = new MotechEvent(EVENT_SUBJECT);
        event.getParameters().put("message-destination", eventListener.getIdentifier());
        outboundEventGateway.sendEventMessage(event);

        waitForHandledEvent(eventListener);

        MotechEvent capturedEvent = eventListener.getEvent();
        Assert.notNull(capturedEvent);
        Assert.notNull(capturedEvent.getId());
    }

    @Test
    public void verifyTransformedTopicEventHasValidId() throws InterruptedException {
        TransformedEventListener eventListener = new TransformedEventListener();
        eventListenerRegistry.registerListener(eventListener, EVENT_SUBJECT);
        MotechEvent event = new MotechEvent(EVENT_SUBJECT);
        outboundEventGateway.broadcastEventMessage(event);

        waitForHandledEvent(eventListener);

        MotechEvent motechEvent = eventListener.getEvent();
        Assert.notNull(motechEvent);
        Assert.notNull(motechEvent.getId());
    }

    private void waitForHandledEvent(TransformedEventListener transformedEventListener) throws InterruptedException {
        synchronized (transformedEventListener) {
            if (!transformedEventListener.isEventHandled()) {
                transformedEventListener.wait(5000);
            }
        }
    }

    @After
    public void tearDown() {
        eventListenerRegistry.clearListenersForBean(EVENT_SUBJECT);
    }
}
