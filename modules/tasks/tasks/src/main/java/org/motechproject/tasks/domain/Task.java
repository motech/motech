package org.motechproject.tasks.domain;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.tasks.json.TaskDeserializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.util.Arrays.asList;

@TypeDiscriminator("doc.type == 'Task'")
@JsonDeserialize(using = TaskDeserializer.class)
public class Task extends MotechBaseDataObject {
    private static final long serialVersionUID = -8754186387983558616L;

    private List<Filter> filters;
    private Map<String, List<TaskAdditionalData>> additionalData;
    private String description;
    private String name;
    private List<TaskActionInformation> actions;
    private TaskEventInformation trigger;
    private boolean enabled;
    private Set<TaskError> validationErrors;

    public Task() {
        this(null, null, null);
    }

    /**
     * @deprecated As of release 0.20, replaced by {@link #Task(String, TaskEventInformation, java.util.List)}
     */
    @Deprecated
    public Task(String name, TaskEventInformation trigger, TaskActionInformation action, Map<String, String> actionInputFields) {
        this(name, trigger, action, actionInputFields, null, null, true);
    }

    public Task(String name, TaskEventInformation trigger, List<TaskActionInformation> actions) {
       this(name, trigger, actions, null, null, true);
    }

    /**
     * @deprecated As of release 0.20, replaced by {@link #Task(String, TaskEventInformation, java.util.List, java.util.List, java.util.Map, boolean)}
     */
    @Deprecated
    public Task(String name, TaskEventInformation trigger, TaskActionInformation action, Map<String, String> actionInputFields,
                List<Filter> filters, Map<String, List<TaskAdditionalData>> additionalData, boolean enabled) {
        this.enabled = enabled;
        this.additionalData = additionalData == null ? new HashMap<String, List<TaskAdditionalData>>() : additionalData;
        this.filters = filters == null ? new ArrayList<Filter>() : filters;
        this.trigger = trigger;
        this.name = name;
        this.validationErrors = new HashSet<>();

        if (action != null) {
            action.setValues(actionInputFields);
            this.actions = asList(action);
        }
    }

    public Task(String name, TaskEventInformation trigger, List<TaskActionInformation> actions,
                List<Filter> filters, Map<String, List<TaskAdditionalData>> additionalData, boolean enabled) {
        this.enabled = enabled;
        this.additionalData = additionalData == null ? new HashMap<String, List<TaskAdditionalData>>() : additionalData;
        this.filters = filters == null ? new ArrayList<Filter>() : filters;
        this.actions = actions == null ? new ArrayList<TaskActionInformation>() : actions;
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

    public void addAction(TaskActionInformation action) {
        if (action != null) {
            actions.add(action);
        }
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

    /**
     * @deprecated As of release 0.20, replaced by {@link #getActions()}
     */
    @Deprecated
    @JsonIgnore
    public TaskActionInformation getAction() {
        return getActions().get(0);
    }

    /**
     * @deprecated As of release 0.20, replaced by {@link #setActions(java.util.List)}
     */
    @Deprecated
    @JsonIgnore
    public void setAction(final TaskActionInformation action) {
        setActions(asList(action));
    }

    public List<TaskActionInformation> getActions() {
        return actions;
    }

    public void setActions(final List<TaskActionInformation> actions) {
        this.actions.clear();

        if (actions != null) {
            this.actions.addAll(actions);
        }
    }

    /**
     * @deprecated As of release 0.20, replaced by {@link org.motechproject.tasks.domain.TaskActionInformation#getValues()}
     */
    @Deprecated
    @JsonIgnore
    public Map<String, String> getActionInputFields() {
        return getAction().getValues();
    }

    /**
     * @deprecated As of release 0.20, replaced by {@link org.motechproject.tasks.domain.TaskActionInformation#setValues(java.util.Map)}
     */
    @Deprecated
    @JsonIgnore
    public void setActionInputFields(final Map<String, String> actionInputFields) {
        getAction().setValues(actionInputFields);
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
        boolean isEqualTo = Objects.equals(enabled, task.enabled) && Objects.equals(actions, task.actions);

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
        return Objects.hash(filters, additionalData, description, name, actions, trigger, enabled, validationErrors);
    }

    @Override
    public String toString() {
        return String.format("Task{filters=%s, additionalData=%s, description='%s', name='%s', actions=%s, trigger=%s, enabled=%s, validationErrors=%s}",
                filters, additionalData, description, name, actions, trigger, enabled, validationErrors);
    }
}
