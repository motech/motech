package org.motechproject.scheduletracking.api.domain;

import org.junit.Before;
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
    public void shouldAlertsIfAMilestoneIsAtLeastDue() {
        Schedule schedule = new Schedule("foo", wallTimeOf(10));
        schedule.addMilestone(new Milestone("One", schedule, wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4)));

        List<Alert> alerts = schedule.alertsFor(weeksAgo(2));
        assertThat(alerts.size(), is(equalTo(0)));

        alerts = schedule.alertsFor(weeksAgo(3));
        assertThat(alerts.size(), is(equalTo(1)));
        assertThat(alerts.get(0).windowName(), is(equalTo(WindowName.Due)));
    }

    @Test
    public void alertsForASingleMilestone() {
        Schedule schedule = new Schedule("foo", wallTimeOf(10));
        schedule.addMilestone(new Milestone("One", schedule, wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4)));

        List<Alert> alerts = schedule.alertsFor(weeksAgo(3));
        assertThat(alerts.size(), is(equalTo(1)));
        assertThat(alerts.get(0).windowName(), is(equalTo(WindowName.Due)));
    }

    @Test
    public void alertsForAScheduleWithMultipleMilestones() {
        Schedule schedule = new Schedule("Schedule", wallTimeOf(52));
        schedule.addMilestone(new Milestone("First", schedule, wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4)));
        schedule.addMilestone(new Milestone("Second", schedule, wallTimeOf(11), wallTimeOf(12), wallTimeOf(13), wallTimeOf(14)));

        List<Alert> alerts = schedule.alertsFor(weeksAgo(3));
        assertThat(alerts.size(), is(equalTo(1)));
        assertThat(alerts.get(0).windowName(), is(equalTo(WindowName.Due)));

        alerts = schedule.alertsFor(weeksAgo(13));
        assertThat(alerts.size(), is(equalTo(2)));
        assertThat(alerts.get(0).windowName(), is(equalTo(WindowName.Past)));
        assertThat(alerts.get(1).windowName(), is(equalTo(WindowName.Due)));
    }
}
