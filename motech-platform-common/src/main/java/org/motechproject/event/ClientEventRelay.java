package org.motechproject.event;

import org.motechproject.event.gateway.EventQueueGateway;
import org.motechproject.event.listener.EventRelay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class handled incoming scheduled events and relays those events to the appropriate event listeners
 */
public class ClientEventRelay implements EventRelay {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EventQueueGateway eventQueueGateway;

    public void sendEventMessage(MotechEvent event) {
        log.info("Sending event: " + event.getSubject());

        eventQueueGateway.sendEventMessage(event);
    }
}
