package org.motechproject.tasks.dto;

import java.util.List;
import java.util.Set;

public class TaskDto {

    private Long id;
    private String description;
    private String name;
    private int failuresInRow;
    private List<TaskActionInformationDto> actions;
    private TaskTriggerInformationDto trigger;
    private boolean enabled;
    private Set<TaskErrorDto> validationErrors;
    private TaskConfigDto taskConfig;
    private boolean hasRegisteredChannel;
    private int numberOfRetries;
    private int retryIntervalInMilliseconds;
    private boolean retryTaskOnFailure;
    private boolean useTimeWindow;
    private String startTime;
    private String endTime;

    public TaskDto() {
    }

    public TaskDto(String name, TaskTriggerInformationDto trigger, List<TaskActionInformationDto> actions) {
        this.name = name;
        this.trigger = trigger;
        this.actions = actions;
    }

    public TaskDto(Long id, String description, String name, int failuresInRow, List<TaskActionInformationDto> actions,
                   TaskTriggerInformationDto trigger, boolean enabled, Set<TaskErrorDto> validationErrors, TaskConfigDto taskConfig,
                   boolean hasRegisteredChannel, int numberOfRetries, int retryIntervalInMilliseconds, boolean retryTaskOnFailure, boolean useTimeWindow, String startTime, String endTime) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.failuresInRow = failuresInRow;
        this.actions = actions;
        this.trigger = trigger;
        this.enabled = enabled;
        this.validationErrors = validationErrors;
        this.taskConfig = taskConfig;
        this.hasRegisteredChannel = hasRegisteredChannel;
        this.numberOfRetries = numberOfRetries;
        this.retryIntervalInMilliseconds = retryIntervalInMilliseconds;
        this.retryTaskOnFailure = retryTaskOnFailure;
        this.useTimeWindow = useTimeWindow;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFailuresInRow() {
        return failuresInRow;
    }

    public void setFailuresInRow(int failuresInRow) {
        this.failuresInRow = failuresInRow;
    }

    public List<TaskActionInformationDto> getActions() {
        return actions;
    }

    public void setActions(List<TaskActionInformationDto> actions) {
        this.actions = actions;
    }

    public TaskTriggerInformationDto getTrigger() {
        return trigger;
    }

    public void setTrigger(TaskTriggerInformationDto trigger) {
        this.trigger = trigger;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setRetryTaskOnFailure(boolean retryTaskOnFailure) {
        this.retryTaskOnFailure = retryTaskOnFailure;
    }

    public boolean isRetryTaskOnFailure() {
        return retryTaskOnFailure;
    }

    public Set<TaskErrorDto> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(Set<TaskErrorDto> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public TaskConfigDto getTaskConfig() {
        return taskConfig;
    }

    public void setTaskConfig(TaskConfigDto taskConfig) {
        this.taskConfig = taskConfig;
    }

    public boolean isHasRegisteredChannel() {
        return hasRegisteredChannel;
    }

    public void setHasRegisteredChannel(boolean hasRegisteredChannel) {
        this.hasRegisteredChannel = hasRegisteredChannel;
    }

    public int getNumberOfRetries() {
        return numberOfRetries;
    }

    public void setNumberOfRetries(int numberOfRetries) {
        this.numberOfRetries = numberOfRetries;
    }

    public int getRetryIntervalInMilliseconds() {
        return retryIntervalInMilliseconds;
    }

    public void setRetryIntervalInMilliseconds(int retryIntervalInMilliseconds) {
        this.retryIntervalInMilliseconds = retryIntervalInMilliseconds;
    }

    public boolean isUseTimeWindow() {
        return useTimeWindow;
    }

    public void setUseTimeWindow(boolean useTimeWindow) {
        this.useTimeWindow = useTimeWindow;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
