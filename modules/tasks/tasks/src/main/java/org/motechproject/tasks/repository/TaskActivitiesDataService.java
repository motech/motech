package org.motechproject.tasks.repository;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.tasks.domain.TaskActivity;
import org.motechproject.tasks.domain.TaskActivityType;

import java.util.List;

/**
 * Data service for task activities.
 */
public interface TaskActivitiesDataService extends MotechDataService<TaskActivity> {

    /**
     * Returns the list of activities for the given task id.
     *
     * @param task  the id of the task, null returns empty list
     * @return the list of matching task activities
     */
    @Lookup(name = "By Task")
    List<TaskActivity> byTask(@LookupField(name = "task") final Long task);

    /**
     * Returns the list of activities for the given task id and of specified type.
     *
     * @param task the id of the task
     * @param activityType the type of activity
     * @return the list of matching task activities
     */
    @Lookup(name = "By Task and Activity Type")
    List<TaskActivity> byTaskAndActivityType(@LookupField(name = "task") final Long task,
                                             @LookupField(name = "activityType") final TaskActivityType activityType);

    /**
     * Returns the count of activities for the given task id and of specified type.
     *
     * @param task the id of the task
     * @param activityType the type of activity
     * @return the count of matching task activities
     */
    long countByTaskAndActivityType(@LookupField(name = "task") final Long task,
                                    @LookupField(name = "activityType") final TaskActivityType activityType);
}
