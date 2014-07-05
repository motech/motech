package org.motechproject.http.agent.components;

import org.motechproject.event.MotechEvent;
import org.springframework.http.ResponseEntity;

public interface CommunicationType {

    void send(MotechEvent motechEvent);

    ResponseEntity<?> sendWithReturnType(MotechEvent motechEvent);
}
