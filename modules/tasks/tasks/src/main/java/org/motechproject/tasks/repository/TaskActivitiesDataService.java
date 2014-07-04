package org.motechproject.tasks.repository;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.tasks.domain.TaskActivity;

import java.util.List;

public interface TaskActivitiesDataService extends MotechDataService<TaskActivity> {

    @Lookup(name = "By Task")
    List<TaskActivity> byTask(@LookupField(name = "task") final Long task);
}
