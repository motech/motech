package org.motechproject.scheduletracking.api.it;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.daysAgo;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.weeksAgo;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.yearsAgo;
import static org.motechproject.util.DateUtil.newDate;
import static org.motechproject.util.DateUtil.newDateTime;
import static org.motechproject.util.DateUtil.now;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationSchedulerTrackingAPI.xml")
public class EnrollmentsSearchIT {

    @Autowired
    private ScheduleTrackingService scheduleTrackingService;
    @Autowired
    private AllEnrollments allEnrollments;

    @After
    public void tearDown() {
        for (Enrollment enrollment : allEnrollments.getAll())
            allEnrollments.remove(enrollment);
    }

    @Test
    public void shouldReturnExternalIdsOfActiveEnrollmentsThatAreEitherInLateOrMaxWindowForAGivenSchedule() {
        DateTime now = now();
        createEnrollment("entity_1", "IPTI Schedule", "IPTI 1", weeksAgo(1), now, new Time(6, 30), EnrollmentStatus.ACTIVE);
        createEnrollment("entity_2", "IPTI Schedule", "IPTI 1", weeksAgo(14), now, new Time(6, 30), EnrollmentStatus.ACTIVE);
        createEnrollment("entity_3", "IPTI Schedule", "IPTI 1", yearsAgo(1), now, new Time(6, 30), EnrollmentStatus.ACTIVE);
        createEnrollment("entity_4", "IPTI Schedule", "IPTI 2", daysAgo(20), daysAgo(20), new Time(6, 30), EnrollmentStatus.ACTIVE);
        createEnrollment("entity_5", "IPTI Schedule", "IPTI 1", yearsAgo(1), now, new Time(6, 30), EnrollmentStatus.DEFAULTED);
        createEnrollment("entity_6", "Delivery", "Default", yearsAgo(1), now, new Time(6, 30), EnrollmentStatus.ACTIVE);

        EnrollmentsQuery query = new EnrollmentsQuery().havingSchedule("IPTI Schedule").currentlyInWindow(WindowName.late, WindowName.max).havingState("active");
        List<String> result = scheduleTrackingService.findExternalIds(query);
        assertEquals(asList(new String[]{ "entity_3", "entity_4" }), result);
    }

    @Test
    public void shouldReturnExternalIdsOfActiveEnrollmentsThatHaveEnteredDueWindowDuringTheLastWeek() {
        DateTime now = now();
        createEnrollment("entity_1", "IPTI Schedule", "IPTI 1", weeksAgo(10), now, new Time(6, 30), EnrollmentStatus.ACTIVE);
        createEnrollment("entity_2", "IPTI Schedule", "IPTI 1", weeksAgo(13).minusDays(1), now, new Time(6, 30), EnrollmentStatus.ACTIVE);
        createEnrollment("entity_3", "IPTI Schedule", "IPTI 1", weeksAgo(14).plusHours(1), now, new Time(6, 30), EnrollmentStatus.ACTIVE);
        createEnrollment("entity_4", "IPTI Schedule", "IPTI 2", weeksAgo(15), now, new Time(6, 30), EnrollmentStatus.ACTIVE);
        createEnrollment("entity_5", "IPTI Schedule", "IPTI 1", weeksAgo(13), now, new Time(6, 30), EnrollmentStatus.DEFAULTED);
        createEnrollment("entity_6", "Delivery", "Default", weeksAgo(14), now, new Time(6, 30), EnrollmentStatus.ACTIVE);

        EnrollmentsQuery query = new EnrollmentsQuery().havingSchedule("IPTI Schedule").havingState("active").havingWindowStartingDuring(WindowName.due, weeksAgo(1), now);
        List<String> result = scheduleTrackingService.findExternalIds(query);
        assertEquals(asList(new String[]{ "entity_2", "entity_3" }), result);
    }

    @Test
    public void shouldReturnExternalIdsOfActiveEnrollmentsThatAreCompletedDuringTheLastWeek() {
        DateTime referenceDate = newDateTime(2012, 10, 1, 0, 0 ,0);
        createEnrollment("entity_1", "IPTI Schedule", "IPTI 2", referenceDate, referenceDate, new Time(6, 30), EnrollmentStatus.ACTIVE);
        scheduleTrackingService.fulfillCurrentMilestone("entity_1", "IPTI Schedule", newDate(2012, 10, 10));
        createEnrollment("entity_2", "IPTI Schedule", "IPTI 2", referenceDate, referenceDate, new Time(6, 30), EnrollmentStatus.ACTIVE);
        scheduleTrackingService.fulfillCurrentMilestone("entity_2", "IPTI Schedule", newDate(2012, 10, 20));
        createEnrollment("entity_3", "IPTI Schedule", "IPTI 1", referenceDate, referenceDate, new Time(6, 30), EnrollmentStatus.ACTIVE);
        scheduleTrackingService.fulfillCurrentMilestone("entity_3", "IPTI Schedule", newDate(2012, 10, 8));
        scheduleTrackingService.fulfillCurrentMilestone("entity_3", "IPTI Schedule", newDate(2012, 10, 9));
        createEnrollment("entity_4", "IPTI Schedule", "IPTI 2", referenceDate, referenceDate, new Time(6, 30), EnrollmentStatus.ACTIVE);
        createEnrollment("entity_5", "IPTI Schedule", "IPTI 1", referenceDate, referenceDate, new Time(6, 30), EnrollmentStatus.DEFAULTED);
        createEnrollment("entity_6", "Delivery", "Default", referenceDate, referenceDate, new Time(6, 30), EnrollmentStatus.ACTIVE);

        EnrollmentsQuery query = new EnrollmentsQuery().havingSchedule("IPTI Schedule").completedDuring(newDateTime(2012, 10, 9, 0, 0, 0), newDateTime(2012, 10, 15, 23, 59, 59));
        List<String> result = scheduleTrackingService.findExternalIds(query);
        assertEquals(asList(new String[]{ "entity_1", "entity_3" }), result);
    }

    private Enrollment createEnrollment(String externalId, String scheduleName, String currentMilestoneName, DateTime referenceDateTime, DateTime enrollmentDateTime, Time preferredAlertTime, EnrollmentStatus enrollmentStatus) {
        Enrollment enrollment = new Enrollment(externalId, scheduleName, currentMilestoneName, referenceDateTime, enrollmentDateTime, preferredAlertTime, enrollmentStatus);
        allEnrollments.add(enrollment);
        return enrollment;
    }
}
