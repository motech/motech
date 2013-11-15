package org.motechproject.config.monitor;

import org.apache.commons.vfs.FileChangeEvent;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.impl.DefaultFileMonitor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.config.core.domain.ConfigLocation;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.server.config.service.ConfigLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationFileMonitorTest {
    @Mock
    private ConfigurationService configurationService;
    @Mock
    private DefaultFileMonitor fileMonitor;
    @Mock
    ConfigLoader configLoader;

    @InjectMocks
    private ConfigurationFileMonitor configFileMonitor = new ConfigurationFileMonitor();

    @Test
    public void shouldStartFileMonitorWhileInitializing() throws IOException {
        final Path tempDirectory = Files.createTempDirectory("motech-config-");
        String configLocation = tempDirectory.toString();
        when(configLoader.getCurrentConfigLocation()).thenReturn(new ConfigLocation(configLocation));

        configFileMonitor.init();

        final ArgumentCaptor<FileObject> argCaptor = ArgumentCaptor.forClass(FileObject.class);
        verify(fileMonitor).addFile(argCaptor.capture());
        final FileObject monitoredLocation = argCaptor.getValue();
        assertEquals(configLocation, monitoredLocation.getName().getPath());
        verify(fileMonitor).start();
    }

    @Test
    public void shouldCreateConfigWhenNewFileCreated() throws IOException {
        FileObject fileObject = mock(FileObject.class);
        configFileMonitor.fileCreated(new FileChangeEvent(fileObject));
    }
}
