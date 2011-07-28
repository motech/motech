package org.motechproject.scheduletracking.api.domain;

import java.util.ArrayList;
import java.util.List;

public class Schedule {
    private String name;
    private List<Milestone> milestones = new ArrayList<Milestone>();

    public Schedule(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addMilestone(Milestone milestone) {
        milestones.add(milestone);
    }

    public List<Alert> alerts(String enroledInMilestone, int enroledAt) {
        return null;
    }
}
