package org.motechproject.eventlogging.service;

import java.util.Set;

import org.motechproject.scheduler.domain.MotechEvent;

public interface EventLoggingService {

    void logEvent(MotechEvent event);

    Set<String> getLoggedEventSubjects();
}
