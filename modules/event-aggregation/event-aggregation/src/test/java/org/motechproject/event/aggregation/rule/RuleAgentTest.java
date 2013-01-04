package org.motechproject.event.aggregation.rule;

import org.junit.Test;
import org.motechproject.event.aggregation.service.AggregatedEventResult;

import java.util.HashMap;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.motechproject.commons.date.util.DateUtil.newDateTime;
import static org.motechproject.testing.utils.TimeFaker.fakeNow;
import static org.motechproject.testing.utils.TimeFaker.stopFakingTime;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class RuleAgentTest {

    RuleAgent ruleAgent;

    @Test
    public void shouldBeTrueIfFirstEventWasReceivedMoreThan10MinutesAgo() {
        try {
            fakeNow(newDateTime(2010, 10, 1, 10, 15, 0));

            AggregatedEventResult aggregatedEvent1 = new AggregatedEventResult(new HashMap<String, Object>(), new HashMap<String, Object>());
            setField(aggregatedEvent1, "timeStamp", newDateTime(2010, 10, 1, 10, 0, 0));
            AggregatedEventResult aggregatedEvent2 = new AggregatedEventResult(new HashMap<String, Object>(), new HashMap<String, Object>());
            setField(aggregatedEvent2, "timeStamp", newDateTime(2010, 10, 1, 10, 6, 0));

            ruleAgent = new RuleAgent("firstEvent.timeStamp.isBefore(now.minusMinutes(10))", asList(aggregatedEvent1, aggregatedEvent2));

            assertTrue(ruleAgent.execute());
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldBeFalseIfFirstEventWasReceivedLessThan10MinutesAgo() {
        try {
            fakeNow(newDateTime(2010, 10, 1, 10, 5, 0));

            AggregatedEventResult aggregatedEvent1 = new AggregatedEventResult(new HashMap<String, Object>(), new HashMap<String, Object>());
            setField(aggregatedEvent1, "timeStamp", newDateTime(2010, 10, 1, 10, 0, 0));
            AggregatedEventResult aggregatedEvent2 = new AggregatedEventResult(new HashMap<String, Object>(), new HashMap<String, Object>());
            setField(aggregatedEvent2, "timeStamp", newDateTime(2010, 10, 1, 10, 6, 0));

            ruleAgent = new RuleAgent("firstEvent.timeStamp.isBefore(now.minusMinutes(10))", asList(aggregatedEvent1, aggregatedEvent2));

            assertFalse(ruleAgent.execute());
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldBeTrueIfNoEventWasReceivedInTheLast10Minutes() {
        try {
            fakeNow(newDateTime(2010, 10, 1, 10, 20, 0));

            AggregatedEventResult aggregatedEvent1 = new AggregatedEventResult(new HashMap<String, Object>(), new HashMap<String, Object>());
            setField(aggregatedEvent1, "timeStamp", newDateTime(2010, 10, 1, 10, 4, 0));
            AggregatedEventResult aggregatedEvent2 = new AggregatedEventResult(new HashMap<String, Object>(), new HashMap<String, Object>());
            setField(aggregatedEvent2, "timeStamp", newDateTime(2010, 10, 1, 10, 9, 0));

            ruleAgent = new RuleAgent("lastEvent.timeStamp.isBefore(now.minusMinutes(10))", asList(aggregatedEvent1, aggregatedEvent2));

            assertTrue(ruleAgent.execute());
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldBeFalseIfSomeEventWasReceivedInTheLast10Minutes() {
        try {
            fakeNow(newDateTime(2010, 10, 1, 10, 20, 0));

            AggregatedEventResult aggregatedEvent1 = new AggregatedEventResult(new HashMap<String, Object>(), new HashMap<String, Object>());
            setField(aggregatedEvent1, "timeStamp", newDateTime(2010, 10, 1, 10, 4, 0));
            AggregatedEventResult aggregatedEvent2 = new AggregatedEventResult(new HashMap<String, Object>(), new HashMap<String, Object>());
            setField(aggregatedEvent2, "timeStamp", newDateTime(2010, 10, 1, 10, 12, 0));

            ruleAgent = new RuleAgent("lastEvent.timeStamp.isBefore(now.minusMinutes(10))", asList(aggregatedEvent1, aggregatedEvent2));

            assertFalse(ruleAgent.execute());
        } finally {
            stopFakingTime();
        }
    }
}
