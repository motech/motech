package org.motechproject.event.gateway;

import org.motechproject.event.MotechEvent;

public interface EventQueueGateway {

    /**
     * Sends the given MotechEvent message as a payload to the message channel
     *  defined in the Spring Integration configuration file.
     *
     * @param motechEvent
     */
    void sendEventMessage(MotechEvent motechEvent);
}
