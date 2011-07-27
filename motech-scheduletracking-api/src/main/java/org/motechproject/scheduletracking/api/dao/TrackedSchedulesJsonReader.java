package org.motechproject.scheduletracking.api.dao;

import org.motechproject.scheduletracking.api.userspecified.ScheduleRecord;

import java.util.List;

public interface TrackedSchedulesJsonReader {
    List<ScheduleRecord> records();
}
