package org.motechproject.eventlogging.service;

import java.util.Set;
import org.motechproject.scheduler.domain.MotechEvent;

/**
 * Class that represents a logging service. A logging service can provide any
 * sort of implementation, but must be able to log events and return a list of
 * events they log.
 */
public interface EventLoggingService {

    /**
     * Logs an event. This could be by file, couch, feed, etc.
     * @param event The event to log.
     */
    void logEvent(MotechEvent event);

    /**
     * Returns a set of event subjects this logger is listening on.
     * @return The list of event subjects.
     */
    Set<String> getLoggedEventSubjects();
}
