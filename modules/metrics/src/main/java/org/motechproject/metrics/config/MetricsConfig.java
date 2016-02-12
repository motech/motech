package org.motechproject.metrics.config;

/**
 * Represents the complete configuration of the Metrics module.
 */
public class MetricsConfig {
    /**
     * Whether or not the Metrics module is enabled, i.e. recording new values and streaming collected metrics.
     */
    private boolean metricsEnabled;

    /**
     * The console reporter configuration.
     */
    private ConsoleReporterConfig consoleReporterConfig;

    /**
     * The Graphite reporter configuration.
     */
    private GraphiteReporterConfig graphiteReporterConfig;

    public boolean isMetricsEnabled() {
        return metricsEnabled;
    }

    public void setMetricsEnabled(boolean metricsEnabled) {
        this.metricsEnabled = metricsEnabled;
    }

    public ConsoleReporterConfig getConsoleReporterConfig() {
        return consoleReporterConfig;
    }

    public void setConsoleReporterConfig(ConsoleReporterConfig consoleReporterConfig) {
        this.consoleReporterConfig = consoleReporterConfig;
    }

    public GraphiteReporterConfig getGraphiteReporterConfig() {
        return graphiteReporterConfig;
    }

    public void setGraphiteReporterConfig(GraphiteReporterConfig graphiteReporterConfig) {
        this.graphiteReporterConfig = graphiteReporterConfig;
    }
}
