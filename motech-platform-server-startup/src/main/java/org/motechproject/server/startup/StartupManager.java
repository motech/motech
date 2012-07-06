package org.motechproject.server.startup;

import org.ektorp.CouchDbConnector;
import org.joda.time.DateTime;
import org.motechproject.server.startup.db.CouchDbManager;
import org.motechproject.server.startup.db.DbConnectionException;
import org.motechproject.server.startup.domain.SettingsRecord;
import org.motechproject.server.startup.repository.AllSettings;
import org.motechproject.server.startup.settings.ConfigFileSettings;
import org.motechproject.server.startup.settings.MotechSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Component
public class StartupManager {

    private static final String SETTINGS_DB = "motech-platform-startup";

    private static final Logger logger = LoggerFactory.getLogger(StartupManager.class);

    private AllSettings allSettings;

    private ConfigFileSettings configFileSettings;
    private SettingsRecord dbSettings;
    private MotechSettings currentSettings;
    private MotechPlatformState platformState = MotechPlatformState.STARTUP;

    @Autowired
    private CouchDbManager couchDbManager;

    @Autowired
    private ConfigLoader configLoader;

    public MotechSettings getSettings() {
        return currentSettings;
    }

    public MotechPlatformState getPlatformState() {
        return platformState;
    }

    @PostConstruct
    public MotechPlatformState startup() {
        configFileSettings = configLoader.loadConfig();
        currentSettings = configFileSettings;

        // check if settings were loaded from config locations
        if (configFileSettings == null) {
            platformState = MotechPlatformState.NEED_CONFIG;
        } else {
            logger.info("Loaded config from " + configFileSettings.getFileURL());
        }

        if (platformState != MotechPlatformState.NEED_CONFIG) {
            syncSettingsWithDb();
        }

        return platformState;
    }

    public MotechPlatformState startupWithSettings(ConfigFileSettings settings) {
        configFileSettings = settings;
        currentSettings = configFileSettings;
        
        syncSettingsWithDb();

        return platformState;
    }
    
    private void syncSettingsWithDb() {
        // test Database
        try {
            couchDbManager.configureDb(configFileSettings.getCouchProperties());
        } catch (DbConnectionException e) {
            logger.error(e.getMessage(), e);
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
                    logger.info("Updating database startup");
                    dbSettings.updateSettings(configFileSettings);
                }

                currentSettings = dbSettings; // use DB config
                saveDbSettings();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
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