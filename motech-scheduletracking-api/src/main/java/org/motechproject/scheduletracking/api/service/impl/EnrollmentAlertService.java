package org.motechproject.scheduletracking.api.service.impl;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.RepeatingSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduletracking.api.domain.*;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.joda.time.DateTimeConstants.MILLIS_PER_DAY;

@Component
public class EnrollmentAlertService {

    private AllTrackedSchedules allTrackedSchedules;
    private MotechSchedulerService schedulerService;

    @Autowired
    public EnrollmentAlertService(AllTrackedSchedules allTrackedSchedules, MotechSchedulerService schedulerService) {
        this.allTrackedSchedules = allTrackedSchedules;
        this.schedulerService = schedulerService;
    }

    public void scheduleAlertsForCurrentMilestone(Enrollment enrollment) {
        Schedule schedule = allTrackedSchedules.getByName(enrollment.getScheduleName());
        Milestone currentMilestone = schedule.getMilestone(enrollment.getCurrentMilestoneName());
        if (currentMilestone == null)
            return;

        String firstMilestoneName = schedule.getFirstMilestone().getName();
        DateTime currentMilestoneStartDate = getCurrentMilestoneStartDate(enrollment);
        for (MilestoneWindow milestoneWindow : currentMilestone.getMilestoneWindows()) {
            if (currentMilestone.windowElapsed(milestoneWindow.getName(), currentMilestoneStartDate))
                continue;

            MilestoneAlert milestoneAlert = MilestoneAlert.fromMilestone(currentMilestone, enrollment.getReferenceDateTime());
            for (Alert alert : milestoneWindow.getAlerts())
                scheduleAlertJob(alert, enrollment, currentMilestone, milestoneWindow, milestoneAlert, currentMilestoneStartDate);
        }
    }

    // duplicated and tested in enrollment service!
    private DateTime getCurrentMilestoneStartDate(Enrollment enrollment) {
        Schedule schedule = allTrackedSchedules.getByName(enrollment.getScheduleName());
        if (enrollment.getCurrentMilestoneName().equals(schedule.getFirstMilestone().getName()))
            return enrollment.getReferenceDateTime();
        return (enrollment.getFulfillments().isEmpty()) ? enrollment.getEnrollmentDateTime() : enrollment.lastFulfilledDate();
    }

    private void scheduleAlertJob(Alert alert, Enrollment enrollment, Milestone currentMilestone, MilestoneWindow milestoneWindow, MilestoneAlert milestoneAlert, DateTime currentMilestoneStartDate) {
        DateTime windowStartDate = currentMilestoneStartDate.plus(currentMilestone.getWindowStart(milestoneWindow.getName()));
        int numberOfAlertsToSchedule = alert.getRemainingAlertCount(windowStartDate, enrollment.getPreferredAlertTime());
        if (numberOfAlertsToSchedule <= 0)
            return;

        MotechEvent event = new MilestoneEvent(enrollment, milestoneAlert, milestoneWindow).toMotechEvent();
        event.getParameters().put(MotechSchedulerService.JOB_ID_KEY, String.format("%s.%d", enrollment.getId(), alert.getIndex()));

        DateTime startTime = alert.getNextAlertDateTime(windowStartDate, enrollment.getPreferredAlertTime());
        long repeatIntervalInMillis = (long) alert.getInterval().toStandardSeconds().getSeconds() * 1000;
        schedulerService.safeScheduleRepeatingJob(new RepeatingSchedulableJob(event, startTime.toDate(), null, numberOfAlertsToSchedule - 1, repeatIntervalInMillis));
    }

    public void unscheduleAllAlerts(Enrollment enrollment) {
        schedulerService.safeUnscheduleAllJobs(String.format("%s-%s", EventSubjects.MILESTONE_ALERT, enrollment.getId()));
    }
}
