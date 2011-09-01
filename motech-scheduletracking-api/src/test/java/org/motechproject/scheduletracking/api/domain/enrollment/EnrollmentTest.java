package org.motechproject.scheduletracking.api.domain.enrollment;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.scheduletracking.api.BaseScheduleTrackingTest;
import org.motechproject.scheduletracking.api.domain.Alert;
import org.motechproject.scheduletracking.api.domain.Schedule;

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
        enrollment = new Enrollment("ID-074285", weeksAgo(3), schedule.getName(), schedule.getFirstMilestone().name());
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
    @Ignore("work in progress - puneet")
    public void shouldMarkAMilestoneAsFulfilled() {
        String nextMilestone = enrollment.fulfillMilestone(schedule);
        assertThat(nextMilestone, is(equalTo("Second Shot")));
    }
}
