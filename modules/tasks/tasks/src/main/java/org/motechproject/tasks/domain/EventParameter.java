package org.motechproject.tasks.domain;

import java.util.Objects;

public class EventParameter extends Parameter {
    private String eventKey;

    public EventParameter() {
        this(null, null);
    }

    public EventParameter(String displayName, String eventKey) {
        this(displayName, eventKey, ParameterType.UNICODE);
    }

    public EventParameter(final String displayName, final String eventKey, final ParameterType type) {
        super(displayName, type);
        this.eventKey = eventKey;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(final String eventKey) {
        this.eventKey = eventKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof EventParameter)) {
            return false;
        }

        if (!super.equals(o)) {
            return false;
        }

        EventParameter that = (EventParameter) o;

        return Objects.equals(eventKey, that.eventKey);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (eventKey != null ? eventKey.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return String.format("EventParameter{eventKey='%s'} %s", eventKey, super.toString());
    }
}
