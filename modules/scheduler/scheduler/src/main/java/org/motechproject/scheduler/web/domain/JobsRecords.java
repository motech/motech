package org.motechproject.scheduler.web.domain;

import java.util.ArrayList;
import java.util.List;
import org.motechproject.scheduler.domain.JobBasicInfo;

/**
 * JobsRecords is the class which wraps the JobBasicInfo list for view layer.
 *
 * @see JobBasicInfo
 * */

public class JobsRecords {
    private Integer page;
    private Integer total;
    private Integer records;
    private List<JobBasicInfo> rows;

    public JobsRecords(Integer page, Integer rows, List<JobBasicInfo> allRecords) {
        this.page = page;
        this.records = allRecords.size();
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
