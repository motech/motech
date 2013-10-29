package org.motechproject.server.startup;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.domain.DBConfig;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.server.config.domain.SettingsRecord;
import org.motechproject.server.config.monitor.ConfigFileMonitor;
import org.motechproject.server.config.service.ConfigLoader;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class StartupManagerTest {

    @Mock
    ConfigLoader configLoader;

    @Mock
    ConfigFileMonitor configFileMonitor;

    @Mock
    ConfigurationService configurationService;

    @Mock
    private EventAdmin eventAdmin;

    @InjectMocks
    StartupManager startupManager = new StartupManager();

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testNoSettings() {
        BootstrapConfig bootstrapConfig = new BootstrapConfig(new DBConfig("http://localhost:5984", null, null), null, ConfigSource.FILE);
        when(configLoader.loadConfig()).thenReturn(null);
        when(configFileMonitor.getCurrentSettings()).thenReturn(null);
        when(configurationService.loadBootstrapConfig()).thenReturn(bootstrapConfig);
        when(configurationService.getPlatformSettings()).thenReturn(new SettingsRecord());

        startupManager.startup();

        assertTrue(startupManager.isConfigRequired());

        assertFalse(startupManager.canLaunchBundles());
        verify(configLoader).loadConfig();

        verify(eventAdmin, never()).postEvent(any(Event.class));
        verify(eventAdmin, never()).sendEvent(any(Event.class));
    }

    @Test
    public void shouldSetPlatformStateToNeedBootstrapIfNoBootstrapConfigFound(){
        when(configurationService.loadBootstrapConfig()).thenReturn(null);

        startupManager.startup();

        assertTrue(startupManager.isBootstrapConfigRequired());
    }
}
