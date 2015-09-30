package org.motechproject.mds.web.rest;

import org.junit.Test;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.util.Order;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ParamParserTest {

    @Test
    public void shouldBuildQueryParams() {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("page", "14");
        requestParams.put("pageSize", "120");
        requestParams.put("sort", "someColumn");
        requestParams.put("order", "desc");

        QueryParams queryParams = ParamParser.buildQueryParams(requestParams);

        assertEquals(Integer.valueOf(14), queryParams.getPage());
        assertEquals(Integer.valueOf(120), queryParams.getPageSize());
        assertNotNull(queryParams.getOrderList());
        assertEquals(1, queryParams.getOrderList().size());
        assertEquals("someColumn", queryParams.getOrderList().get(0).getField());
        assertEquals(Order.Direction.DESC, queryParams.getOrderList().get(0).getDirection());

        // null order

        requestParams.remove("sort");
        requestParams.remove("order");

        queryParams = ParamParser.buildQueryParams(requestParams);

        assertTrue(queryParams.getOrderList().isEmpty());

        // default order direction

        requestParams.put("sort", "anotherColumn");

        queryParams = ParamParser.buildQueryParams(requestParams);

        assertNotNull(queryParams.getOrderList());
        assertEquals(1, queryParams.getOrderList().size());
        assertEquals("anotherColumn", queryParams.getOrderList().get(0).getField());
        assertEquals(Order.Direction.ASC, queryParams.getOrderList().get(0).getDirection());
    }

    @Test
    public void shouldGetLookupName() {
        Map<String, String> requestParams = new HashMap<>();
        assertNull(ParamParser.getLookupName(requestParams));

        requestParams.put("lookup", "findByName");
        assertEquals("findByName", ParamParser.getLookupName(requestParams));
    }

    @Test
    public void shouldGetIds() {
        Map<String, String> requestParams = new HashMap<>();
        assertNull(ParamParser.getId(requestParams));

        requestParams.put("id", "14");
        assertEquals(Long.valueOf(14), ParamParser.getId(requestParams));
    }
}
