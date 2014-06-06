package org.motechproject.eventlogging.service;

import org.motechproject.event.MotechEvent;

import java.util.Set;

/**
 * Interface that represents a logging service. A logging service can provide any
 * sort of implementation, but must be able to log events and return a list of
 * logged events.
 */
public interface EventLoggingService {

    /**
     * Logs an event. The ultimate destination of the logged event depends on the installed logging service's
     * {@link org.motechproject.eventlogging.service.EventLoggingServiceManager} 
     * A default logging service is configured that logs to the database.
     *
     * @param event  the event to log
     */
    void logEvent(MotechEvent event);

    /**
     * Returns a set of event subjects this logger is listening on.
     *
     * @return the list of event subjects
     */
    Set<String> getLoggedEventSubjects();
}
