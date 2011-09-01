package org.motechproject.scheduletracking.api.domain.factory;

import org.junit.Test;
import org.motechproject.dao.MotechJsonReader;
import org.motechproject.scheduletracking.api.dao.TrackedSchedulesJsonReader;
import org.motechproject.scheduletracking.api.dao.TrackedSchedulesJsonReaderImpl;
import org.motechproject.scheduletracking.api.domain.Milestone;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.userspecified.MilestoneRecord;
import org.motechproject.scheduletracking.api.userspecified.ScheduleRecord;
import org.motechproject.scheduletracking.api.userspecified.ScheduleWindowsRecord;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ScheduleFactoryTest {
    @Test
    public void create() {
        ScheduleRecord scheduleRecord = new ScheduleRecord("Vaccination Schedule", "10 Weeks");
        ScheduleWindowsRecord scheduleWindowsRecord = new ScheduleWindowsRecord("1 Week", "2 Weeks", "3 Weeks", "4 Weeks");
        MilestoneRecord milestoneRecord = new MilestoneRecord("IPTI One", scheduleWindowsRecord);
        scheduleRecord.addMilestoneRecord(milestoneRecord);

        Schedule schedule = ScheduleFactory.create(scheduleRecord);
        assertThat(schedule, is(notNullValue()));
        assertThat(schedule.getName(), is(equalTo(scheduleRecord.name())));

        Milestone firstMilestone = schedule.getFirstMilestone();
        assertThat(firstMilestone, is(notNullValue()));
        assertThat(firstMilestone.name(), is(equalTo("IPTI One")));

        assertThat(firstMilestone.getNextMilestone(), is(nullValue()));
    }

    @Test
    public void records() {
        TrackedSchedulesJsonReader jsonReader = new TrackedSchedulesJsonReaderImpl("/simple-schedule.json", new MotechJsonReader());
        ScheduleRecord scheduleRecord = jsonReader.records().get(0);
        Schedule schedule = ScheduleFactory.create(scheduleRecord);

        assertThat(schedule, is(notNullValue()));
        assertThat(schedule.getName(), is(equalTo(scheduleRecord.name())));

        Milestone firstMilestone = schedule.getFirstMilestone();
        assertThat(firstMilestone.name(), is(equalTo(scheduleRecord.milestoneRecords().get(0).name())));
        assertThat(firstMilestone.getNextMilestone().name(), is(equalTo(scheduleRecord.milestoneRecords().get(1).name())));

        Milestone secondMilestone = firstMilestone.getNextMilestone();
        assertThat(secondMilestone.name(), is(equalTo(scheduleRecord.milestoneRecords().get(1).name())));
        assertThat(secondMilestone.getNextMilestone(), is(nullValue()));
    }
}
