package org.motechproject.scheduletracking.api.domain;

import org.joda.time.LocalDate;
import org.motechproject.scheduletracking.api.domain.enrollment.Enrollment;
import org.motechproject.valueobjects.WallTime;

import java.util.*;

public class Schedule implements Referenceable {
    private String name;
    private WallTime totalDuration;
    private Map<String, Milestone> milestones = new LinkedHashMap<String, Milestone>();

    public Schedule(String name, WallTime totalDuration) {
        this.name = name;
        this.totalDuration = totalDuration;
    }

    public List<Alert> alertsFor(LocalDate enrolledDate) {
        ArrayList<Alert> alerts = new ArrayList<Alert>();

        for (Milestone currentMilestone : milestones.values()) {
            WindowName windowName = currentMilestone.applicableWindow(enrolledDate);
            alerts.add(new Alert(windowName, currentMilestone));
        }

        return alerts;
    }

    public String getName() {
        return name;
    }

    public void addMilestone(Milestone milestone) {
        milestones.put(milestone.name(), milestone);
    }

    public Milestone milestone(String name) {
        return milestones.get(name);
    }

    public Date endDate() {
        LocalDate localDate = new LocalDate();
        return localDate.plusDays(totalDuration.inDays()).toDateTimeAtCurrentTime().toDate();
    }

    public Enrollment newEnrollment(String externalId, LocalDate enrollDate) {
        return new Enrollment(externalId, enrollDate, getName());
    }

    public Enrollment newEnrollment(String externalId) {
        return newEnrollment(externalId, LocalDate.now());
    }
}
