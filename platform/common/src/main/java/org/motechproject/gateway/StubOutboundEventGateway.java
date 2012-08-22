package org.motechproject.gateway;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.OutboundEventGateway;

public class StubOutboundEventGateway implements OutboundEventGateway {
    @Override
    public void sendEventMessage(MotechEvent motechEvent) {
    }
}
