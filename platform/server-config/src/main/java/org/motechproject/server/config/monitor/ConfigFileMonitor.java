package org.motechproject.server.config.monitor;

import org.apache.commons.vfs.FileChangeEvent;
import org.apache.commons.vfs.FileListener;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.impl.DefaultFileMonitor;
import org.motechproject.commons.api.MotechException;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.server.config.domain.MotechSettings;
import org.motechproject.server.config.service.ConfigLoader;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * The <code>ConfigFileMonitor</code> is used to monitor changes in config files and send
 * appropriate events.
 */
@Component
public class ConfigFileMonitor implements FileListener {
    public static final String BASE_SUBJECT = "org.motechproject.server.config.file.";
    public static final String FILE_DELETED_EVENT_SUBJECT = BASE_SUBJECT + "deleted";
    public static final String FILE_CHANGED_EVENT_SUBJECT = BASE_SUBJECT + "changed";
    public static final String FILE_CREATED_EVENT_SUBJECT = BASE_SUBJECT + "created";
    public static final String FILE_URL_PARAM = "fileURL";

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigFileMonitor.class);
    private static final Long DELAY = 2500L;

    private ConfigLoader configLoader;
    private PlatformSettingsService platformSettingsService;
    private ConfigurationService configurationService;
    private FileSystemManager systemManager;

    private DefaultFileMonitor fileMonitor;
    private MotechSettings currentSettings;

    private boolean monitorStart;

    @PreDestroy
    public void stop() throws FileSystemException {
        remove();
        this.fileMonitor.stop();
        LOGGER.info("Stopped monitoring system.");
    }

    public void monitor() throws FileSystemException {
        afterPropertiesSet();
        LOGGER.debug("Reading config file.");
        MotechSettings motechSettings = configLoader.loadConfig();

        if (motechSettings != null) {
            try {
                if (currentSettings != null) {
                    remove();
                }

                String path = motechSettings.getFilePath();
                fileMonitor.addFile(systemManager.resolveFile(path));

                if (!monitorStart) {
                    fileMonitor.start();
                    monitorStart = true;
                    LOGGER.info("Started monitoring system.");
                }

                LOGGER.info(String.format("Started monitoring: %s", path));

                currentSettings = motechSettings;
            } catch (FileSystemException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    public MotechSettings getCurrentSettings() {
        return currentSettings;
    }

    @Override
    public void fileCreated(FileChangeEvent fileChangeEvent) {
        String fileName = fileChangeEvent.getFile().getName().getBaseName();

        if (MotechSettings.SETTINGS_FILE_NAME.equals(fileName)) {
            LOGGER.info("Config file was created: " + fileName);
        }
    }

    public void evictProperCache(FileChangeEvent fileChangeEvent) {
        String fileName = fileChangeEvent.getFile().getName().getBaseName();

        if (fileName.equals(MotechSettings.SETTINGS_FILE_NAME)) {
            configurationService.evictMotechSettingsCache();
        } else {
            platformSettingsService.evictBundleSettingsCache();
        }
    }

    @Override
    public void fileDeleted(FileChangeEvent fileChangeEvent) throws FileSystemException {
        String fileName = fileChangeEvent.getFile().getName().getBaseName();

        if (MotechSettings.SETTINGS_FILE_NAME.equals(fileName)) {
            LOGGER.warn("Config file was deleted: " + fileName);

            evictProperCache(fileChangeEvent);
        }
    }

    @Override
    public void fileChanged(FileChangeEvent fileChangeEvent) {
        String fileName = fileChangeEvent.getFile().getName().getBaseName();

        if (MotechSettings.SETTINGS_FILE_NAME.equals(fileName)) {
            LOGGER.info("Config file was changed: " + fileName);

            currentSettings = configLoader.loadConfig();

            evictProperCache(fileChangeEvent);
        }
    }

    public void afterPropertiesSet() throws FileSystemException {
        if (configLoader == null) {
            throw new MotechException("configLoader property is required.");
        }

        if (configurationService == null) {
            throw new MotechException("configurationService property is required.");
        }

        if (systemManager == null) {
            this.systemManager = VFS.getManager();
        }

        this.fileMonitor = new DefaultFileMonitor(this);
        this.fileMonitor.setDelay(DELAY);

        this.currentSettings = null;
    }

    @Autowired
    public void setConfigLoader(final ConfigLoader configLoader) {
        this.configLoader = configLoader;
    }

    @Autowired
    public void setPlatformSettingsService(final PlatformSettingsService platformSettingsService) {
        this.platformSettingsService = platformSettingsService;
    }

    @Autowired
    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void setSystemManager(final FileSystemManager systemManager) {
        this.systemManager = systemManager;
    }


    private void remove() throws FileSystemException {
        String path = currentSettings.getFilePath();
        fileMonitor.removeFile(systemManager.resolveFile(path));
        currentSettings = null;

        LOGGER.info(String.format("Stopped monitoring: %s", path));
    }

}
