package org.motechproject.tasks.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

import java.util.List;
import java.util.Objects;

@TypeDiscriminator("doc.type == 'Channel'")
public class Channel extends MotechBaseDataObject {
    private List<TaskEvent> actionTaskEvents;
    private List<TaskEvent> triggerTaskEvents;
    private String description;
    private String displayName;
    private String moduleName;
    private String moduleVersion;

    public List<TaskEvent> getActionTaskEvents() {
        return actionTaskEvents;
    }

    public void setActionTaskEvents(List<TaskEvent> actionTaskEvents) {
        this.actionTaskEvents = actionTaskEvents;
    }

    public List<TaskEvent> getTriggerTaskEvents() {
        return triggerTaskEvents;
    }

    public void setTriggerTaskEvents(List<TaskEvent> triggerTaskEvents) {
        this.triggerTaskEvents = triggerTaskEvents;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Channel channel = (Channel) o;

        return Objects.equals(actionTaskEvents, channel.actionTaskEvents) &&
                Objects.equals(description, channel.description) && Objects.equals(displayName, channel.displayName) &&
                Objects.equals(moduleName, channel.moduleName) && Objects.equals(moduleVersion, channel.moduleVersion) &&
                Objects.equals(triggerTaskEvents, channel.triggerTaskEvents);
    }

    @Override
    public int hashCode() {
        int result = actionTaskEvents != null ? actionTaskEvents.hashCode() : 0;
        result = 31 * result + (triggerTaskEvents != null ? triggerTaskEvents.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (moduleName != null ? moduleName.hashCode() : 0);
        result = 31 * result + (moduleVersion != null ? moduleVersion.hashCode() : 0);
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return String.format("Channel{actionTaskEvents=%s, triggerTaskEvents=%s, description='%s', moduleName='%s', moduleVersion='%s', displayName='%s'}",
                actionTaskEvents, triggerTaskEvents, description, moduleName, moduleVersion, displayName);
    }
}
