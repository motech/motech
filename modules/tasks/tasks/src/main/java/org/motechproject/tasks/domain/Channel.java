package org.motechproject.tasks.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

import java.util.List;
import java.util.Objects;

@TypeDiscriminator("doc.type == 'Channel'")
public class Channel extends MotechBaseDataObject {
    private static final long serialVersionUID = -5528351408863732084L;

    private List<ActionEvent> actionTaskEvents;
    private List<TriggerEvent> triggerTaskEvents;
    private String description;
    private String displayName;
    private String moduleName;
    private String moduleVersion;

    public List<ActionEvent> getActionTaskEvents() {
        return actionTaskEvents;
    }

    public void setActionTaskEvents(List<ActionEvent> actionTaskEvents) {
        this.actionTaskEvents = actionTaskEvents;
    }

    public List<TriggerEvent> getTriggerTaskEvents() {
        return triggerTaskEvents;
    }

    public void setTriggerTaskEvents(List<TriggerEvent> triggerTaskEvents) {
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
