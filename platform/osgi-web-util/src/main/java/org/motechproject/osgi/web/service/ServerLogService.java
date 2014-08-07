package org.motechproject.osgi.web.service;

import org.motechproject.osgi.web.domain.LogMapping;

import java.util.List;

/**
 * Interface for accessing Logger's configuration from the saved properties
 */
public interface ServerLogService {
    String ROOT_LOGGER_NAME = "root";

    /**
     * Returns the logger details for the root logger
     *
     * @return root logger details
     */
    LogMapping getRootLogLevel();

    /**
     * Changes logging level for the root logger
     *
     * @param level a new logging level for root logger
     */
    void changeRootLogLevel(String level);

    /**
     * Returns details for all loggers. Root logger is NOT included.
     *
     * @return all loggers details
     */
    List<LogMapping> getLogLevels();

    /**
     * Changes the logging level for one, specified logger.
     *
     * @param name name of the logger you wish to change level for
     * @param level a new level for the logger
     */
    void changeLogLevel(String name, String level);

    /**
     * Removes specified logger.
     *
     * @param name name of the logger you wish to remove
     */
    void removeLogger(String name);

    /**
     * Sets the logger properties from scratch.
     */
    void reconfigure();

    /**
     * Returns details for all loggers. Root logger included.
     *
     * @return all loggers details
     */
    List<LogMapping> getAllLogMappings();
}

