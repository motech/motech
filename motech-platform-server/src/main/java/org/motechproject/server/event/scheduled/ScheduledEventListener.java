package org.motechproject.server.event.scheduled;

import org.motechproject.model.MotechScheduledEvent;

public interface ScheduledEventListener {
    public void handle(MotechScheduledEvent event);
}
