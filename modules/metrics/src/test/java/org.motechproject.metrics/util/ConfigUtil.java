package org.motechproject.metrics.util;

import org.motechproject.metrics.config.ConsoleReporterConfig;
import org.motechproject.metrics.config.GraphiteReporterConfig;
import org.motechproject.metrics.config.MetricsConfig;

import java.util.concurrent.TimeUnit;

public class ConfigUtil {
    public static MetricsConfig getDefaultConfig() {
        MetricsConfig config = new MetricsConfig();
        config.setMetricsEnabled(true);
        config.setConsoleReporterConfig(generateConsoleReporterConfig());
        config.setGraphiteReporterConfig(generateGraphiteReporterConfig());
        return config;
    }

    private static ConsoleReporterConfig generateConsoleReporterConfig() {
        ConsoleReporterConfig config = new ConsoleReporterConfig();
        config.setEnabled(true);
        config.setFrequency(1);
        config.setFrequencyUnit(TimeUnit.SECONDS);
        config.setConvertRates(TimeUnit.MILLISECONDS);
        config.setConvertDurations(TimeUnit.SECONDS);
        return config;
    }

    private static GraphiteReporterConfig generateGraphiteReporterConfig() {
        GraphiteReporterConfig config = new GraphiteReporterConfig();
        config.setEnabled(true);
        config.setServerUri("http://foo.com/graphite");
        config.setServerPort(2003);
        config.setFrequency(1);
        config.setFrequencyUnit(TimeUnit.SECONDS);
        config.setConvertRates(TimeUnit.MILLISECONDS);
        config.setConvertDurations(TimeUnit.SECONDS);
        return config;
    }
}
