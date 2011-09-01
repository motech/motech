package org.motechproject.scheduletracking.api.userspecified;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MilestoneRecord {
    private String name;
    private ScheduleWindowsRecord scheduleWindows;
    private List<AlertRecord> alerts = new ArrayList<AlertRecord>();
    private Map<String, String> data = new HashMap<String, String>();

    private MilestoneRecord() {
    }

    public MilestoneRecord(String name, ScheduleWindowsRecord scheduleWindows) {
        this.name = name;
        this.scheduleWindows = scheduleWindows;
    }

    public String name() {
        return name;
    }

    public ScheduleWindowsRecord scheduleWindowsRecord() {
        return scheduleWindows;
    }

    public List<AlertRecord> alerts() {
        return alerts;
    }

    public Map<String, String> data() {
        return data;
    }
}
