package org.motechproject.server.config;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SettingsFacadeTest {

    private static final String BUNDLE_NAME = "org.motechproject.bundle";
    private static final String FILENAME = "settings.properties";
    private static final String LANGUAGE_PROP = "system.language";
    private static final String LANGUAGE_VALUE = "en";


    SettingsFacade settingsFacade = new SettingsFacade();

    @Mock
    BundleContext bundleContext;

    @Mock
    Bundle bundle;

    @Mock
    Properties props;

    @Mock
    PlatformSettingsService platformSettingsService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testGetConfigNoService() {
        settingsFacade.setPlatformSettingsService(null);
        settingsFacade.setBundleContext(null);
        setUpConfig();

        String result = settingsFacade.getProperty(LANGUAGE_PROP);

        assertEquals(LANGUAGE_VALUE, result);
    }

    @Test
    public void testGetConfigWithService() throws IOException {
        settingsFacade.setPlatformSettingsService(platformSettingsService);
        settingsFacade.setBundleContext(bundleContext);
        when(bundleContext.getBundle()).thenReturn(bundle);
        when(bundle.getSymbolicName()).thenReturn(BUNDLE_NAME);
        when(platformSettingsService.getBundleProperties(BUNDLE_NAME, FILENAME)).thenReturn(props);
        when(props.getProperty(LANGUAGE_PROP)).thenReturn(LANGUAGE_VALUE);
        when(props.containsKey(LANGUAGE_PROP)).thenReturn(true);
        setUpConfig();

        String result = settingsFacade.getProperty(LANGUAGE_PROP);

        assertEquals(LANGUAGE_VALUE, result);

        verify(bundleContext, times(2)).getBundle();
        verify(bundle, times(2)).getSymbolicName();
        verify(platformSettingsService, times(2)).getBundleProperties(BUNDLE_NAME, FILENAME);
    }

    @Test
    public void testSetConfig() throws IOException {
        settingsFacade.setPlatformSettingsService(platformSettingsService);
        settingsFacade.setBundleContext(bundleContext);
        when(bundleContext.getBundle()).thenReturn(bundle);
        when(bundle.getSymbolicName()).thenReturn(BUNDLE_NAME);
        when(platformSettingsService.getBundleProperties(BUNDLE_NAME, FILENAME)).thenReturn(null);
        when(props.getProperty(LANGUAGE_PROP)).thenReturn(LANGUAGE_VALUE);
        when(props.containsKey(LANGUAGE_PROP)).thenReturn(true);

        setUpConfig();

        verify(bundleContext, times(2)).getBundle();
        verify(bundle, times(2)).getSymbolicName();
        ArgumentCaptor<Properties> argument = ArgumentCaptor.forClass(Properties.class);
        verify(platformSettingsService).saveBundleProperties(eq(BUNDLE_NAME), eq(FILENAME), argument.capture());
        assertEquals(LANGUAGE_VALUE, argument.getValue().getProperty(LANGUAGE_PROP));
    }

    private void setUpConfig() {
        List<Resource> configFiles = new ArrayList<>();
        configFiles.add(new ClassPathResource("settings.properties"));
        settingsFacade.setConfigFiles(configFiles);
    }
}
