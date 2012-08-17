package org.motechproject.gateway;

import org.motechproject.event.gateway.EventQueueGateway;
import org.motechproject.event.MotechEvent;

public class StubOutboundEventGateway implements EventQueueGateway {
    @Override
    public void sendEventMessage(MotechEvent motechEvent) {
    }
}
