package org.motechproject.osgi.web.service.impl;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.motechproject.commons.api.CastUtils;
import org.motechproject.osgi.web.domain.LogMapping;
import org.motechproject.osgi.web.repository.AllLogMappings;
import org.motechproject.osgi.web.service.ServerLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.apache.log4j.Level.toLevel;
import static org.apache.log4j.LogManager.getLogger;
import static org.apache.log4j.LogManager.getRootLogger;

/**
 * Implementation of the ServerLogService Interface.
 */

@Service("serverLogService")
public final class ServerLogServiceImpl implements ServerLogService {

    private AllLogMappings allLogMappings;

    @Autowired
    public ServerLogServiceImpl(AllLogMappings allLogMappings) {
        this.allLogMappings = allLogMappings;
    }

    @Override
    public void reconfigure() {
        LogMapping mapping = getRootLogLevel();

        if (mapping == null) {
            changeRootLogLevel(getRootLogger().getLevel().toString());
        } else {
            changeRootLogLevel(mapping.getLogLevel());
        }

        List<Logger> loggers = CastUtils.cast(Logger.class, LogManager.getCurrentLoggers());
        List<LogMapping> db = allLogMappings.getAll();

        for (Logger logger : loggers) {
            String name = logger.getName();
            Level level = logger.getLevel();
            mapping = allLogMappings.byLogName(name);
            boolean exists = db.contains(mapping);

            if (level != null) {
                if (exists) {
                    changeLogLevel(mapping.getLogName(), mapping.getLogLevel());
                } else {
                    changeLogLevel(name, level.toString());
                }
            } else {
                if (exists) {
                    removeLogger(name);
                }
            }
        }
    }

    @Override
    public LogMapping getRootLogLevel() {
        return allLogMappings.byLogName(ROOT_LOGGER_NAME);
    }

    @Override
    public void changeRootLogLevel(String level) {
        String upperCase = level.toUpperCase();

        getRootLogger().setLevel(toLevel(upperCase));
        allLogMappings.addOrUpdate(new LogMapping(ROOT_LOGGER_NAME, upperCase));
    }

    @Override
    public List<LogMapping> getLogLevels() {
        List<LogMapping> list = allLogMappings.getAll();
        list.remove(getRootLogLevel());

        return list;
    }

    @Override
    public void changeLogLevel(String name, String level) {
        String upperCase = level.toUpperCase();
        Logger logger = getLogger(name);

        logger.setLevel(toLevel(upperCase));
        allLogMappings.addOrUpdate(new LogMapping(name, upperCase));
    }

    @Override
    public void removeLogger(String name) {
        Logger logger = getLogger(name);

        logger.setLevel(null);
        allLogMappings.removeByLogName(name);
    }
}
