package org.motechproject.server.config.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.commons.couchdb.service.impl.CouchDbManagerImpl;
import org.motechproject.server.config.domain.MotechSettings;
import org.motechproject.server.config.domain.SettingsRecord;
import org.motechproject.server.config.monitor.ConfigFileMonitor;
import org.motechproject.server.config.repository.AllSettings;
import org.motechproject.server.config.service.PlatformSettingsService;

import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class
    PlatformSettingsServiceImplTest {

    @Mock
    CouchDbManagerImpl couchDbManager;

    @Mock
    AllSettings allSettings;

    @Mock
    ConfigFileMonitor configFileMonitor;

    @InjectMocks
    PlatformSettingsService platformSettingsService = new PlatformSettingsServiceImpl();

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testExport() throws IOException {
        SettingsRecord settings = new SettingsRecord();
        settings.savePlatformSetting(MotechSettings.LANGUAGE, "en");
        when(configFileMonitor.getCurrentSettings()).thenReturn(settings);
        when(allSettings.getSettings()).thenReturn(settings);

        Properties p = platformSettingsService.exportPlatformSettings();

        assertTrue(p.containsKey(MotechSettings.LANGUAGE));
        assertEquals(settings.getLanguage(), p.getProperty(MotechSettings.LANGUAGE));
    }
}