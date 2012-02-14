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
import org.motechproject.scheduletracking.api.events.constants.EventSubject;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.motechproject.util.DateUtil;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.WallTimeUnit;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.*;

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
        Milestone milestone = new Milestone("milestone", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        milestone.addAlert(WindowName.earliest, new Alert(new WallTime(0, null), new WallTime(1, WallTimeUnit.Day), 3, 0));
        milestone.addAlert(WindowName.due, new Alert(new WallTime(0, null), new WallTime(1, WallTimeUnit.Week), 2, 1));
        String scheduleName = "my_schedule";
        Schedule schedule = new Schedule(scheduleName);
        schedule.addMilestones(milestone);
        when(allTrackedSchedules.getByName(scheduleName)).thenReturn(schedule);
        String externalId = "entity_1";
        Enrollment enrollment = new Enrollment(externalId, scheduleName, milestone.getName(), weeksAgo(0), weeksAgo(0), new Time(8, 10));
        enrollment.setId("enrollment_1");

        enrollmentDefaultmentService.scheduleJobToCaptureDefaultment(enrollment);

        ArgumentCaptor<RunOnceSchedulableJob> runOnceJobArgumentCaptor = ArgumentCaptor.forClass(RunOnceSchedulableJob.class);
        verify(schedulerService).safeScheduleRunOnceJob(runOnceJobArgumentCaptor.capture());

        RunOnceSchedulableJob job = runOnceJobArgumentCaptor.getValue();
        DefaultmentCaptureEvent event = new DefaultmentCaptureEvent(job.getMotechEvent());
        assertEquals(String.format("%s.enrollment_1", EventSubject.DEFAULTMENT_CAPTURE), event.getJobId());
        assertEquals(weeksAfter(4).toDate(), job.getStartDate());
    }

    @Test
    public void shouldScheduleJobOnlyIfMaxWindowEndDateIsNotInThePast() {
        Milestone milestone = new Milestone("milestone", wallTimeOf(1), wallTimeOf(2), wallTimeOf(3), wallTimeOf(4));
        milestone.addAlert(WindowName.earliest, new Alert(new WallTime(0, null), new WallTime(1, WallTimeUnit.Day), 3, 0));
        milestone.addAlert(WindowName.due, new Alert(new WallTime(0, null), new WallTime(1, WallTimeUnit.Week), 2, 1));
        String scheduleName = "my_schedule";
        Schedule schedule = new Schedule(scheduleName);
        schedule.addMilestones(milestone);
        when(allTrackedSchedules.getByName(scheduleName)).thenReturn(schedule);
        String externalId = "entity_1";
        LocalDate referenceDate = DateUtil.newDate(2012, 1, 1).minusMonths(10);
        LocalDate enrollmentDate = DateUtil.today();
        Enrollment enrollment = new Enrollment(externalId, scheduleName, milestone.getName(), referenceDate, enrollmentDate, new Time(8, 10));
        enrollment.setId("enrollment_1");

        enrollmentDefaultmentService.scheduleJobToCaptureDefaultment(enrollment);

        verify(schedulerService, times(0)).safeScheduleRunOnceJob(any(RunOnceSchedulableJob.class));
    }

    @Test
    public void shouldUnscheduleDefaultmentCaptureJob() {
        Enrollment enrollment = new Enrollment("entity_1", "my_schedule", "milestone", weeksAgo(0), weeksAgo(0), new Time(8, 10));
        enrollment.setId("enrollment_1");

        enrollmentDefaultmentService.unscheduleDefaultmentCaptureJob(enrollment);

        verify(schedulerService).unscheduleAllJobs(String.format("%s.enrollment_1", EnrollmentDefaultmentService.JOB_ID_PREFIX));
    }
}
