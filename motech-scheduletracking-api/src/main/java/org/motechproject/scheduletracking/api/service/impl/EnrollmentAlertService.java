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
        for (MilestoneWindow window : currentMilestone.getMilestoneWindows()) {
            String firstMilestoneName = schedule.getFirstMilestone().getName();
            LocalDate currentMilestoneStartDate = enrollment.getCurrentMilestoneStartDate(firstMilestoneName);
            if (!window.hasElapsed(currentMilestoneStartDate)) {
                for (Alert alert : window.getAlerts())
                    scheduleAlertJob(alert, enrollment, currentMilestone, window, firstMilestoneName, schedule.getName());
            }
        }
    }

    private void scheduleAlertJob(Alert alert, Enrollment enrollment, Milestone milestone, MilestoneWindow milestoneWindow, String firstMilestoneName, String scheduleName) {
        final MilestoneAlert milestoneAlert = MilestoneAlert.fromMilestone(milestone, enrollment.getReferenceDate());
        MotechEvent event = new MilestoneEvent(enrollment.getExternalId(), scheduleName, milestoneAlert, milestoneWindow.getName().toString(), enrollment.getReferenceDate()).toMotechEvent();
        event.getParameters().put(MotechSchedulerService.JOB_ID_KEY, String.format("%s.%d", enrollment.getId(), alert.getIndex()));
        DateTime startTime = getNextAlertStartDate(alert, enrollment, milestoneWindow, firstMilestoneName);
        long repeatIntervalInMillis = (long) alert.getInterval().inDays() * (long) MILLIS_PER_DAY;
        int numberOfAlertsToSchedule = numberOfAlertsToSchedule(alert, enrollment, milestoneWindow, firstMilestoneName);
        if (numberOfAlertsToSchedule > 0)
            schedulerService.safeScheduleRepeatingJob(new RepeatingSchedulableJob(event, startTime.toDate(), null, numberOfAlertsToSchedule - 1, repeatIntervalInMillis));
    }

    private DateTime getNextAlertStartDate(Alert alert, Enrollment enrollment, MilestoneWindow milestoneWindow, String firstMilestoneName) {
        LocalDate currentMilestoneStartDate = enrollment.getCurrentMilestoneStartDate(firstMilestoneName);
        LocalDate milestoneWindowStartDate = milestoneWindow.getStartDate(currentMilestoneStartDate);
        return alert.getNextAlertDateTime(milestoneWindowStartDate, enrollment.getPreferredAlertTime());
    }

    private int numberOfAlertsToSchedule(Alert alert, Enrollment enrollment, MilestoneWindow milestoneWindow, String firstMilestoneName) {
        LocalDate currentMilestoneStartDate = enrollment.getCurrentMilestoneStartDate(firstMilestoneName);
        LocalDate milestoneWindowStartDate = milestoneWindow.getStartDate(currentMilestoneStartDate);

        return alert.getCount() - alert.getElapsedAlertCount(milestoneWindowStartDate, enrollment.getPreferredAlertTime());
    }

    public void unscheduleAllAlerts(Enrollment enrollment) {
        schedulerService.unscheduleAllJobs(String.format("%s-%s", EventSubjects.MILESTONE_ALERT, enrollment.getId()));
    }
}
