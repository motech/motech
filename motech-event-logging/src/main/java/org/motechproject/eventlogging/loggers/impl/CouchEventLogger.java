package org.motechproject.eventlogging.loggers.impl;

import org.motechproject.eventlogging.converter.impl.DefaultCouchToLogConverter;
import org.motechproject.eventlogging.domain.CouchEventLog;
import org.motechproject.eventlogging.domain.CouchLoggableEvent;
import org.motechproject.eventlogging.domain.LoggableEvent;
import org.motechproject.eventlogging.loggers.EventLogger;
import org.motechproject.eventlogging.repository.AllCouchLogs;
import org.motechproject.scheduler.domain.MotechEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CouchEventLogger extends EventLogger {

    private AllCouchLogs allCouchLogs;
    private DefaultCouchToLogConverter eventConverter;

    @Autowired
    public CouchEventLogger(AllCouchLogs allCouchLogs, DefaultCouchToLogConverter eventConverter) {
        this.allCouchLogs = allCouchLogs;
        this.eventConverter = eventConverter;
    }

    @Override
    public void log(MotechEvent eventToLog) {
        for (LoggableEvent loggableEvent : loggableEvents) {
            if (loggableEvent.isLoggableEvent(eventToLog)) {
                if (eventConverter != null) {
                    CouchEventLog couchLog;
                    if (loggableEvent instanceof CouchLoggableEvent) {
                        couchLog = eventConverter.configuredConvertEventToCouchLog(eventToLog, loggableEvent);
                    } else {
                        couchLog = eventConverter.convertToLog(eventToLog);
                    }
                    allCouchLogs.log(couchLog);
                } else {
                    return;
                }
            }
        }
    }

}
