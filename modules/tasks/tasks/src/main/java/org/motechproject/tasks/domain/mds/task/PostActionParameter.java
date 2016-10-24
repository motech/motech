package org.motechproject.tasks.domain.mds.task;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.annotations.CrudEvents;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.event.CrudEventType;
import org.motechproject.mds.util.SecurityMode;
import org.motechproject.tasks.constants.TasksRoles;

import java.util.Objects;

/**
 * Represents a single post action parameter used by a task. This class is part of the task itself and does not describe
 * the parameter itself. This object translates to retrieving a parameter object during task execution.
 */
@Entity(recordHistory = true)
@CrudEvents(CrudEventType.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
@Access(value = SecurityMode.PERMISSIONS, members = {TasksRoles.MANAGE_TASKS})
public class PostActionParameter {

    @Field
    private Long objectId;

    private boolean failIfDataNotFound;

    public PostActionParameter() {
        this(null, false);
    }

    public PostActionParameter(Long objectId, boolean failIfDataNotFound) {
        this.objectId = objectId;
        this.failIfDataNotFound = failIfDataNotFound;
    }

    public boolean isFailIfDataNotFound() {
        return failIfDataNotFound;
    }

    public void setFailIfDataNotFound(boolean failIfDataNotFound) {
        this.failIfDataNotFound = failIfDataNotFound;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(objectId, failIfDataNotFound);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        final PostActionParameter other = (PostActionParameter) obj;

        return Objects.equals(this.objectId, other.objectId)
                && Objects.equals(this.failIfDataNotFound, other.failIfDataNotFound);
    }

    @Override
    public String toString() {
        return String.format(
                "PostActionParameter{objectId=%d, failIfDataNotFound=%s} %s",
                objectId, failIfDataNotFound, super.toString()
        );
    }
}
