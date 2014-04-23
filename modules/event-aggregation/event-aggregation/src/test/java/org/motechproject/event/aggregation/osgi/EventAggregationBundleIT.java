package org.motechproject.event.aggregation.osgi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.aggregation.model.event.AggregatedEvent;
import org.motechproject.event.aggregation.model.rule.AggregationRuleRequest;
import org.motechproject.event.aggregation.model.rule.AggregationState;
import org.motechproject.event.aggregation.model.schedule.PeriodicAggregationRequest;
import org.motechproject.event.aggregation.service.EventAggregationService;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.RepeatingSchedulableJob;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.motechproject.commons.date.util.DateUtil.now;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class EventAggregationBundleIT extends BasePaxIT {

    @Inject
    private EventListenerRegistryService eventListenerRegistry;
    @Inject
    private MotechSchedulerService schedulerService;
    @Inject
    private EventAggregationService eventAggregationService;

    @Override
    protected boolean shouldFakeModuleStartupEvent() {
        return true;
    }

    @Test
    public void testEventAggregationScervice() throws InterruptedException {
        final List<AggregatedEvent> aggregatedEvents = new ArrayList<>();
        final int totalEvents = 3;

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

        try {
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
        } finally {
            schedulerService.safeUnscheduleRepeatingJob("periodic_dispatching_event", "test_aggregation");
        }
    }

    private String id(String s) {
        return String.format("%d_%s", now().getMillis(), s);
    }
}
