package org.motechproject.mds.web.util.query;

import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.Order;
import org.motechproject.mds.web.domain.GridSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Class responsible for building query params from the provided grid
 * settings and parameters. If no ID ordering is provided, ascending ID ordering will
 * be added at the by this builder to provide consistency. If no paging information is provided,
 * it default to the first page and/or 10 rows per page.
 */
public final class QueryParamsBuilder {

    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * Builds query params from the provided grid settings.
     * @param settings the grid settings received
     * @return the provided settings converted to query params
     */
    public static QueryParams buildQueryParams(GridSettings settings) {
        return buildQueryParams(settings, buildOrderList(settings, null));
    }

    /**
     * Builds query params from the provided grid settings and lookup map. If no explicit ordering is provided,
     * the order will be built using the fields we are doing the lookup on.
     * @param settings the grid settings received
     * @param lookupMap the map field map for the lookup - keys are field names, values are the values provided
     * @return query params for this request
     */
    public static QueryParams buildQueryParams(GridSettings settings, Map<String, Object> lookupMap) {
        return buildQueryParams(settings, buildOrderList(settings, lookupMap));
    }

    /**
     * Builds query params using the provided order list. The grid setting will be only used for pagination.
     * @param settings the grid settings received
     * @param orderList the order list that will be used for sorting
     * @return query params for this request
     */
    public static QueryParams buildQueryParams(GridSettings settings, List<Order> orderList) {
        // just check if the page is set
        int page = (settings.getPage() == null) ? DEFAULT_PAGE : settings.getPage();
        int pageSize = (settings.getRows() == null) ? DEFAULT_PAGE_SIZE : settings.getRows();

        QueryParams queryParams = new QueryParams(page, pageSize, orderList);

        if (!queryParams.containsOrderOnField(Constants.Util.ID_FIELD_NAME)) {
            queryParams.addOrder(orderIdAsc());
        }

        return queryParams;
    }

    /**
     * Builds an order list from the provided grid settings and lookup map. Can be used if we want to provide
     * the paging information manually.
     * @param settings the grid settings received
     * @param lookupMap the map field map for the lookup - keys are field names, values are the values provided
     * @return the order list built from the provided params
     */
    public static List<Order> buildOrderList(GridSettings settings, Map<String, Object> lookupMap) {
        if (settings.getSortColumn() != null && !settings.getSortColumn().isEmpty()) {
            // there is a specific order provide, i.e. the user clicked on a column to sort
            return orderListFromGridSettings(settings);
        } else if (lookupMap != null) {
            // we want to build a default order for a lookup using its fields
            return orderListForLookup(lookupMap);
        } else {
            // if this not a lookup and there is no ordering provided, order by ID ascending
            return Collections.singletonList(orderIdAsc());
        }
    }

    private static List<Order> orderListFromGridSettings(GridSettings settings) {
        Order order = new Order(settings.getSortColumn(), settings.getSortDirection());

        List<Order> orderList = new ArrayList<>();
        orderList.add(order);

        if (!Constants.Util.ID_FIELD_NAME.equalsIgnoreCase(order.getField())) {
            // if the ordering is done on a field other than id
            // we want to add the id ordering as backup, so that results stay consistent
            orderList.add(orderIdAsc());
        }

        return orderList;
    }

    private static List<Order> orderListForLookup(Map<String, Object> lookupMap) {
        List<Order> orderList = new ArrayList<>();

        for (String fieldName : lookupMap.keySet()) {
            // we do ascending on each lookup field, which is not related, by default
            if (!fieldName.contains(".")) {
                orderList.add(new Order(fieldName, Order.Direction.ASC));
            }
        }

        if (!lookupMap.containsKey(Constants.Util.ID_FIELD_NAME)) {
            // if this lookup has no id field, then add ordering by the id as the final order
            orderList.add(orderIdAsc());
        }

        return orderList;
    }

    private static Order orderIdAsc() {
        return new Order(Constants.Util.ID_FIELD_NAME, Order.Direction.ASC);
    }

    private QueryParamsBuilder() {
    }
}
