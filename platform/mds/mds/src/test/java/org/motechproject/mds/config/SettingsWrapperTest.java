package org.motechproject.mds.config;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.server.config.SettingsFacade;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.motechproject.mds.config.ModuleSettings.DEFAULT_DELETE_MODE;
import static org.motechproject.mds.config.ModuleSettings.DEFAULT_EMPTY_TRASH;
import static org.motechproject.mds.config.ModuleSettings.DEFAULT_TIME_UNIT;
import static org.motechproject.mds.config.ModuleSettings.DEFAULT_TIME_VALUE;
import static org.motechproject.mds.util.Constants.Config.MODULE_FILE;
import static org.motechproject.mds.util.Constants.Config.MODULE_SETTINGS_CHANGE;

@RunWith(MockitoJUnitRunner.class)
public class SettingsWrapperTest {

    @Mock
    private SettingsFacade settingsFacade;

    @Mock
    private EventRelay eventRelay;

    @Mock
    private Properties moduleProperties;

    @Captor
    private ArgumentCaptor<MotechEvent> eventCaptor;

    private SettingsWrapper settingsWrapper;

    @Before
    public void setUp() throws Exception {
        settingsWrapper = new SettingsWrapper();
        settingsWrapper.setSettingsFacade(settingsFacade);
        settingsWrapper.setEventRelay(eventRelay);

        doReturn(moduleProperties).when(settingsFacade).getProperties(MODULE_FILE);
    }

    @Test
    public void shouldReturnCorrectValues() {
        assertEquals(DEFAULT_DELETE_MODE, settingsWrapper.getDeleteMode());
        assertEquals(DEFAULT_EMPTY_TRASH, settingsWrapper.isEmptyTrash());
        assertEquals(DEFAULT_TIME_VALUE, settingsWrapper.getTimeValue());
        assertEquals(DEFAULT_TIME_UNIT, settingsWrapper.getTimeUnit());

        // settings should contains the same values
        ModuleSettings settings = settingsWrapper.getModuleSettings();

        assertEquals(DEFAULT_DELETE_MODE, settings.getDeleteMode());
        assertEquals(DEFAULT_EMPTY_TRASH, settings.isEmptyTrash());
        assertEquals(DEFAULT_TIME_VALUE, settings.getTimeValue());
        assertEquals(DEFAULT_TIME_UNIT, settings.getTimeUnit());
    }

    @Test
    public void shouldSendEventWhenModuleSettingsAreSaved() throws Exception {
        ModuleSettings moduleSettings = new ModuleSettings();

        settingsWrapper.saveModuleSettings(moduleSettings);

        verify(eventRelay).sendEventMessage(eventCaptor.capture());

        MotechEvent event = eventCaptor.getValue();

        assertNotNull(event);
        assertEquals(MODULE_SETTINGS_CHANGE, event.getSubject());
        assertTrue("Event should not have parameters", event.getParameters().isEmpty());
    }
}
