package org.motechproject.config.monitor;

import org.apache.commons.vfs.FileChangeEvent;
import org.apache.commons.vfs.FileListener;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.impl.DefaultFileMonitor;
import org.motechproject.config.core.MotechConfigurationException;
import org.motechproject.config.core.constants.ConfigurationConstants;
import org.motechproject.config.core.domain.ConfigLocation;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.filters.ConfigFileFilter;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.server.config.service.ConfigLoader;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
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
 * The <code>ConfigFileMonitor</code> is used to monitor changes in config files and send
 * appropriate events.
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

    @PostConstruct
    public void init() throws IOException {
        if (configurationService.getConfigSource() == ConfigSource.FILE) {
            // allow custom monitors to be injected
            if (fileMonitor == null) {
                fileMonitor = new DefaultFileMonitor(this);
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
            configurationService.delete(file.getParentFile().getName());

            Map<String, Object> params = new HashMap<>();
            params.put(ConfigurationConstants.FILE_PATH, file.getAbsolutePath());

            sendEvent(ConfigurationConstants.FILE_DELETED_EVENT_SUBJECT, params);
        }
    }

    @PreDestroy
    public void stop() throws FileSystemException {
        if (fileMonitor != null) {
            fileMonitor.stop();
        }
    }

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
        ServiceReference serviceReference = bundleContext.getServiceReference(ConfigurationConstants.EVENT_RELAY_CLASS_NAME);

        if (serviceReference != null) {
            Object service = bundleContext.getService(serviceReference);
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
