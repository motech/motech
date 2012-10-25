package org.motechproject.event.aggregation.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.aggregation.model.AggregationRuleRecord;
import org.motechproject.event.aggregation.model.AggregationState;
import org.motechproject.event.aggregation.model.mapper.AggregationRuleMapper;
import org.motechproject.event.aggregation.service.impl.AggregationRuleRequest;
import org.motechproject.event.aggregation.service.impl.PeriodicAggregationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.motechproject.commons.date.util.DateUtil.newDateTime;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:testApplicationEventAggregation.xml")
public class AllAggregationRulesIT {

    @Autowired
    AllAggregationRules allAggregationRules;

    private AggregationRuleMapper aggregationRuleMapper;

    @Before
    public void setup() {
        aggregationRuleMapper = new AggregationRuleMapper();
    }

    @After
    public void teardown() {
        allAggregationRules.removeAll();
    }

    @Test
    public void shouldAddAndFindRuleByName() {
        AggregationRuleRecord rule = aggregationRuleMapper.toRecord(new AggregationRuleRequest("foo", "", "event", new ArrayList<String>(), new PeriodicAggregationRequest("1 day", newDateTime(2012, 5, 22)), "aggregation", AggregationState.Running));
        allAggregationRules.addOrReplace(rule);
        assertEquals(rule, allAggregationRules.findByName("foo"));
    }

    @Test
    public void shouldReplaceExistingRule() {
        AggregationRuleRecord rule = aggregationRuleMapper.toRecord(new AggregationRuleRequest("foo", "", "event", new ArrayList<String>(), new PeriodicAggregationRequest("1 day", newDateTime(2012, 5, 22)), "aggregation", AggregationState.Running));
        allAggregationRules.addOrReplace(rule);

        AggregationRuleRecord newRule = aggregationRuleMapper.toRecord(new AggregationRuleRequest("foo", "", "new_event", new ArrayList<String>(), new PeriodicAggregationRequest("1 day", newDateTime(2012, 5, 22)), "aggregation", AggregationState.Running));
        allAggregationRules.addOrReplace(newRule);

        assertEquals("new_event", allAggregationRules.findByName("foo").getSubscribedTo());
    }

    @Test
    public void shouldRemoveExistingRule() {
        List<AggregationRuleRecord> rules = asList(
            aggregationRuleMapper.toRecord(new AggregationRuleRequest("foo", "", "event", new ArrayList<String>(), new PeriodicAggregationRequest("1 day", newDateTime(2012, 5, 22)), "aggregation", AggregationState.Running)),
            aggregationRuleMapper.toRecord(new AggregationRuleRequest("foo2", "", "event2", new ArrayList<String>(), new PeriodicAggregationRequest("1 day", newDateTime(2012, 5, 22)), "aggregation2", AggregationState.Running)));
        allAggregationRules.addOrReplace(rules.get(0));
        allAggregationRules.addOrReplace(rules.get(1));

        allAggregationRules.remove("foo");

        assertNull(allAggregationRules.findByName("foo"));
    }
}
