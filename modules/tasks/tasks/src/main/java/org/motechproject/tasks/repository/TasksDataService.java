package org.motechproject.tasks.repository;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.tasks.domain.Task;

public interface TasksDataService extends MotechDataService<Task> {

    @Lookup
    Task findById(@LookupField(name = "id") Long id);
}
