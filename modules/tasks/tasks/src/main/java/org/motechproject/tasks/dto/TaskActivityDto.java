package org.motechproject.tasks.dto;

import org.joda.time.DateTime;
import org.motechproject.tasks.domain.enums.TaskActivityType;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TaskActivityDto {

    private Long id;
    private String message;
    private Long task;
    private List<String> fields;
    private DateTime date;
    private TaskActivityType activityType;
    private String stackTraceElement;
    private Map<String, Object> parameters;

    public TaskActivityDto(String message, Long task, TaskActivityType activityType) {
        this.message = message;
        this.task = task;
        this.activityType = activityType;
    }

    public TaskActivityDto(Long id, String message, Long task, List<String> fields, DateTime date, TaskActivityType activityType, String stackTraceElement, Map<String, Object> parameters) {
        this.id = id;
        this.message = message;
        this.task = task;
        this.fields = fields;
        this.date = date;
        this.activityType = activityType;
        this.stackTraceElement = stackTraceElement;
        this.parameters = parameters;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTask() {
        return task;
    }

    public void setTask(Long task) {
        this.task = task;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public TaskActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(TaskActivityType activityType) {
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final TaskActivityDto other = (TaskActivityDto) obj;

        return  Objects.equals(this.id, other.id) &&
                Objects.equals(this.message, other.message) &&
                Objects.equals(this.task, other.task) &&
                Objects.equals(this.fields, other.fields) &&
                Objects.equals(getDate(), other.getDate()) &&
                Objects.equals(this.activityType, other.activityType) &&
                Objects.equals(this.stackTraceElement, other.stackTraceElement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, message, task, fields, getDate(), activityType, stackTraceElement);
    }
}
