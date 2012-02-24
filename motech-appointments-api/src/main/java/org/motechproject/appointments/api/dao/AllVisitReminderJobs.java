package org.motechproject.appointments.api.dao;

import org.motechproject.appointments.api.EventKeys;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.RepeatingSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.JobId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AllVisitReminderJobs {

    private MotechSchedulerService schedulerService;

    @Autowired
    public AllVisitReminderJobs(MotechSchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    public void add(Visit visit, String externalId) {
        Reminder reminder = visit.reminder();
        MotechEvent reminderEvent = new MotechEvent(EventKeys.VISIT_REMINDER_EVENT_SUBJECT, getParameters(visit, externalId));
        RepeatingSchedulableJob schedulableJob = new RepeatingSchedulableJob(reminderEvent, reminder.startDate(), reminder.endDate(), reminder.repeatCount(), reminder.intervalSeconds() * 1000);
        schedulerService.safeScheduleRepeatingJob(schedulableJob);
    }

    public void remove(String externalId) {
        schedulerService.safeUnscheduleRepeatingJob(EventKeys.VISIT_REMINDER_EVENT_SUBJECT, externalId);
    }

    private Map<String, Object> getParameters(Visit visit, String externalId) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(EventKeys.EXTERNAL_ID_KEY, externalId);
        parameters.put(MotechSchedulerService.JOB_ID_KEY, new JobId(EventKeys.VISIT_REMINDER_EVENT_SUBJECT, externalId).value());
        parameters.put(EventKeys.APPOINTMENT_ID, visit.appointment().id());
        return parameters;
    }
}
