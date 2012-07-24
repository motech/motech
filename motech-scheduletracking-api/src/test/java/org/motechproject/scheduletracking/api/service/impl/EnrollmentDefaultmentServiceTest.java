package org.motechproject.scheduletracking.api.service.impl;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.scheduler.domain.RunOnceSchedulableJob;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduletracking.api.domain.*;
import org.motechproject.scheduletracking.api.events.DefaultmentCaptureEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.scheduletracking.api.utility.DateTimeUtil.weeksAgo;
import static org.motechproject.scheduletracking.api.utility.PeriodUtil.days;
import static org.motechproject.scheduletracking.api.utility.PeriodUtil.weeks;
import static org.motechproject.util.DateUtil.newDateTime;
import static org.motechproject.util.DateUtil.now;

public class EnrollmentDefaultmentServiceTest {
    private EnrollmentDefaultmentService enrollmentDefaultmentService;

    @Mock
    private MotechSchedulerService schedulerService;

    @Before
    public void setup() {
        initMocks(this);
        enrollmentDefaultmentService = new EnrollmentDefaultmentService(schedulerService);
    }

    @Test
    public void shouldScheduleJobAtEndOfMilestoneToCaptureDefaultmentState() {
        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0, false));
        milestone.addAlert(WindowName.due, new Alert(days(0), weeks(1), 2, 1, false));
        String scheduleName = "my_schedule";
        Schedule schedule = new Schedule(scheduleName);
        schedule.addMilestones(milestone);
        String externalId = "entity_1";
        DateTime now = now();
        Enrollment enrollment = new Enrollment().setExternalId(externalId).setSchedule(schedule).setCurrentMilestoneName(milestone.getName()).setStartOfSchedule(now).setEnrolledOn(now).setPreferredAlertTime(new Time(8, 10)).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
        enrollment.setId("enrollment_1");

        enrollmentDefaultmentService.scheduleJobToCaptureDefaultment(enrollment);

        ArgumentCaptor<RunOnceSchedulableJob> runOnceJobArgumentCaptor = ArgumentCaptor.forClass(RunOnceSchedulableJob.class);
        verify(schedulerService).safeScheduleRunOnceJob(runOnceJobArgumentCaptor.capture());

        RunOnceSchedulableJob job = runOnceJobArgumentCaptor.getValue();
        DefaultmentCaptureEvent event = new DefaultmentCaptureEvent(job.getMotechEvent());
        assertEquals("enrollment_1", event.getJobId());
        assertEquals(now.plusWeeks(4).toDate(), job.getStartDate());
        assertEquals(externalId, event.getExternalId());
    }

    @Test
    public void shouldScheduleJobOnlyIfMaxWindowEndDateIsNotInThePast() {
        Milestone milestone = new Milestone("milestone", weeks(1), weeks(1), weeks(1), weeks(1));
        milestone.addAlert(WindowName.earliest, new Alert(days(0), days(1), 3, 0, false));
        milestone.addAlert(WindowName.due, new Alert(days(0), weeks(1), 2, 1, false));
        String scheduleName = "my_schedule";
        Schedule schedule = new Schedule(scheduleName);
        schedule.addMilestones(milestone);
        String externalId = "entity_1";
        DateTime referenceDateTime = newDateTime(2012, 1, 1, 0, 0, 0).minusMonths(10);
        DateTime enrollmentDateTime = now();
        Enrollment enrollment = new Enrollment().setExternalId(externalId).setSchedule(schedule).setCurrentMilestoneName(milestone.getName()).setStartOfSchedule(referenceDateTime).setEnrolledOn(enrollmentDateTime).setPreferredAlertTime(new Time(8, 10)).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
        enrollment.setId("enrollment_1");

        enrollmentDefaultmentService.scheduleJobToCaptureDefaultment(enrollment);

        verifyNoMoreInteractions(schedulerService);
    }

    @Test
    public void shouldUnscheduleDefaultmentCaptureJob() {
        Schedule schedule = new Schedule("some_schedule");
        Enrollment enrollment = new Enrollment().setExternalId("entity_1").setSchedule(schedule).setCurrentMilestoneName("milestone").setStartOfSchedule(weeksAgo(0)).setEnrolledOn(weeksAgo(0)).setPreferredAlertTime(new Time(8, 10)).setStatus(EnrollmentStatus.ACTIVE).setMetadata(null);
        enrollment.setId("enrollment_1");

        enrollmentDefaultmentService.unscheduleDefaultmentCaptureJob(enrollment);

        verify(schedulerService).safeUnscheduleAllJobs(String.format("%s-enrollment_1", EventSubjects.DEFAULTMENT_CAPTURE));
    }
}
