package org.motechproject.scheduletracking.api.dao;

import org.motechproject.scheduletracking.api.userspecified.ScheduleRecord;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

public interface TrackedSchedulesJsonReader {
    List<ScheduleRecord> records();
}
