package org.motechproject.mds.query;


import org.junit.Before;
import org.junit.Test;
import org.motechproject.mds.testutil.records.Record;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.Order;

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

        testCollection.add(record("zet", 1L));
        testCollection.add(record("test", 2L));
        testCollection.add(record("aaa", 4L));
        testCollection.add(record("something", 3L));
        testCollection.add(record("hmm", 5L));
        testCollection.add(record("hmm", 6L));
        testCollection.add(record("nullRecord", null));
    }

    @Test
    public void shouldOrderByFieldDesc() {
        QueryParams queryParams = new QueryParams(new Order("value", Order.Direction.DESC));

        List<Record> result = InMemoryQueryFilter.filter(testCollection, queryParams);

        assertListByValues(result, asList("zet", "test", "something", "nullRecord", "hmm", "hmm", "aaa"));
        assertListByIds(result, asList(1L, 2L, 3L, null, 5L, 6L, 4L));
    }

    @Test
    public void shouldOrderByAsc() {
        QueryParams queryParams = new QueryParams(new Order("value", Order.Direction.ASC));

        List<Record> result = InMemoryQueryFilter.filter(testCollection, queryParams);

        assertListByValues(result, asList("aaa", "hmm", "hmm", "nullRecord", "something", "test", "zet"));
        assertListByIds(result, asList(4L, 5L, 6L, null, 3L, 2L, 1L));
    }

    @Test
    public void shouldPaginate() {
        Order order = new Order("value", Order.Direction.ASC);

        QueryParams queryParams = new QueryParams(1, 3, order);
        List<Record> result = InMemoryQueryFilter.filter(testCollection, queryParams);
        assertListByValues(result, asList("aaa", "hmm", "hmm"));
        assertListByIds(result, asList(4L, 5L, 6L));

        queryParams = new QueryParams(2, 3, order);
        result = InMemoryQueryFilter.filter(testCollection, queryParams);
        assertListByValues(result, asList("nullRecord", "something", "test"));
        assertListByIds(result, asList(null, 3L, 2L));
    }

    @Test
    public void shouldApplyQueryParams() {
        QueryParams queryParams = new QueryParams(2, 3, new Order("value", Order.Direction.DESC));

        List<Record> result = InMemoryQueryFilter.filter(testCollection, queryParams);

        assertListByValues(result, asList("nullRecord", "hmm", "hmm"));
        assertListByIds(result, asList(null, 5L, 6L));
    }

    @Test
    public void shouldApplyDefaultOrdering() {
        QueryParams queryParams = new QueryParams(2, 2);

        List<Record> result = InMemoryQueryFilter.filter(testCollection, queryParams);

        assertListByValues(result, asList("something", "aaa"));
        assertListByIds(result, asList(3L, 4L));
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

        assertListByValues(result, asList("zet", "test", "something", "aaa", "hmm", "hmm", "nullRecord"));
        assertListByIds(result, asList(1L, 2L, 3L, 4L, 5L, 6L, null));
    }

    @Test
    public void shouldApplyMultipleOrders() {
        QueryParams queryParams = new QueryParams(asList(new Order("value", Order.Direction.DESC),
                new Order(Constants.Util.ID_FIELD_NAME, Order.Direction.DESC)));

        List<Record> result = InMemoryQueryFilter.filter(testCollection, queryParams);

        assertListByValues(result, asList("zet", "test", "something", "nullRecord", "hmm", "hmm", "aaa"));
        assertListByIds(result, asList(1L, 2L, 3L, null, 6L, 5L, 4L));
    }

    private void assertListByValues(List<Record> result, List<String> values) {
        assertEquals(values, extract(result, on(Record.class).getValue()));
    }

    private void assertListByIds(List<Record> result, List<Long> ids) {
        assertEquals(ids, extract(result, on(Record.class).getId()));
    }

    private Record record(String value, Long id) {
        Record record = new Record();
        record.setValue(value);
        record.setId(id);
        return record;
    }
}
