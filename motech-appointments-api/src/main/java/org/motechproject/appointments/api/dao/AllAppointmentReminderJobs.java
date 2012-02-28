package org.motechproject.appointments.api.dao;

import org.motechproject.appointments.api.EventKeys;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.model.CronSchedulableJob;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
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
        scheduleDueDateReminders(appointment, externalId);
    }

    private void scheduleDueDateReminders(Appointment appointment, String externalId) {
        if (appointment.confirmedDate() != null) return;
        Reminder reminder = appointment.reminder();
        scheduleMidnightJobs(appointment, externalId, EventKeys.APPOINTMENT_REMINDER_EVENT_SUBJECT, reminder.startDate(), reminder.endDate());
        scheduleMidnightJobs(appointment, externalId, EventKeys.DAY_AFTER_DUE_DATE_EVENT_SUBJECT, appointment.dueDate().plusDays(1).toDate(), appointment.dueDate().plusDays(1).toDate());
    }

    private void scheduleMidnightJobs(Appointment appointment, String externalId, String eventSubject, Date startTime, Date endTime) {
        MotechEvent reminderEvent = motechEvent(appointment, externalId, eventSubject);
        CronSchedulableJob schedulableJob = new CronSchedulableJob(reminderEvent, "0 0 0 ? * *", startTime, endTime);
        schedulerService.safeScheduleJob(schedulableJob);
    }

    private MotechEvent motechEvent(Appointment appointment, String externalId, String eventSubject) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(EventKeys.EXTERNAL_ID_KEY, externalId);
        parameters.put(MotechSchedulerService.JOB_ID_KEY, eventSubject + "-" + appointment.id());
        parameters.put(EventKeys.APPOINTMENT_ID, appointment.id());
        return new MotechEvent(eventSubject, parameters);
    }

    public void remove(String externalId) {
        schedulerService.safeUnscheduleJob(EventKeys.APPOINTMENT_REMINDER_EVENT_SUBJECT, externalId);
    }

}
