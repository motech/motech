package org.motechproject.scheduler.web.domain;

import org.motechproject.scheduler.contract.JobBasicInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * JobsRecords is the class which wraps the JobBasicInfo list for view layer.
 *
 * @see JobBasicInfo
 */
public class JobsRecords {
    private Integer page;
    private Integer total;
    private Integer records;
    private List<JobBasicInfo> rows;

    /**
     * Constructor.
     *
     * @param page  the number of current page
     * @param rows  the size of the page
     * @param allRecords  the list of all records
     */
    public JobsRecords(Integer page, Integer rows, Integer records, List<JobBasicInfo> allRecords) {
        this.page = page;
        this.records = records;
        this.total = (this.records <= rows) ? 1 : (this.records / rows) + 1;

        this.rows = new ArrayList<>(allRecords);
    }

    public Integer getPage() {
        return page;
    }

    public Integer getTotal() {
        return total;
    }

    public Integer getRecords() {
        return records;
    }

    public List<JobBasicInfo> getRows() {
        return rows;
    }

    @Override
    public String toString() {
        return String.format("JobsRecords{page=%d, total=%d, records=%d, rows=%s}", page, total, records, rows);
    }
}
