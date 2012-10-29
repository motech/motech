package org.motechproject.server.config.monitor;

import org.apache.commons.vfs.FileChangeEvent;
import org.apache.commons.vfs.FileListener;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.impl.DefaultFileMonitor;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.server.config.ConfigLoader;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.settings.ConfigFileSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class ConfigFileMonitor implements FileListener {
    public static final String BASE_SUBJECT = "org.motechproject.server.config.file.";
    public static final String FILE_DELETED_EVENT_SUBJECT = BASE_SUBJECT + "deleted";
    public static final String FILE_CHANGED_EVENT_SUBJECT = BASE_SUBJECT + "changed";
    public static final String FILE_URL_PARAM = "fileURL";

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigFileMonitor.class);
    private static final Long DELAY = 2500L;

    private EventRelay eventRelay;
    private ConfigLoader configLoader;
    private PlatformSettingsService platformSettingsService;
    private FileSystemManager systemManager;

    private DefaultFileMonitor fileMonitor;
    private ConfigFileSettings currentSettings;

    private boolean monitorStart;

    @Override
    protected void finalize() throws Throwable {
        this.fileMonitor.stop();
        LOGGER.info("Stopped monitoring system.");

        super.finalize();
    }

    @PostConstruct
    public void monitor() throws Exception {
        afterPropertiesSet();
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

    public void changeConfigFileLocation(final String location, final boolean save) throws Exception {
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
        sendEventMessage(FILE_DELETED_EVENT_SUBJECT, fileChangeEvent);
    }

    @Override
    public void fileChanged(FileChangeEvent fileChangeEvent) throws Exception {
        LOGGER.warn("Config file was changed...");

        currentSettings = configLoader.loadConfig();
        platformSettingsService.evictMotechSettingsCache();
        sendEventMessage(FILE_CHANGED_EVENT_SUBJECT, fileChangeEvent);
    }

    public void afterPropertiesSet() throws Exception {
        if (eventRelay == null) {
            throw new Exception("eventRelay property is required.");
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
    public void setEventRelay(final EventRelay eventRelay) {
        this.eventRelay = eventRelay;
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

    private void sendEventMessage(final String subject, final FileChangeEvent fileChangeEvent) {
        try {
            eventRelay.sendEventMessage(createMotechEvent(subject, fileChangeEvent));
        } catch (FileSystemException e) {
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
