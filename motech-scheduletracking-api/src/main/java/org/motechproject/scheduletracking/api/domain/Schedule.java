package org.motechproject.scheduletracking.api.domain;

import org.joda.time.LocalDate;
import org.motechproject.scheduletracking.api.domain.enrollment.Enrollment;
import org.motechproject.valueobjects.WallTime;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class Schedule implements Referenceable {
    private String name;
    private WallTime totalDuration;
    private Map<String, Milestone> milestones = new LinkedHashMap<String, Milestone>();

    public Schedule(String name, WallTime totalDuration) {
        this.name = name;
        this.totalDuration = totalDuration;
    }

    public String name() {
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

    public Alert alertFor(Enrollment enrollment) {
        WindowName windowName = null;
        Milestone milestone = null;
        for (Milestone currentMilestone : milestones.values()) {
            windowName = currentMilestone.applicableWindow(enrollment);
            milestone = currentMilestone;
            if (windowName != null) break;
        }
        if (windowName == null) return null;
        return new Alert(windowName, milestone);
    }
}
