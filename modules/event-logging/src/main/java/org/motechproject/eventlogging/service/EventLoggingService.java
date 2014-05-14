package org.motechproject.eventlogging.service;

import org.motechproject.event.MotechEvent;

import java.util.Set;

/**
 * Class that represents a logging service. A logging service can provide any
 * sort of implementation, but must be able to log events and return a list of
 * events they log.
 */
public interface EventLoggingService {

    /**
     * Logs an event. This ultimate destination of the logged event depends on the installed logging services
     * {@link org.motechproject.eventlogging.service.EventLoggingServiceManager} a default logging service
     * is configured that logs to the database.
     *
     * @param event The event to log.
     */
    void logEvent(MotechEvent event);

    /**
     * Returns a set of event subjects this logger is listening on.
     *
     * @return The list of event subjects.
     */
    Set<String> getLoggedEventSubjects();
}
