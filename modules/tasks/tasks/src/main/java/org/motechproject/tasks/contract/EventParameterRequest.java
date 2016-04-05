package org.motechproject.tasks.contract;

import org.motechproject.tasks.domain.mds.channel.EventParameter;

import java.util.Objects;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * Service layer object denoting a {@link EventParameter}. It is a part of the
 * {@link org.motechproject.tasks.contract.TriggerEventRequest} and is used by
 * {@link org.motechproject.tasks.service.ChannelService} for adding new or updating already existent trigger event
 * parameters.
 */
public class EventParameterRequest {

    private static final String UNICODE = "UNICODE";

    private String eventKey;
    private String displayName;
    private String type;

    private EventParameterRequest() {
        this.type = UNICODE;
    }

    /**
     * Constructor.
     *
     * @param displayName  the event parameter display name
     * @param eventKey  the event key
     * @param type  the event parameter type
     */
    public EventParameterRequest(String displayName, String eventKey, String type) {
        this.eventKey = eventKey;
        this.displayName = displayName;
        this.type = isBlank(type) ? UNICODE : type;
    }

    /**
     * Constructor.
     *
     * @param displayName  the event parameter display name
     * @param eventKey  the event key
     */
    public EventParameterRequest(String displayName, String eventKey) {
        this(displayName, eventKey, null);
    }

    /**
     * Returns the key of the event.
     *
     * @return the event key
     */
    public String getEventKey() {
        return eventKey;
    }

    /**
     * Returns the display name of the event.
     *
     * @return the event parameter display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the type of the event.
     *
     * @return the event parameter type
     */
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
