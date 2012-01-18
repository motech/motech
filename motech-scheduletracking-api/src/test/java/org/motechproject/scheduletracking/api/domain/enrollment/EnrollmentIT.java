package org.motechproject.scheduletracking.api.domain.enrollment;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.scheduletracking.api.dao.TrackedSchedulesJsonReader;
import org.motechproject.scheduletracking.api.dao.TrackedSchedulesJsonReaderImpl;
import org.motechproject.scheduletracking.api.domain.Alert;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.domain.factory.ScheduleFactory;
import org.motechproject.scheduletracking.api.userspecified.ScheduleRecord;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class EnrollmentIT {
    private Schedule schedule;

    @Before
    public void setUp() {
        TrackedSchedulesJsonReader jsonReader = new TrackedSchedulesJsonReaderImpl("/simple-schedule.json");
        ScheduleRecord scheduleRecord = jsonReader.records().get(0);
        schedule = ScheduleFactory.create(scheduleRecord);
    }

    @Test
    public void shouldGetAlertsForADueMilestone() {
        List<Alert> alerts = enrollAndGetAlerts(15);

        assertThat(alerts.size(), is(equalTo(1)));
        Alert alert = alerts.get(0);
        assertThat(alert.windowName(), is(equalTo(WindowName.Due)));
        Map<String, String> data = alert.data();
        assertThat(data.get("Foo"), is(equalTo("Bar")));
    }

    @Test
    public void shouldGetAlertsForAPastMilestone() {
        List<Alert> alerts = enrollAndGetAlerts(20);

        assertThat(alerts.size(), is(equalTo(1)));
        Alert alert = alerts.get(0);
        assertThat(alert.windowName(), is(equalTo(WindowName.Past)));
        Map<String, String> data = alert.data();
        assertThat(data.get("Foo"), is(equalTo("Bar")));
    }

    @Test
    public void shouldFulfillAMilestoneAndAlertsForTheOneDueNext() {
        LocalDate enrollmentDate = LocalDate.now().minusWeeks(16);
        LocalDate firstFulfillmentDate = LocalDate.now().minusWeeks(3);
        Enrollment enrollment = schedule.newEnrollment("External ID", enrollmentDate);
        String nextMilestone = enrollment.fulfillMilestone(schedule, firstFulfillmentDate);
        assertThat(nextMilestone, is(equalTo("IPTI 2")));

        List<Alert> alerts = enrollment.getAlerts(schedule);
        assertThat(alerts.size(), is(equalTo(1)));
        Alert alert = alerts.get(0);
        assertThat(alert.windowName(), is(equalTo(WindowName.Due)));
        Map<String, String> data = alert.data();
        assertThat(data.get("doo"), is(equalTo("Bar")));
    }

    private List<Alert> enrollAndGetAlerts(int numberOfWeeksSinceEnrollment) {
        LocalDate fewWeeksAgo = LocalDate.now().minusWeeks(numberOfWeeksSinceEnrollment);
        Enrollment enrollment = schedule.newEnrollment("External ID", fewWeeksAgo);
        return enrollment.getAlerts(schedule);
    }
}
