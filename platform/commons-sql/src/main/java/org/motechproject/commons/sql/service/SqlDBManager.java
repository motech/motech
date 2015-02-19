package org.motechproject.commons.sql.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Classes implementing this interface are responsible for retrieving sql properties
 * from the bootstrap configuration, updating sql-related properties for modules and
 * creating databases for given properties.
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

    /**
     * Create database with given name
     *
     * @return true if database was created properly
     */
    boolean createDatabase(String dbName);

    /**
     * Check if database with given name exists
     *
     * @param dbName database name
     * @return true if database exists, otherwise false
     */
    boolean checkForDatabase(String dbName);

    /**
     * Checks whether table with given name has column with given name.
     *
     * @param table  the name of the table
     * @param column the name of the column
     * @return true if table has column, false otherwise
     * @throws SQLException  when incorrect data was given
     */
    boolean hasColumn(String database, String table, String column) throws SQLException;
}
