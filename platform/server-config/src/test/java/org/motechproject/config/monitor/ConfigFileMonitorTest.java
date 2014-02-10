package org.motechproject.config.monitor;

import org.apache.commons.vfs.FileChangeEvent;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.impl.DefaultFileMonitor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.config.core.MotechConfigurationException;
import org.motechproject.config.core.domain.ConfigLocation;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.server.config.service.ConfigLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConfigFileMonitorTest {

    @Mock
    private ConfigurationService configurationService;
    @Mock
    private DefaultFileMonitor fileMonitor;
    @Mock
    private ConfigLoader configLoader;
    @Mock
    private CoreConfigurationService coreConfigurationService;

    @InjectMocks
    private ConfigFileMonitor configFileMonitor = new ConfigFileMonitor();

    @Before
    public void setUp() {
        configFileMonitor.setFileMonitor(fileMonitor);
    }

    @Test
    public void shouldProcessExistingFilesAndStartFileMonitorWhileInitializing() throws IOException {
        final Path tempDirectory = Files.createTempDirectory("motech-config-");
        String configLocation = tempDirectory.toString();
        when(coreConfigurationService.getConfigLocation()).thenReturn(new ConfigLocation(configLocation));
        when(configurationService.getConfigSource()).thenReturn(ConfigSource.FILE);

        configFileMonitor.init();

        InOrder inOrder = inOrder(configLoader, configurationService, fileMonitor);

        inOrder.verify(configLoader).findExistingConfigs();
        inOrder.verify(configurationService).processExistingConfigs((List<File>) any());

        final ArgumentCaptor<FileObject> fileObjArgCaptor = ArgumentCaptor.forClass(FileObject.class);
        inOrder.verify(fileMonitor).addFile(fileObjArgCaptor.capture());
        final FileObject monitoredLocation = fileObjArgCaptor.getValue();
        assertEquals(configLocation, monitoredLocation.getName().getPath());

        inOrder.verify(fileMonitor).start();
    }

    @Test
    public void shouldSaveConfigWhenNewFileCreated() throws IOException {
        final String fileName = "res:config/org.motechproject.motech-module1/somemodule.properties";
        FileObject fileObject = VFS.getManager().resolveFile(fileName);

        configFileMonitor.fileCreated(new FileChangeEvent(fileObject));

        verify(configurationService).addOrUpdate(new File(fileObject.getName().getPath()));
    }

    @Test
    public void shouldNotSaveConfigWhenNewFileCreatedIsNotSupported() throws IOException {
        final String fileName = "res:config/motech-settings.conf";
        FileObject fileObject = VFS.getManager().resolveFile(fileName);

        configFileMonitor.fileCreated(new FileChangeEvent(fileObject));

        verifyZeroInteractions(configurationService);
    }

    @Test
    public void shouldSaveConfigWhenFileIsChanged() throws IOException {
        final String fileName = "res:config/org.motechproject.motech-module1/somemodule.properties";
        FileObject fileObject = VFS.getManager().resolveFile(fileName);

        configFileMonitor.fileChanged(new FileChangeEvent(fileObject));

        verify(configurationService).addOrUpdate(new File(fileObject.getName().getPath()));
    }

    @Test
    public void shouldUpdateFileMonitoringLocation() throws FileSystemException {
        final String fileName = "res:config/motech-settings.properties";
        ConfigLocation configLocation = new ConfigLocation(fileName);
        FileObject newLocation = VFS.getManager().resolveFile(fileName);
        when(coreConfigurationService.getConfigLocation()).thenReturn(configLocation);

        configFileMonitor.updateFileMonitor();

        InOrder inOrder = inOrder(coreConfigurationService, fileMonitor);
        inOrder.verify(fileMonitor).stop();
        inOrder.verify(fileMonitor).removeFile(any(FileObject.class));
        inOrder.verify(coreConfigurationService).getConfigLocation();
        inOrder.verify(fileMonitor).addFile(newLocation);
        inOrder.verify(fileMonitor).start();
    }

    @Test
    public void shouldDeleteConfigWhenFileIsDeleted() throws FileSystemException {
        final String fileName = "res:config/org.motechproject.motech-module1/somemodule.properties";
        FileObject fileObject = VFS.getManager().resolveFile(fileName);

        configFileMonitor.fileDeleted(new FileChangeEvent(fileObject));

        verify(configurationService).delete(new File(fileObject.getName().getPath()).getParentFile().getName());
    }

    @Test
    public void shouldNotStartFileMonitorIfConfigLoaderThrowsException() throws IOException {
        doThrow(new MotechConfigurationException("file could not be read")).when(configLoader).findExistingConfigs();
        configFileMonitor.init();
        verify(configurationService, never()).processExistingConfigs(anyList());
        verify(fileMonitor, never()).run();
    }

    @Test
    public void shouldNotStartWhenFileSourceIsUI() throws IOException {
        when(configurationService.getConfigSource()).thenReturn(ConfigSource.UI);
        configFileMonitor.init();
        verify(configurationService, never()).processExistingConfigs(anyList());
        verify(fileMonitor, never()).run();
    }
}
