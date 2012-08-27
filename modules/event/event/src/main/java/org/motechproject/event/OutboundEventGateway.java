package org.motechproject.event;

public interface OutboundEventGateway {

    /**
     * Sends the given MotechEvent message as a payload to the message channel
     *  defined in the Spring Integration configuration file.
     *
     * @param motechEvent
     */
    void sendEventMessage(MotechEvent motechEvent);
}
