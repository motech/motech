package org.motechproject.email.purging;

import org.motechproject.email.service.impl.PurgeEmailEventHandlerImpl;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.contract.CronSchedulableJob;
import org.motechproject.scheduler.service.MotechSchedulerService;

import java.util.HashMap;
import java.util.Map;

/**
 * Internal class, using the MotechScheduler for purging emails.
 * This is a separate class in order to make the scheduler dependency optional(optional OSGi import).
 * It will only be instantiated and used if the Scheduler modules is present.
 */
class EmailPurgerInternal {

    private final MotechSchedulerService schedulerService;

    EmailPurgerInternal(Object schedulerService) {
        if (!(schedulerService instanceof MotechSchedulerService)) {
            throw new IllegalArgumentException("Object passed is not a MotechSchedulerService");
        }
        this.schedulerService = (MotechSchedulerService) schedulerService;
    }

    public void schedulePurgingJob(String time, String multiplier) {
        Map<String, Object> params = new HashMap<>();
        params.put("purgeTime", time);
        params.put("purgeMultiplier", multiplier);
        schedulerService.safeScheduleJob(new CronSchedulableJob(
                new MotechEvent(PurgeEmailEventHandlerImpl.PURGE_EMAIL_SUBJECT, params), "0 0 1 * * ?"
        ));
    }

    public void unschedulePurgingJob() {
        schedulerService.unscheduleAllJobs(PurgeEmailEventHandlerImpl.PURGE_EMAIL_SUBJECT);
    }
}
