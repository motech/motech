package org.motechproject.tasks.service;

import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActivity;
import org.motechproject.tasks.domain.TaskActivityType;
import org.motechproject.tasks.ex.TaskHandlerException;

import java.util.List;

/**
 * Service for managing task activities. Task activities are used for storing information about past task executions.
 */
public interface TaskActivityService {

    /**
     * Logs an execution error for the given task.
     *
     * @param task  the failed task, not null
     * @param e  the cause of the error, not null
     */
    void addError(Task task, TaskHandlerException e);

    /**
     * Logs an execution success for the given task.
     *
     * @param task  the succeeded task, not null
     */
    void addSuccess(Task task);

    /**
     * Logs a warning for the given task.
     *
     * @param task  the task, not null
     */
    void addWarning(Task task);

    /**
     * Logs a warning for the given task.
     *
     * @param task  the task, not null
     * @param key  the key of the message
     * @param value  the name of the field that caused the warning
     */
    void addWarning(Task task, String key, String value);

    /**
     * Logs a warning for the given task.
     *
     * @param task  the task, not null
     * @param key  the key of the message
     * @param field  the name of the failed that caused the warning, not null
     * @param e  the exception that caused the warning, not null
     */
    void addWarning(Task task, String key, String field, Exception e);

    /**
     * Retrieves all activities for the given task, sorts them, then iterates backwards until it finds an activity
     * that's not an error or info about the task being disabled.
     *
     * @param task  the task, not null
     * @return the list of errors from the last run
     */
    List<TaskActivity> errorsFromLastRun(Task task);

    /**
     * Deletes all activities for the task with the given ID.
     *
     * @param taskId  the task ID, not null
     */
    void deleteActivitiesForTask(Long taskId);

    /**
     * Returns all activities as a list ordered by date.
     *
     * @return the list of all activities
     */
    List<TaskActivity> getAllActivities();

    /**
     * Returns 10 most recent activities as a list, ordered by date.
     *
     * @return the list of all activities
     */
    List<TaskActivity> getLatestActivities();

    /**
     * Returns list of all activities for task with the given ID.
     *
     * @param taskId  the task ID, null returns null
     * @return  the list of all activities for task with given ID
     */
    List<TaskActivity> getTaskActivities(Long taskId);

    /**
     * Returns the count of all activities for the given task, of the specified type.
     *
     * @param taskId the task ID
     * @param type the type of activity to include in count
     * @return the count of matching activities
     */
    long getTaskActivitiesCount(Long taskId, TaskActivityType type);
}
