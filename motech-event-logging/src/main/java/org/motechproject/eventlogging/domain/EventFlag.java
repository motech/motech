package org.motechproject.eventlogging.domain;

import org.motechproject.scheduler.domain.MotechEvent;

public interface EventFlag {

    boolean passesFlags(MotechEvent event);
}
