package org.motechproject.mds.web.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Class required for jqGrid to exchange data with server
 */
public class Records<T> {
    private Integer page;
    private Integer total;
    private Integer records;
    private List<T> rows;

    public Records(Integer page, Integer rows, List<T> list) {
        if (page != null && rows != null) {
            this.page = page;
            records = list.size();
            this.total = (this.records <= rows) ? 1 : (this.records / rows) + 1;
            this.rows = new ArrayList<>(list.subList((page - 1) * rows, (page * rows > this.records ? this.records : page * rows)));
        } else {
            this.rows = list;
        }
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
