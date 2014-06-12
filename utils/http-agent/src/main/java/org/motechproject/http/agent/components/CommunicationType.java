package org.motechproject.http.agent.components;

import org.motechproject.event.MotechEvent;

public interface CommunicationType {

    void send(MotechEvent motechEvent);
}
