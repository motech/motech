package org.motechproject.mds.util;

import java.io.Serializable;

/**
 * Utility class containing parameters which control order and size of query results.
 * Used mainly for paging/ordering queries from the UI.
 */
public class QueryParams implements Serializable {

    private static final long serialVersionUID = 8635166802886633897L;

    private final Integer page;
    private final Integer pageSize;
    private final Order order;

    public QueryParams(Integer page, Integer pageSize) {
        this(page, pageSize, null);
    }

    public QueryParams(Order order) {
        this(null, null, order);
    }

    public QueryParams(Integer page, Integer pageSize, Order order) {
        this.page = page;
        this.pageSize = pageSize;
        this.order = order;
    }

    public Integer getPage() {
        return page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public Order getOrder() {
        return order;
    }

    public boolean isOrderSet() {
        return order != null;
    }

    public boolean isPagingSet() {
        return page != null && pageSize != null;
    }
}
