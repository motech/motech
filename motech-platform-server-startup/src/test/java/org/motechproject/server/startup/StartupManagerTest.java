package org.motechproject.server.startup;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.server.startup.db.CouchDbManager;
import org.motechproject.server.startup.db.DbConnectionException;
import org.motechproject.server.startup.settings.ConfigFileSettings;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;
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
    StartupManager startupManager = new StartupManager();

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testNoSettings() {
        when(configLoader.loadConfig()).thenReturn(null);

        startupManager.startup();

        assertEquals(startupManager.getPlatformState(), MotechPlatformState.NEED_CONFIG);
        assertNull(startupManager.getSettings());
        verify(configLoader).loadConfig();
    }

    @Test
    public void testNoDb() throws DbConnectionException {
        when(configLoader.loadConfig()).thenReturn(configFileSettings);
        when(configFileSettings.getCouchProperties()).thenReturn(couchDbProperties);
        doThrow(new DbConnectionException("Failure")).when(couchDbManager).configureDb(couchDbProperties);

        startupManager.startup();

        assertEquals(startupManager.getPlatformState(), MotechPlatformState.NO_DB);
        assertEquals(startupManager.getSettings(), configFileSettings);
        verify(configLoader).loadConfig();
        verify(couchDbManager).configureDb(couchDbProperties);
    }
}
