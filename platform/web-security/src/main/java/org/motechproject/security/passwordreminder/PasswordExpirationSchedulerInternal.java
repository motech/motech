package org.motechproject.security.passwordreminder;

import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.contract.CronSchedulableJob;
import org.motechproject.scheduler.service.MotechSchedulerService;

import static org.motechproject.security.constants.EmailConstants.PASSWORD_EXPIRATION_CHECK_EVENT;

/**
 * Responsible for scheduling job that will trigger a check on last password change for all users and will send an event
 * notifying the user about incoming, required password change if needed.
 */
public class PasswordExpirationSchedulerInternal {

    private final MotechSchedulerService schedulerService;

    PasswordExpirationSchedulerInternal(Object schedulerService) {
        if (!(schedulerService instanceof MotechSchedulerService)) {
            throw new IllegalArgumentException("Object passed is not a MotechSchedulerService");
        }
        this.schedulerService = (MotechSchedulerService) schedulerService;
    }

    /**
     * Schedules password reminder job that will be run everyday at midnight.
     */
    public void schedulePasswordReminderJob() {
        CronSchedulableJob job = new CronSchedulableJob(new MotechEvent(PASSWORD_EXPIRATION_CHECK_EVENT), "0 0 0 * * ? *");
        schedulerService.safeScheduleJob(job);
    }
}
