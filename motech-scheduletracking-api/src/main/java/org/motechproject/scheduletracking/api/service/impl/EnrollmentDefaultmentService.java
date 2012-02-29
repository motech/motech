package org.motechproject.scheduletracking.api.service.impl;

import org.joda.time.LocalDate;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.RunOnceSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.Milestone;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.events.DefaultmentCaptureEvent;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.motechproject.scheduletracking.api.events.constants.EventSubjects.DEFAULTMENT_CAPTURE;
import static org.motechproject.util.DateUtil.today;

@Component
public class EnrollmentDefaultmentService {
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

        LocalDate currentMilestoneStartDate = enrollment.getCurrentMilestoneStartDate(schedule.getFirstMilestone().getName());
        LocalDate milestoneEndDate = currentMilestoneStartDate.plus(currentMilestone.getMaximumDuration());

        if (milestoneEndDate.isBefore(today()))
            return;

        MotechEvent event = new DefaultmentCaptureEvent(enrollment.getId(), enrollment.getId()).toMotechEvent();
        schedulerService.safeScheduleRunOnceJob(new RunOnceSchedulableJob(event, milestoneEndDate.toDate()));
    }

    public void unscheduleDefaultmentCaptureJob(Enrollment enrollment) {
        schedulerService.safeUnscheduleAllJobs(String.format("%s-%s", DEFAULTMENT_CAPTURE, enrollment.getId()));
    }
}
