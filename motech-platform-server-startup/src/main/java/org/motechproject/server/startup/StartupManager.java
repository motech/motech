package org.motechproject.server.startup;

import org.joda.time.DateTime;
import org.motechproject.server.config.ConfigLoader;
import org.motechproject.server.config.db.CouchDbManager;
import org.motechproject.server.config.db.DbConnectionException;
import org.motechproject.server.config.domain.SettingsRecord;
import org.motechproject.server.config.service.AllSettings;
import org.motechproject.server.config.settings.ConfigFileSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

public final class StartupManager {
    private static StartupManager instance;

    private static final String SETTINGS_DB = "motech-platform-startup";

    private static final Logger LOGGER = LoggerFactory.getLogger(StartupManager.class);

    private MotechPlatformState platformState = MotechPlatformState.STARTUP;
    private ConfigFileSettings configFileSettings;

    @Autowired
    private CouchDbManager couchDbManager;

    @Autowired
    private ConfigLoader configLoader;

    private StartupManager() {

    }

    public static StartupManager getInstance() {
        if (instance == null) {
            instance = new StartupManager();
        }

        return instance;
    }

    public MotechPlatformState getPlatformState() {
        return platformState;
    }

    public void startup() {
        configFileSettings = configLoader.loadConfig();

        // check if settings were loaded from config locations
        if (configFileSettings == null) {
            platformState = MotechPlatformState.NEED_CONFIG;
        } else {
            LOGGER.info("Loaded config from " + configFileSettings.getFileURL());
            platformState = MotechPlatformState.STARTUP;
        }

        if (platformState != MotechPlatformState.NEED_CONFIG) {
            syncSettingsWithDb();
        }
    }

    public boolean canLaunchBundles() {
        return platformState == MotechPlatformState.FIRST_RUN || platformState == MotechPlatformState.NORMAL_RUN;
    }

    private void syncSettingsWithDb() {
        // test Database
        try {
            couchDbManager.configureDb(configFileSettings.getCouchDBProperties());
        } catch (DbConnectionException e) {
            LOGGER.error(e.getMessage(), e);
            platformState = MotechPlatformState.NO_DB;
        }

        // load db settings
        if (platformState != MotechPlatformState.NO_DB) {
            try {
                AllSettings allSettings = new AllSettings(couchDbManager.getConnector(SETTINGS_DB, true));
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

}
