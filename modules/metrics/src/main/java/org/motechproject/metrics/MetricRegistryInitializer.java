package org.motechproject.metrics;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import org.motechproject.metrics.config.ConsoleReporterConfig;
import org.motechproject.metrics.config.GraphiteReporterConfig;
import org.motechproject.metrics.config.MetricsConfig;
import org.motechproject.metrics.config.MetricsConfigFacade;
import org.motechproject.metrics.service.MetricRegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

/**
 * Responsible for initializing the metric registry when the module loads or configuration details change.
 */
@Component
public class MetricRegistryInitializer {
    private final MetricRegistry metricRegistry;
    private final MetricRegistryService metricRegistryService;
    private final MetricsConfigFacade metricsConfigFacade;

    private ConsoleReporter consoleReporter;
    private GraphiteReporter graphiteReporter;
    private boolean defaultMetricsRegistered = false;

    private static final String JVM_GARBAGE_COLLECTOR_METRICS = "jvm.gc";
    private static final String JVM_MEMORY_METRICS = "jvm.memory";
    private static final String JVM_THREAD_METRICS = "jvm.threads";
    private static final String JVM_FILE_DESCRIPTOR_METRICS = "jvm.files";

    @Autowired
    public MetricRegistryInitializer(MetricRegistry metricRegistry,
                                     MetricRegistryService metricRegistryService,
                                     MetricsConfigFacade metricsConfigFacade) {
        this.metricRegistry = metricRegistry;
        this.metricRegistryService = metricRegistryService;
        this.metricsConfigFacade = metricsConfigFacade;
    }

    @PostConstruct
    public void init() {
        // register the default metrics only when the module first loads
        if (!defaultMetricsRegistered) {
            registerDefaultMetrics();
            defaultMetricsRegistered = true;
        }

        stopReporters();

        MetricsConfig metricsConfig = metricsConfigFacade.getMetricsConfig();

        metricRegistryService.setEnabled(metricsConfig.isMetricsEnabled());

        if (metricsConfig.isMetricsEnabled()) {
            configureConsoleReporter(metricsConfig.getConsoleReporterConfig());
            configureGraphiteReporter(metricsConfig.getGraphiteReporterConfig());
        }
    }

    /**
     * Initializes the console reporter based on the current configuration.
     *
     * @param config the console reporter configuration
     */
    private void configureConsoleReporter(ConsoleReporterConfig config) {
        if (config.isEnabled()) {
            consoleReporter = ConsoleReporter.forRegistry(metricRegistry)
                    .convertRatesTo(config.getConvertRates())
                    .convertDurationsTo(config.getConvertDurations())
                    .build();
            consoleReporter.start(config.getFrequency(),
                    config.getFrequencyUnit());
        }
    }

    /**
     * Initializes the Graphite reporter based on the current configuration.
     *
     * @param config the graphite reporter configuration
     */
    private void configureGraphiteReporter(GraphiteReporterConfig config) {
        if (config.isEnabled()) {
            Graphite graphite = new Graphite(new InetSocketAddress(
                    config.getServerUri(),
                    config.getServerPort()));

            graphiteReporter = GraphiteReporter.forRegistry(metricRegistry)
                    .prefixedWith("motech")
                    .convertRatesTo(config.getConvertRates())
                    .convertDurationsTo(config.getConvertDurations())
                    .filter(MetricFilter.ALL)
                    .build(graphite);

            graphiteReporter.start(
                    config.getFrequency(),
                    config.getFrequencyUnit());
        }
    }

    /**
     * Registers a default set of metrics for the JVM.
     */
    private void registerDefaultMetrics() {
        metricRegistry.register(JVM_GARBAGE_COLLECTOR_METRICS, new GarbageCollectorMetricSet());
        metricRegistry.register(JVM_MEMORY_METRICS, new MemoryUsageGaugeSet());
        metricRegistry.register(JVM_THREAD_METRICS, new ThreadStatesGaugeSet());
        metricRegistry.register(JVM_FILE_DESCRIPTOR_METRICS, new FileDescriptorRatioGauge());
    }

    /**
     * Stop the reporter threads.
     */
    @PreDestroy
    public void stopReporters() {
        if (consoleReporter != null) {
            consoleReporter.stop();
        }
        if (graphiteReporter != null) {
            graphiteReporter.stop();
        }
    }
}
