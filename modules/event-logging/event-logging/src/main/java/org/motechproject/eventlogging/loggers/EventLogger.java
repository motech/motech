package org.motechproject.eventlogging.loggers;

import java.util.ArrayList;
import java.util.List;
import org.motechproject.eventlogging.domain.LoggableEvent;
import org.motechproject.scheduler.domain.MotechEvent;

/**
 * Class that represents a generic event logger. All loggers have a list of
 * loggable events that they should be able to provide logging functionality for.
 */
public abstract class EventLogger {

    protected List<LoggableEvent> loggableEvents = new ArrayList<LoggableEvent>();

    public void addLoggableEvents(List<LoggableEvent> loggableEvents) {
        this.loggableEvents.addAll(loggableEvents);
    }

    public void removeLoggableEvents(List<LoggableEvent> loggableEvents) {
        this.loggableEvents.removeAll(loggableEvents);
    }

    public void clearLoggableEvents() {
        loggableEvents.clear();
    }

    public List<LoggableEvent> getLoggableEvents() {
        return loggableEvents;
    }

    public abstract void log(MotechEvent eventToLog);

}
