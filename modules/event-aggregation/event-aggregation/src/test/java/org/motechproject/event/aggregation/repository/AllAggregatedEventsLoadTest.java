package org.motechproject.event.aggregation.repository;

import org.ektorp.CouchDbConnector;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.aggregation.model.AggregatedEventRecord;
import org.motechproject.event.aggregation.model.Aggregation;
import org.motechproject.event.aggregation.model.AggregationRuleRecord;
import org.motechproject.event.aggregation.model.AggregationState;
import org.motechproject.event.aggregation.model.schedule.CustomAggregationRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.min;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:testApplicationEventAggregation.xml")
public class AllAggregatedEventsLoadTest {

    @Autowired
    AllAggregatedEvents allAggregatedEvents;

    @Autowired
    AllAggregationRules allAggregationRules;

    @Autowired
    @Qualifier("eventAggregationDbConnector")
    CouchDbConnector db;

    @Test
    @Ignore("don't run under ci")
    public void run() {
        setup();
        long start = System.currentTimeMillis();
        List<Aggregation> aggregations = allAggregatedEvents.findAllAggregations("aggregation");
        System.out.println(System.currentTimeMillis() - start);

        // ensure all records are present so there's no rereduce goofup
        assertEquals(3, aggregations.size());
        int eventCount = 0;
        for (Aggregation aggregation : aggregations)
            eventCount += aggregation.getEvents().size();
        assertEquals(80, eventCount);
    }

    public void setup() {
        allAggregationRules.addOrReplace(new AggregationRuleRecord("aggregation", "", "eve", asList("flw_id", "sex"), new CustomAggregationRecord("true"), "pub", AggregationState.Running));
        allAggregationRules.addOrReplace(new AggregationRuleRecord("another_aggregation", "", "even", asList("ext_id"), new CustomAggregationRecord("true"), "publ", AggregationState.Running));

        int max = 100;

        Map<String, Object> aggregationParams, nonAggregationParams;

        List<AggregatedEventRecord> records = new ArrayList<>();

        for (int i = 0; i < max / 5; i++) {
            aggregationParams = new LinkedHashMap<>();
            aggregationParams.put("flw_id", "123");
            aggregationParams.put("sex", "m");
            nonAggregationParams = new LinkedHashMap<>();
            nonAggregationParams.put("data" + i, "foo" + i);
            records.add(new AggregatedEventRecord("aggregation", aggregationParams, nonAggregationParams));

            aggregationParams = new LinkedHashMap<>();
            aggregationParams.put("flw_id", "123");
            aggregationParams.put("sex", "m");
            nonAggregationParams = new LinkedHashMap<>();
            nonAggregationParams.put("data" + i, "fii" + i);
            records.add(new AggregatedEventRecord("aggregation", aggregationParams, nonAggregationParams));

            aggregationParams = new LinkedHashMap<>();
            aggregationParams.put("flw_id", "234");
            aggregationParams.put("sex", "f");
            nonAggregationParams = new LinkedHashMap<>();
            nonAggregationParams.put("data" + i, "fuu" + i);
            records.add(new AggregatedEventRecord("aggregation", aggregationParams, nonAggregationParams));

            aggregationParams = new LinkedHashMap<>();
            aggregationParams.put("flw_id", "234");
            aggregationParams.put("sex", "m");
            nonAggregationParams = new LinkedHashMap<>();
            nonAggregationParams.put("data" + i, "fee" + i);
            records.add(new AggregatedEventRecord("aggregation", aggregationParams, nonAggregationParams));

            aggregationParams = new LinkedHashMap<>();
            aggregationParams.put("ext_id", "123");
            nonAggregationParams = new LinkedHashMap<>();
            nonAggregationParams.put("name", "bor");
            records.add(new AggregatedEventRecord("another_aggregation", aggregationParams, nonAggregationParams));
        }

        int batchSize = min(1000, max);
        for (int i = 0; i < max; i += batchSize) {
            db.executeBulk(records.subList(i, i + batchSize));
            if (i % 100 == 0) {
                System.out.println(String.format("%d %%", (int) ((i / (double) max) * 100)));
            }
        }

        allAggregatedEvents.findAllAggregations("aggregation");// reindex
    }
}
