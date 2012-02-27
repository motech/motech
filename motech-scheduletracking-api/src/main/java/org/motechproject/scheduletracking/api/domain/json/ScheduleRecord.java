package org.motechproject.scheduletracking.api.domain.json;

import java.util.ArrayList;
import java.util.List;

public class ScheduleRecord {
    private String name;
    private List<MilestoneRecord> milestones = new ArrayList<MilestoneRecord>();

    public String name() {
        return name;
    }

    public List<MilestoneRecord> milestoneRecords() {
        return milestones;
    }
}
