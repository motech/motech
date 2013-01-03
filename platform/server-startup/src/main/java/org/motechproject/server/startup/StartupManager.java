package org.motechproject.server.startup;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.DateTime;
import org.motechproject.commons.api.MotechException;
import org.motechproject.server.config.ConfigLoader;
import org.motechproject.server.config.db.DbConnectionException;
import org.motechproject.server.config.domain.SettingsRecord;
import org.motechproject.server.config.service.AllSettings;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.settings.ConfigFileSettings;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;


public final class StartupManager {
    private static StartupManager instance;

    private static final String SETTINGS_DB = "motech-platform-startup";
    private static final String ADMIN_SYMBOLIC_NAME = "org.motechproject.motech-admin-bundle";
    private static final String STARTUP_TOPIC = "org/motechproject/osgi/event/STARTUP";

    private static final Logger LOGGER = LoggerFactory.getLogger(StartupManager.class);

    private MotechPlatformState platformState = MotechPlatformState.STARTUP;
    private ConfigFileSettings configFileSettings;

    @Autowired
    private ConfigLoader configLoader;

    @Autowired
    private PlatformSettingsService platformSettingsService;

    @Autowired
    private EventAdmin eventAdmin;

    @Autowired
    private BundleContext bundleContext;

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

    @PostConstruct
    public void startup() {
        startup(true);
    }

    public void startup(boolean startAllBundles) {
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
            if (startAllBundles) {
                // send an OSGI event indicating that the modules can be started
                eventAdmin.postEvent(new Event(STARTUP_TOPIC, (Map) null));
            } else {
                // only start the admin bundle
                startAdmin();
            }
        }
    }

    private void startAdmin() {
        Bundle adminBundle = getAdminBundle();

        if (adminBundle == null) {
            LOGGER.warn("Admin bundle not installed");
        } else if (adminBundle.getState() == Bundle.ACTIVE) {
            LOGGER.warn("Admin bundle already active");
        } else {
            try {
                adminBundle.start();
            } catch (BundleException e) {
                throw new MotechException("Cannot start admin bundle", e);
            }
        }
    }

    public boolean canLaunchBundles() {
        return platformState == MotechPlatformState.FIRST_RUN || platformState == MotechPlatformState.NORMAL_RUN;
    }

    public ConfigFileSettings getLoadedConfig() {
        return configFileSettings;
    }

    public boolean findCouchDBInstance(final String url) {
        boolean found = false;

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet request = new HttpGet(url);
            request.addHeader("accept", "application/json");

            HttpResponse response = httpClient.execute(request);

            if (response.getStatusLine().getStatusCode() == 200) {
                found = true;
            }
        } catch (IOException e) {
            found = false;
        }

        return found;
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

    private Bundle getAdminBundle() {
        for (Bundle bundle : bundleContext.getBundles()) {
            if (bundle.getSymbolicName().equals(ADMIN_SYMBOLIC_NAME)) {
                return bundle;
            }
        }
        return null;
    }

    private void syncSettingsWithDb() {
        // test Database
        try {
            platformSettingsService.configureCouchDBManager();
        } catch (DbConnectionException e) {
            LOGGER.error(e.getMessage(), e);
            platformState = MotechPlatformState.NO_DB;
        }

        // load db settings
        if (platformState != MotechPlatformState.NO_DB) {
            try {
                AllSettings allSettings = new AllSettings(platformSettingsService.getCouchConnector(SETTINGS_DB));
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
