package org.motechproject.scheduletracking.api.repository;

import org.motechproject.scheduletracking.api.userspecified.ScheduleRecord;

import java.util.List;

public interface TrackedSchedulesJsonReader {
    List<ScheduleRecord> records();
}
