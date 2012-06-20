package org.motechproject.gateway;

import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.gateway.OutboundEventGateway;

public class StubOutboundEventGateway implements OutboundEventGateway {
    @Override
    public void sendEventMessage(MotechEvent motechEvent) {
    }
}
