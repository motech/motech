package org.motechproject.commons.sql.service;

import java.io.IOException;
import java.util.Properties;

/**
 * Classes implementing this interface are responsible for retrieving sql properties
 * from the bootstrap configuration and updating sql-related properties for modules.
 */
public interface SqlDBManager {

    /**
     * Being passed raw properties, inserts correct SQL configuration in the correct places.
     * Correct replacement codes are:
     * <ul>
     *     <li>${sql.driver}</li>
     *     <li>${sql.user}</li>
     *     <li>${sql.password}</li>
     *     <li>${sql.url}</li>
     * </ul>
     *
     * As a result of this method, all occurrences of keys above, get replaced with
     * actual sql properties, retrieved from the provided bootstrap configuration.
     *
     * @param propertiesToUpdate Raw properties, containing replacement codes
     * @return Actual properties, with sql configuration from bootstrap
     * @throws IOException In case an I/O exception occurs when loading / merging properties
     */
    Properties getSqlProperties(Properties propertiesToUpdate) throws IOException;

    /**
     * Allows to order SqlDBManager to read bootstrap configuration once again and update
     * the actual sql properties that are kept by the manager. In regular use scenario, it
     * shouldn't be required to call this method. Use it only, when there's a chance that
     * the bootstrap configuration gets changes during application runtime.
     */
    void updateSqlProperties();

    /**
     * Returns the SQL driver class name that has been chosen during bootstrap configuration
     * of the system.
     *
     * @return Class name of the SQL driver, chosen during bootstrap configuration.
     */
    String getChosenSQLDriver();
}
