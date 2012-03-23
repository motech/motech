package org.motechproject.scheduletracking.api.it;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;
import static org.motechproject.util.DateUtil.newDateTime;
import static org.motechproject.util.DateUtil.now;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationSchedulerTrackingAPI.xml")
public class ScheduleTrackingServiceIT {

    @Autowired
    private ScheduleTrackingService scheduleTrackingService;
    @Autowired
    private AllEnrollments allEnrollments;

    private Enrollment activeEnrollment;

    @After
    public void tearDown() {
        if (activeEnrollment != null)
            allEnrollments.remove(activeEnrollment);
    }

    @Test
    public void shouldUpdateEnrollmentIfAnActiveEnrollmentAlreadyExists() {
        String externalId = "externalId";
        String scheduleName = "IPTI Schedule";

        activeEnrollment = allEnrollments.getActiveEnrollment(externalId, scheduleName);
        assertNull("Active enrollment present", activeEnrollment);

        Time originalPreferredAlertTime = new Time(8, 10);
        DateTime now = now();
        DateTime originalReferenceDateTime = now;
        String enrollmentId = scheduleTrackingService.enroll(new EnrollmentRequest(externalId, scheduleName, originalPreferredAlertTime, originalReferenceDateTime.toLocalDate(), null, null, null, null, null));
        assertNotNull("EnrollmentId is null", enrollmentId);

        activeEnrollment = allEnrollments.get(enrollmentId);
        assertNotNull("No active enrollment present", activeEnrollment);
        assertEquals(originalPreferredAlertTime, activeEnrollment.getPreferredAlertTime());
        assertEquals(newDateTime(originalReferenceDateTime.toLocalDate(), new Time(0, 0)), activeEnrollment.getStartOfSchedule());

        Time updatedPreferredAlertTime = new Time(2, 5);
        DateTime updatedReferenceDate = now.minusDays(1);
        String updatedEnrollmentId = scheduleTrackingService.enroll(new EnrollmentRequest(externalId, scheduleName, updatedPreferredAlertTime, updatedReferenceDate.toLocalDate(), null, null, null, null, null));
        assertEquals(enrollmentId, updatedEnrollmentId);

        activeEnrollment = allEnrollments.get(updatedEnrollmentId);
        assertNotNull("No active enrollment present", activeEnrollment);
        assertEquals(updatedPreferredAlertTime, activeEnrollment.getPreferredAlertTime());
        assertEquals(newDateTime(updatedReferenceDate.toLocalDate(), new Time(0, 0)), activeEnrollment.getStartOfSchedule());
    }
}
