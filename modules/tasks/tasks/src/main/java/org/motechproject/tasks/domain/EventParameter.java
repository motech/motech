package org.motechproject.tasks.domain;

public class EventParameter {
    private String displayName;
    private String eventKey;
    private EventParamType type;

    public EventParameter() {
        this(null, null);
    }

    public EventParameter(String displayName, String eventKey) {
        this(displayName, eventKey, EventParamType.UNICODE);
    }

    public EventParameter(final String displayName, final String eventKey, final EventParamType type) {
        this.displayName = displayName;
        this.eventKey = eventKey;
        this.type = type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(final String eventKey) {
        this.eventKey = eventKey;
    }

    public EventParamType getType() {
        return type;
    }

    public void setType(final EventParamType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EventParameter parameter = (EventParameter) o;

        if (displayName != null ? !displayName.equals(parameter.displayName) : parameter.displayName != null) {
            return false;
        }

        if (eventKey != null ? !eventKey.equals(parameter.eventKey) : parameter.eventKey != null) {
            return false;
        }

        if (type != null ? !type.equals(parameter.type) : parameter.type != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = displayName != null ? displayName.hashCode() : 0;
        result = 31 * result + (eventKey != null ? eventKey.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return String.format("EventParameter{displayName='%s', eventKey='%s', type=%s}", displayName, eventKey, type);
    }
}
