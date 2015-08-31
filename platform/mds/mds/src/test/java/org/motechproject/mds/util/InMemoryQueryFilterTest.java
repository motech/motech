package org.motechproject.mds.util;


import org.junit.Before;
import org.junit.Test;
import org.motechproject.mds.query.InMemoryQueryFilter;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.testutil.records.Record;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InMemoryQueryFilterTest {

    private Collection<Record> testCollection;

    @Before
    public void setUp() {
        testCollection = new HashSet<>();

        testCollection.add(record("zet", 1));
        testCollection.add(record("test", 2));
        testCollection.add(record("aaa", 4));
        testCollection.add(record("something", 3));
        testCollection.add(record("hmm", 5));
    }

    @Test
    public void shouldOrderByFieldDesc() {
        List<Record> result = InMemoryQueryFilter.order(testCollection, "value", Order.Direction.DESC);
        assertListByValues(result, asList("zet", "test", "something", "hmm", "aaa"));
    }

    @Test
    public void shouldOrderByAsc() {
        List<Record> result = InMemoryQueryFilter.order(testCollection, "value", Order.Direction.ASC);
        assertListByValues(result, asList("aaa", "hmm", "something", "test", "zet"));
    }

    @Test
    public void shouldPaginate() {
        List<Record> list = InMemoryQueryFilter.order(testCollection, "value", Order.Direction.ASC);

        List<Record> result = InMemoryQueryFilter.paginate(list, 1, 3);
        assertListByValues(result, asList("aaa", "hmm", "something"));

        result = InMemoryQueryFilter.paginate(list, 2, 2);
        assertListByValues(result, asList("something", "test"));
    }

    @Test
    public void shouldApplyQueryParams() {
        QueryParams queryParams = new QueryParams(2, 3, new Order("value", Order.Direction.DESC));

        List<Record> result = InMemoryQueryFilter.filter(testCollection, queryParams);

        assertListByValues(result, asList("hmm", "aaa"));
    }

    @Test
    public void shouldApplyDefaultOrdering() {
        QueryParams queryParams = new QueryParams(2, 2);

        List<Record> result = InMemoryQueryFilter.filter(testCollection, queryParams);

        assertListByValues(result, asList("something", "aaa"));
    }

    @Test
    public void shouldReturnNothingForTooLargePageNumber() {
        QueryParams queryParams = new QueryParams(11, 3);

        List<Record> result = InMemoryQueryFilter.filter(testCollection, queryParams);

        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldReturnEverythingWhenNoPagingSet() {
        QueryParams queryParams = new QueryParams(null, null);

        List<Record> result = InMemoryQueryFilter.filter(testCollection, queryParams);

        assertListByValues(result, asList("zet", "test", "something", "aaa", "hmm"));
    }

    private void assertListByValues(List<Record> result, List<String> values) {
        assertEquals(values, extract(result, on(Record.class).getValue()));
    }

    private Record record(String value, long id) {
        Record record = new Record();
        record.setValue(value);
        record.setId(id);
        return record;
    }
}
