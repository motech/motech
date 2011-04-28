package org.motechproject.decisiontree.model;

import org.motechproject.model.MotechAuditableDataObject;

/**
 *
 */
public class Action extends MotechAuditableDataObject {

    private static final long serialVersionUID = 1L;

    private String eventId;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    @Override
    public String toString() {
        return "Action{" +
                "eventId='" + eventId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Action action = (Action) o;

        if (eventId != null ? !eventId.equals(action.eventId) : action.eventId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return eventId != null ? eventId.hashCode() : 0;
    }
}
