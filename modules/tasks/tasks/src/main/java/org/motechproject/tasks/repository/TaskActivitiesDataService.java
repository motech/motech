package org.motechproject.tasks.repository;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.tasks.domain.TaskActivity;

import java.util.List;

/**
 * Data service for task activities.
 */
public interface TaskActivitiesDataService extends MotechDataService<TaskActivity> {

    /**
     * Returns the list of activities for the given task name.
     *
     * @param task  the name of the task, null returns empty list
     * @return the list of matching task activities
     */
    @Lookup(name = "By Task")
    List<TaskActivity> byTask(@LookupField(name = "task") final Long task);
}
