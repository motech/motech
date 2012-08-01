package org.motechproject.server.config.monitor;

import org.apache.commons.vfs.FileChangeEvent;
import org.apache.commons.vfs.FileListener;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.impl.DefaultFileMonitor;
import org.joda.time.DateTime;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.domain.RunOnceJobId;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;
import org.motechproject.scheduler.gateway.MotechSchedulerGateway;
import org.motechproject.server.config.ConfigLoader;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.settings.ConfigFileSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class ConfigFileMonitor implements FileListener, InitializingBean {
    public static final String BASE_SUBJECT = "org.motechproject.server.config.file.";
    public static final String FILE_DELETED_EVENT_SUBJECT = BASE_SUBJECT + "deleted";
    public static final String FILE_CHANGED_EVENT_SUBJECT = BASE_SUBJECT + "changed";
    public static final String FILE_URL_PARAM = "fileURL";

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigFileMonitor.class);
    private static final Long DELAY = 2500L;

    private static ConfigFileMonitor instance;

    private MotechSchedulerGateway schedulerGateway;
    private ConfigLoader configLoader;
    private PlatformSettingsService platformSettingsService;
    private FileSystemManager systemManager;

    private DefaultFileMonitor fileMonitor;
    private ConfigFileSettings currentSettings;

    private boolean monitorStart = false;

    private ConfigFileMonitor() {
    }

    public static ConfigFileMonitor getInstance() {
        if (instance == null) {
            LOGGER.debug("Creating new instance of ConfigFileMonitor");
            instance = new ConfigFileMonitor();
        }

        return instance;
    }

    @Override
    protected void finalize() throws Throwable {
        this.fileMonitor.stop();
        LOGGER.info("Stopped monitoring system.");

        super.finalize();
    }

    public void monitor() {
        LOGGER.debug("Reading config file.");
        ConfigFileSettings configFileSettings = configLoader.loadConfig();

        if (configFileSettings != null) {
            try {
                if (currentSettings != null) {
                    remove();
                }

                String path = configFileSettings.getPath();
                fileMonitor.addFile(systemManager.resolveFile(path));

                if (!monitorStart) {
                    fileMonitor.start();
                    monitorStart = true;
                    LOGGER.info("Started monitoring system.");
                }

                LOGGER.info(String.format("Started monitoring: %s", path));

                currentSettings = configFileSettings;
            } catch (FileSystemException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    public ConfigFileSettings getCurrentSettings() {
        return currentSettings;
    }

    public void changeConfigFileLocation(final String location, final boolean save) {
        if (location.startsWith("/")) {
            configLoader.addConfigLocation(String.format("file:%s", location));
        } else {
            configLoader.addConfigLocation(location);
        }

        if (save) {
            try {
                configLoader.save();
            } catch (IOException e) {
                LOGGER.error("Couldn't save the new config file location.", e);
            }
        }

        LOGGER.warn("Changed config file location");

        monitor();
    }

    @Override
    public void fileCreated(FileChangeEvent fileChangeEvent) throws Exception {
    }

    @Override
    public void fileDeleted(FileChangeEvent fileChangeEvent) throws Exception {
        LOGGER.error("Config file was deleted...");

        if (currentSettings != null) {
            remove();
        }

        platformSettingsService.evictMotechSettingsCache();
        scheduleJob(FILE_DELETED_EVENT_SUBJECT, fileChangeEvent);
    }

    @Override
    public void fileChanged(FileChangeEvent fileChangeEvent) throws Exception {
        LOGGER.warn("Config file was changed...");

        currentSettings = configLoader.loadConfig();
        platformSettingsService.evictMotechSettingsCache();
        scheduleJob(FILE_CHANGED_EVENT_SUBJECT, fileChangeEvent);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (schedulerGateway == null) {
            throw new Exception("schedulerGateway property is required.");
        }

        if (configLoader == null) {
            throw new Exception("configLoader property is required.");
        }

        if (platformSettingsService == null) {
            throw new Exception("platformSettingsService property is required.");
        }

        if (systemManager == null) {
            this.systemManager = VFS.getManager();
        }

        this.fileMonitor = new DefaultFileMonitor(this);
        this.fileMonitor.setDelay(DELAY);

        this.currentSettings = null;
    }

    @Autowired
    public void setSchedulerGateway(final MotechSchedulerGateway schedulerGateway) {
        this.schedulerGateway = schedulerGateway;
    }

    @Autowired
    public void setConfigLoader(final ConfigLoader configLoader) {
        this.configLoader = configLoader;
    }

    @Autowired
    public void setPlatformSettingsService(final PlatformSettingsService platformSettingsService) {
        this.platformSettingsService = platformSettingsService;
    }

    public void setSystemManager(final FileSystemManager systemManager) {
        this.systemManager = systemManager;
    }

    private void scheduleJob(final String subject, final FileChangeEvent fileChangeEvent) {
        try {
            MotechEvent motechEvent = createMotechEvent(subject, fileChangeEvent);
            Date startDate = DateTime.now().toDate();
            RunOnceSchedulableJob runOnceSchedulableJob = new RunOnceSchedulableJob(motechEvent, startDate);
            RunOnceJobId runOnceJobId = new RunOnceJobId(motechEvent);

            schedulerGateway.unscheduleJob(runOnceJobId);
            schedulerGateway.scheduleRunOnceJob(runOnceSchedulableJob);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private MotechEvent createMotechEvent(final String subject, final FileChangeEvent fileChangeEvent) throws FileSystemException {
        Map<String, Object> param = new HashMap<>();
        param.put(FILE_URL_PARAM, fileChangeEvent.getFile().getURL().getPath());

        return new MotechEvent(subject, param);
    }

    private void remove() throws FileSystemException {
        String path = currentSettings.getPath();
        fileMonitor.removeFile(systemManager.resolveFile(path));
        currentSettings = null;

        LOGGER.info(String.format("Stopped monitoring: %s", path));
    }

}
