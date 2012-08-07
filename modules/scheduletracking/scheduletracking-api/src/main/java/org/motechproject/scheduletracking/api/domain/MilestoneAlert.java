package org.motechproject.scheduletracking.api.domain;


import org.joda.time.DateTime;

import java.io.Serializable;

public final class MilestoneAlert implements Serializable {
    private String milestoneName;

    private DateTime earliestDateTime;
    private DateTime dueDateTime;
    private DateTime lateDateTime;
    private DateTime defaultmentDateTime;

    public static MilestoneAlert fromMilestone(Milestone milestone, DateTime startOfMilestone) {
        return new MilestoneAlert(milestone.getName(),
                getWindowStartDate(milestone, startOfMilestone, WindowName.earliest),
                getWindowStartDate(milestone, startOfMilestone, WindowName.due),
                getWindowStartDate(milestone, startOfMilestone, WindowName.late),
                getWindowStartDate(milestone, startOfMilestone, WindowName.max));
    }

    private static DateTime getWindowStartDate(Milestone milestone, DateTime startOfMilestone, WindowName windowName) {
        return startOfMilestone.plus(milestone.getWindowStart(windowName));
    }

    private MilestoneAlert() {
    }

    private MilestoneAlert(String milestoneName, DateTime earliestDateTime, DateTime dueDateTime, DateTime lateDateTime, DateTime defaultmentDateTime) {
        this.milestoneName = milestoneName;
        this.earliestDateTime = earliestDateTime;
        this.dueDateTime = dueDateTime;
        this.lateDateTime = lateDateTime;
        this.defaultmentDateTime = defaultmentDateTime;
    }

    public String getMilestoneName() {
        return milestoneName;
    }

    public DateTime getEarliestDateTime() {
        return earliestDateTime;
    }

    public DateTime getDueDateTime() {
        return dueDateTime;
    }

    public DateTime getLateDateTime() {
        return lateDateTime;
    }

    public DateTime getDefaultmentDateTime() {
        return defaultmentDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MilestoneAlert)) {
            return false;
        }

        MilestoneAlert that = (MilestoneAlert) o;

        if (defaultmentDateTime != null ? !defaultmentDateTime.equals(that.defaultmentDateTime) : that.defaultmentDateTime != null) {
            return false;
        }
        if (dueDateTime != null ? !dueDateTime.equals(that.dueDateTime) : that.dueDateTime != null) {
            return false;
        }
        if (earliestDateTime != null ? !earliestDateTime.equals(that.earliestDateTime) : that.earliestDateTime != null) {
            return false;
        }
        if (lateDateTime != null ? !lateDateTime.equals(that.lateDateTime) : that.lateDateTime != null) {
            return false;
        }
        if (milestoneName != null ? !milestoneName.equals(that.milestoneName) : that.milestoneName != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = milestoneName != null ? milestoneName.hashCode() : 0;
        result = 31 * result + (earliestDateTime != null ? earliestDateTime.hashCode() : 0);
        result = 31 * result + (dueDateTime != null ? dueDateTime.hashCode() : 0);
        result = 31 * result + (lateDateTime != null ? lateDateTime.hashCode() : 0);
        result = 31 * result + (defaultmentDateTime != null ? defaultmentDateTime.hashCode() : 0);
        return result;
    }
}
