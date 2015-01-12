package org.motechproject.server.config;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.config.core.MotechConfigurationException;
import org.motechproject.config.service.ConfigurationService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SettingsFacadeTest {

    private static final String BUNDLE_NAME = "org.motechproject.motech-module-bundle";
    private static final String BUNDLE_VERSION = "";
    private static final String FILENAME = "settings.properties";
    private static final String LANGUAGE_PROP = "system.language";
    private static final String LANGUAGE_VALUE = "en";
    private static final String TEST_PROP = "test";
    private static final String TEST_VAL = "test-val";
    private static final String MOCK_FILENAME = "testfile";

    SettingsFacade settingsFacade = new SettingsFacade();

    @Mock
    Properties props;

    @Mock
    ConfigurationService configurationService;

    @Mock
    BundleContext bundleContext;

    @Mock
    Bundle bundle;

    @Mock
    ServiceReference ref;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testSetConfig() throws IOException {
        when(configurationService.getBundleProperties(eq(BUNDLE_NAME), eq(FILENAME), any(Properties.class))).thenReturn(null);
        when(props.getProperty(LANGUAGE_PROP)).thenReturn(LANGUAGE_VALUE);
        when(props.containsKey(LANGUAGE_PROP)).thenReturn(true);

        setUpConfig();

        ArgumentCaptor<Properties> argument = ArgumentCaptor.forClass(Properties.class);
        verify(configurationService).addOrUpdateProperties(eq(BUNDLE_NAME), eq(BUNDLE_VERSION), eq(FILENAME),
                argument.capture(), any(Properties.class));
        assertEquals(LANGUAGE_VALUE, argument.getValue().getProperty(LANGUAGE_PROP));
    }

    @Test
    public void testAsProperties() throws IOException {
        setUpConfig();
        Properties otherProps = new Properties();
        otherProps.put(TEST_PROP, TEST_VAL);
        settingsFacade.saveConfigProperties(MOCK_FILENAME, otherProps);
        settingsFacade.afterPropertiesSet();

        Properties result = settingsFacade.asProperties();

        assertEquals(2, result.size());
        assertEquals(TEST_VAL, result.get(TEST_PROP));
        assertEquals(LANGUAGE_VALUE, result.get(LANGUAGE_PROP));
    }


    @Test(expected = MotechConfigurationException.class)
    public void shouldThrowExceptionsComingFromService() throws IOException {
        when(configurationService.registersProperties(anyString(), anyString()))
                .thenThrow(new MotechConfigurationException("file could not be read"));
        setUpConfig();
    }

    private void setUpConfig() throws IOException {
        setUpOSGiEnv();

        Properties properties = new Properties();
        properties.put(LANGUAGE_PROP, LANGUAGE_VALUE);

        when(configurationService.getBundleProperties(BUNDLE_NAME, FILENAME, properties)).thenReturn(properties);

        List<Resource> configFiles = new ArrayList<>();
        configFiles.add(new ClassPathResource(FILENAME));
        settingsFacade.setConfigFiles(configFiles);



        verify(configurationService).registersProperties(BUNDLE_NAME, FILENAME);
        verify(configurationService).addOrUpdateProperties(BUNDLE_NAME, BUNDLE_VERSION, FILENAME,
                properties, properties);
    }

    private void setUpOSGiEnv() {
        when(bundleContext.getBundle()).thenReturn(bundle);
        when(bundle.getSymbolicName()).thenReturn(BUNDLE_NAME);

        settingsFacade.setBundleContext(bundleContext);

        when(bundleContext.getServiceReference(ConfigurationService.class.getName())).thenReturn(ref);
        when(bundleContext.getService(ref)).thenReturn(configurationService);

        settingsFacade.afterPropertiesSet();
    }
}
