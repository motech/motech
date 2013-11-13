package org.motechproject.config.monitor;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.impl.DefaultFileMonitor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.config.domain.ConfigLocation;
import org.motechproject.config.filestore.ConfigLocationFileStore;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.server.config.service.ConfigLoader;
import org.motechproject.testing.utils.Wait;
import org.motechproject.testing.utils.WaitCondition;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;

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
    private String configLocation;

    @Test
    public void shouldStartFileMonitorWhileInitializing() throws IOException {
        final Path tempDirectory = Files.createTempDirectory("motech-config-");
        /*List<ConfigLocation> configLocations = new ArrayList<>();
        configLocation = tempDirectory.toString();
        configLocations.add(new ConfigLocation(configLocation));
        when(configLocationFileStore.getPath()).thenReturn(tempDirectory.toString());*/



        configFileMonitor.init();

        final ArgumentCaptor<FileObject> argCaptor = ArgumentCaptor.forClass(FileObject.class);
        verify(fileMonitor).addFile(argCaptor.capture());
        final FileObject monitoredLocation = argCaptor.getValue();
        assertEquals(configLocation, monitoredLocation.getName().getPath());
        verify(fileMonitor).start();
    }

    @Test
    public void shouldMonitorConfigFileChanges() throws IOException, InterruptedException {
        final File f = File.createTempFile("module-", ".properties", new File(configLocation));
        new Wait(new WaitCondition() {
            @Override
            public boolean needsToWait() {
                try {
                    verify(configurationService).updateProperties(null, f.getName(), null, null);
                    return false;
                } catch (Exception e) {
                    return true;
                }
            }
        }, 5000).start();
    }

    @Test
    public void should() {
        PropertiesConfiguration propsConfig = new PropertiesConfiguration();
        propsConfig.setBasePath(System.getProperty("user.name") + File.pathSeparator + ".motech");
        propsConfig.setFileName("config-locations.properties");
        ConfigLocationFileStore fileStore = new ConfigLocationFileStore(propsConfig);
        System.out.println(fileStore.getAll());
        for (ConfigLocation configLocation : fileStore.getAll()) {
            System.out.println(configLocation);
        }
    }
}
