package org.motechproject.server.startup;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.joda.time.DateTime;
import org.motechproject.commons.couchdb.service.CouchDbManager;
import org.motechproject.config.domain.BootstrapConfig;
import org.motechproject.config.domain.ConfigSource;
import org.motechproject.server.config.service.ConfigLoader;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.server.config.domain.SettingsRecord;
import org.motechproject.server.config.repository.AllSettings;
import org.motechproject.server.config.domain.ConfigFileSettings;
import org.motechproject.server.config.settings.MotechSettings;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

/**
 * StartupManager controlling and managing the application loading
 */
public final class StartupManager {
    private static final String SETTINGS_DB = "motech-platform-startup";
    private static final String STARTUP_TOPIC = "org/motechproject/osgi/event/STARTUP";
    private static final Logger LOGGER = LoggerFactory.getLogger(StartupManager.class);
    private static StartupManager instance;
    private MotechPlatformState platformState = MotechPlatformState.STARTUP;
    private ConfigFileSettings configFileSettings;
    private AllSettings allSettings;
    private SettingsRecord dbSettings;

    @Autowired
    private ConfigLoader configLoader;

    @Autowired
    private CouchDbManager couchDbManager;

    @Autowired
    private EventAdmin eventAdmin;

    @Autowired
    private ConfigurationService configurationService;

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
        configFileSettings = null;
        allSettings = new AllSettings(couchDbManager.getConnector(SETTINGS_DB));
        dbSettings = allSettings.getSettings();

        if(!dbSettings.isPlatformInitialized()) {
            BootstrapConfig bootstrapConfig = configurationService.loadBootstrapConfig();
            if(ConfigSource.FILE.equals(bootstrapConfig.getConfigSource())) {
                configFileSettings = configLoader.loadConfig();
            }

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
        } else {
            configFileSettings = convertSettingsRecordToConfigFileSettings();
            syncSettingsWithDb();
        }

        if (canLaunchBundles()) {
            // send an OSGI event indicating that the modules can be started
            eventAdmin.postEvent(new Event(STARTUP_TOPIC, (Map) null));
        }
    }

    private ConfigFileSettings convertSettingsRecordToConfigFileSettings() {
        ConfigFileSettings settings = new ConfigFileSettings();
        for (String key : dbSettings.getActivemqProperties().stringPropertyNames()) {
            settings.saveActiveMqSetting(key, dbSettings.getActivemqProperties().getProperty(key));
        }
        settings.saveMotechSetting(MotechSettings.LANGUAGE, dbSettings.getLanguage());
        settings.saveMotechSetting(MotechSettings.LOGINMODE, dbSettings.getLoginMode());
        settings.saveMotechSetting(MotechSettings.PROVIDER_NAME, dbSettings.getProviderName());
        settings.saveMotechSetting(MotechSettings.PROVIDER_URL, dbSettings.getProviderUrl());
        settings.saveMotechSetting(MotechSettings.SERVER_URL, dbSettings.getServerUrl());
        settings.saveMotechSetting(MotechSettings.STATUS_MSG_TIMEOUT, dbSettings.getStatusMsgTimeout());
        settings.saveMotechSetting(MotechSettings.UPLOAD_SIZE, dbSettings.getUploadSize());

        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");

            settings.setMd5checksum(digest.digest(settings.toString().getBytes()));
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("MD5 algorithm not available");
        }

        return settings;
    }

    public boolean canLaunchBundles() {
        return platformState == MotechPlatformState.FIRST_RUN || platformState == MotechPlatformState.NORMAL_RUN;
    }

    /**
     * This function is only called when the default configuration is loaded
     * and is no config in the database or external files
     */
    public ConfigFileSettings getDefaultSettings() {
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
            dbSettings.setPlatformInitialized(true);

            allSettings.addOrUpdateSettings(dbSettings);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            platformState = MotechPlatformState.DB_ERROR;
        }
    }

}
