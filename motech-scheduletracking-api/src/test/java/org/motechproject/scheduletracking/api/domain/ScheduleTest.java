package org.motechproject.scheduletracking.api.domain;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.scheduletracking.api.BaseScheduleTrackingTest;
import org.motechproject.scheduletracking.api.domain.enrollment.Enrollment;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ScheduleTest extends BaseScheduleTrackingTest {

    private Schedule schedule;

    @Before
    public void setUp() {
        schedule = createSchedule();
    }

    @Test
    public void shouldCreateEnrollment() {
        Enrollment enrollment = schedule.newEnrollment("ID-007");
        assertThat(enrollment.getScheduleName(), is(equalTo(schedule.getName())));
        assertThat(enrollment.getNextMilestone(), is(equalTo(schedule.getFirstMilestone().name())));
    }

    @Test
    public void shouldGetAMilestoneBasedOnName() {
        String milestoneName = "First Shot";
        assertThat(schedule.milestone(milestoneName).name(), is(equalTo(milestoneName)));

        milestoneName = "Second Shot";
        assertThat(schedule.milestone(milestoneName).name(), is(equalTo(milestoneName)));

        milestoneName = "Non Existent";
        assertThat(schedule.milestone(milestoneName), is(nullValue()));
    }

    @Test
    public void shouldNotHaveAlertsIfNoMilestoneIsAtLeastDue() {
        Milestone milestone = new Milestone("One", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Schedule schedule = new Schedule("foo", wallTimeOf(10), milestone);

        List<Alert> alerts = schedule.alertsFor(weeksAgo(2), milestone.name());
        assertThat(alerts.size(), is(equalTo(0)));
    }

    @Test
    public void alertsForAScheduleWithSingleMilestone() {
        Milestone milestone = new Milestone("One", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Schedule schedule = new Schedule("Schedule", wallTimeOf(10), milestone);

        List<Alert> alerts = schedule.alertsFor(weeksAgo(3), milestone.name());
        assertThat(alerts.size(), is(equalTo(1)));
        assertThat(alerts.get(0).windowName(), is(equalTo(WindowName.Due)));
    }

    @Test
    public void alertsForAScheduleWithMultipleMilestones() {
        Milestone second = new Milestone("Second", wallTimeOf(11), wallTimeOf(12), wallTimeOf(13), wallTimeOf(14));
        Milestone first = new Milestone("First", second, wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Schedule schedule = new Schedule("Schedule", wallTimeOf(52), first);

        List<Alert> alerts = schedule.alertsFor(weeksAgo(3), first.name());
        assertThat(alerts.size(), is(equalTo(1)));
        assertThat(alerts.get(0).windowName(), is(equalTo(WindowName.Due)));

        alerts = schedule.alertsFor(weeksAgo(13), second.name());
        assertThat(alerts.size(), is(equalTo(1)));
        assertThat(alerts.get(0).windowName(), is(equalTo(WindowName.Due)));
    }
}
