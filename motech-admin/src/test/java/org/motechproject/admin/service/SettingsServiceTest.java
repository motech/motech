package org.motechproject.admin.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.admin.service.impl.SettingsServiceImpl;
import org.motechproject.admin.settings.BundleSettings;
import org.motechproject.admin.settings.SettingsOption;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.settings.MotechSettings;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.server.config.settings.MotechSettings.*;

public class SettingsServiceTest {
    private static final Long BUNDLE_ID = 1L;
    private static final String BUNDLE_SYMBOLIC_NAME = "motech-test";

    private static final String AMQ_REDELIVERY_DELAY_IN_MILLIS_VALUE = "2000";
    private static final String QUARTZ_SCHEDULER_NAME_VALUE = "sample";
    private static final String LANGUAGE_VALUE = "en";

    private static final String BUNDLE_FILENAME = "test.properties";
    private static final String OPTION_KEY = "name";
    private static final String OPTION_VALUE = "test";

    private static final String DB_HOST_VALUE = "localhost";

    @Mock
    PlatformSettingsService platformSettingsService;

    @Mock
    BundleContext bundleContext;

    @Mock
    Bundle bundle;

    @InjectMocks
    SettingsService settingsService = new SettingsServiceImpl();

    @Mock
    MotechSettings motechSettings;

    Properties bundleProperty = new Properties();

    @Before
    public void setUp() throws IOException {
        initMocks(this);

        initMotechSettings();
        initBundle();
        initPlatformSettingsService();
    }

    @Test
    public void testGetSettings() {
        List<SettingsOption> settingsOptionList = settingsService.getSettings();

        assertEquals(4, settingsOptionList.size());

        assertEquals(AMQ_REDELIVERY_DELAY_IN_MILLIS, settingsOptionList.get(0).getKey());
        assertEquals(AMQ_REDELIVERY_DELAY_IN_MILLIS_VALUE, settingsOptionList.get(0).getValue());

        assertEquals(QUARTZ_SCHEDULER_NAME, settingsOptionList.get(1).getKey());
        assertEquals(QUARTZ_SCHEDULER_NAME_VALUE, settingsOptionList.get(1).getValue());

        assertEquals(DB_HOST, settingsOptionList.get(2).getKey());
        assertEquals(DB_HOST_VALUE, settingsOptionList.get(2).getValue());

        assertEquals(LANGUAGE, settingsOptionList.get(3).getKey());
        assertEquals(LANGUAGE_VALUE, settingsOptionList.get(3).getValue());

        verify(platformSettingsService).getPlatformSettings();
    }

    @Test
    public void testGetBundleSettings() throws IOException {
        List<BundleSettings> bundleSettingsList = settingsService.getBundleSettings(BUNDLE_ID);

        assertEquals(1, bundleSettingsList.size());

        BundleSettings bundleSettings = bundleSettingsList.get(0);
        assertEquals(BUNDLE_FILENAME, bundleSettings.getFilename());

        List<SettingsOption> settingsOptions = bundleSettings.getSettings();

        assertEquals(1, settingsOptions.size());
        assertEquals(OPTION_KEY, settingsOptions.get(0).getKey());
        assertEquals(OPTION_VALUE, settingsOptions.get(0).getValue());
    }

    @Test
    public void testSaveBundleSettings() throws IOException {
        SettingsOption option = new SettingsOption(new AbstractMap.SimpleEntry<Object, Object>(OPTION_KEY, OPTION_VALUE));

        BundleSettings settings = new BundleSettings(BUNDLE_FILENAME, Arrays.asList(option));
        settingsService.saveBundleSettings(settings, BUNDLE_ID);

        verify(platformSettingsService).saveBundleProperties(BUNDLE_SYMBOLIC_NAME, BUNDLE_FILENAME, bundleProperty);
    }

    private void initPlatformSettingsService() throws IOException {
        bundleProperty.put(OPTION_KEY, OPTION_VALUE);
        Map<String, Properties> propertiesMap = new HashMap<>(1);
        propertiesMap.put(BUNDLE_FILENAME, bundleProperty);

        when(platformSettingsService.getPlatformSettings()).thenReturn(motechSettings);
        when(platformSettingsService.getAllProperties(BUNDLE_SYMBOLIC_NAME)).thenReturn(propertiesMap);
    }

    private void initBundle() {
        when(bundleContext.getBundle(BUNDLE_ID)).thenReturn(bundle);
        when(bundle.getSymbolicName()).thenReturn(BUNDLE_SYMBOLIC_NAME);
    }

    private void initMotechSettings() {
        Properties activemq = new Properties();
        activemq.put(AMQ_REDELIVERY_DELAY_IN_MILLIS, AMQ_REDELIVERY_DELAY_IN_MILLIS_VALUE);
        Properties quartz = new Properties();
        quartz.put(QUARTZ_SCHEDULER_NAME,QUARTZ_SCHEDULER_NAME_VALUE);
        Properties couchDB = new Properties();
        couchDB.put(DB_HOST, DB_HOST_VALUE);

        when(motechSettings.getActivemqProperties()).thenReturn(activemq);
        when(motechSettings.getQuartzProperties()).thenReturn(quartz);
        when(motechSettings.getCouchDBProperties()).thenReturn(couchDB);
        when(motechSettings.getLanguage()).thenReturn(LANGUAGE_VALUE);
    }
}
