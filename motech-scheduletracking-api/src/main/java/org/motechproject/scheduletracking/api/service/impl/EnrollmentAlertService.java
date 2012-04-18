package org.motechproject.scheduletracking.api.service.impl;

import org.joda.time.DateTime;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.RepeatingSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduletracking.api.domain.*;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.motechproject.util.DateUtil.newDateTime;

@Component
public class EnrollmentAlertService {

    private MotechSchedulerService schedulerService;

    @Autowired
    public EnrollmentAlertService(MotechSchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    public void scheduleAlertsForCurrentMilestone(Enrollment enrollment) {
        System.out.println("Attempting to schedule alerts");
    	Schedule schedule = enrollment.getSchedule();
        Milestone currentMilestone = schedule.getMilestone(enrollment.getCurrentMilestoneName());
        if (currentMilestone == null)
            return;

        DateTime alertReference = enrollment.getReferenceForAlerts();
        for (MilestoneWindow milestoneWindow : currentMilestone.getMilestoneWindows()) {
            if (currentMilestone.windowElapsed(milestoneWindow.getName(), alertReference))
                continue;

            MilestoneAlert milestoneAlert = MilestoneAlert.fromMilestone(currentMilestone, alertReference);
            for (Alert alert : milestoneWindow.getAlerts())
                scheduleAlertJob(alert, enrollment, currentMilestone, milestoneWindow, milestoneAlert, alertReference);
        }
    }

    private void scheduleAlertJob(Alert alert, Enrollment enrollment, Milestone currentMilestone, MilestoneWindow milestoneWindow, MilestoneAlert milestoneAlert, DateTime reference) {
        System.out.println("In scheduleAlertJob");
    	DateTime windowStartDate = reference.plus(currentMilestone.getWindowStart(milestoneWindow.getName()));
        int numberOfAlertsToSchedule = alert.getRemainingAlertCount(windowStartDate, enrollment.getPreferredAlertTime());
 
        System.out.println(numberOfAlertsToSchedule);
        
        if (numberOfAlertsToSchedule <= 0)
            return;

        MotechEvent event = new MilestoneEvent(enrollment, milestoneAlert, milestoneWindow).toMotechEvent();
        event.getParameters().put(MotechSchedulerService.JOB_ID_KEY, String.format("%s.%d", enrollment.getId(), alert.getIndex()));

        DateTime startTime = alert.getNextAlertDateTime(windowStartDate, enrollment.getPreferredAlertTime());
        System.out.println("Scheduling job for: " + milestoneWindow.getName() + " with a reapt of " + numberOfAlertsToSchedule + "at " + startTime);
        long repeatIntervalInMillis = (long) alert.getInterval().toStandardSeconds().getSeconds() * 1000;
        schedulerService.safeScheduleRepeatingJob(new RepeatingSchedulableJob(event, startTime.toDate(), null, numberOfAlertsToSchedule - 1, repeatIntervalInMillis));
    }

    public void unscheduleAllAlerts(Enrollment enrollment) {
        schedulerService.safeUnscheduleAllJobs(String.format("%s-%s", EventSubjects.MILESTONE_ALERT, enrollment.getId()));
    }
}
