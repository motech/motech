package org.motechproject.http.agent.components;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.http.agent.listener.HttpClientEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AsynchronousCall implements CommunicationType {

    private EventRelay eventRelay;

    private HttpClientEventListener httpClientEventListener;

    @Autowired
    public AsynchronousCall(EventRelay eventRelay, HttpClientEventListener httpClientEventListener) {
        this.eventRelay = eventRelay;
        this.httpClientEventListener = httpClientEventListener;
    }

    public void send(MotechEvent motechEvent) {
        eventRelay.sendEventMessage(motechEvent);
    }
}
