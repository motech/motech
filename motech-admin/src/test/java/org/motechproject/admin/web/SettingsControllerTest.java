package org.motechproject.admin.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.admin.service.SettingsService;
import org.motechproject.admin.service.StatusMessageService;
import org.motechproject.admin.settings.Settings;
import org.motechproject.admin.settings.SettingsOption;
import org.motechproject.admin.web.controller.SettingsController;
import org.motechproject.server.config.settings.MotechSettings;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SettingsControllerTest {
    private static final long BUNDLE_ID = 1;

    @Mock
    SettingsService settingsService;

    @Mock
    StatusMessageService statusMessageService;

    @Mock
    List<Settings> bundleSettingsList;

    @Mock
    Map<Object, Object> paramMap;

    @Mock
    HttpServletRequest httpServletRequest;

    @Mock
    Settings bundleSettings;

    @Mock
    Settings platformSettings;

    @InjectMocks
    SettingsController controller = new SettingsController();

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testGetBundleSettings() throws IOException {
        when(settingsService.getBundleSettings(BUNDLE_ID)).thenReturn(bundleSettingsList);

        List<Settings> settings = controller.getBundleSettings(BUNDLE_ID);

        assertEquals(bundleSettingsList, settings);
        verify(settingsService).getBundleSettings(BUNDLE_ID);
    }

    @Test
    public void testSaveBundleSettings() throws IOException {
        when(httpServletRequest.getParameterMap()).thenReturn(paramMap);

        controller.saveBundleSettings(BUNDLE_ID, bundleSettings);

        verify(settingsService).saveBundleSettings(bundleSettings, BUNDLE_ID);
    }

    @Test
    public void testSavePlatformSettings() throws IOException {
        when(httpServletRequest.getParameterMap()).thenReturn(paramMap);

        Settings[] settings = new Settings[0];

        controller.savePlatformSettings(settings);

        verify(settingsService).savePlatformSettings(anyListOf(Settings.class));
    }

    @Test
    public void testGetPlatformSettings() {
        SettingsOption option = new SettingsOption(new AbstractMap.SimpleEntry<Object, Object>(MotechSettings.LANGUAGE, "en"));
        List<Settings> pSettingsList = new ArrayList<>();
        pSettingsList.add(platformSettings);

        when(settingsService.getSettings()).thenReturn(pSettingsList);
        when(platformSettings.getSettings()).thenReturn(Arrays.asList(option));

        List<Settings> result = controller.getPlatformSettings();

        assertEquals(option.getKey(), result.get(0).getSettings().get(0).getKey());
        assertEquals(option.getValue(), result.get(0).getSettings().get(0).getValue());
        assertEquals(option.getType(), result.get(0).getSettings().get(0).getType());

        verify(settingsService).getSettings();
    }
}
