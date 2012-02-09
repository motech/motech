package org.motechproject.scheduletracking.api.service.impl;

import org.joda.time.LocalDate;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.RepeatingSchedulableJob;
import org.motechproject.model.RunOnceSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduletracking.api.domain.*;
import org.motechproject.scheduletracking.api.events.DefaultmentCaptureEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubject;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EnrollmentDefaultmentService {

    public static final String DEFAULTMENT_CAPTURE = "defaultment_capture";

    private AllTrackedSchedules allTrackedSchedules;
    private MotechSchedulerService schedulerService;

    @Autowired
    public EnrollmentDefaultmentService(AllTrackedSchedules allTrackedSchedules, MotechSchedulerService schedulerService) {
        this.allTrackedSchedules = allTrackedSchedules;
        this.schedulerService = schedulerService;
    }

    public void scheduleJobToCaptureDefaultment(Enrollment enrollment) {
        Schedule schedule = allTrackedSchedules.getByName(enrollment.getScheduleName());
        Milestone currentMilestone = schedule.getMilestone(enrollment.getCurrentMilestoneName());
        if (currentMilestone == null)
            return;
        LocalDate startOfLateWindow = getCurrentMilestoneStartDate(enrollment).plusDays(currentMilestone.getMaximumDurationInDays());
        MotechEvent event = new DefaultmentCaptureEvent(enrollment.getId(), String.format("%s.%s.%s", EventSubject.BASE_SUBJECT, DEFAULTMENT_CAPTURE, enrollment.getId())).toMotechEvent();
        schedulerService.scheduleRunOnceJob(new RunOnceSchedulableJob(event, startOfLateWindow.toDate()));
    }

    // TODO: duplicated from EnrollmentAlertService
    LocalDate getCurrentMilestoneStartDate(Enrollment enrollment) {
        Schedule schedule = allTrackedSchedules.getByName(enrollment.getScheduleName());
        if (enrollment.getCurrentMilestoneName().equals(schedule.getFirstMilestone().getName()))
            return enrollment.getReferenceDate();
        return (enrollment.getFulfillments().isEmpty())? enrollment.getEnrollmentDate() : enrollment.getLastFulfilledDate();
    }

    public void unscheduleDefaultmentCaptureJob(Enrollment enrollment) {
        schedulerService.unscheduleAllJobs(String.format("%s.%s.%s", EventSubject.BASE_SUBJECT, DEFAULTMENT_CAPTURE, enrollment.getId()));
    }
}
