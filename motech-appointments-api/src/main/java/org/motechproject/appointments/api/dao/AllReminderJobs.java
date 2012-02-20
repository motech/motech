package org.motechproject.appointments.api.dao;

import org.motechproject.appointments.api.EventKeys;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.RepeatingSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.JobId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AllReminderJobs {

    private MotechSchedulerService schedulerService;

    @Autowired
    public AllReminderJobs(MotechSchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    public void add(Reminder reminder, String externalId) {
        if (reminder == null) return;
        MotechEvent reminderEvent = new MotechEvent(EventKeys.REMINDER_EVENT_SUBJECT, getParameters(reminder, externalId));
        RepeatingSchedulableJob schedulableJob = new RepeatingSchedulableJob(reminderEvent, reminder.startDate(), reminder.endDate(), reminder.repeatCount(), reminder.intervalSeconds() * 1000);
        schedulerService.safeScheduleRepeatingJob(schedulableJob);
    }

    private Map<String, Object> getParameters(Reminder reminder, String externalId) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(EventKeys.REMINDER_ID_KEY, reminder.id());
        parameters.put(EventKeys.EXTERNAL_ID_KEY, externalId);
        parameters.put(EventKeys.JOB_ID_KEY, new JobId(EventKeys.REMINDER_EVENT_SUBJECT, externalId).value());
        return parameters;
    }
}
