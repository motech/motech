package org.motechproject.tasks.domain.mds.channel.builder;

import org.motechproject.tasks.contract.EventParameterRequest;
import org.motechproject.tasks.contract.TriggerEventRequest;
import org.motechproject.tasks.domain.mds.channel.EventParameter;
import org.motechproject.tasks.domain.mds.channel.TriggerEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides methods for constructing trigger events.
 */
public class TriggerEventBuilder {

    private String displayName;

    private String subject;

    private String description;

    private List<EventParameter> eventParameters;

    private String triggerListenerSubject;

    public TriggerEventBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public TriggerEventBuilder setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public TriggerEventBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public TriggerEventBuilder setEventParameters(List<EventParameter> eventParameters) {
        this.eventParameters = eventParameters;
        return this;
    }

    public TriggerEventBuilder setTriggerListenerSubject(String triggerListenerSubject) {
        this.triggerListenerSubject = triggerListenerSubject;
        return this;
    }

    /**
     * Builds an object of {@link TriggerEvent} class.
     *
     * @return the created instance
     */
    public TriggerEvent build() {
        return new TriggerEvent(displayName, subject, description, eventParameters, triggerListenerSubject);
    }

    /**
     * Creates a builder, which allows building trigger events based on the given {@code triggerEventRequest}.
     *
     * @param triggerEventRequest  the trigger event request
     * @return the created builder
     */
    public static TriggerEventBuilder fromTriggerEventRequest(TriggerEventRequest triggerEventRequest) {
        TriggerEventBuilder builder = new TriggerEventBuilder()
                .setDisplayName(triggerEventRequest.getDisplayName())
                .setSubject(triggerEventRequest.getSubject())
                .setDescription(triggerEventRequest.getDescription())
                .setTriggerListenerSubject(triggerEventRequest.getTriggerListenerSubject());

        List<EventParameter> eventParameters = new ArrayList<>();
        for (EventParameterRequest eventParameterRequest : triggerEventRequest.getEventParameters()) {
            eventParameters.add(EventParameterBuilder.fromEventParameterRequest(eventParameterRequest).build());
        }
        builder.setEventParameters(eventParameters);

        return builder;
    }
}
