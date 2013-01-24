package org.motechproject.server.startup;

import org.ektorp.CouchDbConnector;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.motechproject.commons.couchdb.service.impl.CouchDbManagerImpl;
import org.motechproject.server.config.ConfigLoader;
import org.motechproject.server.config.monitor.ConfigFileMonitor;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.service.impl.PlatformSettingsServiceImpl;
import org.motechproject.server.config.settings.ConfigFileSettings;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.server.startup.MotechPlatformState.NEED_CONFIG;

public class StartupManagerTest {

    @Mock
    private CouchDbConnector couchDbConnector;

    @Mock
    CouchDbManagerImpl couchDbManager;

    @Mock
    ConfigLoader configLoader;

    @Mock
    ConfigFileSettings configFileSettings;

    @Mock
    Properties couchDbProperties;

    @Mock
    ConfigFileMonitor configFileMonitor;

    @Mock
    private EventAdmin eventAdmin;

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
        when(configLoader.loadConfig()).thenReturn(null);
        when(configFileMonitor.getCurrentSettings()).thenReturn(null);

        startupManager.startup();

        assertEquals(NEED_CONFIG, startupManager.getPlatformState());
        assertFalse(startupManager.canLaunchBundles());
        assertNull(platformSettingsService.getPlatformSettings());
        verify(configLoader).loadConfig();
        verify(configFileMonitor).getCurrentSettings();

        verify(eventAdmin, never()).postEvent(any(Event.class));
        verify(eventAdmin, never()).sendEvent(any(Event.class));
    }
}
