package org.motechproject.scheduletracking.api.domain;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.scheduletracking.api.BaseScheduleTrackingTest;
import org.motechproject.scheduletracking.api.domain.enrollment.Enrollment;
import org.motechproject.scheduletracking.api.domain.factory.ScheduleFactory;
import org.motechproject.scheduletracking.api.userspecified.MilestoneRecord;
import org.motechproject.scheduletracking.api.userspecified.ScheduleRecord;
import org.motechproject.scheduletracking.api.userspecified.ScheduleWindowsRecord;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ScheduleTest extends BaseScheduleTrackingTest {

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
        Enrollment enrollment = schedule.newEnrollment("ID-007");
        assertThat(enrollment.getScheduleName(), is(equalTo(schedule.getName())));
    }

    @Test
    @Ignore("Work in progress - puneet")
    public void alertsFor() {
        LocalDate twoWeeksAgo = LocalDate.now().minusWeeks(2);
        Schedule schedule = new Schedule("foo", wallTimeOf(10));
        schedule.addMilestone(new Milestone("One", schedule, wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4)));

        List<Alert> alerts = schedule.alertsFor(twoWeeksAgo);
        assertThat(alerts.size(), is(equalTo(1)));
    }
}
