package org.motechproject.scheduletracking.api.dao;

import org.junit.Test;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.userspecified.ScheduleRecord;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AllTrackedSchedulesTest {
    @Test
    public void getSchedule() {
        TrackedSchedulesJsonReader trackedSchedulesJsonReader = mock(TrackedSchedulesJsonReader.class);
        List<ScheduleRecord> records = trackedSchedulesJsonReader.records();
        AllTrackedSchedules allTrackedSchedules = new AllTrackedSchedules(trackedSchedulesJsonReader);
        Schedule schedule = allTrackedSchedules.get("foo");
        assertNull(schedule);
    }
}
