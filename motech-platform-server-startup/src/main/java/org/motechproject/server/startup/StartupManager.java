package org.motechproject.server.startup;

import org.ektorp.CouchDbConnector;
import org.joda.time.DateTime;
import org.motechproject.server.config.ConfigLoader;
import org.motechproject.server.config.db.CouchDbManager;
import org.motechproject.server.config.db.DbConnectionException;
import org.motechproject.server.config.domain.SettingsRecord;
import org.motechproject.server.config.service.AllSettings;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.settings.ConfigFileSettings;
import org.motechproject.server.config.settings.MotechSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

public final class StartupManager {
    private static StartupManager instance;

    private static final String SETTINGS_DB = "motech-platform-startup";

    private static final Logger LOGGER = LoggerFactory.getLogger(StartupManager.class);

    private AllSettings allSettings;

    private ConfigFileSettings configFileSettings;
    private SettingsRecord dbSettings;
    private MotechPlatformState platformState = MotechPlatformState.STARTUP;

    @Autowired
    private CouchDbManager couchDbManager;

    @Autowired
    private ConfigLoader configLoader;

    @Autowired
    private PlatformSettingsService platformSettingsService;

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
        MotechSettings settings = platformSettingsService.getPlatformSettings();
        configFileSettings = settings instanceof ConfigFileSettings ? (ConfigFileSettings) settings : null;

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
            couchDbManager.configureDb(configFileSettings.getCouchProperties());
        } catch (DbConnectionException e) {
            LOGGER.error(e.getMessage(), e);
            platformState = MotechPlatformState.NO_DB;
        }

        // load db settings
        if (platformState != MotechPlatformState.NO_DB) {
            try {
                initSettingsRepository();
                dbSettings = allSettings.getSettings();

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

                saveDbSettings();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                platformState = MotechPlatformState.DB_ERROR;
            }
        }
    }

    private void saveDbSettings() throws Exception {
        dbSettings.setLastRun(DateTime.now());
        dbSettings.setConfigFileChecksum(configFileSettings.getMd5checkSum());

        allSettings.addOrUpdateSettings(dbSettings);
    }

    private void initSettingsRepository() throws Exception {
        CouchDbConnector couchDbConnector = couchDbManager.getConnector(SETTINGS_DB, true);
        allSettings = new AllSettings(couchDbConnector);
    }
}
