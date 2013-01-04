package org.motechproject.event.aggregation.web;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.aggregation.model.AggregatedEventRecord;
import org.motechproject.event.aggregation.model.Aggregation;
import org.motechproject.event.aggregation.model.AggregationState;
import org.motechproject.event.aggregation.model.mapper.AggregationRuleMapper;
import org.motechproject.event.aggregation.repository.AllAggregatedEvents;
import org.motechproject.event.aggregation.repository.AllAggregationRules;
import org.motechproject.event.aggregation.service.AggregationRuleRequest;
import org.motechproject.event.aggregation.service.CronBasedAggregationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/motech/*.xml"})
public class AggregationControllerIT {

    private MockMvc mockAggregationController;

    @Autowired
    AggregationController aggregationController;

    @Autowired
    AllAggregatedEvents allAggregatedEvents;

    @Autowired
    private AllAggregationRules allAggregationRules;

    private AggregationRuleMapper aggregationRuleMapper;

    @Before
    public void setup() throws Exception {
        mockAggregationController = MockMvcBuilders.standaloneSetup(aggregationController).build();
        aggregationRuleMapper = new AggregationRuleMapper();
    }

    @After
    public void teardown() {
        allAggregatedEvents.removeAll();
        allAggregationRules.removeAll();
    }

    @Test
    public void shouldReturnAllAggregationsForARuleAsJson() throws Exception {
        allAggregationRules.addOrReplace(aggregationRuleMapper.toRecord(new AggregationRuleRequest("my_aggregation", "", "subscribedEvent", asList("foo"), new CronBasedAggregationRequest("* * * * * ?"), "publishEvent", AggregationState.Running)));

        Map<String, Object> aggregationParams = new LinkedHashMap<>();
        aggregationParams.put("flw_id", "123");
        Map<String, Object> nonAggregationParams = new LinkedHashMap<>();
        nonAggregationParams.put("data1", "foo");
        allAggregatedEvents.add(new AggregatedEventRecord("my_aggregation", aggregationParams, nonAggregationParams));

        aggregationParams = new LinkedHashMap<>();
        aggregationParams.put("flw_id", "123");
        nonAggregationParams = new LinkedHashMap<>();
        nonAggregationParams.put("data2", "fii");
        allAggregatedEvents.add(new AggregatedEventRecord("my_aggregation", aggregationParams, nonAggregationParams));

        List<Aggregation> aggregation = allAggregatedEvents.findAllAggregations("my_aggregation");

        mockAggregationController.perform(
            get("/aggregations/my_aggregation/valid"))
            .andExpect(
                content().string(new ObjectMapper().writeValueAsString(aggregation)));
    }

    @Test
    public void shouldReturnErrorEventAggregationsForAGivenRuleAsJson() throws Exception {
        allAggregationRules.addOrReplace(aggregationRuleMapper.toRecord(new AggregationRuleRequest("my_aggregation", "", "subscribedEvent", asList("foo"), new CronBasedAggregationRequest("* * * * * ?"), "publishEvent", AggregationState.Running)));

        Map<String, Object> aggregationParams = new LinkedHashMap<>();
        aggregationParams.put("flw_id", "123");
        Map<String, Object> nonAggregationParams = new LinkedHashMap<>();
        nonAggregationParams.put("data1", "foo");
        allAggregatedEvents.add(new AggregatedEventRecord("my_aggregation", aggregationParams, nonAggregationParams));

        aggregationParams = new LinkedHashMap<>();
        aggregationParams.put("flw_id", "123");
        nonAggregationParams = new LinkedHashMap<>();
        nonAggregationParams.put("data2", "fii");
        allAggregatedEvents.add(new AggregatedEventRecord("my_aggregation", aggregationParams, nonAggregationParams, true));

        List<Aggregation> aggregation = allAggregatedEvents.findAllErrorEventsForAggregations("my_aggregation");

        mockAggregationController.perform(
            get("/aggregations/my_aggregation/invalid"))
            .andExpect(
                content().string(new ObjectMapper().writeValueAsString(aggregation)));
    }
}
