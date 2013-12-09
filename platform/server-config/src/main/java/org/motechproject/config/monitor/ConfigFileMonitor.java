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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * The <code>ConfigFileMonitor</code> is used to monitor changes in config files and send
 * appropriate events.
 */

public class ConfigFileMonitor implements FileListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigFileMonitor.class);
    private static final Long DELAY = 3000L;

    private ConfigLoader configLoader;
    private ConfigurationService configurationService;
    private CoreConfigurationService coreConfigurationService;
    private DefaultFileMonitor fileMonitor;
    private FileObject monitoredDir;

    ConfigFileMonitor() {
    }

    public ConfigFileMonitor(ConfigLoader configLoader, ConfigurationService configurationService, CoreConfigurationService coreConfigurationService)
            throws FileSystemException {
        this.configLoader = configLoader;
        this.configurationService = configurationService;
        this.coreConfigurationService = coreConfigurationService;
        this.fileMonitor = new DefaultFileMonitor(this);
        this.fileMonitor.setDelay(DELAY);
    }

    @PostConstruct
    public void init() throws IOException {
        final List<File> files = configLoader.findExistingConfigs();
        configurationService.processExistingConfigs(files);
        setupLocation();
        fileMonitor.start();
    }

    @Override
    public void fileCreated(FileChangeEvent fileChangeEvent) throws IOException {
        FileObject fileObject = fileChangeEvent.getFile();
        LOGGER.info(String.format("Received file creation event for file: %s", fileObject));

        File file = new File(fileObject.getName().getPath());
        if (ConfigFileFilter.isFileSupported(file)) {
            configurationService.addOrUpdate(file);
        }
    }

    @Override
    public void fileChanged(FileChangeEvent fileChangeEvent) {
        FileObject fileObject = fileChangeEvent.getFile();
        LOGGER.info(String.format("Received file update event for file: %s", fileObject));

        File file = new File(fileObject.getName().getPath());
        if (ConfigFileFilter.isFileSupported(file)) {
            configurationService.addOrUpdate(file);
        }
    }

    @Override
    public void fileDeleted(FileChangeEvent fileChangeEvent) throws FileSystemException {
        FileObject fileObject = fileChangeEvent.getFile();
        LOGGER.info(String.format("Received file deletion event for file: %s", fileObject));

        File file = new File(fileObject.getName().getPath());
        if (ConfigFileFilter.isFileSupported(file)) {
            configurationService.delete(file.getParentFile().getName());
        }
    }

    @PreDestroy
    public void stop() throws FileSystemException {
        this.fileMonitor.stop();
    }

    public void updateFileMonitor() throws FileSystemException {
        fileMonitor.stop();
        fileMonitor.removeFile(monitoredDir);
        LOGGER.info(String.format("Stopped Monitoring location %s", monitoredDir));

        setupLocation();
        fileMonitor.start();
    }

    private void setupLocation() throws FileSystemException {
        ConfigLocation configLocation = coreConfigurationService.getConfigLocation();
        monitoredDir = VFS.getManager().resolveFile(configLocation.getLocation());
        fileMonitor.addFile(monitoredDir);
        LOGGER.info(String.format("Setting up monitoring for location: %s", monitoredDir));
    }
}
