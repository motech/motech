package org.motechproject.server.startup;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.motechproject.server.config.ConfigLoader;
import org.motechproject.server.config.db.CouchDbManager;
import org.motechproject.server.config.db.DbConnectionException;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.service.impl.PlatformSettingsServiceImpl;
import org.motechproject.server.config.settings.ConfigFileSettings;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class StartupManagerTest {

    @Mock
    CouchDbManager couchDbManager;

    @Mock
    ConfigLoader configLoader;

    @Mock
    ConfigFileSettings configFileSettings;

    @Mock
    Properties couchDbProperties;

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

        startupManager.startup();

        assertEquals(MotechPlatformState.NEED_CONFIG, startupManager.getPlatformState());
        assertNull(platformSettingsService.getPlatformSettings());
        verify(configLoader, times(2)).loadConfig();
    }

    @Test
    public void testNoDb() throws DbConnectionException {
        when(configLoader.loadConfig()).thenReturn(configFileSettings);
        when(configFileSettings.getCouchProperties()).thenReturn(couchDbProperties);
        doThrow(new DbConnectionException("Failure")).when(couchDbManager).configureDb(couchDbProperties);

        startupManager.startup();

        assertEquals(startupManager.getPlatformState(), MotechPlatformState.NO_DB);
        assertEquals(platformSettingsService.getPlatformSettings(), configFileSettings);
        verify(configLoader, times(2)).loadConfig();
        verify(couchDbManager).configureDb(couchDbProperties);
    }
}
