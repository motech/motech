package org.motechproject.eventlogging.domain;

import org.motechproject.event.MotechEvent;

public interface EventFlag {

    boolean passesFlags(MotechEvent event);
}
