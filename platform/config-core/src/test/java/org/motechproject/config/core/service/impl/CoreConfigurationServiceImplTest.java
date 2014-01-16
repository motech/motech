package org.motechproject.config.core.service.impl;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.config.core.MotechConfigurationException;
import org.motechproject.config.core.bootstrap.BootstrapManager;
import org.motechproject.config.core.bootstrap.Environment;
import org.motechproject.config.core.domain.ConfigLocation;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.filestore.ConfigLocationFileStore;

import java.nio.file.FileSystemException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class CoreConfigurationServiceImplTest {

    private final String bootstrapFileLocation = "file_location";
    private final String bootstrapFile = bootstrapFileLocation + "/bootstrap.properties";
    private final String dbUrl = "http://localhost:5984";
    private final String username = "user";
    private final String password = "pass";
    private final String tenantId = "test_tenant_id";
    private final String configSource = ConfigSource.FILE.getName();
    @Mock
    private Environment environmentMock;
    @Mock
    private BootstrapManager bootstrapManagerMock;
    @Mock
    private ConfigLocationFileStore configLocationFileStoreMock;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private CoreConfigurationServiceImpl coreConfigurationService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        coreConfigurationService = new CoreConfigurationServiceImpl(bootstrapManagerMock, configLocationFileStoreMock);
    }

    @Test
    public void shouldGetConfigLocation() {
        String correctConfigPath = this.getClass().getClassLoader().getResource("config").getPath();
        String inCorrectConfigPath = this.getClass().getClassLoader().getResource("some_random_dir").getPath();
        ConfigLocation incorrectLocation = new ConfigLocation(inCorrectConfigPath);
        ConfigLocation correctLocation = new ConfigLocation(correctConfigPath);
        when(configLocationFileStoreMock.getAll()).thenReturn(Arrays.asList(incorrectLocation, correctLocation));

        ConfigLocation configLocation = coreConfigurationService.getConfigLocation();

        assertEquals(correctLocation, configLocation);
    }

    @Test
    public void shouldThrowExceptionIfNoneOfTheConfigLocationsAreReadable() {
        String inCorrectConfigPath = this.getClass().getClassLoader().getResource("some_random_dir").getPath();
        when(configLocationFileStoreMock.getAll()).thenReturn(Arrays.asList(new ConfigLocation(inCorrectConfigPath)));

        expectedException.expect(MotechConfigurationException.class);
        expectedException.expectMessage("Could not read settings from any of the config locations.");

        coreConfigurationService.getConfigLocation();
    }

    @Test
    public void shouldAddConfigLocation() throws FileSystemException {
        final String location = "/etc";
        coreConfigurationService.addConfigLocation(location);
        verify(configLocationFileStoreMock).add(location);
    }
}
