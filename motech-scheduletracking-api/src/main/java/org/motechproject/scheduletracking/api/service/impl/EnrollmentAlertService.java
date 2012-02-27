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
        LocalDate currentMilestoneStartDate = enrollment.getCurrentMilestoneStartDate(firstMilestoneName);
        for (MilestoneWindow milestoneWindow : currentMilestone.getMilestoneWindows()) {
            if (currentMilestone.windowElapsed(milestoneWindow.getName(), currentMilestoneStartDate))
                continue;

            MilestoneAlert milestoneAlert = MilestoneAlert.fromMilestone(currentMilestone, enrollment.getReferenceDate());
            for (Alert alert : milestoneWindow.getAlerts())
                scheduleAlertJob(alert, enrollment, currentMilestone, milestoneWindow, milestoneAlert, currentMilestoneStartDate);
        }
    }

    private void scheduleAlertJob(Alert alert, Enrollment enrollment, Milestone currentMilestone, MilestoneWindow milestoneWindow, MilestoneAlert milestoneAlert, LocalDate currentMilestoneStartDate) {
        LocalDate milestoneWindowStartDate = currentMilestoneStartDate.plus(currentMilestone.getWindowStart(milestoneWindow.getName()));
        int numberOfAlertsToSchedule = alert.getRemainingAlertCount(milestoneWindowStartDate, enrollment.getPreferredAlertTime());
        if (numberOfAlertsToSchedule <= 0)
            return;

        MotechEvent event = new MilestoneEvent(enrollment, milestoneAlert, milestoneWindow).toMotechEvent();
        event.getParameters().put(MotechSchedulerService.JOB_ID_KEY, String.format("%s.%d", enrollment.getId(), alert.getIndex()));

        DateTime startTime = alert.getNextAlertDateTime(milestoneWindowStartDate, enrollment.getPreferredAlertTime());
        long repeatIntervalInMillis = (long) alert.getInterval().toStandardDays().getDays() * (long) MILLIS_PER_DAY;
        schedulerService.safeScheduleRepeatingJob(new RepeatingSchedulableJob(event, startTime.toDate(), null, numberOfAlertsToSchedule - 1, repeatIntervalInMillis));
    }

    public void unscheduleAllAlerts(Enrollment enrollment) {
        schedulerService.safeUnscheduleAllJobs(String.format("%s-%s", EventSubjects.MILESTONE_ALERT, enrollment.getId()));
    }
}
