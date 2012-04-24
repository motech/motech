package org.motechproject.scheduletracking.api.service.impl;

import org.joda.time.DateTime;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.RepeatingSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduletracking.api.domain.*;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;
import org.motechproject.scheduletracking.api.service.MilestoneAlerts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EnrollmentAlertService {

    private MotechSchedulerService schedulerService;

    @Autowired
    public EnrollmentAlertService(MotechSchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    public void scheduleAlertsForCurrentMilestone(Enrollment enrollment) {
        Schedule schedule = enrollment.getSchedule();
        Milestone currentMilestone = schedule.getMilestone(enrollment.getCurrentMilestoneName());
        if (currentMilestone == null)
            return;

        DateTime currentMilestoneStartDate = enrollment.getCurrentMilestoneStartDate();
        for (MilestoneWindow milestoneWindow : currentMilestone.getMilestoneWindows()) {
            if (currentMilestone.windowElapsed(milestoneWindow.getName(), currentMilestoneStartDate))
                continue;

            MilestoneAlert milestoneAlert = MilestoneAlert.fromMilestone(currentMilestone, currentMilestoneStartDate);
            for (Alert alert : milestoneWindow.getAlerts()) {
                scheduleAlertJob(alert, enrollment, currentMilestone, milestoneWindow, milestoneAlert, currentMilestoneStartDate);
            }
        }
    }

    private void scheduleAlertJob(Alert alert, Enrollment enrollment, Milestone currentMilestone, MilestoneWindow milestoneWindow, MilestoneAlert milestoneAlert, DateTime reference) {
        DateTime alertReference = getAlertReference(alert, enrollment, currentMilestone, milestoneWindow, reference);

        int numberOfAlertsToSchedule = getNumberOfAlertsToSchedule(alert, enrollment, currentMilestone, milestoneWindow, alertReference);
        if (numberOfAlertsToSchedule <= 0)
            return;

        MotechEvent event = new MilestoneEvent(enrollment, milestoneAlert, milestoneWindow).toMotechEvent();
        event.getParameters().put(MotechSchedulerService.JOB_ID_KEY, String.format("%s.%d", enrollment.getId(), alert.getIndex()));

        DateTime startTime = alert.getNextAlertDateTime(alertReference, enrollment.getPreferredAlertTime());
        long repeatIntervalInMillis = (long) alert.getInterval().toStandardSeconds().getSeconds() * 1000;
        schedulerService.safeScheduleRepeatingJob(new RepeatingSchedulableJob(event, startTime.toDate(), null, numberOfAlertsToSchedule - 1, repeatIntervalInMillis));
    }

    private int getNumberOfAlertsToSchedule(Alert alert, Enrollment enrollment, Milestone currentMilestone, MilestoneWindow milestoneWindow, DateTime alertReference) {
        DateTime currentMilestoneStartTime = enrollment.getCurrentMilestoneStartDate();
        DateTime windowEndTime = currentMilestoneStartTime.plus(currentMilestone.getWindowEnd(milestoneWindow.getName()));

        return alert.getRemainingAlertCount(alertReference, windowEndTime, enrollment.getPreferredAlertTime());
    }

    private DateTime getAlertReference(Alert alert, Enrollment enrollment, Milestone currentMilestone, MilestoneWindow milestoneWindow, DateTime reference) {
        DateTime windowStartDate = reference.plus(currentMilestone.getWindowStart(milestoneWindow.getName()));
        DateTime alertReference;
        if (alert.isFloating() && enrollment.getEnrolledOn().isAfter(windowStartDate))
            alertReference = enrollment.getEnrolledOn();
        else
            alertReference = windowStartDate;
        return alertReference;
    }

    public void unscheduleAllAlerts(Enrollment enrollment) {
        schedulerService.safeUnscheduleAllJobs(String.format("%s-%s", EventSubjects.MILESTONE_ALERT, enrollment.getId()));
    }

    public MilestoneAlerts getAlertTimings(Enrollment enrollment) {
        Schedule schedule = enrollment.getSchedule();
        Milestone currentMilestone = schedule.getMilestone(enrollment.getCurrentMilestoneName());
        MilestoneAlerts milestoneAlerts = new MilestoneAlerts();
        if (currentMilestone == null)
            return milestoneAlerts;

        DateTime currentMilestoneStartDate = enrollment.getCurrentMilestoneStartDate();
        for (MilestoneWindow milestoneWindow : currentMilestone.getMilestoneWindows()) {
            List<DateTime> alertTimingsForWindow = new ArrayList<DateTime>();
            for (Alert alert : milestoneWindow.getAlerts()) {
                DateTime alertReference = getAlertReference(alert, enrollment, currentMilestone, milestoneWindow, currentMilestoneStartDate);

                long repeatIntervalInMillis = (long) alert.getInterval().toStandardSeconds().getSeconds() * 1000;
                DateTime startTimeForAlerts = alertReference.plus(alert.getOffset());
                alertTimingsForWindow.add(startTimeForAlerts);
                DateTime currentAlertTime = startTimeForAlerts;
                DateTime nextAlertTime;
                for(int i = 1; i <= (alert.getCount() - 1); i++) {
                    nextAlertTime = currentAlertTime.plus(repeatIntervalInMillis);
                    alertTimingsForWindow.add(nextAlertTime);
                    currentAlertTime = nextAlertTime;
                }
            }
            milestoneAlerts.getAlertTimings().put(milestoneWindow.getName().toString(), alertTimingsForWindow);
        }
        return milestoneAlerts;
    }
}
