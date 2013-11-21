package org.motechproject.config.monitor;

import org.apache.commons.vfs.FileChangeEvent;
import org.apache.commons.vfs.FileListener;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.impl.DefaultFileMonitor;
import org.motechproject.config.core.domain.ConfigLocation;
import org.motechproject.config.core.filestore.ConfigFileFilter;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.server.config.service.ConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;

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
    private CoreConfigurationService coreConfigurationService;
    private DefaultFileMonitor fileMonitor;

    ConfigurationFileMonitor() {
    }

    @Autowired
    public ConfigurationFileMonitor(ConfigLoader configLoader, ConfigurationService configurationService, CoreConfigurationService coreConfigurationService)
            throws FileSystemException {
        this.configLoader = configLoader;
        this.configurationService = configurationService;
        this.coreConfigurationService = coreConfigurationService;
        this.fileMonitor = new DefaultFileMonitor(this);
        this.fileMonitor.setDelay(DELAY);
    }

    @PostConstruct
    public void init() throws IOException {
        configLoader.processExistingConfigs();

        ConfigLocation configLocation = coreConfigurationService.getConfigLocation();
        FileObject monitoredDir = VFS.getManager().resolveFile(configLocation.getLocation());
        fileMonitor.addFile(monitoredDir);
        fileMonitor.start();
    }

    @Override
    public void fileCreated(FileChangeEvent fileChangeEvent) throws IOException {
        FileObject fileObject = fileChangeEvent.getFile();
        LOGGER.info(String.format("Received file creation event for file: %s", fileObject));

        handleFile(fileObject);
    }

    @Override
    public void fileChanged(FileChangeEvent fileChangeEvent) {
        FileObject fileObject = fileChangeEvent.getFile();
        LOGGER.info(String.format("Received file update event for file: %s", fileObject));

        handleFile(fileObject);
    }

    private void handleFile(FileObject fileObject) {
        File file = new File(fileObject.getName().getPath());
        if (!ConfigFileFilter.isFileSupported(file)) {
            return;
        }
        configurationService.addOrUpdate(file);
    }

    @Override
    public void fileDeleted(FileChangeEvent fileChangeEvent) throws FileSystemException {
        LOGGER.info(String.format("Received file deletion event for file: %s", fileChangeEvent.getFile()));
    }

    @PreDestroy
    public void stop() throws FileSystemException {
        this.fileMonitor.stop();
    }
}
