package org.motechproject.tasks.contract;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tasks.domain.mds.channel.TriggerEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Service layer object denoting a {@link TriggerEvent}. It is a part of the
 * {@link org.motechproject.tasks.contract.ChannelRequest} and is used by
 * {@link org.motechproject.tasks.service.ChannelService} for adding new or updating already existent trigger events.
 */
public class TriggerEventRequest {

    private String displayName;
    private String subject;
    private String triggerListenerSubject;
    private List<EventParameterRequest> eventParameters;
    private String description;

    /**
     * Constructor.
     */
    private TriggerEventRequest() {
        eventParameters = new ArrayList<>();
    }

    /**
     * Constructor. The given subject will be used as the actual subject the listener will listen for.
     *
     * @param displayName  the trigger event display name
     * @param subject  the event subject
     * @param description  the event description
     * @param eventParameters  the trigger event parameters
     */
    public TriggerEventRequest(String displayName, String subject, String description, List<EventParameterRequest> eventParameters) {
        this.displayName = displayName;
        this.subject = subject;
        this.description = description;
        this.eventParameters = eventParameters;
        this.triggerListenerSubject = subject;
    }

    /**
     * Constructor. The given {@code triggerListenerSubject} will be used as the actual subject the listener will listen
     * for. If it is not specified subject will be used instead.
     *
     * @param displayName  the trigger event display name
     * @param subject  the event subject
     * @param description  the event description
     * @param eventParameters  the trigger event parameters
     * @param triggerListenerSubject  the listener event subject
     */
    public TriggerEventRequest(String displayName, String subject, String description, List<EventParameterRequest> eventParameters, String triggerListenerSubject) {
        this.displayName = displayName;
        this.description = description;
        this.eventParameters = eventParameters;
        this.triggerListenerSubject = triggerListenerSubject;
        this.subject = subject;
    }

    /**
     * Returns the display name of the trigger event.
     *
     * @return the trigger event display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the subject of the trigger event.
     *
     * @return the trigger event subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Returns the parameters of the trigger event.
     *
     * @return the trigger event parameters
     */
    public List<EventParameterRequest> getEventParameters() {
        return eventParameters;
    }

    /**
     * Returns the description of the trigger event.
     *
     * @return the trigger event description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the trigger listener subject of the trigger event.
     *
     * @return the trigger event listener subject
     */
    public String getTriggerListenerSubject() {
        return StringUtils.isEmpty(triggerListenerSubject) ? subject : triggerListenerSubject;
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


        final TriggerEventRequest other = (TriggerEventRequest) obj;

        return Objects.equals(this.eventParameters, other.eventParameters);
    }

    @Override
    public String toString() {
        return String.format("TriggerEvent{eventParameters=%s}", eventParameters);
    }
}
