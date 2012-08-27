package org.motechproject.event.listener;

import org.motechproject.event.MotechEvent;

public interface EventRelay {
    void sendEventMessage(MotechEvent motechEvent);
}
