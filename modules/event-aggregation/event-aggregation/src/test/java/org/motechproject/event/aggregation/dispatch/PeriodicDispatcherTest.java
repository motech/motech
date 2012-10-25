package org.motechproject.event.aggregation.dispatch;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.event.aggregation.model.AggregationRuleRecord;
import org.motechproject.event.aggregation.model.AggregationState;
import org.motechproject.event.aggregation.model.event.PeriodicDispatchEvent;
import org.motechproject.event.aggregation.model.mapper.AggregationRuleMapper;
import org.motechproject.event.aggregation.service.impl.AggregationRuleRequest;
import org.motechproject.event.aggregation.service.impl.PeriodicAggregationRequest;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.commons.date.util.DateUtil.newDateTime;

public class PeriodicDispatcherTest {

    @Mock
    private EventDispatcher eventDispatcher;

    private PeriodicDispatcher periodicDispatcher;

    private AggregationRuleMapper aggregationRuleMapper;

    @Before
    public void setup() {
        initMocks(this);
        periodicDispatcher = new PeriodicDispatcher(eventDispatcher);
        aggregationRuleMapper = new AggregationRuleMapper();
    }

    @Test
    public void shouldPublishAggregatedEvent() {
        AggregationRuleRecord aggregationRule = aggregationRuleMapper.toRecord(new AggregationRuleRequest(
            "my_aggregation", "", "event", asList("foo", "fuu"), new PeriodicAggregationRequest("1 day", newDateTime(2012, 5, 22)), "aggregated_event", AggregationState.Running));

        periodicDispatcher.handle(new PeriodicDispatchEvent(aggregationRule).toMotechEvent());

        verify(eventDispatcher).dispatch(aggregationRule);
    }
}
