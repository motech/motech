package org.motechproject.tasks.dto;

import org.joda.time.DateTime;
import org.motechproject.tasks.domain.enums.TaskActivityType;

import java.util.List;
import java.util.Map;

public class TaskActivityDto {

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

    public TaskActivityDto(String message, Long task, List<String> fields, DateTime date, TaskActivityType activityType, String stackTraceElement, Map<String, Object> parameters) {
        this.message = message;
        this.task = task;
        this.fields = fields;
        this.date = date;
        this.activityType = activityType;
        this.stackTraceElement = stackTraceElement;
        this.parameters = parameters;
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
}
