package org.motechproject.tasks.domain;

import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.annotations.CrudEvents;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.event.CrudEventType;
import org.motechproject.mds.util.SecurityMode;
import org.motechproject.tasks.constants.TasksRoles;
import org.motechproject.tasks.contract.EventParameterRequest;

import java.util.Objects;

/**
 * Represents a parameter of a trigger event. These parameters can be dragged and dropped within tasks. This class is
 * part of the channel model.
 */
@Entity
@CrudEvents(CrudEventType.NONE)
@Access(value = SecurityMode.PERMISSIONS, members = {TasksRoles.MANAGE_TASKS})
public class EventParameter extends Parameter {

    private static final long serialVersionUID = 2564446352940524099L;

    @Field(required = true)
    private String eventKey;

    /**
     * Constructor.
     */
    public EventParameter() {
        this(null, null);
    }

    /**
     * Constructor.
     *
     * @param displayName  the parameter display name
     * @param eventKey  the event key
     */
    public EventParameter(String displayName, String eventKey) {
        this(displayName, eventKey, ParameterType.UNICODE);
    }

    /**
     * Constructor.
     *
     * @param displayName  the parameter display name
     * @param eventKey  the event key
     * @param type  the parameter type
     */
    public EventParameter(final String displayName, final String eventKey, final ParameterType type) {
        super(displayName, type);
        this.eventKey = eventKey;
    }

    /**
     * Constructor.
     *
     * @param eventParameterRequest  the request for event parameter, not null
     */
    public EventParameter(EventParameterRequest eventParameterRequest) {
        this(eventParameterRequest.getDisplayName(), eventParameterRequest.getEventKey(), ParameterType.fromString(eventParameterRequest.getType()));
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(final String eventKey) {
        this.eventKey = eventKey;
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

        final EventParameter other = (EventParameter) obj;

        return Objects.equals(this.eventKey, other.eventKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventKey);
    }

    @Override
    public String toString() {
        return String.format("EventParameter{eventKey='%s'} %s", eventKey, super.toString());
    }
}
