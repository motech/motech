package org.motechproject.tasks.domain.mds.task;

import org.joda.time.DateTime;
import org.motechproject.commons.date.util.DateTimeSourceUtil;
import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.annotations.CrudEvents;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.Ignore;
import org.motechproject.mds.event.CrudEventType;
import org.motechproject.mds.util.SecurityMode;
import org.motechproject.tasks.constants.TasksRoles;
import org.motechproject.tasks.domain.enums.TaskActivityType;
import org.motechproject.tasks.dto.TaskActivityDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a single task activity. Task activity is a historical entry about a task execution.
 */
@Entity(nonEditable = true)
@CrudEvents(CrudEventType.NONE)
@Access(value = SecurityMode.PERMISSIONS, members = {TasksRoles.MANAGE_TASKS})
public class TaskActivity implements Comparable<TaskActivity> {

    @Field
    private Long id;

    @Field(displayName = "Message")
    private String message;

    @Field(displayName = "Task", required = true)
    private Long task;

    @Field(displayName = "Trigger")
    private String triggerName;

    @Field(displayName = "Fields")
    private List<String> fields;

    @Field(displayName = "Date", required = true)
    private DateTime date;

    @Field(displayName = "Activity Type")
    private TaskActivityType activityType;

    @Field(displayName = "Task execution progress")
    private TaskExecutionProgress executionProgress;

    @Field(displayName = "StackTrace element", type = "text")
    private String stackTraceElement;

    @Field(displayName = "Parameters")
    private Map<String, Object> parameters;

    /**
     * Constructor.
     */
    public TaskActivity() {
        this(null, null, null);
    }

    /**
     * Constructor.
     *
     * @param message  the activity message
     * @param task  the activity task ID
     * @param activityType  the activity type
     */
    public TaskActivity(String message, Long task, TaskActivityType activityType) {
        this(message, new ArrayList<>(), task, activityType, (TaskExecutionProgress) null);
    }

    /**
     * Constructor.
     *
     * @param message  the activity message
     * @param field  the field name
     * @param task  the activity task ID
     * @param activityType  the activity type
     */
    public TaskActivity(String message, String field, Long task, TaskActivityType activityType) {
        this(message, new ArrayList<>(Arrays.asList(field)), task, activityType, (TaskExecutionProgress) null);
    }

    /**
     * Constructor.
     *
     * @param message  the activity message
     * @param fields  the field names
     * @param task  the activity task ID
     * @param activityType  the activity type
     * @param executionProgress the progress of task action executions
     */
    public TaskActivity(String message, List<String> fields, Long task, TaskActivityType activityType, TaskExecutionProgress executionProgress) {
        this(message, fields, task, activityType, null, null, executionProgress);
    }

    /**
     * Constructor.
     *
     * @param message  the activity message
     * @param fields  the field names
     * @param task  the activity ID
     * @param activityType  the activity type
     * @param stackTraceElement  the stack trace that caused the task failure
     */
    public TaskActivity(String message, List<String> fields, Long task, TaskActivityType activityType, String stackTraceElement) {
        this(message, fields, task, activityType, stackTraceElement, null, null);
    }

    /**
     * Constructor.
     *
     * @param message  the activity message
     * @param fields  the field names
     * @param task  the activity ID
     * @param activityType  the activity type
     * @param stackTraceElement  the stack trace that caused the task failure
     * @param parameters the parameters used by the task in this execution
     * @param executionProgress the progress of task action executions
     */
    public TaskActivity(String message, List<String> fields, Long task, TaskActivityType activityType, String stackTraceElement,
                        Map<String, Object> parameters, TaskExecutionProgress executionProgress) {
        this(message, fields, task, null, activityType, stackTraceElement, parameters, executionProgress);
    }

    /**
     * Constructor.
     *
     * @param message  the activity message
     * @param fields  the field names
     * @param task  the activity ID
     * @param triggerName the task trigger name
     * @param activityType  the activity type
     * @param stackTraceElement  the stack trace that caused the task failure
     * @param parameters the parameters used by the task in this execution
     * @param executionProgress the progress of task action executions
     */
    public TaskActivity(String message, List<String> fields, Long task, String triggerName, TaskActivityType activityType, String stackTraceElement,
                        Map<String, Object> parameters, TaskExecutionProgress executionProgress) {
        this.message = message;
        this.fields = fields;
        this.task = task;
        this.triggerName = triggerName;
        this.date = DateTimeSourceUtil.now();
        this.activityType = activityType;
        this.stackTraceElement = stackTraceElement;
        this.parameters = parameters;
        this.executionProgress = executionProgress;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public Long getTask() {
        return task;
    }

    public void setTask(final Long task) {
        this.task = task;
    }

    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(final String triggerName) {
        this.triggerName = triggerName;
    }


    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    @Ignore
    public void setField(String field) {
        if (fields == null) {
            fields = new ArrayList<>();
        } else {
            fields.clear();
        }
        fields.add(field);
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(final DateTime date) {
        this.date = date;
    }

    public TaskActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(final TaskActivityType activityType) {
        this.activityType = activityType;
    }

    public String getStackTraceElement() {
        return stackTraceElement;
    }

    public void setStackTraceElement(String stackTraceElement) {
        this.stackTraceElement = stackTraceElement;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public TaskExecutionProgress getTaskExecutionProgress() {
        return executionProgress;
    }

    public void setTaskExecutionProgress(TaskExecutionProgress executionProgress) {
        this.executionProgress = executionProgress;
    }

    public TaskActivityDto toDto() {
        return new TaskActivityDto(id, message, task, triggerName, fields, date, activityType, stackTraceElement, parameters);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final TaskActivity other = (TaskActivity) obj;

        return Objects.equals(this.message, other.message) &&
                Objects.equals(this.task, other.task) &&
                Objects.equals(this.fields, other.fields) &&
                Objects.equals(getDate(), other.getDate()) &&
                Objects.equals(this.activityType, other.activityType) &&
                Objects.equals(this.stackTraceElement, other.stackTraceElement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, task, fields, getDate(), activityType, stackTraceElement);
    }

    @Override
    public String toString() {
        return String.format("TaskActivity{message='%s', task=%d, field='%s', date=%s, activityType=%s, stackTrace=%s}",
                message, task, fields, date, activityType, stackTraceElement);
    }

    @Override
    public int compareTo(TaskActivity o) {
        return getDate().compareTo(o.getDate());
    }
}
