package org.motechproject.scheduletracking.api.domain;

import org.joda.time.LocalDate;
import org.motechproject.scheduletracking.api.domain.enrollment.Enrollment;
import org.motechproject.valueobjects.WallTime;

import java.util.ArrayList;
import java.util.List;

public class Schedule extends Referenceable {
    private String name;
    private WallTime totalDuration;

    public Schedule(String name, WallTime totalDuration, Milestone firstMilestone) {
        super(firstMilestone);
        this.name = name;
        this.totalDuration = totalDuration;
    }

    public List<Alert> alertsFor(LocalDate enrolledDate, String dueMilestoneName) {
        List<Alert> alerts = new ArrayList<Alert>();

        Milestone dueMilestone = milestone(dueMilestoneName);

        WindowName windowName = dueMilestone.applicableWindow(enrolledDate);
        if (WindowName.Due.compareTo(windowName) <= 0) {
            alerts.add(new Alert(windowName, dueMilestone));
        }

        return alerts;
    }

    public Milestone getFirstMilestone() {
        return getNext();
    }

    public String getName() {
        return name;
    }

    public Milestone milestone(String name) {
        Milestone result = getFirstMilestone();
        while (result != null && !result.getName().equals(name)) result = result.getNext();
        return result;
    }

    public LocalDate getEndDate(LocalDate startDate) {
        return startDate.plusDays(totalDuration.inDays());
    }

    public Enrollment newEnrollment(String externalId, LocalDate enrollDate) {
        return new Enrollment(externalId, enrollDate, getName(), getFirstMilestone().getName());
    }

    public Enrollment newEnrollment(String externalId) {
        return newEnrollment(externalId, LocalDate.now());
    }

    public String nextMilestone(String milestoneName) {
        Milestone milestone = milestone(milestoneName);
        Milestone next = milestone.getNext();

        return next == null ? null : next.getName();
    }
}
