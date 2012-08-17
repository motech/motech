package org.motechproject.gateway;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.gateway.EventQueueGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testMotechEventContext.xml"})
public class EventQueueGatewayIT {

    @Autowired
    private EventQueueGateway eventQueueGateway;

    @Test
    public void shouldPublishMotechEventToEventQueue() {
        MotechEvent motechEvent = new MotechEvent("event object");
        eventQueueGateway.sendEventMessage(motechEvent);
    }
}
