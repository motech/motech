package org.motechproject.metrics.web;

import com.codahale.metrics.MetricRegistry;
import org.motechproject.metrics.web.dto.MetricsDto;
import org.motechproject.metrics.web.dto.MetricType;
import org.motechproject.metrics.web.dto.RatioGaugeDto;
import org.motechproject.metrics.service.MetricRegistryService;
import org.motechproject.metrics.web.dto.RatioGaugeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static org.motechproject.metrics.security.Roles.HAS_MANAGE_METRICS_ROLE;
import static org.motechproject.metrics.security.Roles.HAS_VIEW_METRICS_ROLE;

/**
 * Sends details about the currently registered metrics to the user interface.
 */
@Controller
public class MetricsController {
    private MetricRegistry metricRegistry;
    private MetricRegistryService metricRegistryService;
    private MetricDtoToSupplierConverter converter;

    public MetricsController() {}

    @Autowired
    public MetricsController(MetricRegistry metricRegistry,
                             MetricRegistryService metricRegistryService,
                             MetricDtoToSupplierConverter converter) {
        this.metricRegistry = metricRegistry;
        this.metricRegistryService = metricRegistryService;
        this.converter = converter;
    }

    /**
     * Returns a list of objects (one object per metric type), with the registered names of that metric type and values
     * by which a ratio gauge could be derived.
     * i.e.
     * [
     *  {
     *    "type":"COUNTER",
     *    "names":["org.foo.module.counter1", "org.foo.module.counter2..."],
     *    "values":["COUNT"]
     *  },
     *  ...
     * ]
     * @return a detail of the currently registered metrics
     */
    @RequestMapping(value = "/metrics", method = RequestMethod.GET)
    @ResponseBody
    @PreAuthorize(HAS_VIEW_METRICS_ROLE)
    public List<MetricsDto> getMetrics() {
        List<MetricsDto> ret = new ArrayList<>();

        ret.add(new MetricsDto(MetricType.COUNTER, new TreeSet<>(metricRegistry.getCounters().keySet()), new RatioGaugeValue[]{RatioGaugeValue.COUNT}));
        ret.add(new MetricsDto(MetricType.GAUGE, new TreeSet<>(metricRegistry.getGauges().keySet()), RatioGaugeValue.values()));
        ret.add(new MetricsDto(MetricType.HISTOGRAM, new TreeSet<>(metricRegistry.getHistograms().keySet()), RatioGaugeValue.values()));
        ret.add(new MetricsDto(MetricType.METER, new TreeSet<>(metricRegistry.getMeters().keySet()), RatioGaugeValue.values()));
        ret.add(new MetricsDto(MetricType.TIMER, new TreeSet<>(metricRegistry.getTimers().keySet()), RatioGaugeValue.values()));

        return ret;
    }

    /**
     * Creates a ratio gauge from a ratio gauge dto
     *
     * @param dto the ratio gauge dto
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/metrics/ratioGauge", method = RequestMethod.POST)
    @PreAuthorize(HAS_MANAGE_METRICS_ROLE)
    public void createRatioGauge(@RequestBody RatioGaugeDto dto) {
        metricRegistryService.registerRatioGauge(dto.getName(),
                converter.convert(dto.getNumerator()),
                converter.convert(dto.getDenominator()));
    }
}

