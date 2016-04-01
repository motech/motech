package org.motechproject.tasks.service;

import org.motechproject.mds.query.QueryParams;
import org.motechproject.tasks.domain.mds.task.Task;
import org.motechproject.tasks.domain.mds.task.TaskActivity;
import org.motechproject.tasks.domain.mds.task.TaskActivityType;
import org.motechproject.tasks.exception.TaskHandlerException;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service for managing task activities. Task activities are used for storing information about past task executions.
 */
public interface TaskActivityService {

    /**
     * Logs an execution error for the given task.
     *
     * @param task  the failed task, not null
     * @param e  the cause of the error, not null
     * @param parameters the parameters used by the task when it failed
     */
    void addError(Task task, TaskHandlerException e, Map<String, Object> parameters);

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
     * Deletes all activities for the task with the given ID.
     *
     * @param taskId  the task ID, not null
     */
    void deleteActivitiesForTask(Long taskId);

    /**
     * Returns single TaskActivity with given activity ID.
     *
     * @param activityId the ID of activity instance to be retrieved
     * @return TaskActivity with the given ID
     */
    TaskActivity getTaskActivityById(Long activityId);

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
     * @param activityTypeSet the type of activities
     * @param queryParams query parameters to use while retrieving
     * @return  the list of all activities for task with given ID
     */
    List<TaskActivity> getTaskActivities(Long taskId, Set<TaskActivityType> activityTypeSet, QueryParams queryParams);

    /**
     * Returns the count of all activities for the given task, of the specified type.
     *
     * @param taskId the task ID
     * @param activityTypes the type of activities to include in count
     * @return the count of matching activities
     */
    long getTaskActivitiesCount(Long taskId, Set<TaskActivityType> activityTypes);

    /**
     * Returns the count of all activities for the given task, of the specified type.
     *
     * @param taskId the task ID
     * @param type the type of activity to include in count
     * @return the count of matching activities
     */
    long getTaskActivitiesCount(Long taskId, TaskActivityType type);
}
