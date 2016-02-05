package org.motechproject.metrics.event;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.metrics.api.Counter;
import org.motechproject.metrics.service.MetricRegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Handles the modification of metrics through the event bus or Tasks interface.
 */
@Service
public class MotechEventHandler {
    private final MetricRegistryService metricRegistryService;

    /**
     * Responds to increment or decrement counter events by creating/loading the counter by name and incrementing or
     * decrementing accordingly.
     *
     * @param event payload with parameters that give the name of the counter and value by which to increment or decrement
     */
    @MotechListener(subjects = {EventSubjects.INCREMENT_COUNTER, EventSubjects.DECREMENT_COUNTER})
    public void incrementCounter(MotechEvent event) {
        String name = event.getParameters().get(EventParams.METRIC_NAME).toString();
        long value = (long) event.getParameters().get(EventParams.METRIC_VALUE);

        Counter counter = metricRegistryService.counter(name);

        if (EventSubjects.INCREMENT_COUNTER.equals(event.getSubject())) {
            counter.inc(value);
        } else {
            counter.dec(value);
        }
    }

    /**
     * Responds to mark meter events by creating/loading the meter by name and recording the provided value.
     *
     * @param event payload with parameters that give the name of the meter and value to record
     */
    @MotechListener(subjects = EventSubjects.MARK_METER)
    public void markMeter(MotechEvent event) {
        String name = event.getParameters().get(EventParams.METRIC_NAME).toString();
        long value = (long) event.getParameters().get(EventParams.METRIC_VALUE);

        metricRegistryService.meter(name).mark(value);
    }

    /**
     * Responds to update histogram events by creating/loading the histogram by name and recording the provided value.
     *
     * @param event payload the parameters that give the name of the histogram and value to record
     */
    @MotechListener(subjects = EventSubjects.UPDATE_HISTOGRAM)
    public void updateHistogram(MotechEvent event) {
        String name = event.getParameters().get(EventParams.METRIC_NAME).toString();
        long value = (long) event.getParameters().get(EventParams.METRIC_VALUE);

        metricRegistryService.histogram(name).update(value);
    }

    @Autowired
    public MotechEventHandler(MetricRegistryService metricRegistryService) {
        this.metricRegistryService = metricRegistryService;
    }
}
