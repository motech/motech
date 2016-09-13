package org.motechproject.tasks.web;

import org.motechproject.tasks.dto.TaskActivityDto;

import java.io.Serializable;
import java.util.List;

/**
 * Task activity collection for the tasks UI
 */
public class TaskActivityRecords implements Serializable {

    private static final long serialVersionUID = -410135562206960222L;

    /**
     * The page number.
     */
    private final Integer page;

    /**
     * The number of rows per page.
     */
    private final Integer total;

    /**
     * The total number of records.
     */
    private final Long records;

    /**
     * The data to display in the grid.
     */
    private final List<TaskActivityDto> rows;

    /**
     * Constructs an sms logging view for the jq grid.
     * @param page the page number
     * @param rows the number of rows per page
     * @param totalRecords the total number of records
     * @param taskActivities the data to display in the grid
     */
    public TaskActivityRecords(Integer page, Integer rows, Long totalRecords, List<TaskActivityDto> taskActivities) {
        this.page = page;
        this.records = totalRecords;
        this.total = rows;
        this.rows = taskActivities;
    }

    /**
     * @return the page number
     */
    public Integer getPage() {
        return page;
    }

    /**
     * @return the number of rows per page
     */
    public Integer getTotal() {
        return total;
    }

    /**
     * @return the total number of records
     */
    public Long getRecords() {
        return records;
    }

    /**
     * @return the data display in the grid
     */
    public List<TaskActivityDto> getRows() {
        return rows;
    }

    @Override
    public String toString() {
        return String.format("TaskActivity{page=%d, total=%d, records=%d, rows=%s}", page, total, records, rows);
    }
}
