package org.motechproject.mds.web.rest;

import org.junit.Test;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.util.Order;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
        assertNotNull(queryParams.getOrder());
        assertEquals("someColumn", queryParams.getOrder().getField());
        assertEquals(Order.Direction.DESC, queryParams.getOrder().getDirection());

        // null order

        requestParams.remove("sort");
        requestParams.remove("order");

        queryParams = ParamParser.buildQueryParams(requestParams);

        assertNull(queryParams.getOrder());

        // default order direction

        requestParams.put("sort", "anotherColumn");

        queryParams = ParamParser.buildQueryParams(requestParams);

        assertNotNull(queryParams.getOrder());
        assertEquals("anotherColumn", queryParams.getOrder().getField());
        assertEquals(Order.Direction.ASC, queryParams.getOrder().getDirection());
    }
}
