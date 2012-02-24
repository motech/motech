package org.motechproject.appointments.api.dao;

import org.motechproject.appointments.api.EventKeys;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AllAppointmentReminderJobs {

    private MotechSchedulerService schedulerService;

    @Autowired
    public AllAppointmentReminderJobs(MotechSchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    public void add(Appointment appointment, String externalId) {
        Reminder reminder = appointment.reminder();
        MotechEvent reminderEvent = new MotechEvent(EventKeys.APPOINTMENT_REMINDER_EVENT_SUBJECT, getParameters(appointment, externalId));
        CronSchedulableJob schedulableJob = new CronSchedulableJob(reminderEvent, "0 0 0 ? * *", reminder.startDate(), reminder.endDate());
        schedulerService.safeScheduleJob(schedulableJob);
    }

    public void remove(String externalId) {
        schedulerService.safeUnscheduleJob(EventKeys.APPOINTMENT_REMINDER_EVENT_SUBJECT, externalId);
    }

    private Map<String, Object> getParameters(Appointment appointment, String externalId) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(EventKeys.EXTERNAL_ID_KEY, externalId);
        parameters.put(MotechSchedulerService.JOB_ID_KEY, appointment.id());
        parameters.put(EventKeys.APPOINTMENT_ID, appointment.id());
        return parameters;
    }
}
