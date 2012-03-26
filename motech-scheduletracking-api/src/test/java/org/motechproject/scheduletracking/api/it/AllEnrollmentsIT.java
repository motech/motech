package org.motechproject.scheduletracking.api.it;

import ch.lambdaj.Lambda;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.motechproject.scheduletracking.api.service.impl.EnrollmentService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.motechproject.util.DateUtil.now;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:testApplicationSchedulerTrackingAPI.xml")
public class AllEnrollmentsIT {

    @Autowired
    private AllEnrollments allEnrollments;
    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private AllTrackedSchedules allTrackedSchedules;

    @After
    public void tearDown() {
        allEnrollments.removeAll();
    }

    @Test
    public void shouldAddEnrollment() {
        Schedule schedule = allTrackedSchedules.getByName("IPTI Schedule");
        Enrollment enrollment = new Enrollment("externalId", schedule, "IPTI 1", now(), now(), new Time(now().toLocalTime()), EnrollmentStatus.ACTIVE, null);
        allEnrollments.add(enrollment);

        Enrollment enrollmentFromDb = allEnrollments.get(enrollment.getId());

        assertNotNull(enrollmentFromDb);
        assertNull(enrollmentFromDb.getSchedule());
        assertEquals(EnrollmentStatus.ACTIVE, enrollmentFromDb.getStatus());
    }

    @Test
    public void shouldGetAllEnrollmentsWithSchedulePopulatedInThem() {
        Schedule schedule = allTrackedSchedules.getByName("IPTI Schedule");
        allEnrollments.add(new Enrollment("externalId", schedule, "IPTI 1", now(), now(), new Time(now().toLocalTime()), EnrollmentStatus.ACTIVE, null));

        List<Enrollment> enrollments = allEnrollments.getAll();

        assertEquals(1, enrollments.size());
        Schedule actualSchedule = enrollments.get(0).getSchedule();
        assertEquals(allTrackedSchedules.getByName("IPTI Schedule"), actualSchedule);
    }

    @Test
    public void shouldFindActiveEnrollmentByExternalIdAndScheduleNameWithSchedulePopulatedInThem() {
        String scheduleName = "IPTI Schedule";
        Schedule schedule = allTrackedSchedules.getByName(scheduleName);
        Enrollment enrollment = new Enrollment("entity_1", schedule, "IPTI 1", now(), now(), new Time(DateUtil.now().toLocalTime()), EnrollmentStatus.ACTIVE, null);
        enrollment.setStatus(EnrollmentStatus.UNENROLLED);
        allEnrollments.add(enrollment);

        enrollment = new Enrollment("entity_1", schedule, "IPTI 1", now(), now(), new Time(DateUtil.now().toLocalTime()), EnrollmentStatus.ACTIVE, null);
        allEnrollments.add(enrollment);

        Enrollment activeEnrollment = allEnrollments.getActiveEnrollment("entity_1", scheduleName);
        assertNotNull(activeEnrollment);
        assertEquals(schedule, activeEnrollment.getSchedule());
    }

    @Test
    public void shouldFindActiveEnrollmentByExternalIdAndScheduleName() {
        String scheduleName = "IPTI Schedule";
        Enrollment enrollment = new Enrollment("entity_1", allTrackedSchedules.getByName(scheduleName), "IPTI 1", now(), now(), new Time(DateUtil.now().toLocalTime()), EnrollmentStatus.ACTIVE, null);
        enrollment.setStatus(EnrollmentStatus.UNENROLLED);
        allEnrollments.add(enrollment);

        assertNull(allEnrollments.getActiveEnrollment("entity_1", scheduleName));
    }

    @Test
    public void shouldConvertTheFulfillmentDateTimeIntoCorrectTimeZoneWhenRetrievingAnEnrollmentWithFulfilledMilestoneFromDatabase() {
        String scheduleName = "IPTI Schedule";
        Enrollment enrollment = new Enrollment("entity_1", allTrackedSchedules.getByName(scheduleName), "IPTI 1", now(), now(), new Time(DateUtil.now().toLocalTime()), EnrollmentStatus.ACTIVE, null);
        allEnrollments.add(enrollment);
        DateTime fulfillmentDateTime = DateTime.now();
        enrollment.fulfillCurrentMilestone(fulfillmentDateTime);
        allEnrollments.update(enrollment);

        Enrollment enrollmentFromDatabase = allEnrollments.getActiveEnrollment("entity_1", scheduleName);
        assertEquals(fulfillmentDateTime, enrollmentFromDatabase.getLastFulfilledDate());
    }

    @Test
    public void shouldReturnTheMilestoneStartDateTimeInCorrectTimeZoneForFirstMilestone() {
        DateTime now = DateTime.now();
        String scheduleName = "IPTI Schedule";
        Enrollment enrollment = new Enrollment("entity_1", allTrackedSchedules.getByName(scheduleName), "IPTI 1", now.minusDays(2), now, new Time(now.toLocalTime()), EnrollmentStatus.ACTIVE, null);
        allEnrollments.add(enrollment);

        Enrollment enrollmentFromDatabase = allEnrollments.getActiveEnrollment("entity_1", scheduleName);
        assertEquals(now.minusDays(2), enrollmentFromDatabase.getReferenceForAlerts());
    }

    @Test
    public void shouldReturnTheMilestoneStartDateTimeInCorrectTimeZoneForSecondMilestone() {
        Schedule schedule = allTrackedSchedules.getByName("IPTI Schedule");
        DateTime now = DateTime.now();
        Enrollment enrollment = new Enrollment("entity_1", schedule, "IPTI 1", now.minusDays(2), now, new Time(now.toLocalTime()), EnrollmentStatus.ACTIVE, null);
        allEnrollments.add(enrollment);
        enrollmentService.fulfillCurrentMilestone(enrollment, now);
        allEnrollments.update(enrollment);

        Enrollment enrollmentFromDatabase = allEnrollments.getActiveEnrollment("entity_1", "IPTI Schedule");
        assertEquals(now, enrollmentFromDatabase.getReferenceForAlerts());
    }

    @Test
    public void shouldReturnTheMilestoneStartDateTimeInCorrectTimeZoneWhenEnrollingIntoSecondMilestone() {
        Schedule schedule = allTrackedSchedules.getByName("IPTI Schedule");
        DateTime now = DateTime.now();
        Enrollment enrollment = new Enrollment("entity_1", schedule, "IPTI 2", now.minusDays(2), now, new Time(now.toLocalTime()), EnrollmentStatus.ACTIVE, null);
        allEnrollments.add(enrollment);

        Enrollment enrollmentFromDatabase = allEnrollments.getActiveEnrollment("entity_1", "IPTI Schedule");
        assertEquals(now, enrollmentFromDatabase.getReferenceForAlerts());
    }

    @Test
    public void shouldUpdateEnrollmentIfAnActiveEnrollmentForTheScheduleAlreadyAvailable() {
        DateTime now = now();
        Schedule schedule = allTrackedSchedules.getByName("IPTI Schedule");
        Enrollment enrollment = new Enrollment("externalId", schedule, "IPTI 1", now, now, new Time(8, 10), EnrollmentStatus.ACTIVE, null);
        allEnrollments.add(enrollment);

        Enrollment updatedEnrollment = new Enrollment(enrollment.getExternalId(), schedule, "IPTI 1", enrollment.getStartOfSchedule().plusDays(1), enrollment.getEnrolledOn().plusDays(1), new Time(2, 5), EnrollmentStatus.ACTIVE, null);
        allEnrollments.addOrReplace(updatedEnrollment);

        enrollment = allEnrollments.getActiveEnrollment(enrollment.getExternalId(), "IPTI Schedule");

        assertEquals(updatedEnrollment.getCurrentMilestoneName(), enrollment.getCurrentMilestoneName());
        assertEquals(updatedEnrollment.getStartOfSchedule(), enrollment.getStartOfSchedule());
        assertEquals(updatedEnrollment.getEnrolledOn(), enrollment.getEnrolledOn());
        assertEquals(updatedEnrollment.getPreferredAlertTime(), enrollment.getPreferredAlertTime());
    }

    @Test
    public void shouldCreateEnrollmentIfADefaultedEnrollmentForTheScheduleAlreadyExists() {
        DateTime now = now();
        Schedule schedule = allTrackedSchedules.getByName("IPTI Schedule");
        Enrollment enrollment = new Enrollment("externalId", schedule, "IPTI 1", now, now, new Time(8, 10), EnrollmentStatus.ACTIVE, null);
        enrollment.setStatus(EnrollmentStatus.DEFAULTED);
        allEnrollments.add(enrollment);

        Enrollment newEnrollment = new Enrollment(enrollment.getExternalId(), schedule, "IPTI 1", enrollment.getStartOfSchedule().plusDays(1), enrollment.getEnrolledOn().plusDays(1), new Time(2, 5), EnrollmentStatus.ACTIVE, null);
        allEnrollments.addOrReplace(newEnrollment);

        enrollment = allEnrollments.getActiveEnrollment(enrollment.getExternalId(), "IPTI Schedule");
        assertEquals(newEnrollment.getCurrentMilestoneName(), enrollment.getCurrentMilestoneName());
        assertEquals(newEnrollment.getStartOfSchedule(), enrollment.getStartOfSchedule());
        assertEquals(newEnrollment.getEnrolledOn(), enrollment.getEnrolledOn());
        assertEquals(newEnrollment.getPreferredAlertTime(), enrollment.getPreferredAlertTime());
    }

    @Test
    public void shouldCreateEnrollmentIfAnUnenrolledEnrollmentForTheScheduleAlreadyExists() {
        DateTime now = now();
        Schedule schedule = allTrackedSchedules.getByName("IPTI Schedule");
        Enrollment enrollment = new Enrollment("externalId", schedule, "IPTI 1", now, now, new Time(8, 10), EnrollmentStatus.ACTIVE, null);
        enrollment.setStatus(EnrollmentStatus.UNENROLLED);
        allEnrollments.add(enrollment);

        Enrollment enrollmentWithUpdates = new Enrollment(enrollment.getExternalId(), schedule, "IPTI 1", enrollment.getStartOfSchedule().plusDays(1), enrollment.getEnrolledOn().plusDays(1), new Time(2, 5), EnrollmentStatus.ACTIVE, null);
        allEnrollments.addOrReplace(enrollmentWithUpdates);

        enrollment = allEnrollments.getActiveEnrollment(enrollment.getExternalId(), "IPTI Schedule");

        assertEquals(enrollmentWithUpdates.getCurrentMilestoneName(), enrollment.getCurrentMilestoneName());
        assertEquals(enrollmentWithUpdates.getStartOfSchedule(), enrollment.getStartOfSchedule());
        assertEquals(enrollmentWithUpdates.getEnrolledOn(), enrollment.getEnrolledOn());
        assertEquals(enrollmentWithUpdates.getPreferredAlertTime(), enrollment.getPreferredAlertTime());
    }

    @Test
    public void shouldReturnEnrollmentsThatMatchAGivenExternalId() {
        DateTime now = now();
        Schedule iptiSchedule = allTrackedSchedules.getByName("IPTI Schedule");
        Schedule deliverySchedule = allTrackedSchedules.getByName("Delivery");
        allEnrollments.add(new Enrollment("entity_1", iptiSchedule, "IPTI 1", now, now, new Time(8, 10), EnrollmentStatus.ACTIVE, null));
        allEnrollments.add(new Enrollment("entity_1", deliverySchedule, "Default", now, now, new Time(8, 10), EnrollmentStatus.ACTIVE, null));
        allEnrollments.add(new Enrollment("entity_2", iptiSchedule, "IPTI 1", now, now, new Time(8, 10), EnrollmentStatus.ACTIVE, null));
        allEnrollments.add(new Enrollment("entity_3", iptiSchedule, "IPTI 1", now, now, new Time(8, 10), EnrollmentStatus.ACTIVE, null));

        List<Enrollment> filteredEnrollments = allEnrollments.findByExternalId("entity_1");
        assertEquals(asList(new String[] { "entity_1", "entity_1"}), Lambda.extract(filteredEnrollments, on(Enrollment.class).getExternalId()));
    }
}
