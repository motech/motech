package org.motechproject.admin.service;

import org.motechproject.admin.domain.LogMapping;

import java.util.List;

public interface ServerLogService {
    String ROOT_LOGGER_NAME = "root";
    String CURRENT_LOGGERS_NAME = "current";

    LogMapping getRootLogLevel();

    void changeRootLogLevel(String level);

    List<LogMapping> getLogLevels();

    void changeLogLevel(String name, String level);

    void removeLogger(String name);

}
