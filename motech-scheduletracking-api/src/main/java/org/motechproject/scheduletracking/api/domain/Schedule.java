package org.motechproject.scheduletracking.api.domain;

import org.joda.time.LocalDate;
import org.motechproject.scheduletracking.api.domain.enrollment.Enrollment;
import org.motechproject.valueobjects.WallTime;

import java.util.ArrayList;
import java.util.Date;
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
        while (result != null && !result.name().equals(name)) result = result.getNext();
        return result;
    }

    public Date endDate() {
        LocalDate localDate = new LocalDate();
        return localDate.plusDays(totalDuration.inDays()).toDateTimeAtCurrentTime().toDate();
    }

    public Enrollment newEnrollment(String externalId, LocalDate enrollDate) {
        return new Enrollment(externalId, enrollDate, getName(), getFirstMilestone().name());
    }

    public Enrollment newEnrollment(String externalId) {
        return newEnrollment(externalId, LocalDate.now());
    }

    public String nextMilestone(String milestoneName) {
        Milestone milestone = milestone(milestoneName);
        Milestone next = milestone.getNext();

        if (next == null) return null;
        return next.name();
    }
}
