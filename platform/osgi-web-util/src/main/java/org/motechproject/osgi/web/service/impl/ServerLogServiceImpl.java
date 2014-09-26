package org.motechproject.osgi.web.service.impl;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.motechproject.commons.api.CastUtils;
import org.motechproject.config.core.MotechConfigurationException;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.motechproject.osgi.web.domain.LogMapping;
import org.motechproject.osgi.web.service.ServerLogService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.apache.log4j.Level.toLevel;
import static org.apache.log4j.LogManager.getLogger;
import static org.apache.log4j.LogManager.getRootLogger;

/**
 * Default implementation of the ServerLogService Interface.
 */
@Service("serverLogService")
public final class ServerLogServiceImpl implements ServerLogService {

    private CoreConfigurationService coreConfigurationService;
    private Properties loggingProperties;

    private static final String LOG4J_PROPERTIES = "log4j.properties";
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ServerLogServiceImpl.class);

    @Autowired
    public ServerLogServiceImpl(CoreConfigurationService coreConfigurationService) {
        this.coreConfigurationService = coreConfigurationService;
    }

    @Override
    public void reconfigure() {
        LogMapping mapping = getRootLogLevel();

        if (mapping.getLogLevel() == null) {
            changeRootLogLevel(getRootLogger().getLevel().toString());
        } else {
            changeRootLogLevel(mapping.getLogLevel());
        }

        List<Logger> loggers = CastUtils.cast(Logger.class, LogManager.getCurrentLoggers());

        Properties storedProperties = getLoggingProperties();

        for (Logger logger : loggers) {
            String name = logger.getName();
            Level level = logger.getLevel();
            mapping = new LogMapping(name, storedProperties.getProperty(name));

            if (level != null) {
                if (null != mapping.getLogLevel()) {
                    changeLogLevel(mapping.getLogName(), mapping.getLogLevel());
                } else {
                    changeLogLevel(name, level.toString());
                }
            } else {
                if (null != mapping.getLogLevel()) {
                    removeLogger(name);
                }
            }
        }
    }

    @Override
    public List<LogMapping> getAllLogMappings() {
        return propertiesToLogMapping(getLoggingProperties());
    }

    @Override
    public LogMapping getRootLogLevel() {
        Properties properties = getLoggingProperties();
        String level = (String) properties.get(ROOT_LOGGER_NAME);

        return new LogMapping(ROOT_LOGGER_NAME, level);
    }

    @Override
    public void changeRootLogLevel(String level) {
        String upperCase = level.toUpperCase();

        getRootLogger().setLevel(toLevel(upperCase));
        Properties properties = getLoggingProperties();
        properties.setProperty(ROOT_LOGGER_NAME, upperCase);
        savePropertiesToFile(properties);
        loggingProperties = properties;
    }

    @Override
    public List<LogMapping> getLogLevels() {
        List<LogMapping> list = getAllLogMappings();
        list.remove(getRootLogLevel());

        return list;
    }

    @Override
    public void changeLogLevel(String name, String level) {
        String upperCase = level.toUpperCase();

        Logger logger = getLogger(name);
        logger.setLevel(toLevel(upperCase));

        Properties properties = getLoggingProperties();
        properties.setProperty(name, upperCase);

        savePropertiesToFile(properties);
        loggingProperties = properties;
    }

    @Override
    public void removeLogger(String name) {
        Logger logger = getLogger(name);

        logger.setLevel(null);

        Properties properties = getLoggingProperties();
        properties.remove(name);
        savePropertiesToFile(properties);
        loggingProperties = properties;
    }

    private Properties getLoggingProperties() {
        if (loggingProperties != null) {
            return loggingProperties;
        }

        String filename = getLogPropertiesFilename();

        loggingProperties = new Properties();
        FileInputStream inputStream = null;

        try {
            File log4jFile = new File(filename);
            if (log4jFile.exists()) {
                inputStream = new FileInputStream(log4jFile);
                loggingProperties.load(inputStream);
            } else {
                LOGGER.warn("{} does not exist, cannot read log4j configuration", filename);
            }
        } catch (IOException e) {
            LOGGER.warn("Failed to load properties from file " +  filename, e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return loggingProperties;
    }

    private void savePropertiesToFile(Properties properties) {
        String filename = getLogPropertiesFilename();
        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(filename);
            properties.store(outputStream, "");
        } catch (FileNotFoundException e) {
            LOGGER.warn("Failed to save properties. File " +  filename + " does not exist.", e);
        } catch (IOException e) {
            LOGGER.warn("Failed to save properties to file " + filename, e);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }

    private String getLogPropertiesFilename() {
        return new File(getConfigDir(), LOG4J_PROPERTIES).getAbsolutePath();
    }

    private String getConfigDir() {
        String dir = new File(System.getProperty("user.home"), ".motech/config").getAbsolutePath();
        if (coreConfigurationService != null) {
            try {
                dir = coreConfigurationService.getConfigLocation().getLocation();
            } catch (MotechConfigurationException e) {
                LOGGER.info("No config location, using {} for log4j file", dir);
            }
        }
        return dir;
    }

    private List<LogMapping> propertiesToLogMapping(Properties properties) {
        List<LogMapping> logMappings = new ArrayList<>();
        for (Map.Entry entry : properties.entrySet()) {
            logMappings.add(new LogMapping(entry.getKey().toString(), entry.getValue().toString()));
        }

        return logMappings;
    }
}
