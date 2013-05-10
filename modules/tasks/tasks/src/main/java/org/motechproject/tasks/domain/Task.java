package org.motechproject.tasks.domain;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@TypeDiscriminator("doc.type == 'Task'")
public class Task extends MotechBaseDataObject {
    private static final long serialVersionUID = -8754186387983558616L;

    private List<Filter> filters;
    private Map<String, List<TaskAdditionalData>> additionalData;
    private String description;
    private String name;
    private TaskActionInformation action;
    private TaskEventInformation trigger;
    private boolean enabled;
    private Map<String, String> actionInputFields;
    private Set<TaskError> validationErrors;

    public Task() {
        this(null, null, null, null);
    }

    public Task(String name, TaskEventInformation trigger, TaskActionInformation action, Map<String, String> actionInputFields) {
        this(name, trigger, action, actionInputFields, null, null, true);
    }

    public Task(String name, TaskEventInformation trigger, TaskActionInformation action, Map<String, String> actionInputFields,
                List<Filter> filters, Map<String, List<TaskAdditionalData>> additionalData, boolean enabled) {
        this.enabled = enabled;
        this.actionInputFields = actionInputFields == null ? new HashMap<String, String>() : actionInputFields;
        this.additionalData = additionalData == null ? new HashMap<String, List<TaskAdditionalData>>() : additionalData;
        this.filters = filters == null ? new ArrayList<Filter>() : filters;
        this.action = action;
        this.trigger = trigger;
        this.name = name;
        this.validationErrors = new HashSet<>();
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

    public TaskEventInformation getTrigger() {
        return trigger;
    }

    public void setTrigger(final TaskEventInformation trigger) {
        this.trigger = trigger;
    }

    public TaskActionInformation getAction() {
        return action;
    }

    public void setAction(final TaskActionInformation action) {
        this.action = action;
    }

    public Map<String, String> getActionInputFields() {
        return actionInputFields;
    }

    public void setActionInputFields(final Map<String, String> actionInputFields) {
        this.actionInputFields.clear();

        if (actionInputFields != null) {
            this.actionInputFields.putAll(actionInputFields);
        }
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
        this.additionalData.clear();

        if (additionalData != null) {
            this.additionalData.putAll(additionalData);
        }
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public void setFilters(final List<Filter> filters) {
        this.filters.clear();

        if (filters != null) {
            this.filters.addAll(filters);
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addValidationErrors(Set<TaskError> validationErrors) {
        this.validationErrors.addAll(validationErrors);
    }

    public void removeValidationError(final String message) {
        TaskError taskError = (TaskError) CollectionUtils.find(validationErrors, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return object instanceof TaskError && ((TaskError) object).getMessage().equalsIgnoreCase(message);
            }
        });

        validationErrors.remove(taskError);
    }

    public void setValidationErrors(Set<TaskError> validationErrors) {
        this.validationErrors.clear();

        if (validationErrors != null) {
            this.validationErrors.addAll(validationErrors);
        }
    }

    public Set<TaskError> getValidationErrors() {
        return validationErrors;
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
            isEqualTo = Objects.equals(description, task.description) &&
                    Objects.equals(name, task.name) &&
                    Objects.equals(validationErrors, task.validationErrors);
        }
        return isEqualTo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(filters, additionalData, description, name, action, trigger, enabled, actionInputFields, validationErrors);
    }

    @Override
    public String toString() {
        return String.format("Task{filters=%s, additionalData=%s, description='%s', name='%s', action=%s, trigger=%s, enabled=%s, actionInputFields=%s, validationErrors=%s}",
                filters, additionalData, description, name, action, trigger, enabled, actionInputFields, validationErrors);
    }
}
