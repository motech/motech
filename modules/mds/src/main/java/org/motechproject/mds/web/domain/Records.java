package org.motechproject.mds.web.domain;

import java.util.List;

/**
 * Class required for jqGrid to exchange data with server
 */
public class Records<T> {
    private Integer page;
    private Integer total;
    private Integer records;
    private List<T> rows;

    public Records(int page, int rows, List<T> list) {
        this.page = page;
        this.records = list.size();
        this.total = rows;
        this.rows = list;
    }
    public Records(List<T> list) {
        rows = list;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getRecords() {
        return records;
    }

    public void setRecords(Integer records) {
        this.records = records;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }
}
