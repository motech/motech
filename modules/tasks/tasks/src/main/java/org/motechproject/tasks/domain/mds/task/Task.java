package org.motechproject.tasks.domain.mds.task;

import org.apache.commons.collections.Predicate;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.annotations.Cascade;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.util.SecurityMode;
import org.motechproject.tasks.constants.TasksRoles;
import org.motechproject.tasks.dto.TaskActionInformationDto;
import org.motechproject.tasks.dto.TaskDto;
import org.motechproject.tasks.dto.TaskErrorDto;
import org.motechproject.tasks.domain.mds.channel.Channel;
import org.motechproject.tasks.json.TaskDeserializer;

import javax.jdo.annotations.Unique;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.apache.commons.collections.CollectionUtils.find;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

/**
 * A task is set of actions that are executed in response to a trigger. The actions and the trigger are defined by their
 * respective {@link Channel}s.
 */
@Entity(recordHistory = true)
@JsonDeserialize(using = TaskDeserializer.class)
@Access(value = SecurityMode.PERMISSIONS, members = {TasksRoles.MANAGE_TASKS})
public class Task {

    private static final int DEFAULT_NUMBER_FOR_TASK_RETRIES = 0;
    private static final int DEFAULT_TIME_FOR_RETRY_INTERVAL = 0;

    @Field
    private Long id;

    @Field
    private String description;

    @Field(required = true)
    @Unique
    private String name;

    @Field
    private int failuresInRow;

    @Field
    @Cascade(delete = true)
    private List<TaskActionInformation> actions;

    @Field
    @Cascade(delete = true)
    private TaskTriggerInformation trigger;

    @Field
    private boolean enabled;

    @Field
    @Cascade(delete = true)
    private Set<TaskError> validationErrors;

    @Field
    @Cascade(delete = true)
    private TaskConfig taskConfig;

    @Field
    private boolean hasRegisteredChannel;

    @Field
    private int numberOfRetries;

    @Field
    private int retryIntervalInMilliseconds;

    @Field
    private boolean retryTaskOnFailure;

    /**
     * Constructor.
     */
    public Task() {
        this(null, null, null);
    }

    /**
     * Constructor.
     *
     * @param name  the task name
     * @param trigger  the task trigger
     * @param actions  the list of related actions
     */
    public Task(String name, TaskTriggerInformation trigger, List<TaskActionInformation> actions) {
        this(name, trigger, actions, null, true, true);
    }

    /**
     * Constructor.
     *
     * @param name  the task name
     * @param trigger  the task trigger
     * @param actions  the list of related actions
     * @param taskConfig  the task configuration
     * @param enabled  defines if this task is enabled
     * @param hasRegisteredChannel  defines if this task has a registered channel
     */
    public Task(String name, TaskTriggerInformation trigger, List<TaskActionInformation> actions,
                TaskConfig taskConfig, boolean enabled, boolean hasRegisteredChannel) {
        this.name = name;
        this.actions = actions == null ? new ArrayList<TaskActionInformation>() : actions;
        this.trigger = trigger;
        this.enabled = enabled;
        this.hasRegisteredChannel = hasRegisteredChannel;
        this.taskConfig = taskConfig == null ? new TaskConfig() : taskConfig;
        this.validationErrors = new HashSet<>();
        this.failuresInRow = 0;
        this.numberOfRetries = DEFAULT_NUMBER_FOR_TASK_RETRIES;
        this.retryIntervalInMilliseconds = DEFAULT_TIME_FOR_RETRY_INTERVAL;
        this.retryTaskOnFailure = false;
    }

    /**
     * Stores the given action.
     *
     * @param action  the action
     */
    public void addAction(TaskActionInformation action) {
        if (action != null) {
            actions.add(action);
        }
    }

    /**
     * Increases the counter of task execution failures that occurred since the last successful execution of this task
     * or since the task was enabled.
     */
    public void incrementFailuresInRow() {
        failuresInRow++;
    }

    /**
     * Resets the counter of task execution failures that occurred since the last successful execution of this task or
     * since the task was enabled.
     */
    public void resetFailuresInRow() {
        failuresInRow = 0;
    }

    public int getFailuresInRow() {
        return failuresInRow;
    }

    public void setFailuresInRow(int failuresInRow) {
        this.failuresInRow = failuresInRow;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TaskTriggerInformation getTrigger() {
        return trigger;
    }

    public void setTrigger(final TaskTriggerInformation trigger) {
        this.trigger = trigger;
    }

    public List<TaskActionInformation> getActions() {
        if (actions == null) {
            actions = new ArrayList<>();
        }
        return actions;
    }

    public void setActions(final List<TaskActionInformation> actions) {
        this.actions = actions;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setRetryTaskOnFailure(boolean retryTaskOnFailure) {
        this.retryTaskOnFailure = retryTaskOnFailure;
    }

    public boolean getRetryTaskOnFailure() {
        return retryTaskOnFailure;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addValidationErrors(Set<TaskError> validationErrors) {
        this.getValidationErrors().addAll(validationErrors);
    }

    public void removeValidationError(final String message) {
        TaskError taskError = (TaskError) find(getValidationErrors(), new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return object instanceof TaskError
                        && ((TaskError) object).getMessage().equalsIgnoreCase(message);
            }
        });

        getValidationErrors().remove(taskError);
    }

    public void setValidationErrors(Set<TaskError> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public Set<TaskError> getValidationErrors() {
        if (validationErrors == null) {
            validationErrors = new HashSet<>();
        }
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

    public int getNumberOfRetries() {
        return numberOfRetries;
    }

    public void setNumberOfRetries(int numberOfRetries) {
        this.numberOfRetries = numberOfRetries;
    }

    public boolean retryTaskOnFailure() {
        return getNumberOfRetries() > 0;
    }

    public int getRetryIntervalInMilliseconds() {
        return retryIntervalInMilliseconds;
    }

    public void setRetryIntervalInMilliseconds(int retryIntervalInMilliseconds) {
        this.retryIntervalInMilliseconds = retryIntervalInMilliseconds;
    }

    public TaskDto toDto() {
        List<TaskActionInformationDto> actionDtos = new ArrayList<>();
        Set<TaskErrorDto> errorDtos = new HashSet<>();

        for (TaskActionInformation action : actions) {
            actionDtos.add(action.toDto());
        }

        for (TaskError error :  validationErrors) {
            errorDtos.add(error.toDto());
        }

        return new TaskDto(id, description, name, failuresInRow, actionDtos, trigger.toDto(), enabled, errorDtos,
                taskConfig.toDto(), hasRegisteredChannel, numberOfRetries, retryIntervalInMilliseconds, retryTaskOnFailure);
    }


    @Override
    public int hashCode() {
        return Objects.hash(
                id, description, name, getActions(), trigger, enabled, taskConfig
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

        return Objects.equals(id, this.id)
                && Objects.equals(this.description, other.description)
                && Objects.equals(this.name, other.name)
                && Objects.equals(getActions(), other.getActions())
                && Objects.equals(this.trigger, other.trigger)
                && Objects.equals(this.enabled, other.enabled)
                && Objects.equals(this.hasRegisteredChannel, other.hasRegisteredChannel)
                && Objects.equals(this.taskConfig, other.taskConfig);
    }

    @Override
    public String toString() {
        return String.format(
                "Task{id=%d, description='%s', name='%s', actions=%s, trigger=%s, enabled=%s, taskConfig=%s, hasRegisteredChannel=%s} ",
                id, description, name, getActions(), trigger, enabled, taskConfig, hasRegisteredChannel
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
