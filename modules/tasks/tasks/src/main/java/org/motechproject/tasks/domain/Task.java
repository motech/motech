package org.motechproject.tasks.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@TypeDiscriminator("doc.type == 'Task'")
public class Task extends MotechBaseDataObject {
    private static final long serialVersionUID = -8754186387983558616L;

    private Map<String, String> actionInputFields;
    private Map<String, List<TaskAdditionalData>> additionalData;
    private List<Filter> filters;
    private String action;
    private String description;
    private String trigger;
    private String name;
    private boolean enabled;

    public Task() {
        this(null, null, null, null);
    }

    public Task(String trigger, String action, Map<String, String> actionInputFields, String name) {
        this(true, actionInputFields, null, null, action, trigger, name);
    }

    public Task(boolean enabled, Map<String, String> actionInputFields, Map<String, List<TaskAdditionalData>> additionalData,
                List<Filter> filters, String action, String trigger, String name) {
        this.enabled = enabled;
        this.actionInputFields = actionInputFields;
        this.additionalData = additionalData;
        this.filters = filters;
        this.action = action;
        this.trigger = trigger;
        this.name = name;
    }

    @JsonIgnore
    public boolean containsAdditionalData(String dataProviderId) {
        return additionalData.containsKey(dataProviderId);
    }

    @JsonIgnore
    public List<TaskAdditionalData> getAdditionalData(String dataProviderId) {
        return additionalData.get(dataProviderId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Map<String, List<TaskAdditionalData>> getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(final Map<String, List<TaskAdditionalData>> additionalData) {
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

        return taskObjectCompare(task);
    }

    private boolean taskObjectCompare(Task task) {
        boolean isEqualTo = Objects.equals(enabled, task.enabled) && Objects.equals(action, task.action) &&
                Objects.equals(actionInputFields, task.actionInputFields);

        if (isEqualTo) {
            isEqualTo = Objects.equals(additionalData, task.additionalData) && Objects.equals(filters, task.filters) &&
                    Objects.equals(trigger, task.trigger);
        }
        if (isEqualTo) {
            isEqualTo = Objects.equals(description,
                    task.description) && Objects.equals(name, task.name);
        }
        return isEqualTo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(filters, additionalData, description, name, action, trigger, enabled, actionInputFields);
    }

    @Override
    public String toString() {
        return String.format("Task{actionInputFields=%s, additionalData=%s, filters=%s, action='%s', description='%s', trigger='%s', name='%s', enabled=%s}",
                actionInputFields, additionalData, filters, action, description, trigger, name, enabled);
    }
}
