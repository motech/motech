package org.motechproject.scheduletracking.api.domain;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.scheduletracking.api.BaseScheduleTrackingTest;
import org.motechproject.scheduletracking.api.domain.enrollment.Enrollment;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
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
    }

    @Test
    public void shouldNotHaveAlertsIfNoMilestoneIsAtLeastDue() {
        Milestone milestone = new Milestone("One", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Schedule schedule = new Schedule("foo", wallTimeOf(10), milestone);

        List<Alert> alerts = schedule.alertsFor(weeksAgo(2));
        assertThat(alerts.size(), is(equalTo(0)));
    }

    @Test
    @Ignore("work in progress - puneet")
    public void alertsForAScheduleWithSingleMilestone() {
        Milestone milestone = new Milestone("One", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Schedule schedule = new Schedule("foo", wallTimeOf(10), milestone);

        List<Alert> alerts = schedule.alertsFor(weeksAgo(3));
        assertThat(alerts.size(), is(equalTo(1)));
        assertThat(alerts.get(0).windowName(), is(equalTo(WindowName.Due)));
    }

    @Test
    @Ignore("work in progress - puneet")
    public void alertsForAScheduleWithMultipleMilestones() {
        Milestone second = new Milestone("Second", wallTimeOf(11), wallTimeOf(12), wallTimeOf(13), wallTimeOf(14));
        Milestone first = new Milestone("First", second, wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Schedule schedule = new Schedule("Schedule", wallTimeOf(52), first);

        List<Alert> alerts = schedule.alertsFor(weeksAgo(3));
        assertThat(alerts.size(), is(equalTo(1)));
        assertThat(alerts.get(0).windowName(), is(equalTo(WindowName.Due)));

        alerts = schedule.alertsFor(weeksAgo(13));
        assertThat(alerts.size(), is(equalTo(2)));
        assertThat(alerts.get(0).windowName(), is(equalTo(WindowName.Past)));
        assertThat(alerts.get(1).windowName(), is(equalTo(WindowName.Due)));
    }
}
