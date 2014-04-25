package org.motechproject.email.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * The <code>EmailRecords</code> class wraps the {@Link EmailRecord} list for view layer and
 * stores current item count.
 */

public class EmailRecords<T> {
    private Integer records; // total records
    private Integer total; // total pages
    private Integer page; // page number
    private List<T> rows;

    public EmailRecords() {
        this.records = 0;
        this.rows = new ArrayList<>();
    }

    public EmailRecords(Integer totalRecords, Integer page, Integer totalPages, List<T> allRecords) {
        this.page = page;
        this.records = totalRecords;
        this.total = totalPages;
        this.rows = allRecords;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getTotal() {
        return total;
    }

    public Integer getPage() {
        return page;
    }

    public Integer getRecords() {
        return records;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
        this.records = rows.size();
    }

    @Override
    public String toString() {
        return String.format("EmailRecords{page=%d, total=%d, records=%d, rows=%s}", page, total, records, rows);
    }
}
