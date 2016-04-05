package org.motechproject.tasks.repository;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.tasks.domain.mds.task.TaskDataProvider;

/**
 * Data Service for data providers.
 */
public interface DataProviderDataService extends MotechDataService<TaskDataProvider> {

    /**
     * Returns the data provider with the given name.
     *
     * @param name  the name of the data provider
     * @return  the provider with the given name
     */
    @Lookup(name = "By data provider name")
    TaskDataProvider findByName(@LookupField(name = "name") String name);
}
