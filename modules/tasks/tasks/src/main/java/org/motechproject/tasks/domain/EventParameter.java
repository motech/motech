package org.motechproject.tasks.domain;

public class EventParameter {
    private String displayName;
    private String eventKey;

    public EventParameter() {
        this(null, null);
    }

    public EventParameter(String displayName, String eventKey) {
        this.displayName = displayName;
        this.eventKey = eventKey;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
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

        EventParameter that = (EventParameter) o;

        if (displayName != null ? !displayName.equals(that.displayName) : that.displayName != null) {
            return false;
        }

        if (eventKey != null ? !eventKey.equals(that.eventKey) : that.eventKey != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = eventKey != null ? eventKey.hashCode() : 0;
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return String.format("EventParameter{eventKey='%s', displayName='%s'}", eventKey, displayName);
    }
}
