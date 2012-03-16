package org.motechproject.scheduletracking.api.it;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.domain.Milestone;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.motechproject.scheduletracking.api.service.impl.EnrollmentService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;
import static org.motechproject.scheduletracking.api.utility.PeriodFactory.weeks;
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
    private Enrollment enrollment;
    private Milestone dummyMilestone;
    private Schedule dummySchedule;

    @Before
    public void setUp() {
        dummyMilestone = new Milestone("first_milestone", weeks(13), weeks(1), weeks(2), weeks(0));
        dummySchedule = new Schedule("schedule_name");
        dummySchedule.addMilestones(dummyMilestone);
    }

    @After
    public void tearDown() {
        allEnrollments.removeAll();
    }

    @Test
    public void shouldAddEnrollment() {
        createAndAddEnrollment("externalId", "IPTI Schedule", "IPTI 1");

        enrollment = allEnrollments.get(enrollment.getId());
        assertNotNull(enrollment);
        assertNull(enrollment.getSchedule());
        assertEquals(EnrollmentStatus.ACTIVE, enrollment.getStatus());
    }

    private void createAndAddEnrollment(String externalId, String scheduleName, String currentMilestoneName) {
        Schedule schedule = allTrackedSchedules.getByName(scheduleName);
        enrollment = new Enrollment(externalId, scheduleName, currentMilestoneName, now(), now(), new Time(now().toLocalTime()), EnrollmentStatus.ACTIVE, schedule);
        allEnrollments.add(enrollment);
    }

    @Test
    public void shouldGetAllEnrollmentsWithSchedulePopulatedInThem() {
        createAndAddEnrollment("externalId", "IPTI Schedule", "IPTI 1");

        List<Enrollment> enrollments = allEnrollments.getAll();
        assertEquals(1, enrollments.size());
        Schedule actualSchedule = enrollments.get(0).getSchedule();
        assertEquals(allTrackedSchedules.getByName("IPTI Schedule"), actualSchedule);
    }

    @Test
    public void shouldFindActiveEnrollmentByExternalIdAndScheduleNameWithSchedulePopulatedInThem() {
        String scheduleName = "IPTI Schedule";
        Schedule schedule = allTrackedSchedules.getByName(scheduleName);
        enrollment = new Enrollment("entity_1", scheduleName, "IPTI 1", now(), now(), new Time(DateUtil.now().toLocalTime()), EnrollmentStatus.ACTIVE, schedule);
        enrollment.setStatus(EnrollmentStatus.UNENROLLED);
        allEnrollments.add(enrollment);

        enrollment = new Enrollment("entity_1", scheduleName, "IPTI 1", now(), now(), new Time(DateUtil.now().toLocalTime()), EnrollmentStatus.ACTIVE, schedule);
        allEnrollments.add(enrollment);

        Enrollment activeEnrollment = allEnrollments.getActiveEnrollment("entity_1", scheduleName);
        assertNotNull(activeEnrollment);
        assertEquals(schedule, activeEnrollment.getSchedule());
    }

    @Test
    public void shouldFindActiveEnrollmentByExternalIdAndScheduleName() {
        String scheduleName = "IPTI Schedule";
        enrollment = new Enrollment("entity_1", scheduleName, "IPTI 1", now(), now(), new Time(DateUtil.now().toLocalTime()), EnrollmentStatus.ACTIVE, allTrackedSchedules.getByName(scheduleName));
        enrollment.setStatus(EnrollmentStatus.UNENROLLED);
        allEnrollments.add(enrollment);

        assertNull(allEnrollments.getActiveEnrollment("entity_1", scheduleName));
    }

    @Test
    public void shouldConvertTheFulfillmentDateTimeIntoCorrectTimeZoneWhenRetrievingAnEnrollmentWithFulfilledMilestoneFromDatabase() {
        String scheduleName = "IPTI Schedule";
        enrollment = new Enrollment("entity_1", scheduleName, "IPTI 1", now(), now(), new Time(DateUtil.now().toLocalTime()), EnrollmentStatus.ACTIVE, allTrackedSchedules.getByName(scheduleName));
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
        enrollment = new Enrollment("entity_1", scheduleName, "IPTI 1", now.minusDays(2), now, new Time(now.toLocalTime()), EnrollmentStatus.ACTIVE, allTrackedSchedules.getByName(scheduleName));
        allEnrollments.add(enrollment);

        Enrollment enrollmentFromDatabase = allEnrollments.getActiveEnrollment("entity_1", scheduleName);
        assertEquals(now.minusDays(2), enrollmentFromDatabase.getCurrentMilestoneStartDate());
    }

    @Test
    public void shouldReturnTheMilestoneStartDateTimeInCorrectTimeZoneForSecondMilestone() {
        String scheduleName = "IPTI Schedule";
        Schedule schedule = allTrackedSchedules.getByName(scheduleName);
        DateTime now = DateTime.now();
        enrollment = new Enrollment("entity_1", scheduleName, "IPTI 1", now.minusDays(2), now, new Time(now.toLocalTime()), EnrollmentStatus.ACTIVE, schedule);
        allEnrollments.add(enrollment);
        enrollmentService.fulfillCurrentMilestone(enrollment, now);
        allEnrollments.update(enrollment);

        Enrollment enrollmentFromDatabase = allEnrollments.getActiveEnrollment("entity_1", "IPTI Schedule");
        assertEquals(now, enrollmentFromDatabase.getCurrentMilestoneStartDate());
    }

    @Test
    public void shouldReturnTheMilestoneStartDateTimeInCorrectTimeZoneWhenEnrollingIntoSecondMilestone() {
        DateTime now = DateTime.now();
        enrollment = new Enrollment("entity_1", "IPTI Schedule", "IPTI 2", now.minusDays(2), now, new Time(now.toLocalTime()), EnrollmentStatus.ACTIVE, null);
        allEnrollments.add(enrollment);

        Enrollment enrollmentFromDatabase = allEnrollments.getActiveEnrollment("entity_1", "IPTI Schedule");
        assertEquals(now, enrollmentFromDatabase.getCurrentMilestoneStartDate());
    }

    @Test
    public void shouldUpdateEnrollmentIfAnActiveEnrollmentForTheScheduleAlreadyAvailable() {
        String externalId = "externalId";
        enrollment = new Enrollment(externalId, dummySchedule.getName(), dummyMilestone.getName(), now(), now(), new Time(8, 10), EnrollmentStatus.ACTIVE, null);
        allEnrollments.add(enrollment);

        Enrollment enrollmentWithUpdates = new Enrollment(enrollment.getExternalId(), enrollment.getScheduleName(), dummyMilestone.getName(), enrollment.getReferenceDateTime().plusDays(1), enrollment.getEnrollmentDateTime().plusDays(1), new Time(2, 5), EnrollmentStatus.ACTIVE, null);
        allEnrollments.addOrReplace(enrollmentWithUpdates);

        enrollment = allEnrollments.getActiveEnrollment(enrollment.getExternalId(), dummySchedule.getName());
        assertEquals(enrollmentWithUpdates.getCurrentMilestoneName(), enrollment.getCurrentMilestoneName());
        assertEquals(enrollmentWithUpdates.getReferenceDateTime().toDateTime(DateTimeZone.UTC), enrollment.getReferenceDateTime().toDateTime(DateTimeZone.UTC));
        assertEquals(enrollmentWithUpdates.getEnrollmentDateTime().toDateTime(DateTimeZone.UTC), enrollment.getEnrollmentDateTime().toDateTime(DateTimeZone.UTC));
        assertEquals(enrollmentWithUpdates.getPreferredAlertTime(), enrollment.getPreferredAlertTime());
    }

    @Test
    public void shouldCreateEnrollmentIfADefaultedEnrollmentForTheScheduleAlreadyExists() {
        String externalId = "externalId";
        enrollment = new Enrollment(externalId, dummySchedule.getName(), dummyMilestone.getName(), now(), now(), new Time(8, 10), EnrollmentStatus.ACTIVE, null);
        enrollment.setStatus(EnrollmentStatus.DEFAULTED);
        allEnrollments.add(enrollment);

        Enrollment enrollmentWithUpdates = new Enrollment(enrollment.getExternalId(), enrollment.getScheduleName(), dummyMilestone.getName(), enrollment.getReferenceDateTime().plusDays(1), enrollment.getEnrollmentDateTime().plusDays(1), new Time(2, 5), EnrollmentStatus.ACTIVE, null);
        allEnrollments.addOrReplace(enrollmentWithUpdates);

        enrollment = allEnrollments.getActiveEnrollment(enrollment.getExternalId(), dummySchedule.getName());
        assertEquals(enrollmentWithUpdates.getCurrentMilestoneName(), enrollment.getCurrentMilestoneName());
        assertEquals(enrollmentWithUpdates.getReferenceDateTime(), enrollment.getReferenceDateTime());
        assertEquals(enrollmentWithUpdates.getEnrollmentDateTime(), enrollment.getEnrollmentDateTime());
        assertEquals(enrollmentWithUpdates.getPreferredAlertTime(), enrollment.getPreferredAlertTime());
    }

    @Test
    public void shouldCreateEnrollmentIfAnUnenrolledEnrollmentForTheScheduleAlreadyExists() {
        String externalId = "externalId";
        enrollment = new Enrollment(externalId, dummySchedule.getName(), dummyMilestone.getName(), now(), now(), new Time(8, 10), EnrollmentStatus.ACTIVE, null);
        enrollment.setStatus(EnrollmentStatus.UNENROLLED);
        allEnrollments.add(enrollment);

        Enrollment enrollmentWithUpdates = new Enrollment(enrollment.getExternalId(), enrollment.getScheduleName(), dummyMilestone.getName(), enrollment.getReferenceDateTime().plusDays(1), enrollment.getEnrollmentDateTime().plusDays(1), new Time(2, 5), EnrollmentStatus.ACTIVE, null);
        allEnrollments.addOrReplace(enrollmentWithUpdates);

        enrollment = allEnrollments.getActiveEnrollment(enrollment.getExternalId(), dummySchedule.getName());
        assertEquals(enrollmentWithUpdates.getCurrentMilestoneName(), enrollment.getCurrentMilestoneName());
        assertEquals(enrollmentWithUpdates.getReferenceDateTime(), enrollment.getReferenceDateTime());
        assertEquals(enrollmentWithUpdates.getEnrollmentDateTime(), enrollment.getEnrollmentDateTime());
        assertEquals(enrollmentWithUpdates.getPreferredAlertTime(), enrollment.getPreferredAlertTime());
    }
}
