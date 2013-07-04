package org.motechproject.tasks.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChannelRequest {

    private String displayName;
    private String moduleName;
    private String moduleVersion;
    private String description;
    private List<TriggerEventRequest> triggerTaskEvents;
    private List<ActionEventRequest> actionTaskEvents;

    private ChannelRequest() {
        triggerTaskEvents = new ArrayList<>();
        actionTaskEvents = new ArrayList<>();
    }

    public ChannelRequest(String displayName, String moduleName, String moduleVersion, String description, List<TriggerEventRequest> triggerTaskEvents, List<ActionEventRequest> actionTaskEvents) {
        this.displayName = displayName;
        this.moduleName = moduleName;
        this.moduleVersion = moduleVersion;
        this.description = description;
        this.triggerTaskEvents = triggerTaskEvents;
        this.actionTaskEvents = actionTaskEvents;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getModuleVersion() {
        return moduleVersion;
    }

    public List<TriggerEventRequest> getTriggerTaskEvents() {
        return triggerTaskEvents;
    }

    public List<ActionEventRequest> getActionTaskEvents() {
        return actionTaskEvents;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final ChannelRequest other = (ChannelRequest) obj;

        return Objects.equals(this.triggerTaskEvents, other.triggerTaskEvents) &&
                Objects.equals(this.actionTaskEvents, other.actionTaskEvents) &&
                Objects.equals(this.displayName, other.displayName) &&
                Objects.equals(this.description, other.description) &&
                Objects.equals(this.moduleName, other.moduleName) &&
                Objects.equals(this.moduleVersion, other.moduleVersion);

    }

    @Override
    public int hashCode() {
        return Objects.hash(actionTaskEvents, triggerTaskEvents, description, displayName, moduleName, moduleVersion);
    }

    @Override
    public String toString() {
        return String.format("Channel{actionTaskEvents=%s, triggerTaskEvents=%s, description='%s', moduleName='%s', moduleVersion='%s', displayName='%s'}",
                actionTaskEvents, triggerTaskEvents, description, moduleName, moduleVersion, displayName);
    }
}
