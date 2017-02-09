package org.motechproject.tasks.service;

import org.joda.time.DateTime;
import org.motechproject.commons.api.Range;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.tasks.domain.mds.task.Task;
import org.motechproject.tasks.domain.mds.task.TaskActivity;
import org.motechproject.tasks.domain.enums.TaskActivityType;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service for managing task activities. Task activities are used for storing information about current and past task executions.
 */
public interface TaskActivityService {

    /**
     * Adds a new task activity log and marks it as "In Progress".
     *
     * @param task The task to add the log for
     * @param parameters The event trigger parameters the task was initiated with
     * @return id of the created activity log
     */
    long addTaskStarted(Task task, Map<String, Object> parameters);

    /**
     * Marks the activity as "FILTERED", if task was filtered and not executed.
     *
     * @param activityId the id of the activity
     */
    void addTaskFiltered(Long activityId);

    /**
     * Adds successful execution to the activity of the provided id. If all the task actions are executed, it marks
     * the activity as "SUCCESS".
     *
     * @param activityId the id of the activity
     * @return whether the task is finished
     */
    boolean addSuccessfulExecution(Long activityId);

    /**
     * Adds filtered execution to the activity of the provided id.
     *
     * @param activityId the id of the activity
     */
    void addFilteredExecution(Long activityId);

    /**
     * Adds failed execution to the activity of the provided id, which in consequence marks it as "FAILED".
     *
     * @param activityId the id of the activity
     * @param e the throwable that has caused the task execution failure
     */
    void addFailedExecution(Long activityId, Throwable e);

    /**
     * Logs a warning for the given task.
     *
     * @param task  the task, not null
     */
    void addTaskDisabledWarning(Task task);

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
    void addWarningWithException(Task task, String key, String field, Exception e);

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
     * Returns all the activities as a list.
     *
     * @param activityTypeSet the type of activities
     * @param queryParams query parameters to use while retrieving
     * @param lastExecution
     * @return the list of all activities
     */
    List<TaskActivity> getAllActivities(Set<TaskActivityType> activityTypeSet, QueryParams queryParams, boolean lastExecution);

    /**
     * Returns all the activities as a list.
     *
     * @param activityTypes the type of activities
     * @param dateRange the date range
     * @param queryParams query parameters to use while retrieving
     * @param lastExecution
     * @return the list of all activities
     */
    List<TaskActivity> getAllActivities(Set<TaskActivityType> activityTypes, Range<DateTime> dateRange,
                                               QueryParams queryParams, boolean lastExecution);
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
     * Returns list of all activities for task with the given ID, of specific activity type and the given date range.
     *
     * @param taskId  the task ID, null returns null
     * @param activityTypeSet the type of activities
     * @param dateRange the range of datetime
     * @param queryParams query parameters to use while retrieving
     * @return  the list of all activities for task with given ID
     */
    List<TaskActivity> getTaskActivities(Long taskId, Set<TaskActivityType> activityTypeSet, Range<DateTime> dateRange,
                                         QueryParams queryParams);

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

    /**
     * Returns the count of all activities for the given task, of the specified type and the given date range.
     *
     * @param taskId the task ID
     * @param activityTypes the type of activities to include in count
     * @param dateRange the range of datetime
     * @return the count of matching activities
     */
    long getTaskActivitiesCount(Long taskId, Set<TaskActivityType> activityTypes, Range<DateTime> dateRange);

    /**
     * Returns the count of all activities.
     *
     * @param activityTypes the type of activities to include in count
     * @param lastExecution
     * @return the count of matching activities
     */
    long getAllTaskActivitiesCount(Set<TaskActivityType> activityTypes, boolean lastExecution);

    /**
     * Returns the count of all activities.
     *
     * @param activityTypes the type of activities to include in count
     * @param dateRange the date range
     * @param lastExecution
     * @return the count of matching activities
     */
    long getAllTaskActivitiesCount(Set<TaskActivityType> activityTypes, Range<DateTime> dateRange, boolean lastExecution);
}
