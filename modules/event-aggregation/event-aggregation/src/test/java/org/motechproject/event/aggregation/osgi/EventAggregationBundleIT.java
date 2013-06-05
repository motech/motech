package org.motechproject.event.aggregation.osgi;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.aggregation.model.AggregationState;
import org.motechproject.event.aggregation.service.AggregatedEvent;
import org.motechproject.event.aggregation.service.AggregationRuleRequest;
import org.motechproject.event.aggregation.service.EventAggregationService;
import org.motechproject.event.aggregation.service.PeriodicAggregationRequest;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistry;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.RepeatingSchedulableJob;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.motechproject.commons.date.util.DateUtil.now;

public class EventAggregationBundleIT extends BaseOsgiIT {

    public void testEventAggregationScervice() throws InterruptedException {

        final List<AggregatedEvent> aggregatedEvents = new ArrayList<>();
        final int totalEvents = 3;

        ServiceReference eventListenerRegistryReference = bundleContext.getServiceReference(EventListenerRegistryService.class.getName());
        EventListenerRegistry eventListenerRegistry = (EventListenerRegistry) bundleContext.getService(eventListenerRegistryReference);
        String aggregationEvent = id("agg");
        eventListenerRegistry.registerListener(new EventListener() {
            @Override
            public void handle(MotechEvent event) {
                synchronized (aggregatedEvents) {
                    aggregatedEvents.addAll((List<AggregatedEvent>) event.getParameters().get("aggregated_events"));
                    if (aggregatedEvents.size() >= totalEvents) {
                        aggregatedEvents.notify();
                    }
                }
            }

            @Override
            public String getIdentifier() {
                return "test";
            }
        }, aggregationEvent);

        ServiceReference schedulerServiceReference = bundleContext.getServiceReference(MotechSchedulerService.class.getName());
        MotechSchedulerService schedulerService = (MotechSchedulerService) bundleContext.getService(schedulerServiceReference);
        assertNotNull(schedulerService);

        ServiceReference eventAggregationServiceReference = bundleContext.getServiceReference(EventAggregationService.class.getName());
        EventAggregationService eventAggregationService = (EventAggregationService) bundleContext.getService(eventAggregationServiceReference);
        assertNotNull(eventAggregationService);

        String eventSubject = id("testEvent");
        eventAggregationService.createRule(
            new AggregationRuleRequest("test_aggregation", "test aggregation subscription",
                eventSubject, asList("foo"),
                new PeriodicAggregationRequest("2 seconds", now()),
                aggregationEvent, AggregationState.Running));

        Map<String, Object> params = new HashMap<>();
        params.put("foo", "bar");
        params.put("fuu", "baz");
        params.put(MotechSchedulerService.JOB_ID_KEY, id("simulatedEventJob"));
        final MotechEvent motechEvent = new MotechEvent(eventSubject, params);
        schedulerService.safeScheduleRepeatingJob(
                new RepeatingSchedulableJob(motechEvent, now().plusSeconds(2).toDate(), null, totalEvents - 1, 1000L, false));

        synchronized (aggregatedEvents) {
            aggregatedEvents.wait(30000);
        }
        assertEquals(totalEvents, aggregatedEvents.size());
    }

    private String id(String s) {
        return format("%s_%d", now().getMillis(), s);
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"testEventAggregationBundleContext.xml"};
    }

    @Override
    protected List<String> getImports() {
        return asList("org.motechproject.event.aggregation.model");
    }
}
