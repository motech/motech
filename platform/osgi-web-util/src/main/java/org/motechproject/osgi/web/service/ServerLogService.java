package org.motechproject.osgi.web.service;

import org.motechproject.osgi.web.domain.LogMapping;

import java.util.List;

/**
 * Interface for accessing log4j Logger configuration.
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
     * @param level a new logging level for the root logger
     */
    void changeRootLogLevel(String level);

    /**
     * Returns details for all loggers. Root logger is NOT included.
     *
     * @return details for all loggers, except the root logger
     */
    List<LogMapping> getLogLevels();

    /**
     * Changes the logging level for one, specified logger.
     *
     * @param name name of the logger you wish to change the level for
     * @param level a new level for the logger
     */
    void changeLogLevel(String name, String level);

    /**
     * Removes the specified logger.
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
     * @return all loggers details, including the root logger
     */
    List<LogMapping> getAllLogMappings();
}

