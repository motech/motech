package org.motechproject.tasks.domain.mds.channel.builder;

import org.motechproject.tasks.contract.ActionEventRequest;
import org.motechproject.tasks.contract.ChannelRequest;
import org.motechproject.tasks.contract.TriggerEventRequest;
import org.motechproject.tasks.domain.mds.channel.ActionEvent;
import org.motechproject.tasks.domain.mds.channel.Channel;
import org.motechproject.tasks.domain.mds.channel.TriggerEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides methods for constructing channels.
 */
public class ChannelBuilder {

    private String displayName;

    private String moduleName;

    private String moduleVersion;

    private String description;

    private List<TriggerEvent> triggerTaskEvents;

    private List<ActionEvent> actionTaskEvents;

    public ChannelBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ChannelBuilder setModuleName(String moduleName) {
        this.moduleName = moduleName;
        return this;
    }

    public ChannelBuilder setModuleVersion(String moduleVersion) {
        this.moduleVersion = moduleVersion;
        return this;
    }

    public ChannelBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public ChannelBuilder setTriggerTaskEvents(List<TriggerEvent> triggerTaskEvents) {
        this.triggerTaskEvents = triggerTaskEvents;
        return this;
    }

    public ChannelBuilder setActionTaskEvents(List<ActionEvent> actionTaskEvents) {
        this.actionTaskEvents = actionTaskEvents;
        return this;
    }

    /**
     * Builds an object of {@link Channel} class.
     *
     * @return the created instance
     */
    public Channel build() {
        return new Channel(displayName, moduleName, moduleVersion, description, triggerTaskEvents, actionTaskEvents);
    }

    /**
     * Creates a builder, which allows building Channels based on the given {@code channelRequest}.
     *
     * @param channelRequest  the channel request
     * @return the created builder
     */
    public static ChannelBuilder fromChannelRequest(ChannelRequest channelRequest) {
        ChannelBuilder builder = new ChannelBuilder()
                .setDisplayName(channelRequest.getDisplayName())
                .setModuleName(channelRequest.getModuleName())
                .setModuleVersion(channelRequest.getModuleVersion());

        List<TriggerEvent> triggerEvents = new ArrayList<>();
        for (TriggerEventRequest triggerEventRequest : channelRequest.getTriggerTaskEvents()) {
            triggerEvents.add(TriggerEventBuilder.fromTriggerEventRequest(triggerEventRequest).build());
        }
        builder.setTriggerTaskEvents(triggerEvents);

        List<ActionEvent> actionEvents = new ArrayList<>();
        for (ActionEventRequest actionEventRequest : channelRequest.getActionTaskEvents()) {
            actionEvents.add(ActionEventBuilder.fromActionEventRequest(actionEventRequest).build());
        }
        builder.setActionTaskEvents(actionEvents);

        return builder;
    }
}
