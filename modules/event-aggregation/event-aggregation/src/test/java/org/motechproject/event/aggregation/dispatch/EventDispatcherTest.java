package org.motechproject.event.aggregation.dispatch;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.event.aggregation.model.Aggregation;
import org.motechproject.event.aggregation.model.AggregationRuleRecord;
import org.motechproject.event.aggregation.model.AggregationState;
import org.motechproject.event.aggregation.model.event.AggregationEvent;
import org.motechproject.event.aggregation.model.mapper.AggregationRuleMapper;
import org.motechproject.event.aggregation.model.schedule.CustomAggregationRecord;
import org.motechproject.event.aggregation.repository.AllAggregatedEvents;
import org.motechproject.event.aggregation.repository.AllAggregationRules;
import org.motechproject.event.aggregation.service.AggregatedEventResult;
import org.motechproject.event.aggregation.service.AggregationRuleRequest;
import org.motechproject.event.aggregation.service.PeriodicAggregationRequest;
import org.motechproject.event.listener.EventRelay;

import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.commons.date.util.DateUtil.newDateTime;

public class EventDispatcherTest {

    @Mock
    private EventRelay eventRelay;
    @Mock
    private AllAggregatedEvents allAggregatedEvents;
    @Mock
    private AllAggregationRules allAggregationRules;

    private EventDispatcher eventDispatcher;
    private AggregationRuleMapper aggregationRuleMapper;

    @Before
    public void setup() {
        initMocks(this);
        eventDispatcher = new EventDispatcher(allAggregatedEvents, eventRelay, allAggregationRules);
        aggregationRuleMapper = new AggregationRuleMapper();
    }

    @Test
    public void shouldExtractAggregationAndPublishAggregatedEvent() {
        when(allAggregationRules.findByName("my_aggregation")).thenReturn(
            new AggregationRuleRecord("my_aggregation", "", "event", asList("foo", "fuu"), new CustomAggregationRecord("true"), "aggregated_event", AggregationState.Running));

        Map<String, Object> params1 = new HashMap<>();
        params1.put("foo", "bar");
        Aggregation aggregation1 = new Aggregation("my_aggregation", asList(new AggregatedEventResult(new HashMap<String, Object>(), params1)));
        Map<String, Object> params2 = new HashMap<>();
        params2.put("fuu", "baz");
        Aggregation aggregation2 = new Aggregation("my_aggregation", asList(new AggregatedEventResult(new HashMap<String, Object>(), params2)));
        when(allAggregatedEvents.findAllAggregations("my_aggregation")).thenReturn(asList(aggregation1, aggregation2));

        AggregationRuleRecord aggregationRule = aggregationRuleMapper.toRecord(new AggregationRuleRequest(
            "my_aggregation", "", "event", asList("foo", "fuu"), new PeriodicAggregationRequest("1 day", newDateTime(2012, 5, 22)), "aggregated_event", AggregationState.Running));

        eventDispatcher.dispatch("my_aggregation");

        verify(eventRelay).sendEventMessage(new AggregationEvent(aggregationRule, aggregation1).toMotechEvent());
        verify(eventRelay).sendEventMessage(new AggregationEvent(aggregationRule, aggregation2).toMotechEvent());
    }

    @Test
    public void shouldClearCurrentAggregationAfterPublishingEvent() {
        when(allAggregationRules.findByName("my_aggregation")).thenReturn(
            new AggregationRuleRecord("my_aggregation", "", "event", asList("foo", "fuu"), new CustomAggregationRecord("true"), "aggregated_event", AggregationState.Running));

        Aggregation aggregation1 = mock(Aggregation.class);
        Aggregation aggregation2 = mock(Aggregation.class);
        when(allAggregatedEvents.findAllAggregations("my_aggregation")).thenReturn(asList(aggregation1, aggregation2));

        eventDispatcher.dispatch("my_aggregation");

        verify(allAggregatedEvents).removeByAggregationRule("my_aggregation");
    }
}
