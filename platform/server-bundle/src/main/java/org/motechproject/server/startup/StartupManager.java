package org.motechproject.server.startup;

import org.joda.time.DateTime;
import org.motechproject.commons.api.MotechException;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.config.domain.MotechSettings;
import org.motechproject.server.osgi.util.PlatformConstants;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Properties;

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
    private static final Logger LOGGER = LoggerFactory.getLogger(StartupManager.class);

    private MotechPlatformState platformState = MotechPlatformState.STARTUP;
    private MotechSettings motechSettings;
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
        configurationService.evictMotechSettingsCache();

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
            motechSettings = configurationService.loadConfig();
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
            eventAdmin.postEvent(new Event(PlatformConstants.STARTUP_TOPIC, new HashMap<>()));
        }
    }


    public boolean canLaunchBundles() {
        return isFirstRun() || platformState == NORMAL_RUN;
    }

    /**
     * This function is only called when the default configuration is loaded
     * and is no config in the database or external files
     */
    public MotechSettings getDefaultSettings() {
        return configurationService.loadDefaultConfig();
    }

    private boolean needAdmin() {
        return dbSettings.getLoginMode().isRepository() && !userService.hasActiveMotechAdmin();
    }

    private void syncSettingsWithDb() {
        try {
            if (dbSettings.getLastRun() == null) {
                markPlatformStateAs(FIRST_RUN);
            } else {
                markPlatformStateAs(NORMAL_RUN);
            }

            Properties properties = dbSettings.asProperties();

            if (isFirstRun() || motechSettings == null ||
                    !motechSettings.getConfigFileChecksum().equals(dbSettings.getConfigFileChecksum())) {
                LOGGER.info("Updating database startup");

                Properties propertiesToUpdate = motechSettings.asProperties();
                for (Object key : propertiesToUpdate.keySet()) {
                    Object value = propertiesToUpdate.get(key);
                    properties.put(key, value == null ? "" : value);
                }
            }

            try {
                MessageDigest digest = MessageDigest.getInstance("MD5");
                dbSettings.setConfigFileChecksum(new String(digest.digest(dbSettings.asProperties().toString().getBytes())));
            } catch (NoSuchAlgorithmException e) {
                throw new MotechException("MD5 algorithm not available", e);
            }

            dbSettings.setLastRun(DateTime.now());
            dbSettings.setPlatformInitialized(true);

            configurationService.savePlatformSettings(properties);
        } catch (RuntimeException e) {
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
