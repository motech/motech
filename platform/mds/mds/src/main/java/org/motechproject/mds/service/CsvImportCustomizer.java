package org.motechproject.mds.service;

import java.util.Map;

/**
 * The <code>CsvImportCustomizer</code> interface allows to provide custom methods for finding, creating and updating
 * an instance during csv import.
 *
 * @see org.motechproject.mds.domain.Entity
 */
public interface CsvImportCustomizer {

    /**
     * Retrieves an instance based on the fields imported from csv
     *
     * @param row the imported row containing fields of an instance
     * @param dataService the data service of an entity
     *
     * @return single instance or null if none is found
     */
    Object findExistingInstance(Map<String, String> row, MotechDataService dataService);

    /**
     * Creates an instance using given dataService
     *
     * @param instance the instance to create
     * @param dataService the data service of an entity
     *
     * @return the created instance
     */
    Object doCreate(Object instance, MotechDataService dataService);

    /**
     * Updates an instance using given dataService
     *
     * @param instance the instance to update
     * @param dataService the data service of an entity
     *
     * @return the updated instance
     */
    Object doUpdate(Object instance, MotechDataService dataService);

}
