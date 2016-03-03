package org.motechproject.scheduler.contract;

/**
 * <code>JobsSearchSettings</code> is the class used for passing search criteria to the Service Layer,
 * it tells how the <code>MotechSchedulerDatabaseService</code> should filter jobs information.
 *
 * @see org.motechproject.scheduler.service.MotechSchedulerDatabaseService
 * @see org.motechproject.scheduler.web.controller.JobsController
 */
public class JobsSearchSettings {
    private String name;
    private Integer rows;
    private Integer page;
    private String sortColumn;
    private String sortDirection;
    private String activity;
    private String status;
    private String timeFrom;
    private String timeTo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(String timeFrom) {
        this.timeFrom = timeFrom;
    }

    public String getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(String timeTo) {
        this.timeTo = timeTo;
    }
}
