package org.motechproject.admin.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.admin.domain.AdminSettings;
import org.motechproject.admin.service.impl.SettingsServiceImpl;
import org.motechproject.admin.settings.Settings;
import org.motechproject.admin.settings.SettingsOption;
import org.motechproject.config.monitor.ConfigFileMonitor;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.server.config.domain.MotechSettings;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.security.web.savedrequest.Enumerator;

import java.io.IOException;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.config.core.constants.ConfigurationConstants.AMQ_REDELIVERY_DELAY_IN_MILLIS;
import static org.motechproject.config.core.constants.ConfigurationConstants.LANGUAGE;

public class SettingsServiceTest {
    private static final Long BUNDLE_ID = 1L;
    private static final String BUNDLE_SYMBOLIC_NAME = "motech-test-bundle";

    private static final String AMQ_REDELIVERY_DELAY_IN_MILLIS_VALUE = "2000";
    private static final String LANGUAGE_VALUE = "en";

    private static final String BUNDLE_FILENAME = "test.properties";
    private static final String OPTION_KEY = "name";
    private static final String OPTION_VALUE = "test";

    @Mock
    ConfigurationService configurationService;

    @Mock
    BundleContext bundleContext;

    @Mock
    Bundle bundle;

    @Mock
    MotechSettings motechSettings;

    @Mock
    private ConfigFileMonitor configFileMonitor;

    @Mock
    private EventRelay eventRelay;

    @InjectMocks
    SettingsService settingsService = new SettingsServiceImpl();

    Properties bundleProperty = new Properties();

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        initMotechSettings();
        initBundle();
        initConfigService();
        initConfigurationService();
    }

    @Test
    public void testGetSettings() {
        AdminSettings adminSettings = settingsService.getSettings();

        assertNotNull(adminSettings);

        List<Settings> platformSettingsList = adminSettings.getSettingsList();

        assertEquals(false, adminSettings.isReadOnly());
        assertEquals(2, platformSettingsList.size());

        SettingsOption option = platformSettingsList.get(0).getSettings().get(0);
        assertEquals(AMQ_REDELIVERY_DELAY_IN_MILLIS, option.getKey());
        assertEquals(AMQ_REDELIVERY_DELAY_IN_MILLIS_VALUE, option.getValue());

        option = platformSettingsList.get(1).getSettings().get(0);
        assertEquals(LANGUAGE, option.getKey());
        assertEquals(LANGUAGE_VALUE, option.getValue());

        verify(configurationService).getPlatformSettings();
    }

    @Test
    public void testGetBundleSettings() throws IOException {
        List<Settings> bundleSettingsList = settingsService.getBundleSettings(BUNDLE_ID);

        assertEquals(1, bundleSettingsList.size());

        Settings bundleSettings = bundleSettingsList.get(0);
        assertEquals(BUNDLE_FILENAME, bundleSettings.getSection());

        List<SettingsOption> settingsOptions = bundleSettings.getSettings();

        assertEquals(1, settingsOptions.size());
        assertEquals(OPTION_KEY, settingsOptions.get(0).getKey());
        assertEquals(OPTION_VALUE, settingsOptions.get(0).getValue());
    }

    @Test
    public void testSaveBundleSettings() throws IOException {
        SettingsOption option = new SettingsOption(new AbstractMap.SimpleEntry<Object, Object>(OPTION_KEY, OPTION_VALUE));

        Settings settings = new Settings(BUNDLE_FILENAME, asList(option));
        settingsService.saveBundleSettings(settings, BUNDLE_ID);

        verify(configurationService).addOrUpdateProperties(BUNDLE_SYMBOLIC_NAME, BUNDLE_FILENAME, bundleProperty, null);
    }

    @Test
    public void shouldAddSettingsPath() throws IOException {
        final String path = "some-path";
        settingsService.addSettingsPath(path);
        InOrder inOrder = inOrder(configurationService, configFileMonitor);
        inOrder.verify(configurationService).updateConfigLocation(path);
        inOrder.verify(configFileMonitor).updateFileMonitor();
    }

    private void initConfigService() throws IOException {
        bundleProperty.put(OPTION_KEY, OPTION_VALUE);
        Map<String, Properties> propertiesMap = new HashMap<>(1);
        propertiesMap.put(BUNDLE_FILENAME, bundleProperty);

        when(configurationService.getPlatformSettings()).thenReturn(motechSettings);
        when(configurationService.getAllModuleProperties(eq(BUNDLE_SYMBOLIC_NAME), anyMap())).thenReturn(propertiesMap);
    }

    private void initConfigurationService() throws IOException {
        bundleProperty.put(OPTION_KEY, OPTION_VALUE);

        when(configurationService.getModuleProperties(eq(BUNDLE_SYMBOLIC_NAME), eq(BUNDLE_FILENAME), any(Properties.class))).thenReturn(bundleProperty);
    }

    private void initBundle() throws Exception {
        when(bundleContext.getBundle(BUNDLE_ID)).thenReturn(bundle);
        when(bundle.getSymbolicName()).thenReturn(BUNDLE_SYMBOLIC_NAME);
        when(bundle.findEntries("", "*.properties", false)).thenReturn(new Enumerator(new ArrayList<>(asList(new URL("http://mock.com")))));
    }

    private void initMotechSettings() {
        Properties activemq = new Properties();
        activemq.put(AMQ_REDELIVERY_DELAY_IN_MILLIS, AMQ_REDELIVERY_DELAY_IN_MILLIS_VALUE);

        when(motechSettings.getActivemqProperties()).thenReturn(activemq);
        when(motechSettings.getLanguage()).thenReturn(LANGUAGE_VALUE);
    }
}
