package org.motechproject.event;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistry;
import org.motechproject.event.listener.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testEventContext.xml"})
public class MotechEventTransformerIT {

    private static final String EVENT_SUBJECT = "EVENT_UNIQUE_ID_TEST";

    @Autowired
    private OutboundEventGateway outboundEventGateway;

    @Autowired
    private EventListenerRegistry eventListenerRegistry;


    @Test
    public void verifyTransformedEventHasValidId() throws InterruptedException {
        TransformedEventListener eventListener = new TransformedEventListener();
        eventListenerRegistry.registerListener(eventListener, EVENT_SUBJECT);
        outboundEventGateway.sendEventMessage(new MotechEvent(EVENT_SUBJECT));

        synchronized (eventListener) {
            if (!eventListener.isEventHandled()) {
                eventListener.wait(5000);
            }
        }
        MotechEvent motechEvent = eventListener.getEvent();
        Assert.notNull(motechEvent);
        Assert.notNull(motechEvent.getId());
    }

    class TransformedEventListener implements EventListener {

        private boolean eventHandled;
        private MotechEvent event;

        @Override
        public String getIdentifier() {
            return EVENT_SUBJECT;
        }

        @MotechListener(subjects = EVENT_SUBJECT)
        public synchronized void handle(MotechEvent motechEvent) {
            eventHandled = true;
            event = motechEvent;
            notifyAll();
        }

        public MotechEvent getEvent() {
            return event;
        }

        public boolean isEventHandled() {
            return eventHandled;
        }
    }


    @After
    public void teardown() {
        eventListenerRegistry.clearListenersForBean(EVENT_SUBJECT);
    }
}
