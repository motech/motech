package org.motechproject.http.agent.components;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AsynchronousCall implements CommunicationType {

    private EventRelay eventRelay;

    @Autowired
    public AsynchronousCall(EventRelay eventRelay) {
        this.eventRelay = eventRelay;
    }

    public void send(MotechEvent motechEvent) {
        eventRelay.sendEventMessage(motechEvent);
    }
}
