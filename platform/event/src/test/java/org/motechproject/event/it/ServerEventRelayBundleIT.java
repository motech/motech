package org.motechproject.event.it;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.domain.BuggyListener;
import org.motechproject.event.domain.TrackingListener;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.messaging.MotechEventConfig;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.osgi.framework.BundleContext;

import javax.inject.Inject;
import java.util.List;

import static java.lang.String.format;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class ServerEventRelayBundleIT extends BasePaxIT {

    private static final String MESSAGE_REDELIVERY_TEST = "MESSAGE_REDELIVERY_TEST";
    private static final String EXCEPTION_HANDLING_TEST = "exception-handling-test";

    private MotechEventConfig motechEventConfig;

    @Inject
    private EventRelay eventRelay;

    @Inject
    private EventListenerRegistryService eventListenerRegistry;

    @Inject
    private BundleContext bundleContext;

    @Before
    public void setUp() {
        motechEventConfig = (MotechEventConfig) getBeanFromBundleContext(bundleContext,
                "org.motechproject.motech-platform-event", "motechEventConfig");
    }

    /**
     * For the test to work, set attribute schedulerSupport="true" in the broker element of the activemq.xml
     * Ref: http://activemq.apache.org/delay-and-schedule-message-delivery.html
     */
    @Test
    public void shouldRedeliverQueueMessages_SpecifiedTimes_WithDelay() throws InterruptedException {
        InvalidMessageEventListener eventListener = new InvalidMessageEventListener();
        eventListenerRegistry.registerListener(eventListener, MESSAGE_REDELIVERY_TEST);

        MotechEvent testMessage = new MotechEvent(MESSAGE_REDELIVERY_TEST);
        eventRelay.sendEventMessage(testMessage);

        Boolean isDiscarded = null;
        MotechEvent motechEvent = null;
        for (int pollCount = 0; pollCount < 10; pollCount++) {
            motechEvent = eventListener.getMotechEvent();
            if (motechEvent != null) {
                isDiscarded = (Boolean) motechEvent.getParameters().get(MotechEvent.PARAM_DISCARDED_MOTECH_EVENT);
                if (isDiscarded != null && isDiscarded) {
                    break;
                }
            }
            Thread.sleep(3000);
        }
        assertTrue(isDiscarded != null && isDiscarded);
        assertEquals(motechEventConfig.getMessageMaxRedeliveryCount(), motechEvent.getMessageRedeliveryCount());
        assertEventHandledTimes(eventListener);
    }

    @Test
    public void shouldNotTriggerAllListenersWhenOneListenerFails() throws InterruptedException {
        TrackingListener buggyListener = new BuggyListener(1);
        TrackingListener firstGoodListener = new TrackingListener("first");
        TrackingListener secondGoodListener = new TrackingListener("second");

        eventListenerRegistry.registerListener(buggyListener, EXCEPTION_HANDLING_TEST);
        eventListenerRegistry.registerListener(firstGoodListener, EXCEPTION_HANDLING_TEST);
        eventListenerRegistry.registerListener(secondGoodListener, EXCEPTION_HANDLING_TEST);

        MotechEvent testMessage = new MotechEvent(EXCEPTION_HANDLING_TEST);
        eventRelay.sendEventMessage(testMessage);

        while (buggyListener.getCount() < 2) {
            Thread.sleep(3000);
        }

        assertTrue(buggyListener.getCount() > 1);

        Thread.sleep(2000);

        assertEquals(1, firstGoodListener.getCount());
        assertEquals(1, secondGoodListener.getCount());
    }

    @Test
    public void shouldTriggerAllListenersIfOneListenerFailsWhenRelyingTopicEvent() throws InterruptedException {
        TrackingListener buggyListener = new BuggyListener(1);
        TrackingListener firstGoodListener = new TrackingListener("first");
        TrackingListener secondGoodListener = new TrackingListener("second");

        eventListenerRegistry.registerListener(buggyListener, EXCEPTION_HANDLING_TEST);
        eventListenerRegistry.registerListener(firstGoodListener, EXCEPTION_HANDLING_TEST);
        eventListenerRegistry.registerListener(secondGoodListener, EXCEPTION_HANDLING_TEST);

        MotechEvent testMessage = new MotechEvent(EXCEPTION_HANDLING_TEST);
        eventRelay.broadcastEventMessage(testMessage);

        while (buggyListener.getCount() < 2) {
            Thread.sleep(1000);
        }
        Thread.sleep(2000);

        assertEquals(firstGoodListener.getCount(), 1);
        assertEquals(secondGoodListener.getCount(), 1);
    }


    private void assertEventHandledTimes(InvalidMessageEventListener eventListener) {
        List<DateTime> handledTimes = eventListener.getHandledTimes();
        System.out.println("Handled at times: " + handledTimes);
        int messageMaxRedeliveryCount = motechEventConfig.getMessageMaxRedeliveryCount();
        long delay = motechEventConfig.getMessageRedeliveryDelay();

        for (int i = 0; i < messageMaxRedeliveryCount; i++) {
            long delta = delay * Double.valueOf(Math.pow(2, i)).intValue();
            long diff = handledTimes.get(i + 1).getMillis() - handledTimes.get(i).getMillis();
            diff = (long) Math.ceil(diff / 1000.0);
            assertTrue(format("Expected retry after %d seconds, raised after %d seconds", delta, diff), diff >= delta);
        }
    }

    @After
    public void tearDown() {
        eventListenerRegistry.clearListenersForBean(MESSAGE_REDELIVERY_TEST);
        eventListenerRegistry.clearListenersForBean(EXCEPTION_HANDLING_TEST);
    }
}
