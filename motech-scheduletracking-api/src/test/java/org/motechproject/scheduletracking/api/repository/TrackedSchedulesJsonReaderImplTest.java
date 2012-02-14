package org.motechproject.scheduletracking.api.repository;

import org.junit.Test;
import org.motechproject.scheduletracking.api.domain.userspecified.MilestoneRecord;
import org.motechproject.scheduletracking.api.domain.userspecified.ScheduleRecord;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TrackedSchedulesJsonReaderImplTest {

    @Test
    public void shouldReadTheScheduleJsonFileCorrectly() {
        TrackedSchedulesJsonReader jsonReader = new TrackedSchedulesJsonReaderImpl("/simple-schedule.json");
        List<ScheduleRecord> records = jsonReader.records();
        assertEquals(2, records.size());
        ScheduleRecord iptScheduleRecord = records.get(0);
        ScheduleRecord eddScheduleRecord = records.get(1);

        assertEquals("IPTI Schedule", iptScheduleRecord.name());
        assertEquals("Delivery", eddScheduleRecord.name());

        List<MilestoneRecord> milestoneRecords = iptScheduleRecord.milestoneRecords();
        assertEquals(2, milestoneRecords.size());
        assertEquals("IPTI 1", milestoneRecords.get(0).name());
        assertEquals("IPTI 2", milestoneRecords.get(1).name());

        milestoneRecords = eddScheduleRecord.milestoneRecords();
        assertEquals(1, milestoneRecords.size());
        assertEquals("Default", milestoneRecords.get(0).name());
    }

    @Test
    public void shouldReadEmptyValues() {
        TrackedSchedulesJsonReader jsonReader = new TrackedSchedulesJsonReaderImpl("/simple-schedule.json");
        List<ScheduleRecord> records = jsonReader.records();
        ScheduleRecord scheduleRecord = records.get(0);
        MilestoneRecord secondMilestone = scheduleRecord.milestoneRecords().get(1);
        assertEquals("", secondMilestone.scheduleWindowsRecord().max());
    }
}
