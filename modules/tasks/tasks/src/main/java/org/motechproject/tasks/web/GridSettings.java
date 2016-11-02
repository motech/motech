package org.motechproject.tasks.web;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tasks.domain.enums.TaskActivityType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Models the audit log filter settings UI
 */
public class GridSettings {

    /**
     * The number of rows to display per page.
     */
    private Integer rows;

    /**
     * The page to display, starting from 1.
     */
    private Integer page;

    /**
     * The activity type to search for.
     */
    private String activityType;


    /**
     * @return the number of rows to display per page
     */
    public Integer getRows() {
        return rows;
    }

    /**
     * @param rows the number of rows to display per page
     */
    public void setRows(Integer rows) {
        this.rows = rows;
    }

    /**
     * @return the page to display, starting from 1
     */
    public Integer getPage() {
        return page;
    }

    /**
     * @param page the page to display, starting from 1
     */
    public void setPage(Integer page) {
        this.page = page;
    }

    /**
     * @return the activity types to display
     */
    public String getActivityType() {
        return activityType;
    }

    /**
     * @param activityType the activity types to display
     */
    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public Set<TaskActivityType> getTypesFromString() {
        Set<TaskActivityType> types = new HashSet<>();
        if (StringUtils.isNotBlank(activityType)) {
            String[] statuses = activityType.split(",");
            for (String status : statuses) {
                if (!status.isEmpty()) {
                    types.add(TaskActivityType.valueOf(status));
                }
            }
        } else {
            types.addAll(Arrays.asList(TaskActivityType.values()));
        }
        return types;
    }

    @Override
    public String toString() {
        return "GridSettings{" +
                "rows=" + rows +
                ", page=" + page +
                ", activityType='" + activityType + '\'' +
                '}';
    }
}
