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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.motechproject.commons.date.util.DateUtil.now;

public class EventAggregationBundleIT extends BaseOsgiIT {

    public void testEventAggregationScervice() throws InterruptedException {

        final Object waitLock = new Object();
        final List<List<AggregatedEvent>> aggregation = new ArrayList<>();
        final int[] receivedEvents = {0};
        final int totalEvents = 3;

        ServiceReference eventListenerRegistryReference = bundleContext.getServiceReference(EventListenerRegistryService.class.getName());
        EventListenerRegistry eventListenerRegistry = (EventListenerRegistry) bundleContext.getService(eventListenerRegistryReference);
        eventListenerRegistry.registerListener(new EventListener() {

            @Override
            public void handle(MotechEvent event) {
                List<AggregatedEvent> aggregatedEvents = (List<AggregatedEvent>) event.getParameters().get("aggregated_events");
                aggregation.add(aggregatedEvents);
                receivedEvents[0] += aggregatedEvents.size();
                System.out.println("got " + aggregatedEvents.size() + " events at " + new Date());
                if (receivedEvents[0] >= totalEvents) {
                    synchronized (waitLock) {
                        waitLock.notify();
                    }
                }
            }

            @Override
            public String getIdentifier() {
                return "test";
            }
        }, "agg");

        ServiceReference schedulerServiceReference = bundleContext.getServiceReference(MotechSchedulerService.class.getName());
        MotechSchedulerService schedulerService = (MotechSchedulerService) bundleContext.getService(schedulerServiceReference);
        assertNotNull(schedulerService);

        ServiceReference eventAggregationServiceReference = bundleContext.getServiceReference(EventAggregationService.class.getName());
        EventAggregationService eventAggregationService = (EventAggregationService) bundleContext.getService(eventAggregationServiceReference);

        eventAggregationService.createRule(
            new AggregationRuleRequest("test_aggregation", null, "eve", asList("foo"), new PeriodicAggregationRequest("5 seconds", now()), "agg", AggregationState.Running));

        Map<String, Object> params = new HashMap<>();
        params.put("foo", "bar");
        params.put("fuu", "baz");
        schedulerService.safeScheduleRepeatingJob(
            new RepeatingSchedulableJob(new MotechEvent("eve", params), now().toDate(), null, totalEvents - 1, 6000L, false));

        synchronized (waitLock) {
            waitLock.wait(60000);
        }
        assertEquals(3, aggregation.size());
        assertEquals(1, aggregation.get(0).size());
        assertEquals(1, aggregation.get(1).size());
        assertEquals(1, aggregation.get(2).size());
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
