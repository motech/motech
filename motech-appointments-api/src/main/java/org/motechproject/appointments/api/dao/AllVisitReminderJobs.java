package org.motechproject.appointments.api.dao;

import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.appointments.api.model.jobs.VisitReminderJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AllVisitReminderJobs {

    private MotechSchedulerService schedulerService;

    @Autowired
    public AllVisitReminderJobs(MotechSchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    public void add(Visit visit, String externalId) {
        Reminder reminder = visit.reminder();
        VisitReminderJob visitReminderJob = new VisitReminderJob(externalId, reminder.startDate(), visit);
        schedulerService.safeScheduleJob(visitReminderJob);
    }

    public void remove(String externalId) {
        schedulerService.safeUnscheduleJob(VisitReminderJob.SUBJECT, externalId);
    }
}
