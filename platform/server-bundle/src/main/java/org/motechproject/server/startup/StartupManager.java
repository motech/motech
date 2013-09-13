package org.motechproject.server.startup;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.joda.time.DateTime;
import org.motechproject.commons.couchdb.service.CouchDbManager;
import org.motechproject.server.config.ConfigLoader;
import org.motechproject.server.config.domain.SettingsRecord;
import org.motechproject.server.config.repository.AllSettings;
import org.motechproject.server.config.domain.ConfigFileSettings;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import java.util.Arrays;
import java.util.Map;


public final class StartupManager {
    private static final String SETTINGS_DB = "motech-platform-startup";
    private static final String STARTUP_TOPIC = "org/motechproject/osgi/event/STARTUP";
    private static final Logger LOGGER = LoggerFactory.getLogger(StartupManager.class);
    private static StartupManager instance;
    private MotechPlatformState platformState = MotechPlatformState.STARTUP;
    private ConfigFileSettings configFileSettings;
    @Autowired
    private ConfigLoader configLoader;
    @Autowired
    private CouchDbManager couchDbManager;
    @Autowired
    private EventAdmin eventAdmin;

    private StartupManager() {

    }

    public static StartupManager getInstance() {
        if (instance == null) {
            instance = new StartupManager();
        }

        return instance;
    }

    public boolean isConfigRequired() {
        return platformState == MotechPlatformState.NEED_CONFIG;
    }

    @PostConstruct
    public void startup() {
        if (configFileSettings != null) {
            configFileSettings = null;
        }

        configFileSettings = configLoader.loadConfig();

        // check if settings were loaded from config locations
        if (configFileSettings == null) {
            platformState = MotechPlatformState.NEED_CONFIG;
            configFileSettings = configLoader.loadDefaultConfig();
        } else {
            LOGGER.info("Loaded config from " + configFileSettings.getFileURL());
            platformState = MotechPlatformState.STARTUP;
        }

        if (platformState != MotechPlatformState.NEED_CONFIG) {
            syncSettingsWithDb();
        }

        if (canLaunchBundles()) {
            // send an OSGI event indicating that the modules can be started
            eventAdmin.postEvent(new Event(STARTUP_TOPIC, (Map) null));
        }
    }

    public boolean canLaunchBundles() {
        return platformState == MotechPlatformState.FIRST_RUN || platformState == MotechPlatformState.NORMAL_RUN;
    }

    public ConfigFileSettings getLoadedConfig() {
        return configFileSettings;
    }

    public boolean findActiveMQInstance(final String url) {
        Connection connection = null;
        boolean found = false;

        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(url);
            connection = factory.createConnection();
            connection.start();
        } catch (JMSException e) {
            found = false;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                    found = true;
                } catch (JMSException e) {
                    found = false;
                }
            }
        }

        return found;
    }

    public boolean findSchedulerInstance(final String url) {
        return false;
    }

    private void syncSettingsWithDb() {
        try {
            AllSettings allSettings = new AllSettings(couchDbManager.getConnector(SETTINGS_DB));
            SettingsRecord dbSettings = allSettings.getSettings();

            if (dbSettings.getLastRun() == null) {
                platformState = MotechPlatformState.FIRST_RUN;
            } else {
                platformState = MotechPlatformState.NORMAL_RUN;
            }

            if (platformState == MotechPlatformState.FIRST_RUN ||
                    !Arrays.equals(configFileSettings.getMd5checkSum(), dbSettings.getConfigFileChecksum())) {
                LOGGER.info("Updating database startup");
                dbSettings.updateSettings(configFileSettings);
            }

            dbSettings.setLastRun(DateTime.now());
            dbSettings.setConfigFileChecksum(configFileSettings.getMd5checkSum());

            allSettings.addOrUpdateSettings(dbSettings);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            platformState = MotechPlatformState.DB_ERROR;
        }
    }

}
