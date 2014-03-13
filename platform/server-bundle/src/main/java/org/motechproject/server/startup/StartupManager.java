package org.motechproject.server.startup;

import org.joda.time.DateTime;
import org.motechproject.commons.api.MotechException;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.config.domain.MotechSettings;
import org.motechproject.server.config.domain.SettingsRecord;
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

import static org.motechproject.server.startup.MotechPlatformState.DB_ERROR;
import static org.motechproject.server.startup.MotechPlatformState.FIRST_RUN;
import static org.motechproject.server.startup.MotechPlatformState.NEED_BOOTSTRAP_CONFIG;
import static org.motechproject.server.startup.MotechPlatformState.NEED_CONFIG;
import static org.motechproject.server.startup.MotechPlatformState.NORMAL_RUN;

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
    private EventAdmin eventAdmin;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private MotechUserService userService;

    public boolean isConfigRequired() {
        return platformState == NEED_BOOTSTRAP_CONFIG || platformState == NEED_CONFIG;
    }

    public boolean isBootstrapConfigRequired() {
        return platformState == NEED_BOOTSTRAP_CONFIG;
    }

    @PostConstruct
    public void startup() {
        BootstrapConfig bootstrapConfig = configurationService.loadBootstrapConfig();
        if (bootstrapConfig == null) {
            LOGGER.info("Bootstrap config required from the user.");
            markPlatformStateAs(NEED_BOOTSTRAP_CONFIG);
            return;
        }
        if (configurationService.requiresConfigurationFiles()) {
            markPlatformStateAs(NEED_CONFIG);
            return;
        }

        dbSettings = configurationService.getPlatformSettings();

        if (bootstrapConfig.getConfigSource() == ConfigSource.FILE) {
            settingsRecord = configurationService.loadConfig();
            syncSettingsWithDb();
        }

        if (!dbSettings.isPlatformInitialized()) {
            if (bootstrapConfig.getConfigSource() == ConfigSource.FILE) {
                LOGGER.info("Config source is FILE, and no settings in DB.");

                // only require input on the first user if we don't have an admin in db in repository mode
                if (needAdmin()) {
                    LOGGER.info("We require input on the active admin user");
                    markPlatformStateAs(NEED_CONFIG);
                } else {
                    markPlatformStateAs(NORMAL_RUN);
                }
            } else {
                LOGGER.info("Config source is UI, and no settings in DB. Entering startup.");
                markPlatformStateAs(NEED_CONFIG);
            }
        } else {
            if (needAdmin()) {
                LOGGER.info("Found settings in db, but there is no active admin user. Entering startup");
                markPlatformStateAs(NEED_CONFIG);
                return;
            }
            LOGGER.info("Found settings in db, normal run");
            markPlatformStateAs(NORMAL_RUN);
        }

        if (canLaunchBundles()) {
            // send an OSGI event indicating that the modules can be started
            eventAdmin.postEvent(new Event(STARTUP_TOPIC, (Map) null));
        }
    }


    public boolean canLaunchBundles() {
        return isFirstRun() || platformState == NORMAL_RUN;
    }

    /**
     * This function is only called when the default configuration is loaded
     * and is no config in the database or external files
     */
    public SettingsRecord getDefaultSettings() {
        return configurationService.loadDefaultConfig();
    }

    private boolean needAdmin() {
        return dbSettings.getLoginMode().isRepository() && !userService.hasActiveAdminUser();
    }

    private void syncSettingsWithDb() {
        try {
            if (dbSettings.getLastRun() == null) {
                markPlatformStateAs(FIRST_RUN);
            } else {
                markPlatformStateAs(NORMAL_RUN);
            }

            if (isFirstRun() || settingsRecord == null ||
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
            markPlatformStateAs(DB_ERROR);
        }
    }

    private boolean isFirstRun() {
        return platformState == FIRST_RUN;
    }

    private void markPlatformStateAs(MotechPlatformState platformState) {
        this.platformState = platformState;
    }


}
