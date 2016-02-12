package org.motechproject.metrics.config;

import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Wraps the module's SettingsFacade and facilitates saving and loading configuration to and from Java objects
 * and the properties file.
 */
@Component
public class MetricsConfigFacade {
    private final SettingsFacade settingsFacade;

    private static final String CONFIG_FILE_NAME = "metrics.properties";

    public static final String METRICS_ENABLED = "metrics.enabled";

    public static final String CONSOLE_REPORTER_ENABLED = "reporter.console.enabled";
    public static final String CONSOLE_REPORTER_CONVERT_RATES_UNIT = "reporter.console.convertRatesUnit";
    public static final String CONSOLE_REPORTER_CONVERT_DURATIONS_UNIT = "reporter.console.convertDurationsUnit";
    public static final String CONSOLE_REPORTER_REPORTING_FREQUENCY_VALUE = "reporter.console.reportingFrequency.value";
    public static final String CONSOLE_REPORTER_REPORTING_FREQUENCY_UNIT = "reporter.console.reportingFrequency.unit";

    public static final String GRAPHITE_REPORTER_ENABLED = "reporter.graphite.enabled";
    public static final String GRAPHITE_REPORTER_GRAPHITE_SERVER_URI = "reporter.graphite.server.uri";
    public static final String GRAPHITE_REPORTER_GRAPHITE_SERVER_PORT = "reporter.graphite.server.port";
    public static final String GRAPHITE_REPORTER_CONVERT_RATES_UNIT = "reporter.graphite.convertRatesUnit";
    public static final String GRAPHITE_REPORTER_CONVERT_DURATIONS_UNIT = "reporter.graphite.convertDurationsUnit";
    public static final String GRAPHITE_REPORTER_REPORTING_FREQUENCY_VALUE = "reporter.graphite.reportingFrequency.value";
    public static final String GRAPHITE_REPORTER_REPORTING_FREQUENCY_UNIT = "reporter.graphite.reportingFrequency.unit";

    @Autowired
    public MetricsConfigFacade(@Qualifier("metricsSettings") SettingsFacade settingsFacade) {
        this.settingsFacade = settingsFacade;
    }

    /**
     * Returns whether or not the Metrics module is currently enabled.
     * @return true if the module is enabled, otherwise false.
     */
    public boolean isMetricsEnabled() {
        return Boolean.valueOf(settingsFacade.getProperty(METRICS_ENABLED));
    }

    /**
     * Loads a MetricsConfig object from the settings indicated in the properties file.
     * @return the metrics configuration
     */
    public MetricsConfig getMetricsConfig() {
        MetricsConfig config = new MetricsConfig();

        config.setMetricsEnabled(isMetricsEnabled());
        config.setConsoleReporterConfig(getConsoleReporterConfig());
        config.setGraphiteReporterConfig(getGraphiteReporterConfig());

        return config;
    }

    /**
     * Saves settings from a MetricsConfig object to the properties file.
     * @param config the module configuration
     */
    public void saveMetricsConfig(MetricsConfig config) {
        Properties properties = settingsFacade.asProperties();

        properties.setProperty(METRICS_ENABLED, Boolean.toString(config.isMetricsEnabled()));

        setConsoleReporterProperties(properties, config.getConsoleReporterConfig());
        setGraphiteReporterProperties(properties, config.getGraphiteReporterConfig());

        settingsFacade.saveConfigProperties(CONFIG_FILE_NAME, properties);
    }

    private ConsoleReporterConfig getConsoleReporterConfig() {
        ConsoleReporterConfig config = new ConsoleReporterConfig();

        config.setEnabled(Boolean.valueOf(getPropertyValue(CONSOLE_REPORTER_ENABLED)));
        config.setConvertRates(TimeUnit.valueOf(getPropertyValue(CONSOLE_REPORTER_CONVERT_RATES_UNIT)));
        config.setConvertDurations(TimeUnit.valueOf(getPropertyValue(CONSOLE_REPORTER_CONVERT_DURATIONS_UNIT)));
        config.setFrequency(Integer.valueOf(getPropertyValue(CONSOLE_REPORTER_REPORTING_FREQUENCY_VALUE)));
        config.setFrequencyUnit(TimeUnit.valueOf(getPropertyValue(CONSOLE_REPORTER_REPORTING_FREQUENCY_UNIT)));

        return config;
    }

    private GraphiteReporterConfig getGraphiteReporterConfig() {
        GraphiteReporterConfig config = new GraphiteReporterConfig();

        config.setEnabled(Boolean.valueOf(getPropertyValue(GRAPHITE_REPORTER_ENABLED)));
        config.setServerUri(getPropertyValue(GRAPHITE_REPORTER_GRAPHITE_SERVER_URI));
        config.setServerPort(Integer.valueOf(getPropertyValue(GRAPHITE_REPORTER_GRAPHITE_SERVER_PORT)));
        config.setConvertRates(TimeUnit.valueOf(getPropertyValue(GRAPHITE_REPORTER_CONVERT_RATES_UNIT)));
        config.setConvertDurations(TimeUnit.valueOf(getPropertyValue(GRAPHITE_REPORTER_CONVERT_DURATIONS_UNIT)));
        config.setFrequency(Integer.valueOf(getPropertyValue(GRAPHITE_REPORTER_REPORTING_FREQUENCY_VALUE)));
        config.setFrequencyUnit(TimeUnit.valueOf(getPropertyValue(GRAPHITE_REPORTER_REPORTING_FREQUENCY_UNIT)));

        return config;
    }

    private void setConsoleReporterProperties(Properties properties, ConsoleReporterConfig config) {
        properties.setProperty(CONSOLE_REPORTER_ENABLED, Boolean.toString(config.isEnabled()));
        properties.setProperty(CONSOLE_REPORTER_CONVERT_RATES_UNIT, config.getConvertRates().toString());
        properties.setProperty(CONSOLE_REPORTER_CONVERT_DURATIONS_UNIT, config.getConvertDurations().toString());
        properties.setProperty(CONSOLE_REPORTER_REPORTING_FREQUENCY_VALUE, Integer.toString(config.getFrequency()));
        properties.setProperty(CONSOLE_REPORTER_REPORTING_FREQUENCY_UNIT, config.getFrequencyUnit().toString());
    }

    private void setGraphiteReporterProperties(Properties properties, GraphiteReporterConfig config) {
        properties.setProperty(GRAPHITE_REPORTER_ENABLED, Boolean.toString(config.isEnabled()));
        properties.setProperty(GRAPHITE_REPORTER_GRAPHITE_SERVER_URI, config.getServerUri());
        properties.setProperty(GRAPHITE_REPORTER_GRAPHITE_SERVER_PORT, Integer.toString(config.getServerPort()));
        properties.setProperty(GRAPHITE_REPORTER_CONVERT_RATES_UNIT, config.getConvertRates().toString());
        properties.setProperty(GRAPHITE_REPORTER_CONVERT_DURATIONS_UNIT, config.getConvertDurations().toString());
        properties.setProperty(GRAPHITE_REPORTER_REPORTING_FREQUENCY_VALUE, Integer.toString(config.getFrequency()));
        properties.setProperty(GRAPHITE_REPORTER_REPORTING_FREQUENCY_UNIT, config.getFrequencyUnit().toString());
    }

    private String getPropertyValue(final String propertyKey) {
        String propertyValue = settingsFacade.getProperty(propertyKey);
        return isNotBlank(propertyValue) ? propertyValue : null;
    }

}
