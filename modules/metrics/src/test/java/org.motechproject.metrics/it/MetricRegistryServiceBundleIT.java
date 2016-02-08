package org.motechproject.metrics.it;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.metrics.api.Counter;
import org.motechproject.metrics.api.Gauge;
import org.motechproject.metrics.api.Histogram;
import org.motechproject.metrics.api.Meter;
import org.motechproject.metrics.api.Timer;
import org.motechproject.metrics.service.MetricRegistryService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class MetricRegistryServiceBundleIT extends BasePaxIT {
    @Inject
    private MetricRegistryService metricRegistryService;

    @Test
    public void shouldRegisterMetricRegistryServiceInstance() throws Exception {
        assertNotNull(metricRegistryService);
    }

    @Test
    public void shouldCreateCounter() {
        Counter counter = metricRegistryService.counter("counter");
        Counter copy = metricRegistryService.counter("counter");
        Counter counter2 = metricRegistryService.counter("counter2");

        assertNotNull(counter);
        assertNotNull(copy);
        assertEquals(counter, copy);
        assertNotSame(counter, counter2);
    }

    @Test
    public void shouldCreateHistogram() {
        Histogram histogram = metricRegistryService.histogram("histogram");
        Histogram copy = metricRegistryService.histogram("histogram");
        Histogram histogram2 = metricRegistryService.histogram("histogram2");

        assertNotNull(histogram);
        assertNotNull(copy);
        assertEquals(histogram, copy);
        assertNotSame(histogram, histogram2);
    }

    @Test
    public void shouldCreateMeter() {
        Meter meter = metricRegistryService.meter("meter");
        Meter copy = metricRegistryService.meter("meter");
        Meter meter2 = metricRegistryService.meter("meter2");

        assertNotNull(meter);
        assertNotNull(copy);
        assertEquals(meter, copy);
        assertNotSame(meter, meter2);
    }

    @Test
    public void shouldCreateTimer() {
        Timer timer =  metricRegistryService.timer("timer");
        Timer copy = metricRegistryService.timer("timer");
        Timer timer2 = metricRegistryService.timer("timer2");

        assertNotNull(timer);
        assertNotNull(copy);
        assertEquals(timer, copy);
        assertNotSame(timer, timer2);
    }

    @Test
    public void shouldRegisterGauge() {
        Gauge<Double> gauge = metricRegistryService.registerGauge("gauge", new Gauge<Double>() {
            @Override
            public Double getValue() {
                return 1.0;
            }
        });
        assertNotNull(gauge);
    }

    @Test
    public void shouldRegisterRatioGauge() {
        Gauge<Double> ratioGauge = metricRegistryService.registerRatioGauge("ratioGauge",
                new Supplier<Double>() {
                    @Override
                    public Double get() {
                        return 1.0;
                    }
                },
                new Supplier<Double>(){
                    @Override
                    public Double get() {
                        return 2.0;
                    }
                });
        assertNotNull(ratioGauge);
    }

    @Test
    public void shouldIdentifyMetricAsRegistered() {
        metricRegistryService.counter("counter");
        assertTrue(metricRegistryService.isRegistered("counter"));
    }

    @Test
    public void shouldIdentifyMetricAsNotRegistered() {
        assertFalse(metricRegistryService.isRegistered("foo"));
    }

    @Test
    public void shouldEnableMetrics() {
        // metrics initialized to disabled
        Counter counter = metricRegistryService.counter("counter");
        Meter meter = metricRegistryService.meter("meter");
        Histogram histogram = metricRegistryService.histogram("histogram");
        Timer timer = metricRegistryService.timer("timer");

        counter.inc();
        meter.mark();
        histogram.update(1);
        timer.update(1, TimeUnit.SECONDS);

        assertEquals(0, counter.getCount());
        assertEquals(0, meter.getCount());
        assertEquals(0, histogram.getCount());
        assertEquals(0, timer.getCount());

        // enable the metrics and retest
        metricRegistryService.setEnabled(true);
        counter.inc();
        meter.mark();
        histogram.update(1);
        timer.update(1, TimeUnit.SECONDS);

        assertEquals(1, counter.getCount());
        assertEquals(1, meter.getCount());
        assertEquals(1, histogram.getCount());
        assertEquals(1, timer.getCount());

        // disable the metrics again and retest
        metricRegistryService.setEnabled(false);
        counter.inc();
        meter.mark();
        histogram.update(1);
        timer.update(1, TimeUnit.SECONDS);

        assertEquals(1, counter.getCount());
        assertEquals(1, meter.getCount());
        assertEquals(1, histogram.getCount());
        assertEquals(1, timer.getCount());
    }
}
