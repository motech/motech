package org.motechproject.tasks.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;

@TypeDiscriminator("doc.type == 'Channel'")
public class Channel extends MotechBaseDataObject {
    private static final long serialVersionUID = -5528351408863732084L;

    private List<ActionEvent> actionTaskEvents;
    private List<TriggerEvent> triggerTaskEvents;
    private String description;
    private String displayName;
    private String moduleName;
    private String moduleVersion;

    public Channel() {
        this(null, null, null);
    }

    public Channel(String displayName, String moduleName, String moduleVersion) {
        this(displayName, moduleName, moduleVersion, null, null, null);
    }

    public Channel(String displayName, String moduleName, String moduleVersion, String description,
                   List<TriggerEvent> triggerTaskEvents, List<ActionEvent> actionTaskEvents) {
        this.displayName = displayName;
        this.moduleName = moduleName;
        this.moduleVersion = moduleVersion;
        this.description = description;

        this.actionTaskEvents = actionTaskEvents == null ? new ArrayList<ActionEvent>() : actionTaskEvents;
        this.triggerTaskEvents = triggerTaskEvents == null ? new ArrayList<TriggerEvent>() : triggerTaskEvents;
    }

    public boolean containsTrigger(TaskEventInformation triggerInformation) {
        boolean found = false;

        for (TriggerEvent trigger : getTriggerTaskEvents()) {
            if (equalsIgnoreCase(trigger.getSubject(), triggerInformation.getSubject())) {
                found = true;
                break;
            }
        }

        return found;
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

    public TriggerEvent getTrigger(TaskEventInformation triggerInformation) {
        TriggerEvent found = null;

        for (TriggerEvent trigger : getTriggerTaskEvents()) {
            if (equalsIgnoreCase(trigger.getSubject(), triggerInformation.getSubject())) {
                found = trigger;
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
            this.actionTaskEvents.addAll(actionTaskEvents);
        }
    }

    public List<TriggerEvent> getTriggerTaskEvents() {
        return triggerTaskEvents;
    }

    public void setTriggerTaskEvents(List<TriggerEvent> triggerTaskEvents) {
        this.triggerTaskEvents.clear();

        if (triggerTaskEvents != null) {
            this.triggerTaskEvents.addAll(triggerTaskEvents);
        }
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
