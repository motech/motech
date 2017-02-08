package org.motechproject.mds.query;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.Order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class containing parameters which control order and size of query results.
 * Used mainly for paging/ordering queries from the UI.
 */
public class QueryParams implements Serializable {
    private static final long serialVersionUID = 8635166802886633897L;

    private final Integer page;
    private final Integer pageSize;
    private final List<Order> orderList;
    private final String groupingColumn;

    /**
     * Constant query parameter, that orders records ascending by ID.
     */
    public static final QueryParams ORDER_ID_ASC = new QueryParams(new Order(Constants.Util.ID_FIELD_NAME, Order.Direction.ASC));

    /**
     * Creates query parameters.
     *
     * @param page number of page
     * @param pageSize amount of entries to include, per page
     */
    public QueryParams(Integer page, Integer pageSize) {
        this(page, pageSize, new ArrayList<Order>());
    }

    /**
     * Creates query parameters.
     *
     * @param order specifies order of the records
     */
    public QueryParams(Order order) {
        this(null, null, order);
    }

    /**
     * Creates query parameters.
     *
     * @param orderList the list of order instructions that will be applied to the query
     */
    public QueryParams(List<Order> orderList) {
        this(null, null, orderList);
    }

    /**
     * Creates query parameters.
     *
     * @param page number of page
     * @param pageSize amount of entries to include, per page
     * @param order specifies order of the records
     */
    public QueryParams(Integer page, Integer pageSize, Order order) {
        this(page, pageSize, order, null);
    }

    public QueryParams(Integer page, Integer pageSize, Order order, String groupingColumn) {
        this.page = page;
        this.pageSize = pageSize;
        this.orderList = new ArrayList<>();
        if (order != null) {
            orderList.add(order);
        }
        this.groupingColumn = groupingColumn != null ? groupingColumn : "";
    }

    /**
     * Creates query parameters.
     *
     * @param page number of page
     * @param pageSize amount of entries to include, per page
     * @param orderList the list of order instructions that will be applied to the query
     */
    public QueryParams(Integer page, Integer pageSize, List<Order> orderList) {
        this(page, pageSize, orderList, "");
    }

    public QueryParams(Integer page, Integer pageSize, List<Order> orderList, String groupingColumn) {
        this.page = page;
        this.pageSize = pageSize;
        this.orderList = (orderList == null) ? new ArrayList<>() : orderList;
        this.groupingColumn = groupingColumn;
    }

    public Integer getPage() {
        return page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public String getGroupingColumn() {
        return this.groupingColumn;
    }

    public boolean isOrderSet() {
        return !orderList.isEmpty();
    }

    public boolean isGroupingSet() {
        return !this.groupingColumn.isEmpty();
    }

    public boolean isPagingSet() {
        return page != null && pageSize != null;
    }

    public void addOrder(Order order) {
        orderList.add(order);
    }

    public boolean containsOrderOnField(String fieldName) {
        for (Order order : orderList) {
            if (StringUtils.equals(fieldName, order.getField())) {
                return true;
            }
        }
        return false;
    }

    /**
      * Creates query parameter that sorts records ascending, by the given field.
      *
      * @param field field to sort records by
      * @return query parameter, ordering records ascending
      */
    public static QueryParams ascOrder(String field) {
        return new QueryParams(new Order(field, Order.Direction.ASC));
    }

    /**
      * Creates query parameter that sorts records descending, by the given field.
      *
      * @param field field to sort records by
      * @return query parameter, ordering records descending
      */
    public static QueryParams descOrder(String field) {
        return new QueryParams(new Order(field, Order.Direction.DESC));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o instanceof QueryParams) {
            QueryParams other = (QueryParams) o;

            return ObjectUtils.equals(page, other.page)
                    && ObjectUtils.equals(pageSize, other.pageSize)
                    && ObjectUtils.equals(orderList, other.orderList)
                    && ObjectUtils.equals(groupingColumn, other.groupingColumn);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(page)
                .append(pageSize)
                .append(orderList)
                .append(groupingColumn)
                .toHashCode();
    }
}
