package org.motechproject.eventlogging.matchers;

import org.motechproject.event.MotechEvent;

public interface EventFlag {

    boolean passesFlags(MotechEvent event);
}
