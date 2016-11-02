package org.motechproject.tasks.repository;

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

    /**
     * Returns the list of activities for the given task id.
     *
     * @param task  the id of the task, null returns empty list
     * @return the list of matching task activities
     */
    @Lookup(name = "By Task")
    List<TaskActivity> byTask(@LookupField(name = TASK) final Long task);

    /**
     * Returns the list of activities for the given task id, of specified type and with QueryParams for
     * pagination support.
     *
     * @param task the id of the task
     * @param activityTypes the set of activity types
     * @param queryParams the query parameters to use
     * @return the list of matching task activities
     */
    @Lookup(name = "By Task and Activity Types")
    List<TaskActivity> byTaskAndActivityTypes(@LookupField(name = TASK) final Long task,
                                              @LookupField(name = ACTIVITY_TYPE) final Set<TaskActivityType> activityTypes,
                                              QueryParams queryParams);

    /**
     * Returns the count of activities for the given task id and of specified type.
     *
     * @param task the id of the task
     * @param activityTypes the set of activity types
     * @return the count of matching task activities
     */
    long countByTaskAndActivityTypes(@LookupField(name = TASK) final Long task,
                                     @LookupField(name = ACTIVITY_TYPE) final Set<TaskActivityType> activityTypes);

}
