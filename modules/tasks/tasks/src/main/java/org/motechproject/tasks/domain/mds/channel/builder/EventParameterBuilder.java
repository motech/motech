package org.motechproject.tasks.domain.mds.channel.builder;

import org.motechproject.tasks.contract.EventParameterRequest;
import org.motechproject.tasks.domain.enums.ParameterType;
import org.motechproject.tasks.domain.mds.channel.EventParameter;

/**
 * Provides methods for constructing event parameters.
 */
public class EventParameterBuilder {

    private String displayName;

    private String eventKey;

    private ParameterType parameterType;

    public EventParameterBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public EventParameterBuilder setEventKey(String eventKey) {
        this.eventKey = eventKey;
        return this;
    }

    public EventParameterBuilder setParameterType(String type) {
        this.parameterType = ParameterType.fromString(type);
        return this;
    }

    /**
     * Builds an object of {@link EventParameter} class.
     *
     * @return the created instance
     */
    public EventParameter build() {
        return new EventParameter(displayName, eventKey, parameterType);
    }

    /**
     * Creates a builder, which allows building event parameters based on the given {@code eventParameterRequest}.
     *
     * @param eventParameterRequest  the event parameter request
     * @return the created builder
     */
    public static EventParameterBuilder fromEventParameterRequest(EventParameterRequest eventParameterRequest) {
        return new EventParameterBuilder()
                .setDisplayName(eventParameterRequest.getDisplayName())
                .setEventKey(eventParameterRequest.getEventKey())
                .setParameterType(eventParameterRequest.getType());
    }
}
