package org.motechproject.tasks.domain;

import java.util.List;
import java.util.Objects;

public class TaskEvent {
    private List<EventParameter> eventParameters;
    private String description;
    private String displayName;
    private String subject;

    public List<EventParameter> getEventParameters() {
        return eventParameters;
    }

    public void setEventParameters(List<EventParameter> eventParameters) {
        this.eventParameters = eventParameters;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TaskEvent taskEvent = (TaskEvent) o;

        return Objects.equals(description, taskEvent.description) && Objects.equals(displayName, taskEvent.displayName) &&
                Objects.equals(subject, taskEvent.subject) && Objects.equals(eventParameters, taskEvent.eventParameters);
    }

    @Override
    public int hashCode() {
        int result = eventParameters != null ? eventParameters.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (subject != null ? subject.hashCode() : 0);
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return String.format("TaskEvent{eventParameters=%s, description='%s', subject='%s', displayName='%s'}",
                eventParameters, description, subject, displayName);
    }
}
