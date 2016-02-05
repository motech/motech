package org.motechproject.metrics.ut.config;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.metrics.config.ConsoleReporterConfig;
import org.motechproject.metrics.config.GraphiteReporterConfig;
import org.motechproject.metrics.config.MetricsConfig;
import org.motechproject.metrics.config.MetricsConfigFacade;
import org.motechproject.metrics.util.ConfigUtil;
import org.motechproject.server.config.SettingsFacade;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.motechproject.metrics.config.MetricsConfigFacade.METRICS_ENABLED;
import static org.motechproject.metrics.config.MetricsConfigFacade.CONSOLE_REPORTER_ENABLED;
import static org.motechproject.metrics.config.MetricsConfigFacade.CONSOLE_REPORTER_CONVERT_RATES_UNIT;
import static org.motechproject.metrics.config.MetricsConfigFacade.CONSOLE_REPORTER_CONVERT_DURATIONS_UNIT;
import static org.motechproject.metrics.config.MetricsConfigFacade.CONSOLE_REPORTER_REPORTING_FREQUENCY_VALUE;
import static org.motechproject.metrics.config.MetricsConfigFacade.CONSOLE_REPORTER_REPORTING_FREQUENCY_UNIT;
import static org.motechproject.metrics.config.MetricsConfigFacade.GRAPHITE_REPORTER_ENABLED;
import static org.motechproject.metrics.config.MetricsConfigFacade.GRAPHITE_REPORTER_GRAPHITE_SERVER_URI;
import static org.motechproject.metrics.config.MetricsConfigFacade.GRAPHITE_REPORTER_GRAPHITE_SERVER_PORT;
import static org.motechproject.metrics.config.MetricsConfigFacade.GRAPHITE_REPORTER_CONVERT_RATES_UNIT;
import static org.motechproject.metrics.config.MetricsConfigFacade.GRAPHITE_REPORTER_CONVERT_DURATIONS_UNIT;
import static org.motechproject.metrics.config.MetricsConfigFacade.GRAPHITE_REPORTER_REPORTING_FREQUENCY_VALUE;
import static org.motechproject.metrics.config.MetricsConfigFacade.GRAPHITE_REPORTER_REPORTING_FREQUENCY_UNIT;

@RunWith(MockitoJUnitRunner.class)
public class MetricsConfigFacadeTest {
    @Mock
    private SettingsFacade settingsFacade;

    @Captor
    private ArgumentCaptor<Properties> propertiesCaptor;

    private MetricsConfigFacade metricsConfigFacade;

    @Before
    public void setUp() {
        metricsConfigFacade = new MetricsConfigFacade(settingsFacade);
    }

    @Test
    public void shouldGetMetricsConfig() {
        when(settingsFacade.getProperty(METRICS_ENABLED)).thenReturn("true");

        when(settingsFacade.getProperty(CONSOLE_REPORTER_ENABLED)).thenReturn("true");
        when(settingsFacade.getProperty(CONSOLE_REPORTER_REPORTING_FREQUENCY_VALUE)).thenReturn("1");
        when(settingsFacade.getProperty(CONSOLE_REPORTER_REPORTING_FREQUENCY_UNIT)).thenReturn("SECONDS");
        when(settingsFacade.getProperty(CONSOLE_REPORTER_CONVERT_RATES_UNIT)).thenReturn("MILLISECONDS");
        when(settingsFacade.getProperty(CONSOLE_REPORTER_CONVERT_DURATIONS_UNIT)).thenReturn("SECONDS");

        when(settingsFacade.getProperty(GRAPHITE_REPORTER_ENABLED)).thenReturn("true");
        when(settingsFacade.getProperty(GRAPHITE_REPORTER_GRAPHITE_SERVER_URI)).thenReturn("http://foo.com/graphite");
        when(settingsFacade.getProperty(GRAPHITE_REPORTER_GRAPHITE_SERVER_PORT)).thenReturn("2003");
        when(settingsFacade.getProperty(GRAPHITE_REPORTER_REPORTING_FREQUENCY_VALUE)).thenReturn("1");
        when(settingsFacade.getProperty(GRAPHITE_REPORTER_REPORTING_FREQUENCY_UNIT)).thenReturn("SECONDS");
        when(settingsFacade.getProperty(GRAPHITE_REPORTER_CONVERT_RATES_UNIT)).thenReturn("MILLISECONDS");
        when(settingsFacade.getProperty(GRAPHITE_REPORTER_CONVERT_DURATIONS_UNIT)).thenReturn("SECONDS");

        MetricsConfig config = metricsConfigFacade.getMetricsConfig();

        assertNotNull(config);
        assertEquals(config.isMetricsEnabled(), true);

        ConsoleReporterConfig crConfig = config.getConsoleReporterConfig();
        assertNotNull(crConfig);
        assertEquals(crConfig.isEnabled(), true);
        assertEquals(crConfig.getFrequency(), 1);
        assertEquals(crConfig.getFrequencyUnit(), TimeUnit.SECONDS);
        assertEquals(crConfig.getConvertRates(), TimeUnit.MILLISECONDS);
        assertEquals(crConfig.getConvertDurations(), TimeUnit.SECONDS);

        GraphiteReporterConfig grConfig = config.getGraphiteReporterConfig();
        assertNotNull(grConfig);
        assertEquals(grConfig.isEnabled(), true);
        assertEquals(grConfig.getServerUri(), "http://foo.com/graphite");
        assertEquals(grConfig.getServerPort(), 2003);
        assertEquals(grConfig.getFrequency(), 1);
        assertEquals(grConfig.getFrequencyUnit(), TimeUnit.SECONDS);
        assertEquals(grConfig.getConvertRates(), TimeUnit.MILLISECONDS);
        assertEquals(grConfig.getConvertDurations(), TimeUnit.SECONDS);
    }

    @Test
    public void shouldGetMetricsEnabled() {
        when(settingsFacade.getProperty(METRICS_ENABLED)).thenReturn("true");

        assertTrue(metricsConfigFacade.isMetricsEnabled());
    }

    @Test
    public void shouldGetMetricsDisabled() {
        when(settingsFacade.getProperty(METRICS_ENABLED)).thenReturn("false");

        assertFalse(metricsConfigFacade.isMetricsEnabled());
    }

    @Test
    public void shouldSaveNewConfig() {
        when(settingsFacade.asProperties()).thenReturn(new Properties());

        MetricsConfig config = ConfigUtil.getDefaultConfig();
        metricsConfigFacade.saveMetricsConfig(config);

        verify(settingsFacade).saveConfigProperties(any(), propertiesCaptor.capture());

        Properties properties = propertiesCaptor.getValue();

        assertNotNull(properties);

        assertEquals(properties.getProperty(METRICS_ENABLED), "true");

        assertEquals(properties.getProperty(CONSOLE_REPORTER_ENABLED), "true");
        assertEquals(properties.getProperty(CONSOLE_REPORTER_REPORTING_FREQUENCY_VALUE), "1");
        assertEquals(properties.getProperty(CONSOLE_REPORTER_REPORTING_FREQUENCY_UNIT), "SECONDS");
        assertEquals(properties.getProperty(CONSOLE_REPORTER_CONVERT_RATES_UNIT), "MILLISECONDS");
        assertEquals(properties.getProperty(CONSOLE_REPORTER_CONVERT_DURATIONS_UNIT), "SECONDS");

        assertEquals(properties.getProperty(GRAPHITE_REPORTER_ENABLED), "true");
        assertEquals(properties.getProperty(GRAPHITE_REPORTER_GRAPHITE_SERVER_URI), "http://foo.com/graphite");
        assertEquals(properties.getProperty(GRAPHITE_REPORTER_GRAPHITE_SERVER_PORT), "2003");
        assertEquals(properties.getProperty(GRAPHITE_REPORTER_REPORTING_FREQUENCY_VALUE), "1");
        assertEquals(properties.getProperty(GRAPHITE_REPORTER_REPORTING_FREQUENCY_UNIT), "SECONDS");
        assertEquals(properties.getProperty(GRAPHITE_REPORTER_CONVERT_RATES_UNIT), "MILLISECONDS");
        assertEquals(properties.getProperty(GRAPHITE_REPORTER_CONVERT_DURATIONS_UNIT), "SECONDS");
    }

}
