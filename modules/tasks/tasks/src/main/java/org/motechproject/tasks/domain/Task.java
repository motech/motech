package org.motechproject.tasks.domain;

import org.apache.commons.collections.Predicate;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.tasks.json.TaskDeserializer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.find;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

/**
 * A task is set of actions that are executed in response to a trigger. The actions and the trigger are defined by their respective {@link Channel}s.
 */
@TypeDiscriminator("doc.type == 'Task'")
@JsonDeserialize(using = TaskDeserializer.class)
public class Task extends MotechBaseDataObject {
    private static final long serialVersionUID = -8754186387983558616L;

    private String description;
    private String name;
    private List<TaskActionInformation> actions;
    private TaskEventInformation trigger;
    private boolean enabled;
    private Set<TaskError> validationErrors;
    private TaskConfig taskConfig;
    private boolean hasRegisteredChannel;

    public Task() {
        this(null, null, null);
    }

    public Task(String name, TaskEventInformation trigger, List<TaskActionInformation> actions) {
        this(name, trigger, actions, null, true, true);
    }

    public Task(String name, TaskEventInformation trigger, List<TaskActionInformation> actions,
                TaskConfig taskConfig, boolean enabled, boolean hasRegisteredChannel) {
        this.name = name;
        this.actions = actions == null ? new ArrayList<TaskActionInformation>() : actions;
        this.trigger = trigger;
        this.enabled = enabled;
        this.hasRegisteredChannel = hasRegisteredChannel;
        this.taskConfig = taskConfig == null ? new TaskConfig() : taskConfig;
        this.validationErrors = new HashSet<>();
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

    public List<TaskActionInformation> getActions() {
        return actions;
    }

    public void setActions(final List<TaskActionInformation> actions) {
        this.actions.clear();

        if (actions != null) {
            this.actions.addAll(actions);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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
        TaskError taskError = (TaskError) find(validationErrors, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return object instanceof TaskError
                        && ((TaskError) object).getMessage().equalsIgnoreCase(message);
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

    public boolean hasValidationErrors() {
        return isNotEmpty(validationErrors);
    }

    public TaskConfig getTaskConfig() {
        return taskConfig;
    }

    public void setTaskConfig(TaskConfig taskConfig) {
        this.taskConfig = taskConfig;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                description, name, actions, trigger, enabled, validationErrors, taskConfig
        );
    }

    @Override   // NO CHECKSTYLE CyclomaticComplexity
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final Task other = (Task) obj;

        return Objects.equals(this.description, other.description)
                && Objects.equals(this.name, other.name)
                && Objects.equals(this.actions, other.actions)
                && Objects.equals(this.trigger, other.trigger)
                && Objects.equals(this.enabled, other.enabled)
                && Objects.equals(this.hasRegisteredChannel, other.hasRegisteredChannel)
                && Objects.equals(this.validationErrors, other.validationErrors)
                && Objects.equals(this.taskConfig, other.taskConfig);
    }

    @Override
    public String toString() {
        return String.format(
                "Task{description='%s', name='%s', actions=%s, trigger=%s, enabled=%s, validationErrors=%s, taskConfig=%s, hasRegisteredChannel=%s} ",
                description, name, actions, trigger, enabled, validationErrors, taskConfig, hasRegisteredChannel
        );
    }

    @JsonProperty("hasRegisteredChannel")
    public void setHasRegisteredChannel(boolean hasRegisteredChannel) {
        this.hasRegisteredChannel = hasRegisteredChannel;
    }

    @JsonProperty("hasRegisteredChannel")
    public boolean hasRegisteredChannel() {
        return hasRegisteredChannel;
    }
}
