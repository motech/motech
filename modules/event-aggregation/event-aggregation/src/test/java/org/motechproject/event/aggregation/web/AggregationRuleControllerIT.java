package org.motechproject.event.aggregation.web;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.aggregation.model.AggregationRuleRecord;
import org.motechproject.event.aggregation.model.AggregationState;
import org.motechproject.event.aggregation.model.mapper.AggregationRuleMapper;
import org.motechproject.event.aggregation.repository.AllAggregationRules;
import org.motechproject.event.aggregation.service.AggregationRuleRequest;
import org.motechproject.event.aggregation.service.CronBasedAggregationRequest;
import org.motechproject.event.aggregation.service.CustomAggregationRequest;
import org.motechproject.event.aggregation.service.PeriodicAggregationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.motechproject.commons.date.util.DateUtil.newDateTime;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/motech/*.xml"})
public class AggregationRuleControllerIT {

    private MockMvc mockAggregationRuleController;

    @Autowired
    AggregationRuleController aggregationRuleController;

    @Autowired
    AllAggregationRules allAggregationRules;

    private AggregationRuleMapper aggregationRuleMapper;

    @Before
    public void setup() throws Exception {
        mockAggregationRuleController = MockMvcBuilders.standaloneSetup(aggregationRuleController).build();
        aggregationRuleMapper = new AggregationRuleMapper();
    }

    @After
    public void teardown() {
        allAggregationRules.removeAll();
    }

    @Test
    public void shouldReturnAllRulesAsJson() throws Exception {
        List<AggregationRuleRecord> rules = asList(
            aggregationRuleMapper.toRecord(new AggregationRuleRequest("aggregation1", "", "subscribedEvent1", asList("foo"), new CronBasedAggregationRequest("* * * * * ?"), "publishEvent1", AggregationState.Running)),
            aggregationRuleMapper.toRecord(new AggregationRuleRequest("aggregation2", "", "subscribedEvent2", asList("fuu"), new CustomAggregationRequest(""), "publishEvent2", AggregationState.Running))
        );
        allAggregationRules.addOrReplace(rules.get(0));
        allAggregationRules.addOrReplace(rules.get(1));

        mockAggregationRuleController.perform(
            get("/rules"))
        .andExpect(
            content().string(new ObjectMapper().writeValueAsString(allAggregationRules.getAll())));
    }

    @Test
    public void shouldReturnASingleRuleByNameAsJson() throws Exception {
        List<AggregationRuleRequest> rules = asList(
            new AggregationRuleRequest("aggregation1", "eve", "subscribedEvent1", asList("foo"), new CronBasedAggregationRequest("* * * * * ?"), "publishEvent1", AggregationState.Running),
            new AggregationRuleRequest("aggregation2", "eve", "subscribedEvent2", asList("fuu"), new CustomAggregationRequest("true"), "publishEvent2", AggregationState.Running)
        );
        allAggregationRules.addOrReplace(aggregationRuleMapper.toRecord(rules.get(0)));
        allAggregationRules.addOrReplace(aggregationRuleMapper.toRecord(rules.get(1)));

        mockAggregationRuleController.perform(
            get("/rules/aggregation2"))
        .andExpect(
            content().string(new ObjectMapper().writeValueAsString(rules.get(1))));
    }

    @Test
    public void shouldCreateARule() throws Exception {
        AggregationRuleRequest ruleRequest = new AggregationRuleRequest("aggregation1", "", "subscribedEvent1", asList("foo"), new CronBasedAggregationRequest("* * * * * ?"), "publishEvent1", AggregationState.Running);

        mockAggregationRuleController.perform(
            put("/rules")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ObjectMapper().writeValueAsString(ruleRequest).getBytes("UTF-8")))
            .andExpect(
                status().is(201)
            );
        assertNotNull(allAggregationRules.findByName("aggregation1"));
    }

    @Test
    public void shouldReplaceAnExistingRule() throws Exception {
        List<AggregationRuleRecord> rules = asList(
            aggregationRuleMapper.toRecord(new AggregationRuleRequest("aggregation1", "", "subscribedEvent1", asList("foo"), new CronBasedAggregationRequest("* * * * * ?"), "publishEvent1", AggregationState.Running)),
            aggregationRuleMapper.toRecord(new AggregationRuleRequest("aggregation2", "", "subscribedEvent2", asList("fuu"), new CustomAggregationRequest(""), "publishEvent2", AggregationState.Running))
        );
        allAggregationRules.addOrReplace(rules.get(0));
        allAggregationRules.addOrReplace(rules.get(1));

        AggregationRuleRequest updatedRule = new AggregationRuleRequest("aggregation1", "", "subscribedEvent3", asList("foo"), new CronBasedAggregationRequest("* * * * * ?"), "publishEvent3", AggregationState.Running);

        mockAggregationRuleController.perform(
            put("/rules")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ObjectMapper().writeValueAsString(updatedRule).getBytes("UTF-8")))
            .andExpect(
                status().is(201)
            );
        assertEquals("subscribedEvent3", allAggregationRules.findByName("aggregation1").getSubscribedTo());
    }

    @Test
    public void shouldReturnHttp400ForBadJson() throws Exception {
        mockAggregationRuleController.perform(
            put("/rules")
                .contentType(MediaType.APPLICATION_JSON)
                .body("foobar".getBytes("UTF-8")))
            .andExpect(
                status().is(400));
    }

    @Test
    public void shouldValidateRequestFields() throws Exception {
        AggregationRuleRequest request = new AggregationRuleRequest(null, "foo", "bar", asList("baz"), new CustomAggregationRequest("true"), "fuu", AggregationState.Running);
        FieldError error = new FieldError("name", "Must be present");
        mockAggregationRuleController.perform(
            put("/rules")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ObjectMapper().writeValueAsString(request).getBytes()))
            .andExpect(
                status().is(400))
            .andExpect(
                content().string(new ObjectMapper().writeValueAsString(asList(error))));
    }

    @Test
    public void shouldValidateNestedFields() throws Exception {
        AggregationRuleRequest request = new AggregationRuleRequest("aggregation", "", "subscribeEvent", asList("foo"), new PeriodicAggregationRequest("foo", newDateTime(2012, 10, 5)), "publishEvent", AggregationState.Running);
        FieldError error = new FieldError("aggregationSchedule.period", "Not a valid period value");
        mockAggregationRuleController.perform(
            put("/rules")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ObjectMapper().writeValueAsString(request).getBytes()))
            .andExpect(
                status().is(400))
            .andExpect(
                content().string(new ObjectMapper().writeValueAsString(asList(error))));
    }

    @Test
    public void shouldDeleteAnExistingRule() throws Exception {
        List<AggregationRuleRecord> rules = asList(
            aggregationRuleMapper.toRecord(new AggregationRuleRequest("aggregation1", "", "subscribedEvent1", asList("foo"), new CronBasedAggregationRequest("* * * * * ?"), "publishEvent1", AggregationState.Running)),
            aggregationRuleMapper.toRecord(new AggregationRuleRequest("aggregation2", "", "subscribedEvent2", asList("fuu"), new CustomAggregationRequest(""), "publishEvent2", AggregationState.Running))
        );
        allAggregationRules.addOrReplace(rules.get(0));
        allAggregationRules.addOrReplace(rules.get(1));

        mockAggregationRuleController.perform(
            delete("/rules/aggregation2"));

        List<AggregationRuleRecord> allRules = allAggregationRules.getAll();
        assertEquals(asList(rules.get(0)), allRules);
    }
}
