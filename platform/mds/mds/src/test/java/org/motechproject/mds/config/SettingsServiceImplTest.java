package org.motechproject.mds.config;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.config.impl.SettingsServiceImpl;
import org.motechproject.mds.domain.ConfigSettings;
import org.motechproject.mds.repository.AllConfigSettings;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.motechproject.mds.config.ModuleSettings.DEFAULT_DELETE_MODE;
import static org.motechproject.mds.config.ModuleSettings.DEFAULT_EMPTY_TRASH;
import static org.motechproject.mds.config.ModuleSettings.DEFAULT_TIME_UNIT;
import static org.motechproject.mds.config.ModuleSettings.DEFAULT_TIME_VALUE;
import static org.motechproject.mds.util.Constants.Config.MODULE_FILE;

@RunWith(MockitoJUnitRunner.class)
public class SettingsServiceImplTest {

    @Mock
    private AllConfigSettings allConfigSettings;

    @Mock
    private Properties moduleProperties;

    private ConfigSettings configSettings;

    @Mock
    private MdsConfig mdsConfig;

    private SettingsServiceImpl settingsServiceImpl;

    @Before
    public void setUp() throws Exception {
        settingsServiceImpl = new SettingsServiceImpl();
        settingsServiceImpl.setAllConfigSettings(allConfigSettings);
        settingsServiceImpl.setMdsConfig(mdsConfig);
        configSettings = null;

        doReturn(moduleProperties).when(mdsConfig).getProperties(MODULE_FILE);
        doReturn(configSettings).when(allConfigSettings).retrieve("id",1);
    }

    @Test
    public void shouldReturnCorrectValues() {
        assertEquals(DEFAULT_DELETE_MODE, settingsServiceImpl.getDeleteMode());
        assertEquals(DEFAULT_EMPTY_TRASH, settingsServiceImpl.isEmptyTrash());
        assertEquals(DEFAULT_TIME_VALUE, settingsServiceImpl.getTimeValue());
        assertEquals(DEFAULT_TIME_UNIT, settingsServiceImpl.getTimeUnit());

        // settings should contains the same values
        ModuleSettings settings = settingsServiceImpl.getModuleSettings();

        assertEquals(DEFAULT_DELETE_MODE, settings.getDeleteMode());
        assertEquals(DEFAULT_EMPTY_TRASH, settings.isEmptyTrash());
        assertEquals(DEFAULT_TIME_VALUE, settings.getTimeValue());
        assertEquals(DEFAULT_TIME_UNIT, settings.getTimeUnit());
    }
}
