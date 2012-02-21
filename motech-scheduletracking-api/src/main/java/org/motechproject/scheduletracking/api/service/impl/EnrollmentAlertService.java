package org.motechproject.scheduletracking.api.service.impl;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.RepeatingSchedulableJob;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduletracking.api.domain.*;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubject;
import org.motechproject.scheduletracking.api.repository.AllTrackedSchedules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
            if (!window.hasElapsed(getCurrentMilestoneStartDate(enrollment))) {
                for (Alert alert : window.getAlerts())
                    scheduleAlertJob(alert, enrollment, schedule, currentMilestone, window);
            }
        }
    }

    private void scheduleAlertJob(Alert alert, Enrollment enrollment, Schedule schedule, Milestone milestone, MilestoneWindow milestoneWindow) {
        MotechEvent event = new MilestoneEvent(enrollment.getExternalId(), schedule.getName(), milestone.getName(), milestoneWindow.getName().toString(), enrollment.getReferenceDate()).toMotechEvent();
        event.getParameters().put(MotechSchedulerService.JOB_ID_KEY, String.format("%s.%d", enrollment.getId(), alert.getIndex()));
        DateTime startTime = newDateTime(getNextAlertStartDate(alert, enrollment, milestoneWindow), enrollment.getPreferredAlertTime());
        long repeatIntervalInMillis = (long) alert.getInterval().inDays() * (long) MILLIS_PER_DAY;
        RepeatingSchedulableJob job = new RepeatingSchedulableJob(event, startTime.toDate(), null, numberOfAlertsToSchedule(alert, enrollment, milestoneWindow), repeatIntervalInMillis);
        schedulerService.safeScheduleRepeatingJob(job);
    }

    private LocalDate getNextAlertStartDate(Alert alert, Enrollment enrollment, MilestoneWindow milestoneWindow) {
        LocalDate idealStartOfAlerts = getStartDateOfWindow(enrollment, milestoneWindow).plusDays(alert.getOffset().inDays());
        int elapsedAlerts = numberOfElapsedAlerts(alert, enrollment, milestoneWindow);
        return idealStartOfAlerts.plusDays(elapsedAlerts * alert.getInterval().inDays());
    }

    private int numberOfAlertsToSchedule(Alert alert, Enrollment enrollment, MilestoneWindow milestoneWindow) {
        return alert.getRepeatCount() - numberOfElapsedAlerts(alert, enrollment, milestoneWindow);
    }

    private int numberOfElapsedAlerts(Alert alert, Enrollment enrollment, MilestoneWindow milestoneWindow) {
        DateTime now = now();
        LocalDate today = now.toLocalDate();
        LocalDate idealStartOfAlerts = getStartDateOfWindow(enrollment, milestoneWindow).plusDays(alert.getOffset().inDays());
        if (idealStartOfAlerts.isBefore(today)) {
            int daysSinceIdealStartOfAlert = daysBetween(idealStartOfAlerts, today).getDays();
            return (int) Math.ceil(daysSinceIdealStartOfAlert / (double) alert.getInterval().inDays());
        } else if (idealStartOfAlerts.equals(today)) {
            return (enrollment.getPreferredAlertTime().isBefore(new Time(now.getHourOfDay(), now.getMinuteOfHour())))? 1 : 0;
        }
        return 0;
    }

    private LocalDate getStartDateOfWindow(Enrollment enrollment, MilestoneWindow milestoneWindow) {
        return getCurrentMilestoneStartDate(enrollment).plusDays(milestoneWindow.getStart().inDays());
    }

    public LocalDate getCurrentMilestoneStartDate(Enrollment enrollment) {
        Schedule schedule = allTrackedSchedules.getByName(enrollment.getScheduleName());
        if (enrollment.getCurrentMilestoneName().equals(schedule.getFirstMilestone().getName()))
            return enrollment.getReferenceDate();
        return (enrollment.getFulfillments().isEmpty()) ? enrollment.getEnrollmentDate() : enrollment.lastFulfilledDate();
    }

    public void unscheduleAllAlerts(Enrollment enrollment) {
        schedulerService.unscheduleAllJobs(String.format("%s-%s", EventSubject.MILESTONE_ALERT, enrollment.getId()));
    }
}
