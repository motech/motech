package org.motechproject.event.aggregation.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.aggregation.model.AggregatedEventRecord;
import org.motechproject.event.aggregation.model.AggregationRuleRecord;
import org.motechproject.event.aggregation.model.AggregationState;
import org.motechproject.event.aggregation.model.schedule.CustomAggregationRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Arrays.asList;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:testApplicationEventAggregation.xml")
public class DummyDataSetup {

    @Autowired
    AllAggregatedEvents allAggregatedEvents;
    @Autowired
    private AllAggregationRules allAggregatedRules;

    @Test
        public void dummyData() {
        allAggregatedRules.add(new AggregationRuleRecord("aggregation", "", "eve", asList("flw_id", "sex"), new CustomAggregationRecord("true"), "ent", AggregationState.Running));

        Map<String, Object> aggregationParams = new LinkedHashMap<>();
        aggregationParams.put("flw_id", "123");
        aggregationParams.put("sex", "m");
        Map<String, Object> nonAggregationParams = new LinkedHashMap<>();
        nonAggregationParams.put("data1", "foo");
        allAggregatedEvents.add(new AggregatedEventRecord("aggregation", aggregationParams, nonAggregationParams));

        aggregationParams = new LinkedHashMap<>();
        aggregationParams.put("flw_id", "123");
        aggregationParams.put("sex", "m");
        nonAggregationParams = new LinkedHashMap<>();
        nonAggregationParams.put("data2", "fii");
        allAggregatedEvents.add(new AggregatedEventRecord("aggregation", aggregationParams, nonAggregationParams));

        aggregationParams = new LinkedHashMap<>();
        aggregationParams.put("flw_id", "234");
        aggregationParams.put("sex", "f");
        nonAggregationParams = new LinkedHashMap<>();
        nonAggregationParams.put("data", "fuu");
        allAggregatedEvents.add(new AggregatedEventRecord("aggregation", aggregationParams, nonAggregationParams, true));

        aggregationParams = new LinkedHashMap<>();
        aggregationParams.put("flw_id", "234");
        aggregationParams.put("sex", "m");
        nonAggregationParams = new LinkedHashMap<>();
        nonAggregationParams.put("data", "fee");
        allAggregatedEvents.add(new AggregatedEventRecord("aggregation", aggregationParams, nonAggregationParams));

        aggregationParams = new LinkedHashMap<>();
        aggregationParams.put("ext_id", "123");
        nonAggregationParams = new LinkedHashMap<>();
        nonAggregationParams.put("name", "bor");
        allAggregatedEvents.add(new AggregatedEventRecord("another_aggregation", aggregationParams, nonAggregationParams));
    }
}
