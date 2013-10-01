package org.motechproject.server.startup;

import org.ektorp.CouchDbConnector;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.motechproject.commons.couchdb.service.impl.CouchDbManagerImpl;
import org.motechproject.config.domain.BootstrapConfig;
import org.motechproject.config.domain.ConfigSource;
import org.motechproject.config.domain.DBConfig;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.server.config.monitor.ConfigFileMonitor;
import org.motechproject.server.config.repository.AllSettings;
import org.motechproject.server.config.service.ConfigLoader;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.service.impl.PlatformSettingsServiceImpl;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class StartupManagerTest {

    @Mock
    private CouchDbConnector couchDbConnector;

    @Mock
    CouchDbManagerImpl couchDbManager;

    @Mock
    ConfigLoader configLoader;

    @Mock
    ConfigFileMonitor configFileMonitor;

    @Mock
    ConfigurationService configurationService;

    @Mock
    private EventAdmin eventAdmin;

    @Mock
    AllSettings allSettings;

    @InjectMocks
    @Spy
    PlatformSettingsService platformSettingsService = new PlatformSettingsServiceImpl();

    @InjectMocks
    StartupManager startupManager = StartupManager.getInstance();

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testNoSettings() {
        BootstrapConfig bootstrapConfig = new BootstrapConfig(new DBConfig("http://localhost:5984", null, null), null, ConfigSource.FILE);
        when(allSettings.getSettings()).thenReturn(null);
        when(configLoader.loadConfig()).thenReturn(null);
        when(configFileMonitor.getCurrentSettings()).thenReturn(null);
        when(configurationService.loadBootstrapConfig()).thenReturn(bootstrapConfig);
        when(couchDbManager.getConnector(any(String.class))).thenReturn(couchDbConnector);

        startupManager.startup();

        assertTrue(startupManager.isConfigRequired());

        assertFalse(startupManager.canLaunchBundles());
        assertNull(platformSettingsService.getPlatformSettings());
        verify(configLoader).loadConfig();
        verify(allSettings).getSettings();

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
