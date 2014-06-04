package org.motechproject.email.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * The <code>EmailRecords</code> class wraps the {@link EmailRecord} list and
 * stores the current item count.
 */

public class EmailRecords<T> {
    private Integer records; // total records
    private Integer total; // total pages
    private Integer page; // page number
    private List<T> rows;

    /**
     * Creates a new instance of <code>EmailRecords</code>, which contains no records.
     */
    public EmailRecords() {
        this.records = 0;
        this.rows = new ArrayList<>();
    }

    /**
     * Creates a new instance of <code>EmailRecords</code>, with all fields set to
     * the values specified in the parameters. The <code>page</code> and <code>totalPages</code>
     * parameters are for the purposes of paginating the list of records in the UI.
     *
     * @param totalRecords  the total number of records
     * @param page  the current page
     * @param totalPages  the total number of pages
     * @param allRecords  the list of records
     */
    public EmailRecords(Integer totalRecords, Integer page, Integer totalPages, List<T> allRecords) {
        this.page = page;
        this.records = totalRecords;
        this.total = totalPages;
        this.rows = allRecords;
    }

    /**
     * Sets the total number of records.
     *
     * @param total  the total number of records
     */
    public void setTotal(Integer total) {
        this.total = total;
    }

    /**
     * Gets the total number of pages.
     *
     * @return the total number of pages
     */
    public Integer getTotal() {
        return total;
    }

    /**
     * Gets the current page.
     *
     * @return the current page
     */
    public Integer getPage() {
        return page;
    }

    /**
     * Gets the total number of records.
     *
     * @return the total number of records
     */
    public Integer getRecords() {
        return records;
    }

    /**
     * Gets the list of records.
     *
     * @return the list of records
     */
    public List<T> getRows() {
        return rows;
    }

    /**
     * Sets the list of records.
     *
     * @param rows  the list of records
     */
    public void setRows(List<T> rows) {
        this.rows = rows;
        this.records = rows.size();
    }

    /**
     * Returns a string representation of this <code>EmailRecords</code> object.
     *
     * @return a string representation of this <code>EmailRecords</code> object
     */
    @Override
    public String toString() {
        return String.format("EmailRecords{page=%d, total=%d, records=%d, rows=%s}", page, total, records, rows);
    }
}
