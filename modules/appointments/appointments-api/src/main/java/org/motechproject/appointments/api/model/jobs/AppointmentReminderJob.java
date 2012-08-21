package org.motechproject.appointments.api.model.jobs;

import org.motechproject.appointments.api.EventKeys;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;

import java.util.HashMap;
import java.util.Map;

public class AppointmentReminderJob extends CronSchedulableJob {

    public static final String SUBJECT = EventKeys.APPOINTMENT_REMINDER_EVENT_SUBJECT;

    public AppointmentReminderJob(String externalId, String jobId, Reminder reminder, String visitName) {
        super(createMotechEvent(externalId, visitName, jobId), "0 0 0 ? * *", reminder.startDate(), reminder.endDate());
    }

    private static MotechEvent createMotechEvent(String externalId, String visitName, String jobId) {
        return new MotechEvent(SUBJECT, getParameters(externalId, visitName, jobId));
    }

    private static Map<String, Object> getParameters(String externalId, String visitName, String jobId) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(EventKeys.EXTERNAL_ID_KEY, externalId);
        parameters.put(MotechSchedulerService.JOB_ID_KEY, jobId);
        parameters.put(EventKeys.VISIT_NAME, visitName);
        return parameters;
    }

    public static String getJobIdUsing(String externalId, String visitName, Integer reminderCount) {
        return externalId + visitName + reminderCount;
    }
}
