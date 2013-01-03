package org.motechproject.server.startup;

import org.ektorp.CouchDbConnector;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.motechproject.server.config.ConfigLoader;
import org.motechproject.server.config.db.CouchDbManager;
import org.motechproject.server.config.db.DbConnectionException;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.server.startup.MotechPlatformState.NEED_CONFIG;

public class StartupManagerTest {

    @Mock
    private CouchDbConnector couchDbConnector;

    @Mock
    CouchDbManager couchDbManager;

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
//        when(couchDbManager.getConnector("motech-platform-startup", true)).thenReturn(couchDbConnector);
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

    @Test
    public void testNoDb() throws DbConnectionException {
        when(configLoader.loadConfig()).thenReturn(configFileSettings);
        when(configFileMonitor.getCurrentSettings()).thenReturn(configFileSettings);
        when(configFileSettings.getCouchDBProperties()).thenReturn(couchDbProperties);
        doThrow(new DbConnectionException("Failure")).when(platformSettingsService).configureCouchDBManager();

        startupManager.startup();

        verify(configLoader).loadConfig();
        verify(platformSettingsService).configureCouchDBManager();

        assertEquals(startupManager.getPlatformState(), MotechPlatformState.NO_DB);
        assertEquals(platformSettingsService.getPlatformSettings(), configFileSettings);
        assertFalse(startupManager.canLaunchBundles());

        verify(configFileMonitor).getCurrentSettings();

        verify(eventAdmin, never()).postEvent(any(Event.class));
        verify(eventAdmin, never()).sendEvent(any(Event.class));
    }
}
