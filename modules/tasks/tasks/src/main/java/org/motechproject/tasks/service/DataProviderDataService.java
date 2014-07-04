package org.motechproject.tasks.service;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.tasks.domain.TaskDataProvider;

public interface DataProviderDataService extends MotechDataService<TaskDataProvider> {

    @Lookup(name = "By data provider name")
    TaskDataProvider findByName(@LookupField(name = "name") String name);
}
