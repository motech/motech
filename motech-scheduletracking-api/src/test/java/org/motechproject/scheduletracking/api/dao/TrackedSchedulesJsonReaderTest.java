package org.motechproject.scheduletracking.api.dao;

import org.junit.Test;
import org.motechproject.scheduletracking.api.userspecified.MilestoneRecord;
import org.motechproject.scheduletracking.api.userspecified.ScheduleRecord;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TrackedSchedulesJsonReaderTest {
    @Test
    public void shouldReadTheScheduleJsonFileCorrectly() {
        TrackedSchedulesJsonReader jsonReader = new TrackedSchedulesJsonReaderImpl("/simple-schedule.json");
        List<ScheduleRecord> records = jsonReader.records();
        assertEquals(1, records.size());
        ScheduleRecord scheduleRecord = records.get(0);
        assertEquals("IPTI Schedule", scheduleRecord.name());

        List<MilestoneRecord> milestones = scheduleRecord.milestoneRecords();
        assertEquals(2, milestones.size());

        MilestoneRecord firstMilestone = milestones.get(0);
        assertEquals("IPTI 1", firstMilestone.name());
	    
        MilestoneRecord secondMilestone = milestones.get(1);
        assertEquals("IPTI 2", secondMilestone.name());
    }
}
