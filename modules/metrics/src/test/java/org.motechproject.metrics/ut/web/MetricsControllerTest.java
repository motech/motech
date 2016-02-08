package org.motechproject.metrics.ut.web;

import com.codahale.metrics.Counter;
import com.codahale.metrics.ExponentiallyDecayingReservoir;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.metrics.service.MetricRegistryService;
import org.motechproject.metrics.web.MetricDtoToSupplierConverter;
import org.motechproject.metrics.web.MetricsController;
import org.motechproject.metrics.web.dto.MetricDto;
import org.motechproject.metrics.web.dto.MetricType;
import org.motechproject.metrics.web.dto.MetricsDto;
import org.motechproject.metrics.web.dto.RatioGaugeDto;
import org.motechproject.metrics.web.dto.RatioGaugeValue;
import org.springframework.http.MediaType;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class MetricsControllerTest {
    @Mock
    private MetricRegistry metricRegistry;

    @Mock
    private MetricRegistryService metricRegistryService;

    @Mock
    private MetricDtoToSupplierConverter converter;

    @Captor
    private ArgumentCaptor<String> nameCaptor;

    @Captor
    private ArgumentCaptor<MetricDto> metricDtoCaptor;

    private MockMvc controller;

    @Before
    public void setUp() {
        controller = MockMvcBuilders.standaloneSetup(new MetricsController(metricRegistry, metricRegistryService, converter)).build();
    }

    @Test
    public void shouldReturnMetrics() throws Exception {
        List<MetricsDto> expected = generateMetrics();

        when(metricRegistry.getCounters()).thenReturn(getCounters());
        when(metricRegistry.getGauges()).thenReturn(getGauges());
        when(metricRegistry.getHistograms()).thenReturn(getHistograms());
        when(metricRegistry.getMeters()).thenReturn(getMeters());
        when(metricRegistry.getTimers()).thenReturn(getTimers());

        controller.perform(get("/metrics"))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(expected)));
    }

    @Test
    public void shouldDeserializeRatioGaugeDto() throws Exception {
        String ratioGaugeName = "ratioGaugeName";
        RatioGaugeDto ratioGauge = new RatioGaugeDto();
        ratioGauge.setName(ratioGaugeName);

        MetricDto numerator = new MetricDto();
        numerator.setName("numeratorName");
        numerator.setType(MetricType.COUNTER);
        numerator.setValue(RatioGaugeValue.COUNT);

        MetricDto denominator = new MetricDto();
        denominator.setName("denominatorName");
        denominator.setType(MetricType.METER);
        denominator.setValue(RatioGaugeValue.COUNT);

        ratioGauge.setNumerator(numerator);
        ratioGauge.setDenominator(denominator);

        controller.perform(post("/metrics/ratioGauge")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ObjectMapper().writeValueAsBytes(ratioGauge)))
                .andExpect(status().isOk());

        verify(metricRegistryService).registerRatioGauge(nameCaptor.capture(), any(), any());
        assertEquals(ratioGaugeName, nameCaptor.getValue());

        verify(converter, times(2)).convert(metricDtoCaptor.capture());
        List<MetricDto> metricDtos = metricDtoCaptor.getAllValues();

        assertEquals("numeratorName", metricDtos.get(0).getName());
        assertEquals(MetricType.COUNTER, metricDtos.get(0).getType());
        assertEquals(RatioGaugeValue.COUNT, metricDtos.get(0).getValue());

        assertEquals("denominatorName", metricDtos.get(1).getName());
        assertEquals(MetricType.METER, metricDtos.get(1).getType());
        assertEquals(RatioGaugeValue.COUNT, metricDtos.get(1).getValue());
    }

    private SortedMap<String, Counter> getCounters() {
        SortedMap<String, Counter> counters = new TreeMap<>();
        counters.put("counterA", new Counter());
        counters.put("counterB", new Counter());
        return counters;
    }

    private SortedMap<String, Gauge> getGauges() {
        SortedMap<String, Gauge> gauges = new TreeMap<>();
        gauges.put("gaugeA", () -> null);
        gauges.put("gaugeB", () -> null);
        return gauges;
    }

    private SortedMap<String, Histogram> getHistograms() {
        SortedMap<String, Histogram> histograms = new TreeMap<>();
        histograms.put("histogramA", new Histogram(new ExponentiallyDecayingReservoir()));
        histograms.put("histogramB", new Histogram(new ExponentiallyDecayingReservoir()));
        return histograms;
    }

    private SortedMap<String, Meter> getMeters() {
        SortedMap<String, Meter> meters = new TreeMap<>();
        meters.put("meterA", new Meter());
        meters.put("meterB", new Meter());
        return meters;
    }

    private SortedMap<String, Timer> getTimers() {
        SortedMap<String, Timer> timers = new TreeMap<>();
        timers.put("timerA", new Timer());
        timers.put("timerB", new Timer());
        return timers;
    }

    private List<MetricsDto> generateMetrics() {
        List<MetricsDto> metrics = new ArrayList<>();

        metrics.add(new MetricsDto(
                MetricType.COUNTER,
                new TreeSet<String>(asList("counterA", "counterB")),
                new RatioGaugeValue[]{RatioGaugeValue.COUNT}));

        metrics.add(new MetricsDto(
                MetricType.GAUGE,
                new TreeSet<String>(asList("gaugeA", "gaugeB")),
                RatioGaugeValue.values()));

        metrics.add(new MetricsDto(
                MetricType.HISTOGRAM,
                new TreeSet<String>(asList("histogramA", "histogramB")),
                RatioGaugeValue.values()));

        metrics.add(new MetricsDto(
                MetricType.METER,
                new TreeSet<String>(asList("meterA", "meterB")),
                RatioGaugeValue.values()));

        metrics.add(new MetricsDto(
                MetricType.TIMER,
                new TreeSet<String>(asList("timerA", "timerB")),
                RatioGaugeValue.values()));

        return metrics;
    }

}
