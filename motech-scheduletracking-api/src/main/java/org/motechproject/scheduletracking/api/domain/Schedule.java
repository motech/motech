package org.motechproject.scheduletracking.api.domain;

import org.joda.time.LocalDate;
import org.motechproject.valueobjects.WallTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Schedule implements Serializable {

    private String name;
    private WallTime totalDuration;
    private List<Milestone> milestones = new ArrayList<Milestone>();

    public Schedule(String name, WallTime totalDuration) {
        this.name = name;
        this.totalDuration = totalDuration;
    }

    public String getName() {
        return name;
    }

    public void addMilestones(Milestone... milestonesList) {
        milestones.addAll(Arrays.asList(milestonesList));
    }

    public List<Milestone> getMilestones() {
        return milestones;
    }

    public Milestone getFirstMilestone() {
        return milestones.get(0);
    }

    public Milestone getMilestone(String milestoneName) {
        for (Milestone milestone : milestones)
            if (milestone.getName().equals(milestoneName))
                return milestone;
        return null;
    }

    public LocalDate getEndDate(LocalDate startDate) {
        return startDate.plusDays(totalDuration.inDays());
    }

    public List<AlertEvent> getAlerts(LocalDate lastFulfilledDate, String currentMilestoneName) {
        List<AlertEvent> alertEvents = new ArrayList<AlertEvent>();
        Milestone milestone = getMilestone(currentMilestoneName);

        WindowName windowName = milestone.getApplicableWindow(lastFulfilledDate);
        alertEvents.add(new AlertEvent(windowName, milestone));

        return alertEvents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Schedule schedule = (Schedule) o;

        if (name != null ? !name.equals(schedule.name) : schedule.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
