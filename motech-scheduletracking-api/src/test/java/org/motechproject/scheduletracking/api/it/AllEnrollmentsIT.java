package org.motechproject.scheduletracking.api.it;

import ch.lambdaj.Lambda;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.scheduletracking.api.service.impl.EnrollmentService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.motechproject.util.DateUtil.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:testApplicationSchedulerTrackingAPI.xml")
public class AllEnrollmentsIT {

    @Autowired
    private AllEnrollments allEnrollments;
    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private AllTrackedSchedules allTrackedSchedules;
    @Autowired
    private ScheduleTrackingService scheduleTrackingService;

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
    public void shouldReturnEnrollmentsThatMatchAGivenExternalId() {
        DateTime now = now();
        Schedule iptiSchedule = allTrackedSchedules.getByName("IPTI Schedule");
        Schedule deliverySchedule = allTrackedSchedules.getByName("Delivery");
        allEnrollments.add(new Enrollment("entity_1", iptiSchedule, "IPTI 1", now, now, new Time(8, 10), EnrollmentStatus.ACTIVE, null));
        allEnrollments.add(new Enrollment("entity_1", deliverySchedule, "Default", now, now, new Time(8, 10), EnrollmentStatus.ACTIVE, null));
        allEnrollments.add(new Enrollment("entity_2", iptiSchedule, "IPTI 1", now, now, new Time(8, 10), EnrollmentStatus.ACTIVE, null));
        allEnrollments.add(new Enrollment("entity_3", iptiSchedule, "IPTI 1", now, now, new Time(8, 10), EnrollmentStatus.ACTIVE, null));

        List<Enrollment> filteredEnrollments = allEnrollments.findByExternalId("entity_1");
//        assertNotNull(filteredEnrollments.get(0).getSchedule());
        assertEquals(asList(new String[] { "entity_1", "entity_1"}), Lambda.extract(filteredEnrollments, on(Enrollment.class).getExternalId()));
    }

    @Test
    public void shouldReturnEnrollmentsThatMatchAGivenSchedule() {
        DateTime now = now();
        Schedule iptiSchedule = allTrackedSchedules.getByName("IPTI Schedule");
        Schedule deliverySchedule = allTrackedSchedules.getByName("Delivery");
        allEnrollments.add(new Enrollment("entity_1", iptiSchedule, "IPTI 1", now, now, new Time(8, 10), EnrollmentStatus.ACTIVE, null));
        allEnrollments.add(new Enrollment("entity_2", deliverySchedule, "Default", now, now, new Time(8, 10), EnrollmentStatus.ACTIVE, null));
        allEnrollments.add(new Enrollment("entity_3", iptiSchedule, "IPTI 1", now, now, new Time(8, 10), EnrollmentStatus.ACTIVE, null));
        allEnrollments.add(new Enrollment("entity_4", iptiSchedule, "IPTI 1", now, now, new Time(8, 10), EnrollmentStatus.ACTIVE, null));

        List<Enrollment> filteredEnrollments = allEnrollments.findBySchedule("IPTI Schedule");
        assertNotNull(filteredEnrollments.get(0).getSchedule());
        assertEquals(asList(new String[] { "entity_1", "entity_3", "entity_4" }), Lambda.extract(filteredEnrollments, on(Enrollment.class).getExternalId()));
    }

    @Test
    public void shouldReturnEnrollmentsThatMatchGivenStatus() {
        DateTime now = now();
        Schedule iptiSchedule = allTrackedSchedules.getByName("IPTI Schedule");
        Schedule deliverySchedule = allTrackedSchedules.getByName("Delivery");
        allEnrollments.add(new Enrollment("entity_1", iptiSchedule, "IPTI 1", now, now, new Time(8, 10), EnrollmentStatus.COMPLETED, null));
        allEnrollments.add(new Enrollment("entity_2", deliverySchedule, "Default", now, now, new Time(8, 10), EnrollmentStatus.DEFAULTED, null));
        allEnrollments.add(new Enrollment("entity_3", iptiSchedule, "IPTI 1", now, now, new Time(8, 10), EnrollmentStatus.UNENROLLED, null));
        allEnrollments.add(new Enrollment("entity_4", iptiSchedule, "IPTI 1", now, now, new Time(8, 10), EnrollmentStatus.ACTIVE, null));

        List<Enrollment> filteredEnrollments = allEnrollments.findByStatus(EnrollmentStatus.ACTIVE);
        assertEquals(asList(new String[] { "entity_4"}), Lambda.extract(filteredEnrollments, on(Enrollment.class).getExternalId()));

        filteredEnrollments = allEnrollments.findByStatus(EnrollmentStatus.DEFAULTED);
        assertNotNull(filteredEnrollments.get(0).getSchedule());
        assertEquals(asList(new String[] { "entity_2"}), Lambda.extract(filteredEnrollments, on(Enrollment.class).getExternalId()));
    }

    @Test
    public void shouldReturnEnrollmentsThatWereCompletedDuringTheGivenTimeRage() {
        LocalDate today = today();
        scheduleTrackingService.enroll(new EnrollmentRequest("entity_1", "IPTI Schedule", new Time(8, 10), today, null, today, null, "IPTI 1", null));

        scheduleTrackingService.enroll(new EnrollmentRequest("entity_2", "IPTI Schedule", new Time(8, 10), today.minusWeeks(2), null, today.minusWeeks(2), null, "IPTI 1", null));
        scheduleTrackingService.fulfillCurrentMilestone("entity_2", "IPTI Schedule", today.minusDays(2));
        scheduleTrackingService.fulfillCurrentMilestone("entity_2", "IPTI Schedule", today.minusDays(1));

        scheduleTrackingService.enroll(new EnrollmentRequest("entity_3", "IPTI Schedule", new Time(8, 10), today, null, today, null, "IPTI 2", null));
        scheduleTrackingService.fulfillCurrentMilestone("entity_3", "IPTI Schedule", today, new Time(0, 0));

        scheduleTrackingService.enroll(new EnrollmentRequest("entity_4", "IPTI Schedule", new Time(8, 10), today.minusYears(2), null, today, null, "IPTI 1", null));

        scheduleTrackingService.enroll(new EnrollmentRequest("entity_5", "IPTI Schedule", new Time(8, 10), today.minusWeeks(2), null, today.minusWeeks(2), null, "IPTI 1", null));
        scheduleTrackingService.fulfillCurrentMilestone("entity_5", "IPTI Schedule", today.minusDays(10));
        scheduleTrackingService.fulfillCurrentMilestone("entity_5", "IPTI Schedule", today.minusDays(9));

        DateTime start = newDateTime(today.minusWeeks(1), new Time(0, 0));
        DateTime end = newDateTime(today, new Time(0, 0));
        List<Enrollment> filteredEnrollments = allEnrollments.completedDuring(start, end);
        assertNotNull(filteredEnrollments.get(0).getSchedule());
        assertEquals(asList(new String[] { "entity_2", "entity_3" }), Lambda.extract(filteredEnrollments, on(Enrollment.class).getExternalId()));
    }
}
