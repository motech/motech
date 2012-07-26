package org.motechproject.scheduletracking.api.service.impl;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduletracking.api.domain.*;
import org.motechproject.scheduletracking.api.domain.exception.InvalidEnrollmentException;
import org.motechproject.scheduletracking.api.domain.exception.ScheduleTrackingException;
import org.motechproject.scheduletracking.api.domain.json.ScheduleRecord;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.repository.AllSchedules;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.scheduletracking.api.service.contract.UpdateCriteria;
import org.motechproject.util.DateUtil;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.weeksAgo;
import static org.motechproject.scheduletracking.api.utility.PeriodUtil.days;
import static org.motechproject.scheduletracking.api.utility.PeriodUtil.weeks;
import static org.motechproject.util.DateUtil.*;

public class ScheduleTrackingServiceImplTest {
    private ScheduleTrackingService scheduleTrackingService;

    @Mock
    private AllSchedules allSchedules;
    @Mock
    private MotechSchedulerService schedulerService;
    @Mock
    private AllEnrollments allEnrollments;
    @Mock
    private EnrollmentService enrollmentService;
    @Mock
    private EnrollmentsQueryService enrollmentsQueryService;
    @Mock
    private EnrollmentRecordMapper enrollmentRecordMapper;

    public static final Map<String, String> EMPTY_METADATA_LIST = new HashMap<String, String>();

    @Before
    public void setUp() {
        initMocks(this);
        scheduleTrackingService = new ScheduleTrackingServiceImpl(allSchedules, allEnrollments, enrollmentService, enrollmentsQueryService, enrollmentRecordMapper);
    }

    @Test
    public void shouldEnrollEntityIntoFirstMilestoneOfSchedule() {
        String scheduleName = "my_schedule";
        Schedule schedule = new Schedule(scheduleName);
        Milestone secondMilestone = new Milestone("second_milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone firstMilestone = new Milestone("first_milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        schedule.addMilestones(firstMilestone, secondMilestone);
        when(allSchedules.getByName(scheduleName)).thenReturn(schedule);

        String externalId = "my_entity_1";
        DateTime referenceDateTime = now().minusDays(10);
        Time preferredAlertTime = new Time(8, 10);
        scheduleTrackingService.enroll(new EnrollmentRequest().setExternalId(externalId).setScheduleName(scheduleName).setPreferredAlertTime(preferredAlertTime).setReferenceDate(referenceDateTime.toLocalDate()));

        verify(enrollmentService).enroll(externalId, scheduleName, firstMilestone.getName(), newDateTime(referenceDateTime.toLocalDate(), new Time(0, 0)), newDateTime(now().toLocalDate(), new Time(0, 0)), preferredAlertTime, EMPTY_METADATA_LIST);
    }

    @Test
    public void shouldEnrollEntityIntoGivenMilestoneOfTheSchedule() {
        Milestone secondMilestone = new Milestone("second_milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone firstMilestone = new Milestone("first_milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        String scheduleName = "my_schedule";
        Schedule schedule = new Schedule(scheduleName);
        schedule.addMilestones(firstMilestone, secondMilestone);
        when(allSchedules.getByName(scheduleName)).thenReturn(schedule);

        String externalId = "entity_1";
        Time preferredAlertTime = new Time(8, 10);
        DateTime referenceDateTime = newDateTime(2012, 11, 2, 0, 0, 0);
        scheduleTrackingService.enroll(new EnrollmentRequest().setExternalId(externalId).setScheduleName(scheduleName).setPreferredAlertTime(preferredAlertTime).setReferenceDate(referenceDateTime.toLocalDate()).setStartingMilestoneName(secondMilestone.getName()));

        verify(enrollmentService).enroll(externalId, scheduleName, secondMilestone.getName(), newDateTime(referenceDateTime.toLocalDate(), new Time(0, 0)), newDateTime(now().toLocalDate(), new Time(0, 0)), preferredAlertTime, EMPTY_METADATA_LIST);
    }

    @Test
    public void shouldEnrollEntityIntoAScheduleWithMetadata() {
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(new Milestone("milestone1", weeks(1), weeks(1), weeks(1), weeks(1)));
        when(allSchedules.getByName("my_schedule")).thenReturn(schedule);

        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put("foo", "bar");
        metadata.put("fuu", "baz");
        scheduleTrackingService.enroll(new EnrollmentRequest().setExternalId("entity_1").setScheduleName("my_schedule").setPreferredAlertTime(new Time(8, 10)).setReferenceDate(newDateTime(2012, 11, 2, 0, 0, 0).toLocalDate()).setStartingMilestoneName("milestone1").setMetadata(metadata));

        Map<String, String> expectedMetadata = new HashMap<String, String>();
        expectedMetadata.put("foo", "bar");
        expectedMetadata.put("fuu", "baz");

        verify(enrollmentService).enroll("entity_1", "my_schedule", "milestone1", newDateTime(newDateTime(2012, 11, 2, 0, 0, 0).toLocalDate(), new Time(0, 0)), newDateTime(now().toLocalDate(), new Time(0, 0)), new Time(8, 10), expectedMetadata);
    }

    @Test
    public void shouldScheduleOneRepeatJobForTheSingleAlertInTheFirstMilestone() {
        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0, false));
        String scheduleName = "my_schedule";
        Schedule schedule = new Schedule(scheduleName);
        schedule.addMilestones(milestone);
        when(allSchedules.getByName(scheduleName)).thenReturn(schedule);

        String externalId = "entity_1";
        DateTime referenceDateTime = newDateTime(2012, 11, 2, 0, 0, 0);
        Time preferredAlertTime = new Time(8, 10);
        scheduleTrackingService.enroll(new EnrollmentRequest().setExternalId(externalId).setScheduleName(scheduleName).setPreferredAlertTime(preferredAlertTime).setReferenceDate(referenceDateTime.toLocalDate()));

        verify(enrollmentService).enroll(externalId, scheduleName, milestone.getName(), newDateTime(referenceDateTime.toLocalDate(), new Time(0, 0)), newDateTime(now().toLocalDate(), new Time(0, 0)), preferredAlertTime, EMPTY_METADATA_LIST);
    }

    @Test
    public void shouldFulfillTheCurrentMilestone() {
        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0, false));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);
        when(allSchedules.getByName("my_schedule")).thenReturn(schedule);

        when(allEnrollments.getActiveEnrollment("entity_1", "my_schedule")).thenReturn(null);
        scheduleTrackingService.enroll(new EnrollmentRequest().setExternalId("entity_1").setScheduleName("my_schedule").setPreferredAlertTime(new Time(8, 10)).setReferenceDate(new LocalDate(2012, 11, 2)));

        Enrollment enrollment = mock(Enrollment.class);
        when(allEnrollments.getActiveEnrollment("entity_1", "my_schedule")).thenReturn(enrollment);

        scheduleTrackingService.fulfillCurrentMilestone("entity_1", "my_schedule", today(), new Time(0, 0));

        verify(enrollmentService).fulfillCurrentMilestone(enrollment, newDateTime(now().toLocalDate(), new Time(0, 0)));
    }

    @Test
    public void shouldFulfillTheCurrentMilestoneDefaultingTheTimeComponent() {
        Enrollment enrollment = mock(Enrollment.class);
        MilestoneFulfillment fulfillment = mock(MilestoneFulfillment.class);
        when(enrollment.getFulfillments()).thenReturn(asList(new MilestoneFulfillment[]{fulfillment}));
        when(enrollment.getLastFulfilledDate()).thenReturn(newDateTime(2012, 2, 10, 8, 20, 0));
        when(allEnrollments.getActiveEnrollment("entity_1", "my_schedule")).thenReturn(enrollment);

        scheduleTrackingService.fulfillCurrentMilestone("entity_1", "my_schedule", newDate(2012, 2, 10));

        verify(enrollmentService).fulfillCurrentMilestone(enrollment, newDateTime(2012, 2, 10, new Time(0, 0)));
    }

    @Test
    public void shouldNotFulfillTheCurrentMilestoneIftheFulfillmentDateTimeMatchesLastMilestoneFulfillmentDate() {
        Enrollment enrollment = mock(Enrollment.class);
        MilestoneFulfillment fulfillment = mock(MilestoneFulfillment.class);
        when(enrollment.getFulfillments()).thenReturn(asList(new MilestoneFulfillment[]{fulfillment}));
        when(enrollment.getLastFulfilledDate()).thenReturn(newDateTime(2012, 2, 10, 8, 20, 0));
        when(allEnrollments.getActiveEnrollment("entity_1", "my_schedule")).thenReturn(enrollment);

        scheduleTrackingService.fulfillCurrentMilestone("entity_1", "my_schedule", newDate(2012, 2, 10), new Time(8, 20));

        verifyZeroInteractions(enrollmentService);
    }

    @Test
    public void shouldFulfillTheCurrentMilestoneWithTheSpecifiedDateOnlyUsingDefaultTime() {
        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0, false));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);
        when(allSchedules.getByName("my_schedule")).thenReturn(schedule);

        when(allEnrollments.getActiveEnrollment("entity_1", "my_schedule")).thenReturn(null);
        scheduleTrackingService.enroll(new EnrollmentRequest().setExternalId("entity_1").setScheduleName("my_schedule").setPreferredAlertTime(new Time(8, 10)).setReferenceDate(new LocalDate(2012, 11, 2)).setReferenceTime(null).setEnrollmentDate(null).setEnrollmentTime(null).setStartingMilestoneName(null).setMetadata(null));

        Enrollment enrollment = mock(Enrollment.class);
        when(allEnrollments.getActiveEnrollment("entity_1", "my_schedule")).thenReturn(enrollment);

        DateTime fulfillmentDateTime = newDateTime(2012, 12, 10, 0, 0, 0);
        scheduleTrackingService.fulfillCurrentMilestone("entity_1", "my_schedule", fulfillmentDateTime.toLocalDate(), new Time(0, 0));

        verify(enrollmentService).fulfillCurrentMilestone(enrollment, fulfillmentDateTime);
    }

    @Test
    public void shouldFulfillTheCurrentMilestoneWithTheSpecifiedDateAndTime() {
        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0, false));
        Schedule schedule = new Schedule("my_schedule");
        schedule.addMilestones(milestone);
        when(allSchedules.getByName("my_schedule")).thenReturn(schedule);

        when(allEnrollments.getActiveEnrollment("entity_1", "my_schedule")).thenReturn(null);
        scheduleTrackingService.enroll(new EnrollmentRequest().setExternalId("entity_1").setScheduleName("my_schedule").setPreferredAlertTime(new Time(8, 10)).setReferenceDate(new LocalDate(2012, 11, 2)).setReferenceTime(null).setEnrollmentDate(null).setEnrollmentTime(null).setStartingMilestoneName(null).setMetadata(null));

        Enrollment enrollment = mock(Enrollment.class);
        when(allEnrollments.getActiveEnrollment("entity_1", "my_schedule")).thenReturn(enrollment);

        scheduleTrackingService.fulfillCurrentMilestone("entity_1", "my_schedule", newDate(2012, 12, 10), new Time(3, 30));

        verify(enrollmentService).fulfillCurrentMilestone(enrollment, newDateTime(2012, 12, 10, 3, 30, 0));
    }

    @Test(expected = InvalidEnrollmentException.class)
    public void shouldFailToFulfillCurrentMilestoneIfItIsNotFoundOrNotActive() {
        when(allEnrollments.getActiveEnrollment("WRONG-ID", "WRONG-NAME")).thenReturn(null);

        scheduleTrackingService.fulfillCurrentMilestone("WRONG-ID", "WRONG-NAME", today(), new Time(0, 0));

        verifyZeroInteractions(enrollmentService);
    }

    @Test
    public void shouldUnenrollEntityFromTheSchedule() {
        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0, false));
        String scheduleName = "my_schedule";
        Schedule schedule = new Schedule(scheduleName);
        schedule.addMilestones(milestone);
        when(allSchedules.getByName(scheduleName)).thenReturn(schedule);

        String externalId = "entity_1";
        Enrollment enrollment = new Enrollment().setExternalId("entity_1").setSchedule(schedule).setCurrentMilestoneName("milestone").setStartOfSchedule(weeksAgo(4)).setEnrolledOn(weeksAgo(4)).setPreferredAlertTime(new Time(8, 10)).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
        when(allEnrollments.getActiveEnrollment("entity_1", "my_schedule")).thenReturn(enrollment);
        scheduleTrackingService.unenroll(externalId, Arrays.asList(scheduleName));

        verify(enrollmentService).unenroll(enrollment);
    }

    @Test
    public void shouldSafelyUnenrollEntityFromListOfSchedule() {
        Milestone milestone1 = new Milestone("milestone1", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone1.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0, false));
        String schedule1Name = "my_schedule1";
        Schedule schedule1 = new Schedule(schedule1Name);
        schedule1.addMilestones(milestone1);
        when(allSchedules.getByName(schedule1Name)).thenReturn(schedule1);

        Milestone milestone2 = new Milestone("milestone2", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone2.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0, false));
        String schedule2Name = "my_schedule2";
        Schedule schedule2 = new Schedule(schedule2Name);
        schedule2.addMilestones(milestone2);
        when(allSchedules.getByName(schedule2Name)).thenReturn(schedule2);

        String externalId = "entity_1";
        Enrollment enrollment1 = new Enrollment().setExternalId(externalId).setSchedule(schedule1).setCurrentMilestoneName("milestone1").setStartOfSchedule(weeksAgo(4)).setEnrolledOn(weeksAgo(4)).setPreferredAlertTime(new Time(8, 10)).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
        when(allEnrollments.getActiveEnrollment(externalId, schedule1Name)).thenReturn(enrollment1);
        Enrollment enrollment2 = new Enrollment().setExternalId(externalId).setSchedule(schedule2).setCurrentMilestoneName("milestone2").setStartOfSchedule(weeksAgo(4)).setEnrolledOn(weeksAgo(4)).setPreferredAlertTime(new Time(8, 10)).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
        when(allEnrollments.getActiveEnrollment(externalId, schedule2Name)).thenReturn(enrollment2);

        scheduleTrackingService.unenroll(externalId, Arrays.asList(schedule1Name, schedule2Name));

        verify(enrollmentService).unenroll(enrollment1);
        verify(enrollmentService).unenroll(enrollment2);
    }

    @Test
    public void shouldNotThrowAnyExceptionIfEntityIsNotEnrolledIntoSchedule() {
        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0, false));
        String scheduleName = "scheduleName";
        Schedule schedule = new Schedule(scheduleName);
        schedule.addMilestones(milestone);
        when(allSchedules.getByName(scheduleName)).thenReturn(schedule);

        when(allEnrollments.getActiveEnrollment("entity_1", scheduleName)).thenReturn(null);
        scheduleTrackingService.unenroll("entity_1", Arrays.asList(scheduleName));
    }

    @Test
    public void shouldReturnEnrollmentDetails() {
        String externalId = "external id";
        String scheduleName = "schedule name";
        Schedule schedule = new Schedule("some_schedule");
        final Enrollment enrollment = new Enrollment().setExternalId(externalId).setSchedule(schedule).setCurrentMilestoneName(null).setStartOfSchedule(null).setEnrolledOn(null).setPreferredAlertTime(null).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
        when(allEnrollments.getActiveEnrollment(externalId, scheduleName)).thenReturn(enrollment);

        EnrollmentRecord record = mock(EnrollmentRecord.class);
        when(enrollmentRecordMapper.map(enrollment)).thenReturn(record);

        assertEquals(record, scheduleTrackingService.getEnrollment(externalId, scheduleName));
    }

    @Test(expected = InvalidEnrollmentException.class)
    public void shouldNotFulfillAnyInactiveEnrollment() {
        String externalId = "externalId";
        String scheduleName = "scheduleName";
        when(allEnrollments.getActiveEnrollment(externalId, scheduleName)).thenReturn(null);

        scheduleTrackingService.fulfillCurrentMilestone(externalId, scheduleName, today(), new Time(0, 0));
    }

    @Test
    public void shouldReturnListOfExternalIdsForTheGivenQuery() {
        EnrollmentsQuery enrollmentQuery = mock(EnrollmentsQuery.class);
        Enrollment enrollment1 = mock(Enrollment.class);
        Enrollment enrollment2 = mock(Enrollment.class);
        List<Enrollment> enrollments = asList(enrollment1, enrollment2);
        when(enrollmentsQueryService.search(enrollmentQuery)).thenReturn(enrollments);

        EnrollmentRecord record1 = mock(EnrollmentRecord.class);
        when(enrollmentRecordMapper.map(enrollment1)).thenReturn(record1);

        EnrollmentRecord record2 = mock(EnrollmentRecord.class);
        when(enrollmentRecordMapper.map(enrollment2)).thenReturn(record2);

        assertEquals(asList(new EnrollmentRecord[]{record1, record2}), scheduleTrackingService.search(enrollmentQuery));
    }

    @Test
    public void shouldReturnListOfEnrollmentRecordsForTheGivenQuery() {
        Schedule schedule = new Schedule("some_schedule");
        EnrollmentsQuery enrollmentQuery = mock(EnrollmentsQuery.class);
        Enrollment enrollment1 = new Enrollment().setExternalId("external_id_1").setSchedule(schedule).setCurrentMilestoneName(null).setStartOfSchedule(null).setEnrolledOn(null).setPreferredAlertTime(null).setStatus(null).setMetadata(null);
        Enrollment enrollment2 = new Enrollment().setExternalId("external_id_2").setSchedule(schedule).setCurrentMilestoneName(null).setStartOfSchedule(null).setEnrolledOn(null).setPreferredAlertTime(null).setStatus(null).setMetadata(null);
        List<Enrollment> enrollments = asList(enrollment1, enrollment2);

        when(enrollmentsQueryService.search(enrollmentQuery)).thenReturn(enrollments);

        EnrollmentRecord record1 = mock(EnrollmentRecord.class);
        when(enrollmentRecordMapper.mapWithDates(enrollment1)).thenReturn(record1);
        EnrollmentRecord record2 = mock(EnrollmentRecord.class);
        when(enrollmentRecordMapper.mapWithDates(enrollment2)).thenReturn(record2);

        assertEquals(asList(new EnrollmentRecord[]{record1, record2}), scheduleTrackingService.searchWithWindowDates(enrollmentQuery));
    }

    @Test
    public void shouldUpdateValuesOnEnrollment() {
        Schedule schedule = new Schedule("some_schedule");
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put("foo1", "bar1");
        metadata.put("foo2", "bar2");
        Enrollment enrollment = new Enrollment().setExternalId("external_id_1").setSchedule(schedule).setCurrentMilestoneName(null).setStartOfSchedule(null).setEnrolledOn(null).setPreferredAlertTime(null).setStatus(null).setMetadata(metadata);
        HashMap<String, String> toBeUpdatedMetadata = new HashMap<String, String>();
        toBeUpdatedMetadata.put("foo2", "val2");
        toBeUpdatedMetadata.put("foo3", "val3");

        when(allEnrollments.getActiveEnrollment("foo", "some_schedule")).thenReturn(enrollment);

        ArgumentCaptor<Enrollment> enrollmentArgumentCaptor = ArgumentCaptor.forClass(Enrollment.class);

        scheduleTrackingService.updateEnrollment("foo", "some_schedule", new UpdateCriteria().metadata(toBeUpdatedMetadata));

        verify(allEnrollments).update(enrollmentArgumentCaptor.capture());
        Enrollment updatedEnrollment = enrollmentArgumentCaptor.getValue();

        Map<String, String> updatedMetadata = updatedEnrollment.getMetadata();
        assertEquals(3, updatedMetadata.size());
        assertEquals("bar1", updatedMetadata.get("foo1"));
        assertEquals("val2", updatedMetadata.get("foo2"));
        assertEquals("val3", updatedMetadata.get("foo3"));
    }

    @Test(expected = InvalidEnrollmentException.class)
    public void UpdateShouldThrowExceptionForInvalidData() {
        when(allEnrollments.getActiveEnrollment("foo", "some_schedule")).thenReturn(null);
        scheduleTrackingService.updateEnrollment("foo", "some_schedule", new UpdateCriteria().metadata(new HashMap<String, String>()));
        verifyNoMoreInteractions(allEnrollments);
    }

    @Test
    public void shouldInvokeEnrollmentServiceForAlertTimings() {
        String scheduleName = "my_schedule";
        Schedule schedule = new Schedule(scheduleName);
        Milestone secondMilestone = new Milestone("second_milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone firstMilestone = new Milestone("first_milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        schedule.addMilestones(firstMilestone, secondMilestone);
        when(allSchedules.getByName(scheduleName)).thenReturn(schedule);

        String externalId = "my_entity_1";
        LocalDate referenceDate = DateUtil.today();
        Time referenceTime = new Time(8, 10);
        LocalDate enrollmentDate = DateUtil.today();
        Time enrollmentTime = new Time(8, 10);
        Time preferredAlertTime = new Time(8, 10);

        scheduleTrackingService.getAlertTimings(new EnrollmentRequest().setExternalId(externalId).setScheduleName(scheduleName).setPreferredAlertTime(preferredAlertTime).setReferenceDate(referenceDate).setReferenceTime(referenceTime).setEnrollmentDate(enrollmentDate).setEnrollmentTime(enrollmentTime).setStartingMilestoneName("first_milestone").setMetadata(null));

        verify(enrollmentService).getAlertTimings(externalId, scheduleName, firstMilestone.getName(), newDateTime(referenceDate, new Time(8, 10)), newDateTime(now().toLocalDate(), new Time(8, 10)), preferredAlertTime);
    }

    @Test
    public void shouldInvokeEnrollmentServiceForAlertTimingsWithStartingMilestoneFromEnrollmentRequest() {
        String scheduleName = "my_schedule";
        Schedule schedule = new Schedule(scheduleName);
        Milestone secondMilestone = new Milestone("second_milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone firstMilestone = new Milestone("first_milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        schedule.addMilestones(firstMilestone, secondMilestone);
        when(allSchedules.getByName(scheduleName)).thenReturn(schedule);

        String externalId = "my_entity_1";
        LocalDate referenceDate = DateUtil.today();
        Time referenceTime = new Time(8, 10);
        LocalDate enrollmentDate = DateUtil.today();
        Time enrollmentTime = new Time(8, 10);
        Time preferredAlertTime = new Time(8, 10);

        scheduleTrackingService.getAlertTimings(new EnrollmentRequest().setExternalId(externalId).setScheduleName(scheduleName).setPreferredAlertTime(preferredAlertTime).setReferenceDate(referenceDate).setReferenceTime(referenceTime).setEnrollmentDate(enrollmentDate).setEnrollmentTime(enrollmentTime).setStartingMilestoneName("second_milestone").setMetadata(null));

        verify(enrollmentService).getAlertTimings(externalId, scheduleName, secondMilestone.getName(), newDateTime(referenceDate, new Time(8, 10)), newDateTime(now().toLocalDate(), new Time(8, 10)), preferredAlertTime);
    }

    @Test
    public void shouldInvokeEnrollmentServiceForAlertTimingsWithStartingMilestoneFromScheduleFirstMilestone() {
        String scheduleName = "my_schedule";
        Schedule schedule = new Schedule(scheduleName);
        Milestone secondMilestone = new Milestone("second_milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        Milestone firstMilestone = new Milestone("first_milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        schedule.addMilestones(firstMilestone, secondMilestone);
        when(allSchedules.getByName(scheduleName)).thenReturn(schedule);

        String externalId = "my_entity_1";
        LocalDate referenceDate = DateUtil.today();
        Time referenceTime = new Time(8, 10);
        LocalDate enrollmentDate = DateUtil.today();
        Time enrollmentTime = new Time(8, 10);
        Time preferredAlertTime = new Time(8, 10);

        scheduleTrackingService.getAlertTimings(new EnrollmentRequest().setExternalId(externalId).setScheduleName(scheduleName).setPreferredAlertTime(preferredAlertTime).setReferenceDate(referenceDate).setReferenceTime(referenceTime).setEnrollmentDate(enrollmentDate).setEnrollmentTime(enrollmentTime).setStartingMilestoneName(null).setMetadata(null));

        verify(enrollmentService).getAlertTimings(externalId, scheduleName, firstMilestone.getName(), newDateTime(referenceDate, new Time(8, 10)), newDateTime(now().toLocalDate(), new Time(8, 10)), preferredAlertTime);
    }

    @Test(expected = ScheduleTrackingException.class)
    public void shouldThrowAnExceptionIfScheduleIsInvalidWhileAskingForAlertTimings() {
        String scheduleName = "my_schedule";
        when(allSchedules.getByName(scheduleName)).thenReturn(null);

        scheduleTrackingService.getAlertTimings(new EnrollmentRequest().setExternalId(null).setScheduleName(scheduleName).setPreferredAlertTime(null).setReferenceDate(null).setReferenceTime(null).setEnrollmentDate(null).setEnrollmentTime(null).setStartingMilestoneName(null).setMetadata(null));
    }

    @Test
    public void shouldSaveGivenSchedulesInDb() throws IOException, URISyntaxException {
        File file = new File(getClass().getResource("/schedules/simple-schedule.json").toURI());
        String scheduleJson = FileUtils.readFileToString(file);

        scheduleTrackingService.add(scheduleJson);

        ArgumentCaptor<ScheduleRecord> scheduleRecordArgumentCaptor = ArgumentCaptor.forClass(ScheduleRecord.class);
        verify(allSchedules).add(scheduleRecordArgumentCaptor.capture());

        ScheduleRecord scheduleRecord = scheduleRecordArgumentCaptor.getValue();
        assertEquals("IPTI Schedule", scheduleRecord.name());
    }
}
