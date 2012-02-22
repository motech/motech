package org.motechproject.scheduletracking.api.service.impl;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.RepeatingSchedulableJob;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduletracking.api.domain.*;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.lang.Math.ceil;
import static java.lang.Math.min;
import static org.joda.time.DateTimeConstants.MILLIS_PER_DAY;
import static org.joda.time.Days.daysBetween;
import static org.motechproject.util.DateUtil.newDateTime;
import static org.motechproject.util.DateUtil.now;

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
            LocalDate currentMilestoneStartDate = enrollment.getCurrentMilestoneStartDate(schedule.getFirstMilestone().getName());
            if (!window.hasElapsed(currentMilestoneStartDate)) {
                for (Alert alert : window.getAlerts())
                    scheduleAlertJob(alert, enrollment, schedule, currentMilestone, window);
            }
        }
    }

    private void scheduleAlertJob(Alert alert, Enrollment enrollment, Schedule schedule, Milestone milestone, MilestoneWindow milestoneWindow) {
        final MilestoneAlert milestoneAlert = MilestoneAlert.fromMilestone(milestone, enrollment.getReferenceDate());
        MotechEvent event = new MilestoneEvent(enrollment.getExternalId(), schedule.getName(), milestoneAlert, milestoneWindow.getName().toString(), enrollment.getReferenceDate()).toMotechEvent();
        event.getParameters().put(MotechSchedulerService.JOB_ID_KEY, String.format("%s.%d", enrollment.getId(), alert.getIndex()));
        DateTime startTime = newDateTime(getNextAlertStartDate(alert, enrollment, milestoneWindow), enrollment.getPreferredAlertTime());
        long repeatIntervalInMillis = (long) alert.getInterval().inDays() * (long) MILLIS_PER_DAY;
        int numberOfAlertsToSchedule = numberOfAlertsToSchedule(alert, enrollment, milestoneWindow);
        if (numberOfAlertsToSchedule > 0)
            schedulerService.safeScheduleRepeatingJob(new RepeatingSchedulableJob(event, startTime.toDate(), null, numberOfAlertsToSchedule - 1, repeatIntervalInMillis));
    }

    private LocalDate getNextAlertStartDate(Alert alert, Enrollment enrollment, MilestoneWindow milestoneWindow) {
        LocalDate idealStartOfAlerts = getStartDateOfWindow(enrollment, milestoneWindow).plusDays(alert.getOffset().inDays());
        int elapsedAlerts = elapsedAlertsAsOfNow(alert, enrollment, milestoneWindow);
        return idealStartOfAlerts.plusDays(elapsedAlerts * alert.getInterval().inDays());
    }

    private int numberOfAlertsToSchedule(Alert alert, Enrollment enrollment, MilestoneWindow milestoneWindow) {
        return alert.getCount() - elapsedAlertsAsOfNow(alert, enrollment, milestoneWindow);
    }

    private int elapsedAlertsAsOfNow(Alert alert, Enrollment enrollment, MilestoneWindow milestoneWindow) {
        DateTime now = now();
        LocalDate today = now.toLocalDate();
        LocalDate idealStartOfAlerts = getStartDateOfWindow(enrollment, milestoneWindow).plusDays(alert.getOffset().inDays());
        int elapsedAlerts = 0;
        if (idealStartOfAlerts.isBefore(today)) {
            int daysSinceIdealStartOfAlert = daysBetween(idealStartOfAlerts, today).getDays();
            elapsedAlerts = (int) ceil(daysSinceIdealStartOfAlert / (double) alert.getInterval().inDays());
        } else if (idealStartOfAlerts.equals(today)) {
            elapsedAlerts = (enrollment.getPreferredAlertTime().isBefore(new Time(now.getHourOfDay(), now.getMinuteOfHour()))) ? 1 : 0;
        }
        return min(elapsedAlerts, alert.getCount() + 1);
    }

    private LocalDate getStartDateOfWindow(Enrollment enrollment, MilestoneWindow milestoneWindow) {
        Schedule schedule = allTrackedSchedules.getByName(enrollment.getScheduleName());

        LocalDate currentMilestoneStartDate = enrollment.getCurrentMilestoneStartDate(schedule.getFirstMilestone().getName());
        return currentMilestoneStartDate.plusDays(milestoneWindow.getStart().inDays());
    }

    public void unscheduleAllAlerts(Enrollment enrollment) {
        schedulerService.unscheduleAllJobs(String.format("%s-%s", EventSubjects.MILESTONE_ALERT, enrollment.getId()));
    }
}
