package org.motechproject.scheduletracking.api.domain.factory;

import org.junit.Ignore;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class ScheduleFactoryTest {
    @Test
    @Ignore("work in progress - puneet")
    public void create() {
        ScheduleRecord scheduleRecord = new ScheduleRecord("IPTI Schedule", "10 Weeks");
        ScheduleWindowsRecord scheduleWindowsRecord = new ScheduleWindowsRecord("1 Week", "2 Weeks", "3 Weeks", "4 Weeks");
        MilestoneRecord milestoneRecord = new MilestoneRecord("IPTI One", "IPTI Schedule", scheduleWindowsRecord);
        scheduleRecord.addMilestoneRecord(milestoneRecord);

        Schedule schedule = ScheduleFactory.create(scheduleRecord);
        assertNotNull(schedule);
        String firstMilestone = schedule.getFirstMilestone();
        assertThat(firstMilestone, is(equalTo("IPTI One")));
        Milestone milestone = schedule.milestone(firstMilestone);
        assertNotNull(milestone);
        assertThat(schedule, is(equalTo(milestone.refersTo())));
    }

    @Test
    public void records() {
        TrackedSchedulesJsonReader jsonReader = new TrackedSchedulesJsonReaderImpl("/simple-schedule.json", new MotechJsonReader());
        ScheduleRecord scheduleRecord = jsonReader.records().get(0);
        Schedule schedule = ScheduleFactory.create(scheduleRecord);

        assertThat(schedule, is(notNullValue()));

    }
}
