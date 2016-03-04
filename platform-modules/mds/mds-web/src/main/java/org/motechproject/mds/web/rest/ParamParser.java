package org.motechproject.mds.web.rest;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.util.Order;
import org.motechproject.mds.web.exception.InvalidParameterException;

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
    public static final String LOOKUP_NAME = "lookup";
    public static final String ID = "id";
    public static final String INCLUDE_BLOB = "includeBlob";

    public static QueryParams buildQueryParams(Map<String, String> requestParams) {
        Integer page;
        Integer pageSize;
        String sortBy;
        String orderDir;
        Order order;

        try {
            page = getInteger(requestParams, PAGE, 1);
        } catch (NumberFormatException e) {
            throw new InvalidParameterException("Given page is not a number!", e);
        }

        try {
            pageSize = getInteger(requestParams, PAGE_SIZE, 20);
        } catch (NumberFormatException e) {
            throw new InvalidParameterException("Given page size is not a number!", e);
        }

        sortBy = requestParams.get(SORT_BY);
        orderDir = requestParams.get(ORDER_DIR);

        order = buildOrder(sortBy, orderDir);

        return new QueryParams(page, pageSize, order);
    }

    public static String getLookupName(Map<String, String> requestParams) {
        return requestParams.get(LOOKUP_NAME);
    }

    public static Long getId(Map<String, String> requestParams) {
        try {
            Long id = null;
            if (requestParams.containsKey(ID)) {
                id = Long.valueOf(requestParams.get(ID));
            }
            return id;
        } catch (NumberFormatException e) {
            throw new InvalidParameterException("Given ID is not a number!", e);
        }

    }

    public static Boolean getIncludeBlob(Map<String, String> requestParams) {

        Boolean includeBlob = null;

        if (requestParams.containsKey(INCLUDE_BLOB)) {

            String value = requestParams.get(INCLUDE_BLOB);

            if (value.toLowerCase().equals("true")) {
                includeBlob = Boolean.TRUE;
            } else if (value.toLowerCase().equals("false")) {
                includeBlob = Boolean.FALSE;
            } else {
                throw new InvalidParameterException("The \"includeBlob\" is neither \"true\" nor \"false\"");
            }
        }

        return includeBlob;
    }

    private static Integer getInteger(Map<String, String> requestParams, String key, Integer defaultVal) {
        return requestParams.containsKey(key) ? Integer.valueOf(requestParams.get(key)) : defaultVal;
    }

    private static Order buildOrder(String sortBy, String orderDir) {
        if (StringUtils.isBlank(sortBy) && StringUtils.isBlank(orderDir)) {
            return null;
        }

        String sortField = sortBy;
        if (StringUtils.isBlank(sortField)) {
            sortField = ID;
        }

        return new Order(sortField, orderDir);
    }

    private ParamParser() {
    }
}
