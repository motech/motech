package org.motechproject.scheduler.builder;

import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.contract.CronSchedulableJob;
import org.motechproject.scheduler.contract.RepeatingJobId;
import org.motechproject.scheduler.contract.RepeatingPeriodJobId;
import org.motechproject.scheduler.contract.RepeatingPeriodSchedulableJob;
import org.motechproject.scheduler.contract.RepeatingSchedulableJob;
import org.motechproject.scheduler.contract.RunOnceJobId;
import org.motechproject.scheduler.contract.RunOnceSchedulableJob;
import org.motechproject.scheduler.contract.SchedulableJob;
import org.motechproject.scheduler.exception.MotechSchedulerException;
import org.motechproject.scheduler.trigger.PeriodIntervalTrigger;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

import static org.motechproject.scheduler.constants.SchedulerConstants.CRON;
import static org.motechproject.scheduler.constants.SchedulerConstants.EVENT_TYPE_KEY_NAME;
import static org.motechproject.scheduler.constants.SchedulerConstants.REPEATING;
import static org.motechproject.scheduler.constants.SchedulerConstants.REPEATING_PERIOD;
import static org.motechproject.scheduler.constants.SchedulerConstants.RUN_ONCE;
import static org.motechproject.scheduler.constants.SchedulerConstants.UI_DEFINED;

public final class SchedulableJobBuilder {

    private static final int SECOND = 1000;

    public static SchedulableJob buildJob(JobKey key, JobDataMap dataMap, Trigger trigger) throws SchedulerException {

        SchedulableJob job;

        switch (getJobType(key)) {
            case CRON:
                job = buildCronSchedulableJob(trigger);
                break;
            case REPEATING:
                job = buildRepeatingSchedulableJob(trigger);
                break;
            case REPEATING_PERIOD:
                job = buildRepeatingPeriodSchedulableJob(trigger);
                break;
            case RUN_ONCE:
                job = buildRunOnceSchedulableJob();
                break;
            default:
                throw new MotechSchedulerException(String.format("Unknown job type: \n %s\n %s", key.getName(),
                        key.getGroup()));
        }

        job.setMotechEvent(new MotechEvent(dataMap.getString(EVENT_TYPE_KEY_NAME), dataMap.getWrappedMap()));
        job.setUiDefined(dataMap.getBoolean(UI_DEFINED));
        job.setStartDate(new DateTime(trigger.getStartTime()));

        return job;
    }

    private static SchedulableJob buildCronSchedulableJob(Trigger trigger) {
        CronTrigger cronTrigger = (CronTrigger) trigger;
        CronSchedulableJob job = new CronSchedulableJob();
        job.setEndDate(getEndDate(cronTrigger));
        job.setCronExpression(cronTrigger.getCronExpression());
        return job;
    }

    private static SchedulableJob buildRepeatingSchedulableJob(Trigger trigger) {
        SimpleTrigger simpleTrigger = (SimpleTrigger) trigger;
        RepeatingSchedulableJob job = new RepeatingSchedulableJob();
        job.setEndDate(getEndDate(simpleTrigger));
        job.setRepeatCount(simpleTrigger.getRepeatCount());
        job.setRepeatIntervalInSeconds((int) simpleTrigger.getRepeatInterval() / SECOND);
        return job;
    }

    private static SchedulableJob buildRepeatingPeriodSchedulableJob(Trigger trigger) {
        PeriodIntervalTrigger periodTrigger = (PeriodIntervalTrigger) trigger;
        RepeatingPeriodSchedulableJob job = new RepeatingPeriodSchedulableJob();
        job.setEndDate(getEndDate(periodTrigger));
        job.setRepeatPeriod(periodTrigger.getRepeatPeriod());
        return job;
    }

    private static SchedulableJob buildRunOnceSchedulableJob() {
        return new RunOnceSchedulableJob();
    }

    private static DateTime getEndDate(Trigger trigger) {
        return new DateTime(trigger.getEndTime());
    }

    private static String getJobType(JobKey jobKey) throws SchedulerException {
        if (jobKey.getName().endsWith(RunOnceJobId.SUFFIX_RUNONCEJOBID)) {
            return RUN_ONCE;
        } else if (jobKey.getName().endsWith(RepeatingJobId.SUFFIX_REPEATJOBID)) {
            return REPEATING;
        } else if (jobKey.getName().endsWith(RepeatingPeriodJobId.SUFFIX_REPEATPERIODJOBID)) {
            return REPEATING_PERIOD;
        } else {
            return CRON;
        }
    }

    /**
     * Utility class should not be initiated.
     */
    private SchedulableJobBuilder() {
    }

}
