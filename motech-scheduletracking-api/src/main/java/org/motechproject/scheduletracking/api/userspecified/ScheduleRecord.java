package org.motechproject.scheduletracking.api.userspecified;

import java.util.ArrayList;
import java.util.List;

public class ScheduleRecord {
    private String name;
    private List<MilestoneRecord> milestoneRecords = new ArrayList<MilestoneRecord>();

    public String name() {
        return name;
    }

    public List<MilestoneRecord> milestoneRecords() {
        return milestoneRecords;
    }

    public ScheduleRecord addMilestoneRecord(MilestoneRecord milestoneRecord) {
        milestoneRecords().add(milestoneRecord);
        return this;
    }
}
