package org.motechproject.tasks.domain;

import java.io.Serializable;
import java.util.Objects;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public abstract class TaskEvent implements Serializable {
    private static final long serialVersionUID = 5631056137997502252L;

    private String description;
    private String displayName;
    private String subject;

    protected TaskEvent() {
        this(null, null, null);

    }

    protected TaskEvent(String description, String displayName, String subject) {
        this.description = description;
        this.displayName = displayName;
        this.subject = subject;
    }

    public boolean hasSubject() {
        return isNotBlank(subject);
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
                subjectEquals(other.subject);
    }

    @Override
    public String toString() {
        return String.format("TaskEvent{description='%s', displayName='%s', subject='%s'}",
                description, displayName, subject);
    }

    protected boolean subjectEquals(String subject) {
        return Objects.equals(this.subject, subject);
    }

}
