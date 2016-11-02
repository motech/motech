package org.motechproject.tasks.dto;

import org.motechproject.tasks.domain.enums.ParameterType;

public class EventParameterDto extends ParameterDto {

    private String eventKey;

    public EventParameterDto(String displayName, ParameterType type, String eventKey) {
        super(displayName, type);
        this.eventKey = eventKey;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }
}
