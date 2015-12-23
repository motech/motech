package org.motechproject.commons.sql.service;

import org.motechproject.commons.sql.util.JdbcUrl;

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
     * Current replacement codes are:
     * <ul>
     *     <li>${sql.driver}</li>
     *     <li>${sql.user}</li>
     *     <li>${sql.password}</li>
     *     <li>${sql.url}</li>
     *     <li>${sql.quartz.delegateClass}</li>
     * </ul>
     *
     * As a result of calling this method, all occurrences of the above keys get replaced with
     * actual sql properties, retrieved from the provided bootstrap configuration.
     *
     * @param propertiesToUpdate Raw properties, containing replacement codes
     * @return Actual properties, with sql configuration from bootstrap
     * @throws IOException In case an I/O exception occurs when loading / merging properties
     */
    Properties getSqlProperties(Properties propertiesToUpdate) throws IOException;

    /**
     * Returns the SQL driver class name that has been chosen during bootstrap configuration
     * of the system.
     *
     * @return Class name of the SQL driver, chosen during bootstrap configuration.
     */
    String getChosenSQLDriver();

    /**
     * Create a database with the given name
     *
     * @return true if the database was created properly
     */
    boolean createDatabase(String dbName);

    /**
     * Check if a database with the given name exists
     *
     * @param dbName database name
     * @return true if database exists, otherwise false
     */
    boolean checkForDatabase(String dbName);

    /**
     * Checks whether table with the given name has a column with the given name.
     *
     * @param table  the name of the table
     * @param column the name of the column
     * @return true if the table has that column, false otherwise
     * @throws SQLException  when incorrect data was given
     */
    boolean hasColumn(String database, String table, String column) throws SQLException;

    /**
     * Builds jdbc URL from the given connection URL.
     *
     * @param connectionUrl the database connection URL
     * @return the jdbc URL from the given connection URL
     */
    JdbcUrl prepareConnectionUri(String connectionUrl);
}
