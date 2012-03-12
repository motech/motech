package org.motechproject.appointments.api.dao;

import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.appointments.api.model.jobs.AppointmentReminderJob;
import org.motechproject.appointments.api.model.jobs.VisitReminderJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AllReminderJobs {

    private MotechSchedulerService schedulerService;

    @Autowired
    public AllReminderJobs(MotechSchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    public void addAppointmentJob(String externalId, Visit visit) {
        List<Reminder> reminders = visit.appointmentReminders();
        for (int i = 0; i < reminders.size(); i++) {
            String jobId = AppointmentReminderJob.getJobIdUsing(externalId, visit.name(), i);
            schedulerService.safeScheduleJob(new AppointmentReminderJob(externalId, jobId, reminders.get(i), visit.name()));
        }
    }

    public void addVisitJob(String externalId, Visit visit) {
        if (visit.reminder() == null) return;
        VisitReminderJob visitReminderJob = new VisitReminderJob(externalId, visit);
        schedulerService.safeScheduleJob(visitReminderJob);
    }

    public void removeAppointmentJob(String externalId, Visit visit) {
        schedulerService.unscheduleAllJobs(AppointmentReminderJob.SUBJECT + externalId + visit.name());
    }

    public void removeVisitJob(String externalId, Visit visit) {
        schedulerService.unscheduleAllJobs(VisitReminderJob.SUBJECT + externalId + visit.name());
    }

    public void rescheduleAppointmentJob(String externalId, Visit visit) {
        removeAppointmentJob(externalId, visit);
        addAppointmentJob(externalId, visit);
    }

    public void rescheduleVisitJob(String externalId, Visit visit) {
        removeVisitJob(externalId, visit);
        addVisitJob(externalId, visit);
    }

    public void removeAll(String externalId) {
        schedulerService.unscheduleAllJobs(AppointmentReminderJob.SUBJECT + externalId);
        schedulerService.unscheduleAllJobs(VisitReminderJob.SUBJECT + externalId);
    }
}
