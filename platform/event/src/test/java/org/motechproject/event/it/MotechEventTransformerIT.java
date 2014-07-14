package org.motechproject.event.it;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.annotations.TransformedEventListener;
import org.motechproject.event.queue.OutboundEventGateway;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.osgi.framework.BundleContext;
import org.springframework.util.Assert;

import javax.inject.Inject;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class MotechEventTransformerIT extends BasePaxIT {

    private static final String EVENT_SUBJECT = "EVENT_UNIQUE_ID_TEST";

    private OutboundEventGateway outboundEventGateway;

    @Inject
    private BundleContext bundleContext;

    @Inject
    private EventListenerRegistryService eventListenerRegistry;

    @Before
    public void setup() {
        outboundEventGateway = (OutboundEventGateway) getBeanFromBundleContext(bundleContext,
                "org.motechproject.motech-platform-event", "outboundEventGateway");
    }

    @Test
    public void verifyTransformedEventHasValidId() throws InterruptedException {
        TransformedEventListener eventListener = new TransformedEventListener();
        eventListenerRegistry.registerListener(eventListener, EVENT_SUBJECT);
        outboundEventGateway.sendEventMessage(new MotechEvent(EVENT_SUBJECT));

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
    public void teardown() {
        eventListenerRegistry.clearListenersForBean(EVENT_SUBJECT);
    }
}
