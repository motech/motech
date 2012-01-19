package org.motechproject.scheduletracking.api.domain;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.scheduletracking.api.BaseScheduleTrackingTest;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EnrollmentTest extends BaseScheduleTrackingTest {
    private Enrollment enrollment;
    private Schedule schedule;

    @Before
    public void setUp() {
        schedule = createSchedule();
        enrollment = new Enrollment("ID-074285", weeksAgo(3), schedule.getName(), schedule.getFirstMilestone().getName());
    }

    @Test
    public void shouldSetTheFirstMilestoneAsNextMilestoneOnCreation() {
        assertThat(enrollment.getNextMilestone(), is(equalTo("First Shot")));
    }

    @Test
    public void shouldGetPendingAlerts() {
        List<Alert> alerts = enrollment.getAlerts(schedule);
        assertThat(alerts.size(), is(equalTo(1)));
    }

    @Test
    public void shouldTakeIntoAccountPreviousFulfilledMilestoneWhileCalculatingAlertsForSubsequentMilestones() {
        enrollment = new Enrollment("ID-074285", weeksAgo(7), schedule.getName(), schedule.getFirstMilestone().getName());
        enrollment.fulfillMilestone(schedule, weeksAgo(3));

        List<Alert> alerts = enrollment.getAlerts(schedule);
        assertThat(alerts.size(), is(equalTo(1)));
        assertThat(alerts.get(0).windowName(), is(equalTo(WindowName.Due)));
    }

    @Test
    public void shouldMarkAMilestoneAsFulfilled() {
        String nextMilestone = enrollment.fulfillMilestone(schedule);

        assertThat(nextMilestone, is(equalTo("Second Shot")));
        List<MilestoneFulfillment> fulfillments = enrollment.getFulfillments();
        assertThat(fulfillments.size(), is(equalTo(1)));
    }
}
