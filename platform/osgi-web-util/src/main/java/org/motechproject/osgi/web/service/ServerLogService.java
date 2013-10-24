package org.motechproject.osgi.web.service;

import org.motechproject.osgi.web.domain.LogMapping;

import java.util.List;

/**
 * Interface for accessing Logger's configuration from the database
 */
public interface ServerLogService {
    String ROOT_LOGGER_NAME = "root";
    String CURRENT_LOGGERS_NAME = "current";

    LogMapping getRootLogLevel();

    void changeRootLogLevel(String level);

    List<LogMapping> getLogLevels();

    void changeLogLevel(String name, String level);

    void removeLogger(String name);

    void reconfigure();

}

