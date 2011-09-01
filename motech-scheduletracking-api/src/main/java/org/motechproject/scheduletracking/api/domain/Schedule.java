package org.motechproject.scheduletracking.api.domain;

import org.joda.time.LocalDate;
import org.motechproject.scheduletracking.api.domain.enrollment.Enrollment;
import org.motechproject.valueobjects.WallTime;

import java.util.*;

public class Schedule extends Referenceable {
    private String name;
    private WallTime totalDuration;
    private Map<String, Milestone> milestones = new LinkedHashMap<String, Milestone>();

    public Schedule(String name, WallTime totalDuration, Referenceable firstMilestone) {
        super(firstMilestone);
        this.name = name;
        this.totalDuration = totalDuration;
    }

    public List<Alert> alertsFor(LocalDate enrolledDate) {
        List<Alert> alerts = new ArrayList<Alert>();

        for (Milestone milestone : milestones.values()) {
            WindowName windowName = milestone.applicableWindow(enrolledDate);
            if (WindowName.Due.compareTo(windowName) <= 0) {
                alerts.add(new Alert(windowName, milestone));
            }
        }

        return alerts;
    }

    public Milestone getFirstMilestone() {
        return (Milestone) getNext();
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
        return new Enrollment(externalId, enrollDate, this);
    }

    public Enrollment newEnrollment(String externalId) {
        return newEnrollment(externalId, LocalDate.now());
    }
}
