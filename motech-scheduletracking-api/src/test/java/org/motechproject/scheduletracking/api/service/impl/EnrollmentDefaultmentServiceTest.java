package org.motechproject.scheduletracking.api.service.impl;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.model.RunOnceSchedulableJob;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduletracking.api.domain.*;
import org.motechproject.scheduletracking.api.events.DefaultmentCaptureEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.weeksAfter;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.weeksAgo;
import static org.motechproject.scheduletracking.api.utility.PeriodFactory.days;
import static org.motechproject.scheduletracking.api.utility.PeriodFactory.weeks;

public class EnrollmentDefaultmentServiceTest {
    private EnrollmentDefaultmentService enrollmentDefaultmentService;

    @Mock
    private AllTrackedSchedules allTrackedSchedules;
    @Mock
    private MotechSchedulerService schedulerService;

    @Before
    public void setup() {
        initMocks(this);
        enrollmentDefaultmentService = new EnrollmentDefaultmentService(allTrackedSchedules, schedulerService);
    }

    @Test
    public void shouldScheduleJobAtEndOfMilestoneToCaptureDefaultmentState() {

        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0));
        milestone.addAlert(WindowName.due, new Alert(days(0), weeks(1), 2, 1));
        String scheduleName = "my_schedule";
        Schedule schedule = new Schedule(scheduleName);
        schedule.addMilestones(milestone);
        when(allTrackedSchedules.getByName(scheduleName)).thenReturn(schedule);
        String externalId = "entity_1";
        Enrollment enrollment = new Enrollment(externalId, scheduleName, milestone.getName(), weeksAgo(0), weeksAgo(0), new Time(8, 10), EnrollmentStatus.Active);
        enrollment.setId("enrollment_1");

        enrollmentDefaultmentService.scheduleJobToCaptureDefaultment(enrollment);

        ArgumentCaptor<RunOnceSchedulableJob> runOnceJobArgumentCaptor = ArgumentCaptor.forClass(RunOnceSchedulableJob.class);
        verify(schedulerService).safeScheduleRunOnceJob(runOnceJobArgumentCaptor.capture());

        RunOnceSchedulableJob job = runOnceJobArgumentCaptor.getValue();
        DefaultmentCaptureEvent event = new DefaultmentCaptureEvent(job.getMotechEvent());
        assertEquals("enrollment_1", event.getJobId());
        assertEquals(weeksAfter(4).toDate(), job.getStartDate());
    }

    @Test
    public void shouldScheduleJobOnlyIfMaxWindowEndDateIsNotInThePast() {
        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0));
        milestone.addAlert(WindowName.due, new Alert(days(0), weeks(1), 2, 1));
        String scheduleName = "my_schedule";
        Schedule schedule = new Schedule(scheduleName);
        schedule.addMilestones(milestone);
        when(allTrackedSchedules.getByName(scheduleName)).thenReturn(schedule);
        String externalId = "entity_1";
        LocalDate referenceDate = DateUtil.newDate(2012, 1, 1).minusMonths(10);
        LocalDate enrollmentDate = DateUtil.today();
        Enrollment enrollment = new Enrollment(externalId, scheduleName, milestone.getName(), referenceDate, enrollmentDate, new Time(8, 10), EnrollmentStatus.Active);
        enrollment.setId("enrollment_1");

        enrollmentDefaultmentService.scheduleJobToCaptureDefaultment(enrollment);

        verify(schedulerService, times(0)).safeScheduleRunOnceJob(any(RunOnceSchedulableJob.class));
    }

    @Test
    public void shouldUnscheduleDefaultmentCaptureJob() {
        Enrollment enrollment = new Enrollment("entity_1", "my_schedule", "milestone", weeksAgo(0), weeksAgo(0), new Time(8, 10), EnrollmentStatus.Active);
        enrollment.setId("enrollment_1");

        enrollmentDefaultmentService.unscheduleDefaultmentCaptureJob(enrollment);

        verify(schedulerService).safeUnscheduleAllJobs(String.format("%s-enrollment_1", EventSubjects.DEFAULTMENT_CAPTURE));
    }
}
