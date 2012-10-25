package org.motechproject.event.aggregation.repository;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.aggregation.model.AggregatedEventRecord;
import org.motechproject.event.aggregation.model.Aggregation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:testApplicationEventAggregation.xml")
public class AllAggregatedEventsIT {

    @Autowired
    AllAggregatedEvents allAggregatedEvents;

    @After
    public void teardown() {
        allAggregatedEvents.removeAll();
    }

    @Test
    public void shouldAddNewEventToAggregation() {
        Map<String, Object> params = new HashMap<>();
        params.put("foo", "bar");
        AggregatedEventRecord aggregatedEvent = new AggregatedEventRecord("aggregation", params, new HashMap<String, Object>());
        
        allAggregatedEvents.addIfAbsent(aggregatedEvent);
        
        assertEquals(aggregatedEvent, allAggregatedEvents.find("aggregation", params, new HashMap<String, Object>()));
    }

    @Test
    public void shouldNotAddDuplicateEventToAggregation() {
        Map<String, Object> params = new HashMap<>();
        params.put("foo", "bar");
        AggregatedEventRecord aggregatedEvent = new AggregatedEventRecord("aggregation", params, new HashMap<String, Object>());

        allAggregatedEvents.addIfAbsent(aggregatedEvent);

        Map<String, Object> duplicateParams = new HashMap<>();
        duplicateParams.put("foo", "bar");
        AggregatedEventRecord duplicateAggregatedEvent = new AggregatedEventRecord("aggregation", duplicateParams, new HashMap<String, Object>());

        allAggregatedEvents.addIfAbsent(duplicateAggregatedEvent);

        assertEquals(1, allAggregatedEvents.getAll().size());
    }

    @Test
    public void shouldFindAllAggregatedEvents() {
        Map<String, Object> params = new HashMap<>();
        params.put("foo", "bar");
        allAggregatedEvents.addIfAbsent(new AggregatedEventRecord("aggregation", params, new HashMap<String, Object>()));

        params = new HashMap<>();
        params.put("foo", "baz");
        allAggregatedEvents.addIfAbsent(new AggregatedEventRecord("aggregation", params, new HashMap<String, Object>()));

        params = new HashMap<>();
        params.put("foo", "bur");
        allAggregatedEvents.addIfAbsent(new AggregatedEventRecord("aggregation", params, new HashMap<String, Object>(), true));

        params = new HashMap<>();
        params.put("foo", "bor");
        allAggregatedEvents.addIfAbsent(new AggregatedEventRecord("another_aggregation", params, new HashMap<String, Object>()));

        List<AggregatedEventRecord> aggregatedEvents = allAggregatedEvents.findAllAggregated("aggregation");
        assertEquals(2, aggregatedEvents.size());
        assertEquals("bar", aggregatedEvents.get(0).getAggregationParams().get("foo"));
        assertEquals("baz", aggregatedEvents.get(1).getAggregationParams().get("foo"));
    }

    @Test
    public void shouldFindAllBadEvents() {
        Map<String, Object> params = new HashMap<>();
        params.put("foo", "bar");
        allAggregatedEvents.addIfAbsent(new AggregatedEventRecord("aggregation", params, new HashMap<String, Object>()));

        params = new HashMap<>();
        params.put("fuu", "baz");
        allAggregatedEvents.addIfAbsent(new AggregatedEventRecord("aggregation", params, new HashMap<String, Object>(), true));

        params = new HashMap<>();
        params.put("fii", "bur");
        allAggregatedEvents.addIfAbsent(new AggregatedEventRecord("aggregation", params, new HashMap<String, Object>(), true));

        params = new HashMap<>();
        params.put("foo", "bor");
        allAggregatedEvents.addIfAbsent(new AggregatedEventRecord("another_aggregation", params, new HashMap<String, Object>(), true));

        List<AggregatedEventRecord> aggregatedEvents = allAggregatedEvents.findAllErrored("aggregation");
        assertEquals(2, aggregatedEvents.size());
        assertEquals("baz", aggregatedEvents.get(0).getAggregationParams().get("fuu"));
        assertEquals("bur", aggregatedEvents.get(1).getAggregationParams().get("fii"));
    }

    @Test
    public void shouldGroupAggregationByFieldValues() {
        Map<String, Object> aggregationParams = new LinkedHashMap<>();
        aggregationParams.put("flw_id", "123");
        aggregationParams.put("sex", "m");
        Map<String, Object> nonAggregationParams = new LinkedHashMap<>();
        nonAggregationParams.put("data1", "foo");
        allAggregatedEvents.addIfAbsent(new AggregatedEventRecord("aggregation", aggregationParams, nonAggregationParams));

        aggregationParams = new LinkedHashMap<>();
        aggregationParams.put("flw_id", "123");
        aggregationParams.put("sex", "m");
        nonAggregationParams = new LinkedHashMap<>();
        nonAggregationParams.put("data2", "fii");
        allAggregatedEvents.addIfAbsent(new AggregatedEventRecord("aggregation", aggregationParams, nonAggregationParams));

        aggregationParams = new LinkedHashMap<>();
        aggregationParams.put("flw_id", "234");
        aggregationParams.put("sex", "f");
        nonAggregationParams = new LinkedHashMap<>();
        nonAggregationParams.put("data", "fuu");
        allAggregatedEvents.addIfAbsent(new AggregatedEventRecord("aggregation", aggregationParams, nonAggregationParams));

        aggregationParams = new LinkedHashMap<>();
        aggregationParams.put("flw_id", "234");
        aggregationParams.put("sex", "m");
        nonAggregationParams = new LinkedHashMap<>();
        nonAggregationParams.put("data", "fee");
        allAggregatedEvents.addIfAbsent(new AggregatedEventRecord("aggregation", aggregationParams, nonAggregationParams));

        aggregationParams = new LinkedHashMap<>();
        aggregationParams.put("ext_id", "123");
        nonAggregationParams = new LinkedHashMap<>();
        nonAggregationParams.put("name", "bor");
        allAggregatedEvents.addIfAbsent(new AggregatedEventRecord("another_aggregation", aggregationParams, nonAggregationParams));

        List<Aggregation> aggregations = allAggregatedEvents.findAllAggregations("aggregation");
        assertEquals(3, aggregations.size());

        assertEquals(asList("aggregation", "aggregation", "aggregation"), extract(aggregations, on(Aggregation.class).getAggregationRuleName()));

        Aggregation aggregate = aggregations.get(0);
        assertEquals("123", aggregate.getEvents().get(0).getAggregationParams().get("flw_id"));
        assertEquals("m",   aggregate.getEvents().get(0).getAggregationParams().get("sex"));
        assertEquals(2,     aggregate.getEvents().size());
        assertEquals("fii", aggregate.getEvents().get(0).getNonAggregationParams().get("data2"));
        assertEquals("foo", aggregate.getEvents().get(1).getNonAggregationParams().get("data1"));

        aggregate = aggregations.get(1);
        assertEquals("234", aggregate.getEvents().get(0).getAggregationParams().get("flw_id"));
        assertEquals("f",   aggregate.getEvents().get(0).getAggregationParams().get("sex"));
        assertEquals(1,     aggregate.getEvents().get(0).getNonAggregationParams().size());
        assertEquals("fuu", aggregate.getEvents().get(0).getNonAggregationParams().get("data"));

        aggregate = aggregations.get(2);
        assertEquals("234", aggregate.getEvents().get(0).getAggregationParams().get("flw_id"));
        assertEquals("m",   aggregate.getEvents().get(0).getAggregationParams().get("sex"));
        assertEquals(1,     aggregate.getEvents().get(0).getNonAggregationParams().size());
        assertEquals("fee", aggregate.getEvents().get(0).getNonAggregationParams().get("data"));
    }

    @Test
    public void shouldNotAggregateErroredEvents() {
        Map<String, Object> aggregationParams = new LinkedHashMap<>();
        aggregationParams.put("flw_id", "123");
        aggregationParams.put("sex", "m");
        Map<String, Object> nonAggregationParams = new LinkedHashMap<>();
        nonAggregationParams.put("data1", "foo");
        allAggregatedEvents.addIfAbsent(new AggregatedEventRecord("aggregation", aggregationParams, nonAggregationParams));

        aggregationParams = new LinkedHashMap<>();
        aggregationParams.put("flw_id", "123");
        aggregationParams.put("sex", "m");
        nonAggregationParams = new LinkedHashMap<>();
        nonAggregationParams.put("data2", "fii");
        allAggregatedEvents.addIfAbsent(new AggregatedEventRecord("aggregation", aggregationParams, nonAggregationParams, true));

        aggregationParams = new LinkedHashMap<>();
        aggregationParams.put("flw_id", "234");
        aggregationParams.put("sex", "f");
        nonAggregationParams = new LinkedHashMap<>();
        nonAggregationParams.put("data", "fuu");
        allAggregatedEvents.addIfAbsent(new AggregatedEventRecord("aggregation", aggregationParams, nonAggregationParams, true));

        aggregationParams = new LinkedHashMap<>();
        aggregationParams.put("flw_id", "234");
        aggregationParams.put("sex", "m");
        nonAggregationParams = new LinkedHashMap<>();
        nonAggregationParams.put("data", "fee");
        allAggregatedEvents.addIfAbsent(new AggregatedEventRecord("aggregation", aggregationParams, nonAggregationParams));

        List<Aggregation> aggregations = allAggregatedEvents.findAllAggregations("aggregation");
        assertEquals(2, aggregations.size());

        Aggregation aggregate = aggregations.get(0);
        assertEquals("123", aggregate.getEvents().get(0).getAggregationParams().get("flw_id"));
        assertEquals("m",   aggregate.getEvents().get(0).getAggregationParams().get("sex"));
        assertEquals(1,     aggregate.getEvents().size());
        assertEquals("foo", aggregate.getEvents().get(0).getNonAggregationParams().get("data1"));

        aggregate = aggregations.get(1);
        assertEquals("234", aggregate.getEvents().get(0).getAggregationParams().get("flw_id"));
        assertEquals("m",   aggregate.getEvents().get(0).getAggregationParams().get("sex"));
        assertEquals(1,     aggregate.getEvents().get(0).getNonAggregationParams().size());
        assertEquals("fee", aggregate.getEvents().get(0).getNonAggregationParams().get("data"));
    }

    @Test
    public void shouldFindByAggregationRule() {
        Map<String, Object> params = new HashMap<>();
        params.put("foo", "bar");
        allAggregatedEvents.addIfAbsent(new AggregatedEventRecord("aggregation", params, new HashMap<String, Object>()));

        params = new HashMap<>();
        params.put("foo", "baz");
        allAggregatedEvents.addIfAbsent(new AggregatedEventRecord("aggregation", params, new HashMap<String, Object>()));

        params = new HashMap<>();
        params.put("foo", "bur");
        allAggregatedEvents.addIfAbsent(new AggregatedEventRecord("aggregation", params, new HashMap<String, Object>(), true));

        params = new HashMap<>();
        params.put("foo", "bor");
        allAggregatedEvents.addIfAbsent(new AggregatedEventRecord("another_aggregation", params, new HashMap<String, Object>()));

        List<AggregatedEventRecord> aggregatedEvents = allAggregatedEvents.findByAggregationRule("aggregation");
        assertEquals(3, aggregatedEvents.size());
        assertEquals("bar", aggregatedEvents.get(0).getAggregationParams().get("foo"));
        assertEquals("baz", aggregatedEvents.get(1).getAggregationParams().get("foo"));
        assertEquals("bur", aggregatedEvents.get(2).getAggregationParams().get("foo"));
    }

    @Test
    public void shouldRemoveByAggregationRule() {
        Map<String, Object> params = new HashMap<>();
        params.put("foo", "bar");
        allAggregatedEvents.addIfAbsent(new AggregatedEventRecord("aggregation", params, new HashMap<String, Object>()));

        params = new HashMap<>();
        params.put("foo", "baz");
        allAggregatedEvents.addIfAbsent(new AggregatedEventRecord("aggregation", params, new HashMap<String, Object>()));

        params = new HashMap<>();
        params.put("foo", "bur");
        allAggregatedEvents.addIfAbsent(new AggregatedEventRecord("aggregation", params, new HashMap<String, Object>(), true));

        params = new HashMap<>();
        params.put("foo", "bor");
        allAggregatedEvents.addIfAbsent(new AggregatedEventRecord("another_aggregation", params, new HashMap<String, Object>()));

        allAggregatedEvents.removeByAggregationRule("aggregation");
        assertEquals(0, allAggregatedEvents.findByAggregationRule("aggregation").size());
    }
}
