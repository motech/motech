package org.motechproject.tasks.domain.mds.channel;

import org.motechproject.mds.annotations.CrudEvents;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.event.CrudEventType;

import java.io.Serializable;
import java.util.Objects;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Represents a single task event. Task event is an abstract base for events utilized in the task module. It serves as a
 * base for both {@link ActionEvent}s and {@link TriggerEvent}s. It is a part of the channel model.
 */
@Entity
@CrudEvents(CrudEventType.NONE)
public abstract class TaskEvent implements Serializable {

    private static final long serialVersionUID = 5631056137997502252L;

    @Field
    private String name;

    @Field
    private String description;

    @Field
    private String displayName;

    @Field
    private String subject;

    /**
     * Constructor.
     */
    protected TaskEvent() {
        this(null, null, null);
    }

    /**
     * Constructor.
     *
     * @param description  the event description
     * @param displayName  the event display name
     * @param subject  the event subject
     */
    protected TaskEvent(String description, String displayName, String subject) {
        this(null, description, displayName, subject);
    }

    /**
     * Constructor.
     *
     * @param name  the event name
     * @param description  the event description
     * @param displayName  the event display name
     * @param subject  the event subject
     */
    protected TaskEvent(String name, String description, String displayName, String subject) {
        this.name = name;
        this.description = description;
        this.displayName = displayName;
        this.subject = subject;
    }
    public boolean containsParameter(String key) {
        return false;
    }

    public boolean hasSubject() {
        return isNotBlank(subject);
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
                equalsSubject(other.subject);
    }

    @Override
    public String toString() {
        return String.format("TaskEvent{description='%s', displayName='%s', subject='%s'}",
                description, displayName, subject);
    }

    protected boolean equalsSubject(String subject) {
        return Objects.equals(this.subject, subject);
    }

}
