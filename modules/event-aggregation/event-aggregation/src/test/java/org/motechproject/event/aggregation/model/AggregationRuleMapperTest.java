package org.motechproject.event.aggregation.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.Period;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.event.aggregation.model.mapper.AggregationRuleMapper;
import org.motechproject.event.aggregation.model.schedule.PeriodicAggregationRecord;
import org.motechproject.event.aggregation.service.AggregationRuleRequest;
import org.motechproject.event.aggregation.service.PeriodicAggregationRequest;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.motechproject.commons.date.util.DateUtil.newDateTime;

public class AggregationRuleMapperTest {

    private AggregationRuleMapper aggregationRuleMapper;

    @Before
    public void setup() {
        aggregationRuleMapper = new AggregationRuleMapper();
    }

    public static class Foo {
        @JsonProperty
        AggregationState state;

        public Foo() {
        }
    }
    @Test
    public void shouldCreateRecordFromReqest() {
        AggregationRuleRecord record = aggregationRuleMapper.toRecord(new AggregationRuleRequest("foo", "", "event", asList("bar"), new PeriodicAggregationRequest("1 day", newDateTime(2012, 5, 22)), "aggregation", AggregationState.Paused));
        assertEquals("foo", record.getName());
        assertEquals("event", record.getSubscribedTo());
        assertEquals(asList("bar"), record.getFields());
        assertEquals(Period.days(1), ((PeriodicAggregationRecord) record.getAggregationSchedule()).getPeriod());
        assertEquals("aggregation", record.getPublishAs());
        assertEquals(AggregationState.Paused, record.getState());
    }

}
