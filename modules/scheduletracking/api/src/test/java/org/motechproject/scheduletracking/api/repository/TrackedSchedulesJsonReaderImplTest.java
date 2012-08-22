package org.motechproject.scheduletracking.api.repository;

import org.junit.Test;
import org.motechproject.scheduletracking.api.domain.json.MilestoneRecord;
import org.motechproject.scheduletracking.api.domain.json.ScheduleRecord;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TrackedSchedulesJsonReaderImplTest {
    @Test
    public void shouldReadTheScheduleJsonFileCorrectly() {
        List<ScheduleRecord> records = new TrackedSchedulesJsonReaderImpl().getAllSchedules("/schedules");
        assertEquals(10, records.size());

        ScheduleRecord iptScheduleRecord = findRecord("IPTI Schedule", records);
        ScheduleRecord eddScheduleRecord = findRecord("Delivery", records);

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
        List<ScheduleRecord> records = new TrackedSchedulesJsonReaderImpl().getAllSchedules("/schedules");
        ScheduleRecord scheduleRecord = findRecord("IPTI Schedule", records);
        MilestoneRecord secondMilestone = scheduleRecord.milestoneRecords().get(1);
        assertEquals("", secondMilestone.scheduleWindowsRecord().max().get(0));
    }

    @Test
    public void shouldBeAbleReadJsonFilesFromADirectory() {
        assertNotNull(new TrackedSchedulesJsonReaderImpl().getAllSchedules("/schedules"));
    }

    private ScheduleRecord findRecord(String name, List<ScheduleRecord> records) {
        for (ScheduleRecord record : records)
            if (record.name().equals(name))
                return record;
        return null;
    }
}
