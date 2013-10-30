package org.motechproject.server.startup;

import org.joda.time.DateTime;
import org.motechproject.commons.api.MotechException;
import org.motechproject.config.domain.BootstrapConfig;
import org.motechproject.config.domain.ConfigSource;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.server.config.domain.MotechSettings;
import org.motechproject.server.config.domain.SettingsRecord;
import org.motechproject.server.config.service.ConfigLoader;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

/**
 * StartupManager controlling and managing the application loading
 */
@Component
public class StartupManager {
    private static final String STARTUP_TOPIC = "org/motechproject/osgi/event/STARTUP";
    private static final Logger LOGGER = LoggerFactory.getLogger(StartupManager.class);

    private MotechPlatformState platformState = MotechPlatformState.STARTUP;
    private SettingsRecord settingsRecord;
    private MotechSettings dbSettings;

    @Autowired
    private ConfigLoader configLoader;

    @Autowired
    private EventAdmin eventAdmin;

    @Autowired
    private ConfigurationService configurationService;

    public boolean isConfigRequired() {
        return platformState == MotechPlatformState.NEED_BOOTSTRAP_CONFIG || platformState == MotechPlatformState.NEED_CONFIG;
    }

    public boolean isBootstrapConfigRequired() {
        return platformState == MotechPlatformState.NEED_BOOTSTRAP_CONFIG;
    }

    @PostConstruct
    public void startup() {
        BootstrapConfig bootstrapConfig = configurationService.loadBootstrapConfig();
        if (bootstrapConfig == null) {
            LOGGER.info("Bootstrap config required from the user.");
            platformState = MotechPlatformState.NEED_BOOTSTRAP_CONFIG;
            return;
        }

        dbSettings = configurationService.getPlatformSettings();

        if (!dbSettings.isPlatformInitialized()) {
            platformState = MotechPlatformState.NEED_CONFIG;
            if (ConfigSource.FILE.equals(bootstrapConfig.getConfigSource())) {
                LOGGER.info("Config source is FILE, and no settings in DB. We require input on the first user.");
                settingsRecord = configLoader.loadConfig();
            } else {
                LOGGER.info("Config source is UI, and no settings in DB. Entering startup.");
            }
        } else {
            LOGGER.info("Found settings in db, normal run");

            syncSettingsWithDb();
            platformState = MotechPlatformState.NORMAL_RUN;
        }

        if (canLaunchBundles()) {
            // send an OSGI event indicating that the modules can be started
            eventAdmin.postEvent(new Event(STARTUP_TOPIC, (Map) null));
        }
    }

    public boolean canLaunchBundles() {
        return platformState == MotechPlatformState.FIRST_RUN || platformState == MotechPlatformState.NORMAL_RUN;
    }

    /**
     * This function is only called when the default configuration is loaded
     * and is no config in the database or external files
     */
    public SettingsRecord getDefaultSettings() {
        return configLoader.loadDefaultConfig();
    }

    private void syncSettingsWithDb() {
        try {
            if (dbSettings.getLastRun() == null) {
                platformState = MotechPlatformState.FIRST_RUN;
            } else {
                platformState = MotechPlatformState.NORMAL_RUN;
            }

            if (platformState == MotechPlatformState.FIRST_RUN || settingsRecord == null ||
                    !Arrays.equals(settingsRecord.getConfigFileChecksum(), dbSettings.getConfigFileChecksum())) {
                LOGGER.info("Updating database startup");
                dbSettings.updateSettings(settingsRecord);
            }

            try {
                MessageDigest digest = MessageDigest.getInstance("MD5");
                dbSettings.setConfigFileChecksum(digest.digest(dbSettings.getPlatformSettings().toString().getBytes()));
            } catch (NoSuchAlgorithmException e) {
                throw new MotechException("MD5 algorithm not available", e);
            }

            dbSettings.setLastRun(DateTime.now());
            dbSettings.setPlatformInitialized(true);

            configurationService.savePlatformSettings(dbSettings);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            platformState = MotechPlatformState.DB_ERROR;
        }
    }
}
