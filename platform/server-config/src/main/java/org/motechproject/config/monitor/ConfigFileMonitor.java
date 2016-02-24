package org.motechproject.config.monitor;

import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.motechproject.config.core.exception.MotechConfigurationException;
import org.motechproject.config.core.constants.ConfigurationConstants;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigLocation;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.filters.ConfigFileFilter;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.osgi.web.util.OSGiServiceUtils;
import org.motechproject.server.config.service.ConfigLoader;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class used for monitoring changes in configuration files and sending appropriate events.
 */
@Component
public class ConfigFileMonitor implements FileListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigFileMonitor.class);
    private static final Long DELAY = 3000L;

    @Autowired
    private ConfigLoader configLoader;
    @Autowired
    private ConfigurationService configurationService;
    @Autowired
    private CoreConfigurationService coreConfigurationService;

    @Autowired(required = false)
    private BundleContext bundleContext;

    private DefaultFileMonitor fileMonitor;
    private FileObject monitoredDir;

    public void setFileMonitor(DefaultFileMonitor fileMonitor) {
        this.fileMonitor = fileMonitor;
    }

    /**
     * Initializes the configuration file monitor. This method will be automatically called after creation and
     * dependency injection. It is done to make sure that injected dependencies are set and ready to use.
     */
    @PostConstruct
    public void init() throws IOException {
        BootstrapConfig bootstrapConfig = configurationService.loadBootstrapConfig();
        if (bootstrapConfig != null && bootstrapConfig.getConfigSource() == ConfigSource.FILE) {
            // allow custom monitors to be injected
            if (fileMonitor == null) {
                fileMonitor = new DefaultFileMonitor(this);
                // allow raw configs, which are one directory down, under /raw/, to be monitored
                fileMonitor.setRecursive(true);
            }

            fileMonitor.setDelay(DELAY);

            final List<File> files = new ArrayList<>();

            try {
                files.addAll(configLoader.findExistingConfigs());
            } catch (MotechConfigurationException ex) {
                LOGGER.error(ex.getMessage(), ex);
                return;
            }

            configurationService.processExistingConfigs(files);

            startFileMonitor();
        }
    }


    @Override
    public void fileCreated(FileChangeEvent fileChangeEvent) throws IOException {
        FileObject fileObject = fileChangeEvent.getFile();
        LOGGER.info(String.format("Received file creation event for file: %s", fileObject));

        File file = new File(fileObject.getName().getPath());
        if (ConfigFileFilter.isFileSupported(file)) {
            configurationService.addOrUpdate(file);

            Map<String, Object> params = new HashMap<>();
            params.put(ConfigurationConstants.FILE_PATH, file.getAbsolutePath());

            sendEvent(ConfigurationConstants.FILE_CREATED_EVENT_SUBJECT, params);
        }
    }

    @Override
    public void fileChanged(FileChangeEvent fileChangeEvent) {
        FileObject fileObject = fileChangeEvent.getFile();
        LOGGER.info(String.format("Received file update event for file: %s", fileObject));

        File file = new File(fileObject.getName().getPath());
        if (ConfigFileFilter.isFileSupported(file)) {
            configurationService.addOrUpdate(file);

            Map<String, Object> params = new HashMap<>();
            params.put(ConfigurationConstants.FILE_PATH, file.getAbsolutePath());

            sendEvent(ConfigurationConstants.FILE_CHANGED_EVENT_SUBJECT, params);
        }
    }

    @Override
    public void fileDeleted(FileChangeEvent fileChangeEvent) throws FileSystemException {
        FileObject fileObject = fileChangeEvent.getFile();
        LOGGER.info(String.format("Received file deletion event for file: %s", fileObject));

        File file = new File(fileObject.getName().getPath());
        if (ConfigFileFilter.isFileSupported(file)) {
            configurationService.deleteByBundle(file.getParentFile().getName());

            Map<String, Object> params = new HashMap<>();
            params.put(ConfigurationConstants.FILE_PATH, file.getAbsolutePath());

            sendEvent(ConfigurationConstants.FILE_DELETED_EVENT_SUBJECT, params);
        }
    }

    /**
     * Stops the file monitor.
     */
    @PreDestroy
    public void stop() throws FileSystemException {
        if (fileMonitor != null) {
            fileMonitor.stop();
        }
    }

    /**
     * Updates the file monitor.
     */
    public void updateFileMonitor() throws FileSystemException {
        if (fileMonitor == null) {
            LOGGER.debug("File monitor updated in UI mode, ignoring");
        } else {
            fileMonitor.stop();
            fileMonitor.removeFile(monitoredDir);
            LOGGER.info(String.format("Stopped Monitoring location %s", monitoredDir));
            startFileMonitor();
        }
    }


    private void startFileMonitor() throws FileSystemException {
        setupLocation();
        fileMonitor.start();
    }

    private void setupLocation() throws FileSystemException {
        ConfigLocation configLocation = coreConfigurationService.getConfigLocation();
        monitoredDir = VFS.getManager().resolveFile(configLocation.getLocation());
        fileMonitor.addFile(monitoredDir);
        LOGGER.info(String.format("Setting up monitoring for location: %s", monitoredDir));
    }

    private void sendEvent(String subject, Map<String, Object> params) {
        if (bundleContext == null) {
            return;
        }

        Object service = OSGiServiceUtils.findService(bundleContext, ConfigurationConstants.EVENT_RELAY_CLASS_NAME);

        if (service != null) {
            Class<?> serviceClass = service.getClass();
            Class<?> motechEventClass;

            try {
                motechEventClass = serviceClass.getClassLoader().loadClass(ConfigurationConstants.MOTECH_EVENT_CLASS_NAME);
                Method sendEventMessage = serviceClass.getMethod("sendEventMessage", motechEventClass);
                Object obj = motechEventClass.getDeclaredConstructor(String.class, Map.class).newInstance(subject, params);

                sendEventMessage.invoke(service, obj);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                LOGGER.error("Can't invoke sendEventMessage method.", e);
            }
        }
    }
}
