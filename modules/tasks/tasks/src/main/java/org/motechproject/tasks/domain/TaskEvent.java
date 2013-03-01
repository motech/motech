package org.motechproject.tasks.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class TaskEvent implements Serializable {
    private static final long serialVersionUID = 5631056137997502252L;

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

    @Override
    public int hashCode() {
        return Objects.hash(description, displayName, subject);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final TaskEvent other = (TaskEvent) obj;

        return Objects.equals(this.description, other.description) &&
                Objects.equals(this.displayName, other.displayName) &&
                Objects.equals(this.subject, other.subject);
    }

    @Override
    public String toString() {
        return String.format("TaskEvent{description='%s', displayName='%s', subject='%s'}",
                description, displayName, subject);
    }

}
