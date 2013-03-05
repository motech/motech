package org.motechproject.tasks.domain;

import java.util.List;
import java.util.Objects;

public class TriggerEvent extends TaskEvent {
    private static final long serialVersionUID = 4235157487991610105L;

    private List<EventParameter> eventParameters;

    public TriggerEvent() {
        this(null, null, null, null);
    }

    public TriggerEvent(String description, String displayName, String subject, List<EventParameter> eventParameters) {
        super(description, displayName, subject);
        this.eventParameters = eventParameters;
    }

    public List<EventParameter> getEventParameters() {
        return eventParameters;
    }

    public void setEventParameters(List<EventParameter> eventParameters) {
        this.eventParameters = eventParameters;
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventParameters);
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

        final TriggerEvent other = (TriggerEvent) obj;

        return Objects.equals(this.eventParameters, other.eventParameters);
    }

    @Override
    public String toString() {
        return String.format("TriggerEvent{eventParameters=%s}", eventParameters);
    }
}
