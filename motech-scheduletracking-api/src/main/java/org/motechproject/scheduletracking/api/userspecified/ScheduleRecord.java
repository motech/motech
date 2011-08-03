package org.motechproject.scheduletracking.api.userspecified;

import java.util.ArrayList;
import java.util.List;

public class ScheduleRecord {
    private String name;
    private String totalDuration;
    private List<MilestoneRecord> milestones = new ArrayList<MilestoneRecord>();

    private ScheduleRecord() {
    }

    public ScheduleRecord(String name, String totalDuration) {
        this.name = name;
        this.totalDuration = totalDuration;
    }

    public String name() {
        return name;
    }

    public List<MilestoneRecord> milestoneRecords() {
        return milestones;
    }

    public String totalDuration() {
        return totalDuration;
    }

    public ScheduleRecord addMilestoneRecord(MilestoneRecord milestoneRecord) {
        milestoneRecords().add(milestoneRecord);
        return this;
    }
}
