package org.motechproject.tasks.repository;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.tasks.domain.Task;

import java.util.List;

public interface TasksDataService extends MotechDataService<Task> {

    @Lookup
    List<Task> findTasksByName(@LookupField(name = "name") String name);

}
