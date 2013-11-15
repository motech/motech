package org.motechproject.config.monitor;

import org.apache.commons.vfs.FileChangeEvent;
import org.apache.commons.vfs.FileListener;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.impl.DefaultFileMonitor;
import org.motechproject.config.core.domain.ConfigLocation;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.server.config.domain.MotechSettings;
import org.motechproject.server.config.service.ConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Properties;

/**
 * The <code>ConfigFileMonitor</code> is used to monitor changes in config files and send
 * appropriate events.
 */

@Component
public class ConfigurationFileMonitor implements FileListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationFileMonitor.class);
    private static final Long DELAY = 3000L;

    private ConfigLoader configLoader;
    private ConfigurationService configurationService;

    private DefaultFileMonitor fileMonitor;

    ConfigurationFileMonitor() {
    }

    @Autowired
    public ConfigurationFileMonitor(ConfigLoader configLoader, ConfigurationService configurationService)
            throws FileSystemException {
        this.configLoader = configLoader;
        this.configurationService = configurationService;
        this.fileMonitor = new DefaultFileMonitor(this);
        this.fileMonitor.setDelay(DELAY);
    }

    @PostConstruct
    public void init() throws FileSystemException {
        ConfigLocation configLocation = configLoader.getCurrentConfigLocation();
        FileObject monitoredDir = VFS.getManager().resolveFile(configLocation.getLocation());
        fileMonitor.addFile(monitoredDir);
    }

    public void start() {
        fileMonitor.start();
    }

    @PreDestroy
    public void stop() throws FileSystemException {
        this.fileMonitor.stop();
    }

    @Override
    public void fileCreated(FileChangeEvent fileChangeEvent) throws IOException {
        FileObject file = fileChangeEvent.getFile();
        String module = file.getParent().getName().getBaseName();
        String fileName = file.getName().getBaseName();
        Properties properties = new Properties();
        properties.load(file.getContent().getInputStream());

        configurationService.addOrUpdateProperties(module, fileName, properties, null);
    }

    public void evictProperCache(FileChangeEvent fileChangeEvent) {
        String fileName = fileChangeEvent.getFile().getName().getBaseName();

        if (fileName.equals(MotechSettings.SETTINGS_FILE_NAME)) {
            configurationService.evictMotechSettingsCache();
        } else {
            /*platformSettingsService.evictBundleSettingsCache();*/
        }
    }

    @Override
    public void fileDeleted(FileChangeEvent fileChangeEvent) throws FileSystemException {
        String fileName = fileChangeEvent.getFile().getName().getBaseName();
        System.out.println("File deleted............" + fileName);
        if (MotechSettings.SETTINGS_FILE_NAME.equals(fileName)) {
            LOGGER.warn("Config file was deleted: " + fileName);

            evictProperCache(fileChangeEvent);
        }
    }

    @Override
    public void fileChanged(FileChangeEvent fileChangeEvent) {
        String fileName = fileChangeEvent.getFile().getName().getBaseName();
        System.out.println("File changed............" + fileName);
        if (MotechSettings.SETTINGS_FILE_NAME.equals(fileName)) {
            LOGGER.info("Config file was changed: " + fileName);

            /*currentSettings = configLoader.loadConfig();*/

            evictProperCache(fileChangeEvent);
        }
    }
}
