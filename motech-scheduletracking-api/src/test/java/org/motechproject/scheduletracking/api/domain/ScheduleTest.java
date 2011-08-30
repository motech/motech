package org.motechproject.scheduletracking.api.domain;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.scheduletracking.api.domain.enrollment.Enrollment;
import org.motechproject.scheduletracking.api.domain.factory.ScheduleFactory;
import org.motechproject.scheduletracking.api.userspecified.MilestoneRecord;
import org.motechproject.scheduletracking.api.userspecified.ScheduleRecord;
import org.motechproject.scheduletracking.api.userspecified.ScheduleWindowsRecord;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ScheduleTest {

    private Schedule schedule;

    @Before
    public void setUp() {
        ScheduleRecord scheduleRecord = new ScheduleRecord("Polio Vaccination", "10 Weeks");
        ScheduleWindowsRecord scheduleWindowsRecord = new ScheduleWindowsRecord("1 Week", "2 Weeks", "3 Weeks", "4 Weeks");
        MilestoneRecord milestoneRecord = new MilestoneRecord("First Injection", "Polio Vaccination", scheduleWindowsRecord);
        scheduleRecord.addMilestoneRecord(milestoneRecord);

        schedule = ScheduleFactory.create(scheduleRecord);
    }

    @Test
    public void shouldCreateEnrollment() {
        Enrollment enrollment = schedule.newEnrollment("ID-007", LocalDate.now());
        assertThat(enrollment.getScheduleName(), is(equalTo(schedule.getName())));
    }
}
