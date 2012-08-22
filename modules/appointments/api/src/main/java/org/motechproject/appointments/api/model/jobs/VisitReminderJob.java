package org.motechproject.appointments.api.model.jobs;

import org.motechproject.appointments.api.EventKeys;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;

import java.util.HashMap;
import java.util.Map;

public class VisitReminderJob extends CronSchedulableJob {

    public static final String SUBJECT = EventKeys.VISIT_REMINDER_EVENT_SUBJECT;

    public VisitReminderJob(String externalId, Visit visit) {
        super(createMotechEvent(externalId, visit), "0 0 0 ? * *", visit.reminder().startDate(), visit.reminder().endDate());
    }

    private static MotechEvent createMotechEvent(String externalId, Visit visit) {
        return new MotechEvent(SUBJECT, getParameters(externalId, visit));
    }

    private static Map<String, Object> getParameters(String externalId, Visit visit) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(EventKeys.EXTERNAL_ID_KEY, externalId);
        parameters.put(MotechSchedulerService.JOB_ID_KEY, getJobIdUsing(externalId, visit));
        parameters.put(EventKeys.VISIT_NAME, visit.name());
        return parameters;
    }

    private static String getJobIdUsing(String externalId, Visit visit) {
        return externalId + visit.name();
    }
}
