package org.motechproject.tasks.domain.mds.channel;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.annotations.Cascade;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.Ignore;
import org.motechproject.mds.util.SecurityMode;
import org.motechproject.tasks.constants.TasksRoles;
import org.motechproject.tasks.domain.mds.task.TaskActionInformation;
import org.motechproject.tasks.dto.ActionEventDto;
import org.motechproject.tasks.dto.ChannelDto;

import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Unique;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single task channel. Channel contains the list of triggers from the given module and the list of actions
 * that can be taken by that module.
 */
@Entity(maxFetchDepth = 2)
@Access(value = SecurityMode.PERMISSIONS, members = {TasksRoles.MANAGE_TASKS})
@Unique(name = "MODULENAME_VERSION", members = {"moduleName", "moduleVersion"})
public class Channel {

    @Field
    @Cascade(delete = true)
    private List<ActionEvent> actionTaskEvents = new ArrayList<>();

    @Field
    @Cascade(delete = true)
    @Persistent(mappedBy = "channel")
    private List<TriggerEvent> triggerTaskEvents = new ArrayList<>();

    @Field
    private String description;

    @Field(required = true)
    private String displayName;

    @Field(required = true)
    private String moduleName;

    @Field
    private String moduleVersion;

    @Ignore
    private boolean providesTriggers;

    /**
     * Constructor.
     */
    public Channel() {
        this(null, null, null);
    }

    /**
     * Constructor.
     *
     * @param displayName  the channel display name
     * @param moduleName  the channel module name
     * @param moduleVersion  the module version
     */
    public Channel(String displayName, String moduleName, String moduleVersion) {
        this(displayName, moduleName, moduleVersion, null, null, null);
    }

    /**
     * Constructor.
     *
     * @param displayName  the channel display name
     * @param moduleName  the channel module name
     * @param moduleVersion  the module version
     * @param description  the channel description
     * @param triggerTaskEvents  the list of events for provided triggers
     * @param actionTaskEvents  the list of events for provided actions
     */
    public Channel(String displayName, String moduleName, String moduleVersion, String description,
                   List<TriggerEvent> triggerTaskEvents, List<ActionEvent> actionTaskEvents) {
        this.displayName = displayName;
        this.moduleName = moduleName;
        this.moduleVersion = moduleVersion;
        this.description = description;

        if (actionTaskEvents != null) {
            this.actionTaskEvents.addAll(actionTaskEvents);
        }
        if (triggerTaskEvents != null) {
            this.triggerTaskEvents.addAll(triggerTaskEvents);
        }
        this.providesTriggers = CollectionUtils.isNotEmpty(triggerTaskEvents);
    }

    public boolean containsAction(TaskActionInformation actionInformation) {
        boolean found = false;

        for (ActionEvent action : getActionTaskEvents()) {
            if (action.accept(actionInformation)) {
                found = true;
                break;
            }
        }

        return found;
    }

    public ActionEvent getAction(TaskActionInformation actionInformation) {
        ActionEvent found = null;

        for (ActionEvent action : getActionTaskEvents()) {
            if (action.accept(actionInformation)) {
                found = action;
                break;
            }
        }

        return found;
    }

    public void addActionTaskEvent(ActionEvent actionEvent) {
        actionTaskEvents.add(actionEvent);
    }

    public List<ActionEvent> getActionTaskEvents() {
        return actionTaskEvents;
    }

    public void setActionTaskEvents(List<ActionEvent> actionTaskEvents) {
        this.actionTaskEvents.clear();

        if (actionTaskEvents != null) {
            for (ActionEvent action : actionTaskEvents) {
                addActionTaskEvent(new ActionEvent(action));
            }
        }
    }

    public List<TriggerEvent> getTriggerTaskEvents() {
        return triggerTaskEvents;
    }

    public void setTriggerTaskEvents(List<TriggerEvent> triggerTaskEvents) {
        this.triggerTaskEvents.clear();

        if (triggerTaskEvents != null) {
            for (TriggerEvent trigger : triggerTaskEvents) {
                this.triggerTaskEvents.add(new TriggerEvent(trigger));
            }
        }

        setProvidesTriggers(CollectionUtils.isNotEmpty(this.triggerTaskEvents));
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleVersion() {
        return moduleVersion;
    }

    public void setModuleVersion(String moduleVersion) {
        this.moduleVersion = moduleVersion;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isProvidesTriggers() {
        return providesTriggers;
    }

    public void setProvidesTriggers(boolean providesTriggers) {
        this.providesTriggers = providesTriggers;
    }

    public ChannelDto toDto() {
        List<ActionEventDto> actionEventDtos = new ArrayList<>();

        for (ActionEvent actionEvent : actionTaskEvents) {
            actionEventDtos.add(actionEvent.toDto());
        }

        return new ChannelDto(actionEventDtos, description, displayName, moduleName, moduleVersion, providesTriggers);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final Channel other = (Channel) obj;

        return Objects.equals(this.actionTaskEvents, other.actionTaskEvents) &&
                Objects.equals(this.triggerTaskEvents, other.triggerTaskEvents) &&
                Objects.equals(this.description, other.description) &&
                Objects.equals(this.displayName, other.displayName) &&
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
