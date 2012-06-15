package org.motechproject.scheduler.event;

import org.motechproject.scheduler.domain.MotechEvent;

public interface EventRelay
{
    public void sendEventMessage(MotechEvent motechEvent);
}
