package org.motechproject.mds.web.rest;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.util.Order;

import java.util.Map;

/**
 * A class parsing request params map from REST requests.
 * Helps build query params from requestParams.
 */
public final class ParamParser {

    public static final String PAGE = "page";
    public static final String PAGE_SIZE = "pageSize";
    public static final String SORT_BY = "sort";
    public static final String ORDER_DIR = "order";

    public static QueryParams buildQueryParams(Map<String, String> requestParams) {
        Integer page = getInteger(requestParams, PAGE);
        Integer pageSize = getInteger(requestParams, PAGE_SIZE);
        String sortBy = requestParams.get(SORT_BY);
        String orderDir = requestParams.get(ORDER_DIR);

        Order order = buildOrder(sortBy, orderDir);

        return new QueryParams(page, pageSize, order);
    }

    private static Integer getInteger(Map<String, String> requestParams, String key) {
        return requestParams.containsKey(key) ? Integer.valueOf(requestParams.get(key)) : null;
    }

    private static Order buildOrder(String sortBy, String orderDir) {
        if (StringUtils.isBlank(sortBy) && StringUtils.isBlank(orderDir)) {
            return null;
        }

        String sortField = sortBy;
        if (StringUtils.isBlank(sortField)) {
            sortField = "id";
        }

        return new Order(sortField, orderDir);
    }

    private ParamParser() {
    }
}
