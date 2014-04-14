package org.motechproject.eventlogging.loggers.impl;

import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.eventlogging.converter.impl.DefaultCouchToLogConverter;
import org.motechproject.eventlogging.domain.CouchEventLog;
import org.motechproject.eventlogging.domain.CouchLoggableEvent;
import org.motechproject.eventlogging.domain.EventLog;
import org.motechproject.eventlogging.domain.LoggableEvent;
import org.motechproject.eventlogging.loggers.EventLogger;
import org.motechproject.eventlogging.service.EventLogService;

import java.util.HashMap;

public class CouchEventLogger extends EventLogger {

    private DefaultCouchToLogConverter eventConverter;
    private EventLogService logService;

    public CouchEventLogger(DefaultCouchToLogConverter eventConverter, EventLogService logService) {
        this.eventConverter = eventConverter;
        this.logService = logService;
    }

    @Override
    public void log(MotechEvent eventToLog) {
        for (LoggableEvent loggableEvent : getLoggableEvents()) {
            if (loggableEvent.isLoggableEvent(eventToLog)) {
                if (eventConverter != null) {
                    CouchEventLog couchLog;
                    if (loggableEvent instanceof CouchLoggableEvent) {
                        couchLog = eventConverter.configuredConvertEventToCouchLog(eventToLog, loggableEvent);
                    } else {
                        couchLog = eventConverter.convertToLog(eventToLog);
                    }
                    //allCouchLogs.log(couchLog);

                    EventLog log = new EventLog("aaa", new HashMap<String, Object>(), DateTime.now());
                    if (logService != null && couchLog.getSubject().contains("moduleSettingsChange")) {
                        logService.create(log);
                    }
                } else {
                    return;
                }
            }
        }
    }

}
