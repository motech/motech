package org.motechproject.appointments.api;

import org.motechproject.appointments.api.dao.AllReminders;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.RepeatingSchedulableJob;
import org.motechproject.model.RunOnceSchedulableJob;
import org.motechproject.scheduler.MotechSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class ReminderService {

    private MotechSchedulerService schedulerService;
    private AllReminders allReminders;

    @Autowired
    public ReminderService(MotechSchedulerService schedulerService, AllReminders allReminders) {
        this.schedulerService = schedulerService;
        this.allReminders = allReminders;
    }

    public void addReminder(Reminder reminder) {
        allReminders.add(reminder);
        createReminderJobs(reminder);
    }

    private void createReminderJobs(Reminder reminder) {
        MotechEvent reminderEvent = new MotechEvent(EventKeys.REMINDER_EVENT_SUBJECT, getParameters(reminder));
        if (null != reminder.getUnits()) {
            RepeatingSchedulableJob schedulableJob = new RepeatingSchedulableJob(reminderEvent, reminder.getStartDate(), reminder.getEndDate(), reminder.getRepeatCount(), reminder.getIntervalSeconds() * 1000);
            schedulerService.safeScheduleRepeatingJob(schedulableJob);
        } else {
            RunOnceSchedulableJob schedulableJob = new RunOnceSchedulableJob(reminderEvent, reminder.getStartDate());
            schedulerService.safeScheduleRunOnceJob(schedulableJob);
        }
    }

    private Map<String, Object> getParameters(Reminder reminder) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(EventKeys.REMINDER_SUBJECT_ID, reminder.getReminderSubjectId());
        parameters.put(EventKeys.REMINDER_ID_KEY, reminder.getId());
        parameters.put(EventKeys.EXTERNAL_ID_KEY, reminder.getExternalId());
        parameters.put(EventKeys.JOB_ID_KEY, reminder.getReminderSubjectId());
        return parameters;
    }
}
