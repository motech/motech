package org.motechproject.tasks.dto;

import java.util.List;

public class TriggerEventDto {

    private String name;
    private String description;
    private String displayName;
    private String subject;
    private List<EventParameterDto> eventParameters;
    private String triggerListenerSubject;

    public TriggerEventDto(String name, String description, String displayName, String subject, List<EventParameterDto> eventParameters, String triggerListenerSubject) {
        this.name = name;
        this.description = description;
        this.displayName = displayName;
        this.subject = subject;
        this.eventParameters = eventParameters;
        this.triggerListenerSubject = triggerListenerSubject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<EventParameterDto> getEventParameters() {
        return eventParameters;
    }

    public void setEventParameters(List<EventParameterDto> eventParameters) {
        this.eventParameters = eventParameters;
    }

    public String getTriggerListenerSubject() {
        return triggerListenerSubject;
    }

    public void setTriggerListenerSubject(String triggerListenerSubject) {
        this.triggerListenerSubject = triggerListenerSubject;
    }
}
