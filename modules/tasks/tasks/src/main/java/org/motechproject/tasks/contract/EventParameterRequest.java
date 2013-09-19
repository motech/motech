package org.motechproject.tasks.contract;

import java.util.Objects;

import static org.apache.commons.lang.StringUtils.isBlank;

public class EventParameterRequest {

    private static final String UNICODE = "UNICODE";

    private String eventKey;
    private String displayName;
    private String type;

    private EventParameterRequest() {
        this.type = UNICODE;
    }

    public EventParameterRequest(String displayName, String eventKey, String type) {
        this.eventKey = eventKey;
        this.displayName = displayName;
        this.type = isBlank(type) ? UNICODE : type;
    }

    public EventParameterRequest(String displayName, String eventKey) {
        this(displayName, eventKey, null);
    }

    public String getEventKey() {
        return eventKey;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getType() {
        return type;
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

        final EventParameterRequest other = (EventParameterRequest) obj;

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
