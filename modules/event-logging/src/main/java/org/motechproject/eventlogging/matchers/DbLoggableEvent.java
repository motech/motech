package org.motechproject.eventlogging.matchers;

import java.util.List;

public class DbLoggableEvent extends LoggableEvent {

    private LogMappings mappings;

    public DbLoggableEvent(List<String> eventSubjects, List<? extends EventFlag> flags, LogMappings mappings) {
        super(eventSubjects, flags);
        this.mappings = mappings;
    }

    public LogMappings getMappings() {
        return mappings;
    }

    public void setMappings(LogMappings mappings) {
        this.mappings = mappings;
    }
}
