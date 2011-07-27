package org.motechproject.scheduletracking.api.dao;

import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.domain.factory.ScheduleFactory;
import org.motechproject.scheduletracking.api.userspecified.ScheduleRecord;

import java.util.List;

public class AllTrackedSchedules {
    private TrackedSchedulesJsonReader jsonReader;

    public AllTrackedSchedules(TrackedSchedulesJsonReader jsonReader) {
        this.jsonReader = jsonReader;
    }

    public Schedule get(String name) {
        List<ScheduleRecord> scheduleRecords = jsonReader.records();
        ScheduleRecord scheduleRecord = null;
        for (ScheduleRecord schedule : scheduleRecords) {
            if (schedule.name().equals(name)) scheduleRecord = schedule;
        }
        if (scheduleRecord == null) return null;

        return ScheduleFactory.create(scheduleRecord);
    }
}
