package org.motechproject.admin.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.admin.service.SettingsService;
import org.motechproject.admin.service.StatusMessageService;
import org.motechproject.admin.settings.BundleSettings;
import org.motechproject.admin.settings.SettingsOption;
import org.motechproject.admin.web.controller.SettingsController;
import org.motechproject.server.config.settings.MotechSettings;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
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
    List<BundleSettings> bundleSettingsList;

    @Mock
    Map<Object, Object> paramMap;

    @Mock
    HttpServletRequest httpServletRequest;

    @InjectMocks
    SettingsController controller = new SettingsController();

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testGetBundleSettings() throws IOException {
        when(settingsService.getBundleSettings(BUNDLE_ID)).thenReturn(bundleSettingsList);

        List<BundleSettings> settings = controller.getBundleSettings(BUNDLE_ID);

        assertEquals(bundleSettingsList, settings);
        verify(settingsService).getBundleSettings(BUNDLE_ID);
    }

    @Test
    public void testSaveBundleSettings() throws IOException {
        when(httpServletRequest.getParameterMap()).thenReturn(paramMap);

        controller.saveBundleSettings(BUNDLE_ID, httpServletRequest);

        verify(settingsService).saveBundleSettings(anyListOf(SettingsOption.class), eq(BUNDLE_ID));
    }

    @Test
    public void testSavePlatformSettings() throws IOException {
        when(httpServletRequest.getParameterMap()).thenReturn(paramMap);

        controller.savePlatformSettings(httpServletRequest);

        verify(settingsService).savePlatformSettings(anyListOf(SettingsOption.class));
    }

    @Test
    public void testGetPlatformSettings() {
        SettingsOption option = new SettingsOption(new AbstractMap.SimpleEntry<Object, Object>(MotechSettings.LANGUAGE, "en"));

        when(settingsService.getSettings()).thenReturn(Arrays.asList(option));

        List<SettingsOption> settings = controller.getPlatformSettings();

        assertEquals(option.getKey(), settings.get(0).getKey());
        assertEquals(option.getValue(), settings.get(0).getValue());
        assertEquals(option.getType(), settings.get(0).getType());

        verify(settingsService).getSettings();
    }
}
