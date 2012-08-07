package org.motechproject.eventlogging.domain;

import java.util.List;

public class CouchLoggableEvent extends LoggableEvent {

    private CouchLogMappings mappings;

    public CouchLoggableEvent(List<String> eventSubjects, List<? extends EventFlag> flags, CouchLogMappings mappings) {
        super(eventSubjects, flags);
        this.mappings = mappings;
    }

    public CouchLogMappings getMappings() {
        return mappings;
    }

    public void setMappings(CouchLogMappings mappings) {
        this.mappings = mappings;
    }
}
