package org.motechproject.scheduletracking.api.dao;

import org.junit.Test;
import org.motechproject.dao.MotechJsonReader;
import org.motechproject.scheduletracking.api.userspecified.ScheduleRecord;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TrackedSchedulesJsonReaderTest {
    @Test
    public void records() {
        TrackedSchedulesJsonReader jsonReader = new TrackedSchedulesJsonReaderImpl("/simple-schedule.json", new MotechJsonReader());
        List<ScheduleRecord> records = jsonReader.records();
        assertEquals(1, records.size());
        ScheduleRecord scheduleRecord = records.get(0);
        assertEquals("IPTI Schedule", scheduleRecord.name());
    }
}
