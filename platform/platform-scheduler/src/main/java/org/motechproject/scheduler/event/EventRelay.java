package org.motechproject.scheduler.event;

import org.motechproject.scheduler.domain.MotechEvent;

public interface EventRelay {
    void sendEventMessage(MotechEvent motechEvent);
}
