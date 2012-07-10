package org.motechproject.scheduletracking.api.repository;

import org.motechproject.scheduletracking.api.domain.json.ScheduleRecord;

import java.util.List;

public interface TrackedSchedulesJsonReader {
    public ScheduleRecord getSchedule(String schduleJson);
    public List<ScheduleRecord> getAllSchedules(String definitionsDirectoryName);
}
