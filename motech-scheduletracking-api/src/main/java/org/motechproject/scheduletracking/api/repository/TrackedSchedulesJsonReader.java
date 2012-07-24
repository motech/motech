package org.motechproject.scheduletracking.api.repository;

import org.motechproject.scheduletracking.api.domain.json.ScheduleRecord;

import java.util.List;

public interface TrackedSchedulesJsonReader {
    ScheduleRecord getSchedule(String schduleJson);
    List<ScheduleRecord> getAllSchedules(String definitionsDirectoryName);
}
