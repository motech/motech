package org.motechproject.tasks.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

import java.util.Map;

@TypeDiscriminator("doc.type == 'Task'")
public class Task extends MotechBaseDataObject {
    private boolean enabled;
    private Map<String, String> actionInputFields;
    private Map<String, String> additionalData;
    private Map<String, String> filter;
    private String action;
    private String trigger;

    public Task() {
        this(null, null, null);
    }

    public Task(String trigger, String action, Map<String, String> actionInputFields) {
        this(true, actionInputFields, null, null, action, trigger);
    }

    public Task(boolean enabled, Map<String, String> actionInputFields, Map<String, String> additionalData, Map<String, String> filter, String action, String trigger) {
        this.enabled = enabled;
        this.actionInputFields = actionInputFields;
        this.additionalData = additionalData;
        this.filter = filter;
        this.action = action;
        this.trigger = trigger;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(final String trigger) {
        this.trigger = trigger;
    }

    public String getAction() {
        return action;
    }

    public void setAction(final String action) {
        this.action = action;
    }

    public Map<String, String> getActionInputFields() {
        return actionInputFields;
    }

    public void setActionInputFields(final Map<String, String> actionInputFields) {
        this.actionInputFields = actionInputFields;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Map<String, String> getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(final Map<String, String> additionalData) {
        this.additionalData = additionalData;
    }

    public Map<String, String> getFilter() {
        return filter;
    }

    public void setFilter(final Map<String, String> filter) {
        this.filter = filter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Task task = (Task) o;

        if (enabled != task.enabled) {
            return false;
        }

        if (action != null ? !action.equals(task.action) : task.action != null) {
            return false;
        }

        if (actionInputFields != null ? !actionInputFields.equals(task.actionInputFields) : task.actionInputFields != null) {
            return false;
        }

        if (additionalData != null ? !additionalData.equals(task.additionalData) : task.additionalData != null) {
            return false;
        }

        if (filter != null ? !filter.equals(task.filter) : task.filter != null) {
            return false;
        }

        if (trigger != null ? !trigger.equals(task.trigger) : task.trigger != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (enabled ? 1 : 0);
        result = 31 * result + (actionInputFields != null ? actionInputFields.hashCode() : 0);
        result = 31 * result + (additionalData != null ? additionalData.hashCode() : 0);
        result = 31 * result + (filter != null ? filter.hashCode() : 0);
        result = 31 * result + (action != null ? action.hashCode() : 0);
        result = 31 * result + (trigger != null ? trigger.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return String.format("Task{trigger='%s', action='%s', actionInputFields=%s, enabled=%s, additionalData=%s, filter=%s}",
                trigger, action, actionInputFields, enabled, additionalData, filter);
    }
}
