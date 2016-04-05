package org.motechproject.tasks.contract;

import org.motechproject.tasks.domain.mds.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Service layer object denoting a {@link Channel}. Used by {@link org.motechproject.tasks.service.ChannelService}. It
 * is used for registering new and updating already existent
 * channels.
 */
public class ChannelRequest {

    private String displayName;
    private String moduleName;
    private String moduleVersion;
    private String description;
    private List<TriggerEventRequest> triggerTaskEvents;
    private List<ActionEventRequest> actionTaskEvents;

    /**
     * Constructor.
     */
    private ChannelRequest() {
        triggerTaskEvents = new ArrayList<>();
        actionTaskEvents = new ArrayList<>();
    }

    /**
     * Constructor.
     *
     * @param displayName  the channel display name
     * @param moduleName  the module symbolic name
     * @param moduleVersion  the module version
     * @param description  the channel description
     * @param triggerTaskEvents  the triggers definitions
     * @param actionTaskEvents  the actions definitions
     */
    public ChannelRequest(String displayName, String moduleName, String moduleVersion, String description, List<TriggerEventRequest> triggerTaskEvents, List<ActionEventRequest> actionTaskEvents) {
        this.displayName = displayName;
        this.moduleName = moduleName;
        this.moduleVersion = moduleVersion;
        this.description = description;
        this.triggerTaskEvents = triggerTaskEvents != null ? triggerTaskEvents : new ArrayList<>();
        this.actionTaskEvents = actionTaskEvents != null ? actionTaskEvents : new ArrayList<>();
    }

    /**
     * Returns the display name of the channel.
     *
     * @return the channel display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the symbolic name of the module.
     *
     * @return the module symbolic name
     */
    public String getModuleName() {
        return moduleName;
    }

    /**
     * Returns the version of the module.
     *
     * @return the module version
     */
    public String getModuleVersion() {
        return moduleVersion;
    }

    /**
     * Returns the task trigger events for this channel.
     *
     * @return the task trigger events
     */
    public List<TriggerEventRequest> getTriggerTaskEvents() {
        return triggerTaskEvents;
    }

    /**
     * Returns the task action events for this channel.
     *
     * @return the task action events
     */
    public List<ActionEventRequest> getActionTaskEvents() {
        return actionTaskEvents;
    }

    /**
     * Returns the description of the channel.
     *
     * @return the channel description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the module name of this channel.
     *
     * @param moduleName  the channel module name
     */
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * Sets the module version of this channel.
     *
     * @param moduleVersion  the module version
     */
    public void setModuleVersion(String moduleVersion) {
        this.moduleVersion = moduleVersion;
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
