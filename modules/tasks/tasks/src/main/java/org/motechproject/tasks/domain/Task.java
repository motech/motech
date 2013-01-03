package org.motechproject.tasks.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@TypeDiscriminator("doc.type == 'Task'")
public class Task extends MotechBaseDataObject {
    private Map<String, String> actionInputFields;
    private Map<String, String> additionalData;
    private List<Filter> filters;
    private String action;
    private String description;
    private String trigger;
    private boolean enabled;

    public Task() {
        this(null, null, null);
    }

    public Task(String trigger, String action, Map<String, String> actionInputFields) {
        this(true, actionInputFields, null, null, action, trigger);
    }

    public Task(boolean enabled, Map<String, String> actionInputFields, Map<String, String> additionalData, List<Filter> filters, String action, String trigger) {
        this.enabled = enabled;
        this.actionInputFields = actionInputFields;
        this.additionalData = additionalData;
        this.filters = filters;
        this.action = action;
        this.trigger = trigger;
    }

    @JsonIgnore
    public boolean hasFilters() {
        return !filters.isEmpty();
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

    public List<Filter> getFilters() {
        return filters;
    }

    public void setFilters(final List<Filter> filters) {
        this.filters = filters;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

        return Objects.equals(enabled, task.enabled) && Objects.equals(action, task.action) &&
                Objects.equals(actionInputFields, task.actionInputFields) &&
                Objects.equals(additionalData, task.additionalData) && Objects.equals(filters, task.filters) &&
                Objects.equals(trigger, task.trigger) && Objects.equals(description, task.description);
    }

    @Override
    public int hashCode() {
        int result = (enabled ? 1 : 0);
        result = 31 * result + (actionInputFields != null ? actionInputFields.hashCode() : 0);
        result = 31 * result + (additionalData != null ? additionalData.hashCode() : 0);
        result = 31 * result + (filters != null ? filters.hashCode() : 0);
        result = 31 * result + (action != null ? action.hashCode() : 0);
        result = 31 * result + (trigger != null ? trigger.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return String.format("Task{actionInputFields=%s, additionalData=%s, filters=%s, action='%s', description='%s', trigger='%s', enabled=%s}",
                actionInputFields, additionalData, filters, action, description, trigger, enabled);
    }
}
