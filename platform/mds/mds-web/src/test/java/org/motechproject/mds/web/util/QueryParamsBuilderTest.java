package org.motechproject.mds.web.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.Order;
import org.motechproject.mds.web.domain.GridSettings;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QueryParamsBuilderTest {

    @Mock
    private GridSettings gridSettings;

    @Test
    public void shouldBuildQueryParamsFromGridSettings() {
        when(gridSettings.getPage()).thenReturn(3);
        when(gridSettings.getRows()).thenReturn(50);
        when(gridSettings.getSortColumn()).thenReturn("field");
        when(gridSettings.getSortDirection()).thenReturn("desc");

        QueryParams queryParams = QueryParamsBuilder.buildQueryParams(gridSettings);

        assertNotNull(queryParams);
        assertPagination(queryParams, 3, 50);
        assertEquals(2, queryParams.getOrderList().size());
        assertOrderPresent(queryParams, 0, "field", Order.Direction.DESC);
        assertDefaultIdOrder(queryParams, 1);
    }

    @Test
    public void shouldBuildDefaultQueryParams() {
        when(gridSettings.getPage()).thenReturn(null);
        when(gridSettings.getRows()).thenReturn(null);

        QueryParams queryParams = QueryParamsBuilder.buildQueryParams(gridSettings);

        assertNotNull(queryParams);
        assertPagination(queryParams, QueryParamsBuilder.DEFAULT_PAGE, QueryParamsBuilder.DEFAULT_PAGE_SIZE);
        assertEquals(1, queryParams.getOrderList().size());
        assertDefaultIdOrder(queryParams, 0);
    }

    @Test
    public void shouldNotAddIdOrderingIfItsPresent() {
        when(gridSettings.getSortColumn()).thenReturn(Constants.Util.ID_FIELD_NAME);
        when(gridSettings.getSortDirection()).thenReturn("descending");

        QueryParams queryParams = QueryParamsBuilder.buildQueryParams(gridSettings);

        assertNotNull(queryParams);
        assertEquals(1, queryParams.getOrderList().size());
        assertOrderPresent(queryParams, 0, Constants.Util.ID_FIELD_NAME, Order.Direction.DESC);
    }

    @Test
    public void shouldBuildQueryParamsForLookups() {
        when(gridSettings.getPage()).thenReturn(7);
        when(gridSettings.getRows()).thenReturn(80);
        // the values are not important
        Map<String, Object> lookupMap = new LinkedHashMap<>();
        lookupMap.put("field1", null);
        lookupMap.put("field2", null);
        lookupMap.put("field3", null);

        QueryParams queryParams = QueryParamsBuilder.buildQueryParams(gridSettings, lookupMap);

        assertNotNull(queryParams);
        assertPagination(queryParams, 7, 80);
        assertEquals(4, queryParams.getOrderList().size());
        assertOrderPresent(queryParams, 0, "field1", Order.Direction.ASC);
        assertOrderPresent(queryParams, 1, "field2", Order.Direction.ASC);
        assertOrderPresent(queryParams, 2, "field3", Order.Direction.ASC);
        assertDefaultIdOrder(queryParams, 3);
    }

    private void assertOrderPresent(QueryParams queryParams, int index,
                                    String field, Order.Direction direction) {
        Order order = queryParams.getOrderList().get(index);

        assertNotNull(order);
        assertEquals(field, order.getField());
        assertEquals(direction, order.getDirection());
    }

    private void assertDefaultIdOrder(QueryParams queryParams, int index) {
        assertOrderPresent(queryParams, index, Constants.Util.ID_FIELD_NAME, Order.Direction.ASC);
    }

    private void assertPagination(QueryParams queryParams, Integer expectedPage, Integer expectedPageSize) {
        assertEquals(expectedPage, queryParams.getPage());
        assertEquals(expectedPageSize, queryParams.getPageSize());
    }
}
