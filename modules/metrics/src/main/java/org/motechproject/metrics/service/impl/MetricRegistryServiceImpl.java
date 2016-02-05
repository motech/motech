package org.motechproject.metrics.service.impl;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.RatioGauge;
import org.motechproject.metrics.api.Counter;
import org.motechproject.metrics.api.Gauge;
import org.motechproject.metrics.api.Histogram;
import org.motechproject.metrics.api.Meter;
import org.motechproject.metrics.api.Metric;
import org.motechproject.metrics.api.Timer;
import org.motechproject.metrics.config.MetricsConfigFacade;
import org.motechproject.metrics.model.CounterAdapter;
import org.motechproject.metrics.model.Enablable;
import org.motechproject.metrics.model.GaugeAdapter;
import org.motechproject.metrics.model.HistogramAdapter;
import org.motechproject.metrics.model.MeterAdapter;
import org.motechproject.metrics.model.TimerAdapter;
import org.motechproject.metrics.service.MetricRegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Service("metricRegistryService")
public class MetricRegistryServiceImpl implements MetricRegistryService {
    private final MetricRegistry metricRegistry;
    private final MetricsConfigFacade metricsConfigFacade;

    private Map<String, Metric> metrics;

    @Autowired
    public MetricRegistryServiceImpl(MetricRegistry metricRegistry, MetricsConfigFacade metricsConfigFacade) {
        this.metricRegistry = metricRegistry;
        this.metricsConfigFacade = metricsConfigFacade;
        metrics = new ConcurrentHashMap<>();
    }

    @Override
    public void setEnabled(boolean enabled) {
        for (Metric metric: metrics.values()) {
            if (metric instanceof Enablable && ((Enablable) metric).isEnabled() != enabled) {
                ((Enablable) metric).setEnabled(enabled);
            }
        }
    }

    @Override
    public Counter counter(final String name) {
        return getOrAdd(name, MetricBuilder.COUNTERS);
    }

    @Override
    public Histogram histogram(final String name) {
        return getOrAdd(name, MetricBuilder.HISTOGRAMS);
    }

    @Override
    public Meter meter(final String name) {
        return getOrAdd(name, MetricBuilder.METERS);
    }

    @Override
    public Timer timer(final String name) {
        return getOrAdd(name, MetricBuilder.TIMERS);
    }

    @Override
    public <T> Gauge<T> registerGauge(final String name, final Gauge<T> gauge) {
        com.codahale.metrics.Gauge<T> theGauge;
        try {
            theGauge = metricRegistry.register(name, new com.codahale.metrics.Gauge<T>() {
                @Override
                public T getValue() {
                    return gauge.getValue();
                }
            });
        } catch (IllegalArgumentException ex) {
            throw ex;
        }
        return new GaugeAdapter<T>(theGauge);
    }

    @Override
    public <T extends Number> Gauge<Double> registerRatioGauge(final String name, Supplier<T> numerator, Supplier<T> denominator) {
        com.codahale.metrics.RatioGauge theGauge;
        try {
            theGauge = metricRegistry.register(name, new RatioGauge() {
                @Override
                protected Ratio getRatio() {
                    return Ratio.of(numerator.get().doubleValue(), denominator.get().doubleValue());
                }
            });
        } catch (IllegalArgumentException ex) {
            throw ex;
        }
        return new GaugeAdapter<>(theGauge);
    }

    @Override
    public boolean isRegistered(String name) {
        return metricRegistry.getNames().contains(name);
    }

    @SuppressWarnings("unchecked")
    private <T extends Metric> T getOrAdd(String name, MetricBuilder<T> builder) {
        Metric metric = metrics.get(name);

        if (metric == null) {
            try {
                T built = builder.createMetric(name, metricRegistry, metricsConfigFacade);
                metrics.put(name, built);
                return built;
            } catch (IllegalArgumentException ex) {
                throw ex;
            }
        } else if (builder.isInstance(metric)) {
            return (T) metric;
        } else {
            throw new IllegalArgumentException("Name already associated with metric of different type!");
        }
    }

    /**
     * Encapsulates the default method by which counters, histograms, meters, and timers are created and wrapped in the
     * appropriate adapter.
     *
     * @param <T> the type of metric
     */
    private interface MetricBuilder<T extends Metric> {
        MetricBuilder<Counter> COUNTERS = new MetricBuilder<Counter>() {
            @Override
            public Counter createMetric(String name, MetricRegistry registry, MetricsConfigFacade config) throws IllegalArgumentException {
                com.codahale.metrics.Counter counter = registry.counter(name);
                return new CounterAdapter(counter, config.isMetricsEnabled());
            }

            @Override
            public boolean isInstance(Metric metric) {
                return Counter.class.isInstance(metric);
            }
        };

        MetricBuilder<Histogram> HISTOGRAMS = new MetricBuilder<Histogram>() {
            @Override
            public Histogram createMetric(String name, MetricRegistry registry, MetricsConfigFacade config) throws IllegalArgumentException {
                com.codahale.metrics.Histogram histogram = registry.histogram(name);
                return new HistogramAdapter(histogram, config.isMetricsEnabled());
            }

            @Override
            public boolean isInstance(Metric metric) {
                return Histogram.class.isInstance(metric);
            }
        };

        MetricBuilder<Meter> METERS = new MetricBuilder<Meter>() {
            @Override
            public Meter createMetric(String name, MetricRegistry registry, MetricsConfigFacade config) throws IllegalArgumentException {
                com.codahale.metrics.Meter meter = registry.meter(name);
                return new MeterAdapter(meter, config.isMetricsEnabled());
            }

            @Override
            public boolean isInstance(Metric metric) {
                return Meter.class.isInstance(metric);
            }
        };

        MetricBuilder<Timer> TIMERS = new MetricBuilder<Timer>() {
            @Override
            public Timer createMetric(String name, MetricRegistry registry, MetricsConfigFacade config) throws IllegalArgumentException {
                com.codahale.metrics.Timer timer = registry.timer(name);
                return new TimerAdapter(timer, config.isMetricsEnabled());
            }

            @Override
            public boolean isInstance(Metric metric) {
                return Timer.class.isInstance(metric);
            }
        };

        /**
         * Create and return a a new metric.
         *
         * @param name the name to associate with the metric
         * @param registry the metric registry
         * @param config the module configuration
         * @return an implementation of a metric of the appropriate type
         * @throws IllegalArgumentException if the provided name is associated with a different type of metric
         */
        T createMetric(String name, MetricRegistry registry, MetricsConfigFacade config) throws IllegalArgumentException;

        /**
         * Test whether is the provided metric is the same type the instance of the builder is responsible for making.
         *
         * @param metric the metric to test
         *
         * @return true if the type of metric is the same type that the builder makes, false otherwise.
         */
        boolean isInstance(Metric metric);
    }
}
