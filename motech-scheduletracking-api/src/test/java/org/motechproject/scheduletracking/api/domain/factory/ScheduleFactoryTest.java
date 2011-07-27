package org.motechproject.scheduletracking.api.domain.factory;

import org.junit.Test;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.userspecified.MilestoneRecord;
import org.motechproject.scheduletracking.api.userspecified.ScheduleRecord;
import org.motechproject.scheduletracking.api.userspecified.ScheduleWindowsRecord;

import static org.junit.Assert.*;

public class ScheduleFactoryTest {
    @Test
    public void create() {
        ScheduleRecord scheduleRecord = new ScheduleRecord();
        ScheduleWindowsRecord scheduleWindowsRecord = new ScheduleWindowsRecord("1 Week", "2 Weeks", "3 Weeks", "4 Weeks");
        MilestoneRecord milestoneRecord = new MilestoneRecord("IPTI1", "IPTI One", scheduleWindowsRecord);
        scheduleRecord.addMilestoneRecord(milestoneRecord);
        Schedule schedule = ScheduleFactory.create(scheduleRecord);
        assertNotNull(schedule);
    }
}
