package org.motechproject.tasks.repository;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.tasks.domain.mds.task.Task;

import java.util.List;

/**
 * Data service for tasks.
 */
public interface TasksDataService extends MotechDataService<Task> {

    /**
     * Returns the list of tasks with the given name.
     *
     * @param name  the task name, null returns empty list
     * @return the list of matching tasks
     */
    @Lookup
    List<Task> findTasksByName(@LookupField(name = "name") String name);

}
