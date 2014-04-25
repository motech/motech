package org.motechproject.eventlogging.loggers.impl;

import org.motechproject.event.MotechEvent;
import org.motechproject.eventlogging.converter.impl.DefaultDbToLogConverter;
import org.motechproject.eventlogging.domain.DbLoggableEvent;
import org.motechproject.eventlogging.domain.EventLog;
import org.motechproject.eventlogging.domain.LoggableEvent;
import org.motechproject.eventlogging.loggers.EventLogger;
import org.motechproject.eventlogging.service.EventLogService;

/**
 * Implementation of the {@link EventLogger} class.
 * The <code>DbEventLogger</code> class is responsible for logging
 * event records in the database.
 */
public class DbEventLogger extends EventLogger {

    private EventLogService eventLogService;
    private DefaultDbToLogConverter eventConverter;

    public DbEventLogger(EventLogService eventLogService, DefaultDbToLogConverter eventConverter) {
        this.eventLogService = eventLogService;
        this.eventConverter = eventConverter;
    }

    @Override
    public void log(MotechEvent eventToLog) {
        for (LoggableEvent loggableEvent : getLoggableEvents()) {
            if (loggableEvent.isLoggableEvent(eventToLog)) {
                if (eventConverter != null) {
                    EventLog eventLog;
                    if (loggableEvent instanceof DbLoggableEvent) {
                        eventLog = eventConverter.configuredConvertEventToDbLog(eventToLog, loggableEvent);
                    } else {
                        eventLog = eventConverter.convertToLog(eventToLog);
                    }
                    eventLogService.create(eventLog);
                } else {
                    return;
                }
            }
        }
    }

}
