package org.motechproject.event.aggregation.aggregate;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.aggregation.model.AggregatedEventRecord;
import org.motechproject.event.aggregation.model.AggregationRuleRecord;
import org.motechproject.event.aggregation.model.AggregationState;
import org.motechproject.event.aggregation.model.schedule.CustomAggregationRecord;
import org.motechproject.event.aggregation.repository.AllAggregatedEvents;
import org.motechproject.event.aggregation.repository.AllAggregationRules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EventAggregatorTest {

    EventAggregator eventAggregator;

    @Mock
    private AllAggregatedEvents allAggregatedEvents;
    @Mock
    private AllAggregationRules allAggregationRules;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldAddEventToAggregation() {
        eventAggregator = new EventAggregator("aggregation", asList("foo"), allAggregatedEvents, allAggregationRules);

        AggregationRuleRecord rule = new AggregationRuleRecord("aggregation", "", "event", asList("foo"), new CustomAggregationRecord("true"), "aggregate", AggregationState.Running);
        when(allAggregationRules.findByName("aggregation")).thenReturn(rule);

        Map<String, Object> params = new HashMap<>();
        params.put("foo", "bar");

        when(allAggregatedEvents.find("aggregation", params, new HashMap<String, Object>())).thenReturn(null);

        eventAggregator.handle(new MotechEvent("subject", params));

        AggregatedEventRecord aggregatedEvent = new AggregatedEventRecord("aggregation", params, new HashMap<String, Object>());
        verify(allAggregatedEvents).add(aggregatedEvent);
    }

    @Test
    public void shouldMarkAggregateEventIfItHasAnyFieldMissing() {
        eventAggregator = new EventAggregator("aggregation", asList("foo", "fuu"), allAggregatedEvents, allAggregationRules);

        AggregationRuleRecord rule = new AggregationRuleRecord("aggregation", "", "event", asList("foo"), new CustomAggregationRecord("true"), "aggregate", AggregationState.Running);
        when(allAggregationRules.findByName("aggregation")).thenReturn(rule);

        Map<String, Object> params = new HashMap<>();
        params.put("foo", "bar");

        when(allAggregatedEvents.find("aggregation", params, new HashMap<String, Object>())).thenReturn(null);

        eventAggregator.handle(new MotechEvent("subject", params));

        AggregatedEventRecord aggregatedEvent = new AggregatedEventRecord("aggregation", params, new HashMap<String, Object>(), true);
        verify(allAggregatedEvents).add(aggregatedEvent);
    }

    @Test
    public void shouldSortKeysInAggregatedParametersMap() {
        eventAggregator = new EventAggregator("aggregation", asList("fee", "foo", "fuu", "fin", "fus"), allAggregatedEvents, allAggregationRules);

        AggregationRuleRecord rule = new AggregationRuleRecord("aggregation", "", "event", asList("foo"), new CustomAggregationRecord("true"), "aggregate", AggregationState.Running);
        when(allAggregationRules.findByName("aggregation")).thenReturn(rule);

        Map<String, Object> params = new HashMap<>(2);
        params.put("foo", "bar");
        params.put("fus", "bar");
        params.put("fuu", "bur");
        params.put("fee", "baz");
        params.put("fin", "baz");

        when(allAggregatedEvents.find("aggregation", params, new HashMap<String, Object>())).thenReturn(null);

        eventAggregator.handle(new MotechEvent("subject", params));

        ArgumentCaptor<AggregatedEventRecord> captor = ArgumentCaptor.forClass(AggregatedEventRecord.class);
        verify(allAggregatedEvents).add(captor.capture());
        assertEquals(asList("fee", "fin", "foo", "fus", "fuu"), new ArrayList<>(captor.getValue().getAggregationParams().keySet()));
    }

    @Test
    public void shouldNotAggregateWhilePaused() {
        eventAggregator = new EventAggregator("aggregation", asList("fee", "foo", "fuu", "fin", "fus"), allAggregatedEvents, allAggregationRules);

        AggregationRuleRecord rule = new AggregationRuleRecord("aggregation", "", "event", asList("foo"), new CustomAggregationRecord("true"), "aggregate", AggregationState.Paused);
        when(allAggregationRules.findByName("aggregation")).thenReturn(rule);

        Map<String, Object> params = new HashMap<>();
        params.put("foo", "bar");
        eventAggregator.handle(new MotechEvent("subject", params));

        verify(allAggregatedEvents, never()).add(any(AggregatedEventRecord.class));
    }
}
