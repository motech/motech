package org.motechproject.eventlogging.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.motechproject.eventlogging.domain.LoggableEvent;
import org.motechproject.eventlogging.loggers.EventLogger;
import org.motechproject.eventlogging.loggers.impl.FileEventLogger;
import org.motechproject.eventlogging.service.EventLoggingService;
import org.motechproject.scheduler.domain.MotechEvent;

public class FileEventLoggingService implements EventLoggingService {

    private List<FileEventLogger> fileEventLoggers = new ArrayList<FileEventLogger>();

    public FileEventLoggingService() {

    }

    public FileEventLoggingService(List<FileEventLogger> fileEventLoggers) {
        if (fileEventLoggers != null) {
            this.fileEventLoggers = fileEventLoggers;
        }
    }

    @Override
    public void logEvent(MotechEvent event) {
        for (FileEventLogger fileEventLogger : fileEventLoggers) {
            fileEventLogger.log(event);
        }

    }

    @Override
    public Set<String> getLoggedEventSubjects() {
        Set<String> eventSubjectsSet = new HashSet<String>();

        for (EventLogger eventLogger : fileEventLoggers) {
            List<LoggableEvent> events = eventLogger.getLoggableEvents();
            for (LoggableEvent event : events) {
                List<String> eventSubjects = event.getEventSubjects();
                eventSubjectsSet.addAll(eventSubjects);
            }
        }

        return eventSubjectsSet;
    }

}
