package org.motechproject.scheduletracking.api.userspecified;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class MilestoneRecord {
    private String name;
    private String referenceDate;
    private ScheduleWindowsRecord scheduleWindowsRecord;
    private List<AlertRecord> alerts = new ArrayList<AlertRecord>();
    private Dictionary<String, String> data = new Hashtable<String, String>();

    public MilestoneRecord() {
    }

    public MilestoneRecord(String name, String referenceDate, ScheduleWindowsRecord scheduleWindowsRecord) {
        this.name = name;
        this.referenceDate = referenceDate;
        this.scheduleWindowsRecord = scheduleWindowsRecord;
    }

    public String name() {
        return name;
    }

    public String referenceDate() {
        return referenceDate;
    }

    public ScheduleWindowsRecord scheduleWindowsRecord() {
        return scheduleWindowsRecord;
    }

    public List<AlertRecord> alerts() {
        return alerts;
    }
}
