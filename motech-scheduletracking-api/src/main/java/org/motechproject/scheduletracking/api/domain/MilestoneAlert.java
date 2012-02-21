package org.motechproject.scheduletracking.api.domain;


import org.joda.time.LocalDate;

import java.io.Serializable;

public class MilestoneAlert implements Serializable {
    private String milestoneName;

    private LocalDate earliestDate;
    private LocalDate dueDate;
    private LocalDate lateDate;
    private LocalDate defaultmentDate;

    public static MilestoneAlert fromMilestone(Milestone milestone, LocalDate referenceDate){
        return new MilestoneAlert(milestone.getName(),
                getWindowEndDate(milestone, referenceDate, WindowName.earliest),
                getWindowEndDate(milestone, referenceDate, WindowName.due),
                getWindowEndDate(milestone, referenceDate, WindowName.late),
                getWindowEndDate(milestone, referenceDate, WindowName.max));
    }

    private static LocalDate getWindowEndDate(Milestone milestone, LocalDate referenceDate, WindowName windowName) {
        return referenceDate.plusDays(milestone.getMilestoneWindow(windowName).getEnd().inDays());
    }

    private MilestoneAlert() {
    }

    private MilestoneAlert(String milestoneName, LocalDate earliestDate, LocalDate dueDate, LocalDate lateDate, LocalDate defaultmentDate) {
        this.milestoneName = milestoneName;
        this.earliestDate = earliestDate;
        this.dueDate = dueDate;
        this.lateDate = lateDate;
        this.defaultmentDate = defaultmentDate;
    }

    public String getMilestoneName() {
        return milestoneName;
    }

    public LocalDate getEarliestDate() {
        return earliestDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getLateDate() {
        return lateDate;
    }

    public LocalDate getDefaultmentDate() {
        return defaultmentDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MilestoneAlert)) return false;

        MilestoneAlert that = (MilestoneAlert) o;

        if (defaultmentDate != null ? !defaultmentDate.equals(that.defaultmentDate) : that.defaultmentDate != null)
            return false;
        if (dueDate != null ? !dueDate.equals(that.dueDate) : that.dueDate != null) return false;
        if (earliestDate != null ? !earliestDate.equals(that.earliestDate) : that.earliestDate != null) return false;
        if (lateDate != null ? !lateDate.equals(that.lateDate) : that.lateDate != null) return false;
        if (milestoneName != null ? !milestoneName.equals(that.milestoneName) : that.milestoneName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = milestoneName != null ? milestoneName.hashCode() : 0;
        result = 31 * result + (earliestDate != null ? earliestDate.hashCode() : 0);
        result = 31 * result + (dueDate != null ? dueDate.hashCode() : 0);
        result = 31 * result + (lateDate != null ? lateDate.hashCode() : 0);
        result = 31 * result + (defaultmentDate != null ? defaultmentDate.hashCode() : 0);
        return result;
    }
}
