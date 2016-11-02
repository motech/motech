package org.motechproject.tasks.repository;

import org.joda.time.DateTime;
import org.motechproject.commons.api.Range;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.tasks.domain.mds.task.TaskActivity;
import org.motechproject.tasks.domain.enums.TaskActivityType;

import java.util.List;
import java.util.Set;

/**
 * Data service for task activities.
 */
public interface TaskActivitiesDataService extends MotechDataService<TaskActivity> {

    String TASK = "task";
    String ACTIVITY_TYPE = "activityType";
    String DATE = "date";

    /**
     * Returns the list of activities for the given task id.
     *
     * @param task the id of the task, null returns empty list
     * @return the list of matching task activities
     */
    @Lookup(name = "By Task")
    List<TaskActivity> byTask(@LookupField(name = TASK) final Long task);

    /**
     * Returns the list of activities for the given task id, of specified type and with QueryParams for
     * pagination support.
     *
     * @param task          the id of the task
     * @param activityTypes the set of activity types
     * @param queryParams   the query parameters to use
     * @return the list of matching task activities
     */
    @Lookup(name = "By Task and Activity Types")
    List<TaskActivity> byTaskAndActivityTypes(@LookupField(name = TASK) final Long task,
                                              @LookupField(name = ACTIVITY_TYPE) final Set<TaskActivityType> activityTypes,
                                              QueryParams queryParams);

    /**
     * Returns the count of activities for the given task id and of specified type.
     *
     * @param task          the id of the task
     * @param activityTypes the set of activity types
     * @return the count of matching task activities
     */
    long countByTaskAndActivityTypes(@LookupField(name = TASK) final Long task,
                                     @LookupField(name = ACTIVITY_TYPE) final Set<TaskActivityType> activityTypes);

    /**
     * Returns the list of activities for the given task id, of specified type, the given date range and with QueryParams for
     * pagination support.
     *
     * @param task          the id of the task
     * @param activityTypes the set of activity types
     * @param dateRange     the range of date
     * @param queryParams   the query parameters to use
     * @return the list of matching task activities
     */
    @Lookup(name = "By Task and Activity Types And Date")
    List<TaskActivity> byTaskAndActivityTypesAndDate(@LookupField(name = TASK) final Long task,
                                              @LookupField(name = ACTIVITY_TYPE) final Set<TaskActivityType> activityTypes,
                                              @LookupField(name = DATE) final Range<DateTime> dateRange, QueryParams queryParams);

    /**
     * Returns the count of activities for the given task id, of specified type and the given date range.
     *
     * @param task          the id of the task
     * @param activityTypes the set of activity types
     * @param dateRange     the range of date
     * @return the count of matching task activities
     */
    long countByTaskAndActivityTypesAndDate(@LookupField(name = TASK) final Long task,
                                     @LookupField(name = ACTIVITY_TYPE) final Set<TaskActivityType> activityTypes,
                                     @LookupField(name = DATE) final Range<DateTime> dateRange);

    /**
     * Returns the list of activities of specified type and with QueryParams for
     * pagination support.
     *
     * @param activityTypes the set of activity types
     * @param queryParams   the query parameters to use
     * @return the list of matching task activities
     */
    @Lookup(name = "By Activity Types")
    List<TaskActivity> byActivityTypes(@LookupField(name = ACTIVITY_TYPE) final Set<TaskActivityType> activityTypes,
                                              QueryParams queryParams);

    /**
     * Returns the count of all the task activities
     *
     * @return the count of all task activities
     */
    long countByActivityTypes(@LookupField(name = ACTIVITY_TYPE) final Set<TaskActivityType> activityTypes);

    /**
     * Returns the list of activities of specified type and with QueryParams for
     * pagination support.
     *
     * @param activityTypes the set of activity types
     * @param dateRange     the range of date
     * @param queryParams   the query parameters to use
     * @return the list of matching task activities
     */
    @Lookup(name = "By Activity Types and Date range")
    List<TaskActivity> byActivityTypesAndDate(@LookupField(name = ACTIVITY_TYPE) final Set<TaskActivityType> activityTypes,
                                              @LookupField(name = DATE) final Range<DateTime> dateRange, QueryParams queryParams);

    /**
     * Returns the count of all the task activities based on the given activity types and date range
     *
     * @return the count of task activities
     */
    long countByActivityTypesAndDate(@LookupField(name = ACTIVITY_TYPE) final Set<TaskActivityType> activityTypes,
                                     @LookupField(name = DATE) final Range<DateTime> dateRange);
}
