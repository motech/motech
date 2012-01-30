package org.motechproject.scheduletracking.api.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
