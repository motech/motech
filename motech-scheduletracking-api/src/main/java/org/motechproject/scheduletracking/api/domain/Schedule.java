package org.motechproject.scheduletracking.api.domain;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.MutablePeriod;
import org.joda.time.Period;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.motechproject.util.DateUtil.now;
import static org.motechproject.util.DateUtil.today;

public class Schedule implements Serializable {
    private String name;
    private List<Milestone> milestones = new ArrayList<Milestone>();

    public Schedule(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addMilestones(Milestone... milestonesList) {
        milestones.addAll(Arrays.asList(milestonesList));
    }

    public Milestone getFirstMilestone() {
        return milestones.get(0);
    }

    public List<Milestone> getMilestones() {
        return milestones;
    }

    public Milestone getMilestone(String milestoneName) {
        for (Milestone milestone : milestones)
            if (milestone.getName().equals(milestoneName))
                return milestone;
        return null;
    }

    public String getNextMilestoneName(String currentMilestoneName) {
        int currentIndex = milestones.indexOf(getMilestone(currentMilestoneName));
        if (currentIndex < milestones.size() - 1)
            return milestones.get(currentIndex + 1).getName();
        return null;
    }

    public Period getDuration() {
        MutablePeriod duration = new MutablePeriod();
        for (Milestone milestone : milestones) {
            duration.add(milestone.getMaximumDuration());
        }
        return duration.toPeriod();
    }

    public boolean hasExpiredSince(DateTime referenceDateTime) {
        return referenceDateTime.plus(getDuration()).isBefore(now());
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
