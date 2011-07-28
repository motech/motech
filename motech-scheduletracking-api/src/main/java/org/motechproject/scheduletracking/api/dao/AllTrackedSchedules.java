package org.motechproject.scheduletracking.api.dao;

import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.domain.factory.ScheduleFactory;
import org.motechproject.scheduletracking.api.userspecified.ScheduleRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

@Component
public class AllTrackedSchedules {
    @Autowired
    private TrackedSchedulesJsonReader jsonReader;
    private Dictionary<String, Schedule> schedules = new Hashtable<String, Schedule>();

    @Autowired
    public AllTrackedSchedules(TrackedSchedulesJsonReader jsonReader) {
        this.jsonReader = jsonReader;

        List<ScheduleRecord> scheduleRecords = jsonReader.records();
        for (ScheduleRecord scheduleRecord : scheduleRecords) {
            schedules.put(scheduleRecord.name(), ScheduleFactory.create(scheduleRecord));
        }
    }

    public Schedule get(String name) {
        return schedules.get(name);
    }
}