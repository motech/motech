package org.motechproject.scheduletracking.api.service;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.Milestone;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.repository.AllEnrollments;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.motechproject.util.DateUtil;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.WallTimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.wallTimeOf;

public class ScheduleTrackingServiceImplTest {
    @Mock
    private AllTrackedSchedules allTrackedSchedules;
    @Mock
    private MotechSchedulerService schedulerService;
    @Mock
    private AllEnrollments allEnrollments;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldEnrollPatientIntoFirstMilestoneOfSchedule() {

        Milestone secondMilestone = new Milestone("second_milestone", null, wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Milestone firstMilestone = new Milestone("first_milestone", secondMilestone, wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        when(allTrackedSchedules.getByName("my_schedule")).thenReturn(new Schedule("my_schedule", new WallTime(10, WallTimeUnit.Week), firstMilestone));

        ScheduleTrackingService scheduleTrackingService = new ScheduleTrackingServiceImpl(schedulerService, allTrackedSchedules, allEnrollments);

        LocalDate referenceDate = DateUtil.newDate(2012, 1, 2);
        scheduleTrackingService.enroll(new EnrollmentRequest("my_entity_1", "my_schedule", new Time(8, 10), referenceDate));

        ArgumentCaptor<Enrollment> enrollmentArgumentCaptor = ArgumentCaptor.forClass(Enrollment.class);
        verify(allEnrollments).add(enrollmentArgumentCaptor.capture());

        Enrollment enrollment = enrollmentArgumentCaptor.getValue();
        assertEquals("my_entity_1", enrollment.getExternalId());
        assertEquals("my_schedule", enrollment.getScheduleName());
        assertEquals(firstMilestone.getName(), enrollment.getCurrentMilestoneName());

        ArgumentCaptor<CronSchedulableJob> cronSchedulableJobArgumentCaptor = ArgumentCaptor.forClass(CronSchedulableJob.class);
        verify(schedulerService).scheduleJob(cronSchedulableJobArgumentCaptor.capture());

        CronSchedulableJob cronSchedulableJob = cronSchedulableJobArgumentCaptor.getValue();
        assertEquals("0 10/0 8-8 * * ?", cronSchedulableJob.getCronExpression());
        assertEquals(DateUtil.today().toDate(), cronSchedulableJob.getStartTime());
        assertEquals(referenceDate.plusWeeks(10).toDate(), cronSchedulableJob.getEndTime());
    }

    @Test
    public void shouldEnrollPatientIntoGivenMilestoneOfTheSchedule() {
        Milestone secondMilestone = new Milestone("second_milestone", null, wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        Milestone firstMilestone = new Milestone("first_milestone", secondMilestone, wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        when(allTrackedSchedules.getByName("my_schedule")).thenReturn(new Schedule("my_schedule", new WallTime(10, WallTimeUnit.Week), firstMilestone));

        ScheduleTrackingService scheduleTrackingService = new ScheduleTrackingServiceImpl(schedulerService, allTrackedSchedules, allEnrollments);

        scheduleTrackingService.enroll(new EnrollmentRequest("entity_1", "my_schedule", new Time(8, 10), new LocalDate(2012, 11, 2), "second_milestone"));

        ArgumentCaptor<Enrollment> enrollmentArgumentCaptor = ArgumentCaptor.forClass(Enrollment.class);
        verify(allEnrollments).add(enrollmentArgumentCaptor.capture());

        Enrollment enrollment = enrollmentArgumentCaptor.getValue();
        assertEquals(secondMilestone.getName(), enrollment.getCurrentMilestoneName());
    }
}
