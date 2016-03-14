package org.motechproject.scheduler.builder;

import org.joda.time.DateTime;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.contract.CronSchedulableJob;
import org.motechproject.scheduler.contract.DayOfWeekSchedulableJob;
import org.motechproject.scheduler.contract.RepeatingJobId;
import org.motechproject.scheduler.contract.RepeatingPeriodJobId;
import org.motechproject.scheduler.contract.RepeatingPeriodSchedulableJob;
import org.motechproject.scheduler.contract.RepeatingSchedulableJob;
import org.motechproject.scheduler.contract.RunOnceJobId;
import org.motechproject.scheduler.contract.RunOnceSchedulableJob;
import org.motechproject.scheduler.contract.SchedulableJob;
import org.motechproject.scheduler.exception.MotechSchedulerException;
import org.motechproject.scheduler.trigger.PeriodIntervalTrigger;
import org.motechproject.scheduler.util.CronExpressionUtil;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

import static org.motechproject.scheduler.constants.SchedulerConstants.CRON;
import static org.motechproject.scheduler.constants.SchedulerConstants.DAY_OF_WEEK;
import static org.motechproject.scheduler.constants.SchedulerConstants.EVENT_TYPE_KEY_NAME;
import static org.motechproject.scheduler.constants.SchedulerConstants.IGNORE_PAST_FIRES_AT_START;
import static org.motechproject.scheduler.constants.SchedulerConstants.IS_DAY_OF_WEEK;
import static org.motechproject.scheduler.constants.SchedulerConstants.REPEATING;
import static org.motechproject.scheduler.constants.SchedulerConstants.REPEATING_PERIOD;
import static org.motechproject.scheduler.constants.SchedulerConstants.RUN_ONCE;
import static org.motechproject.scheduler.constants.SchedulerConstants.UI_DEFINED;
import static org.motechproject.scheduler.constants.SchedulerConstants.USE_ORIGINAL_FIRE_TIME_AFTER_MISFIRE;

/**
 * Responsible for building jobs based on the given information;
 */
public final class SchedulableJobBuilder {

    private static final int SECOND = 1000;

    /**
     * Builds a job based on the given key, data map and trigger.
     *
     * @param key  the job key
     * @param dataMap  the job data map
     * @param trigger  the job trigger
     * @return  the created job
     * @throws SchedulerException when there were problems while building job
     */
    public static SchedulableJob buildJob(JobKey key, JobDataMap dataMap, Trigger trigger) throws SchedulerException {

        SchedulableJob job;

        switch (getJobType(key, dataMap)) {
            case CRON:
                job = buildCronSchedulableJob(trigger, dataMap);
                break;
            case REPEATING:
                job = buildRepeatingSchedulableJob(trigger, dataMap);
                break;
            case REPEATING_PERIOD:
                job = buildRepeatingPeriodSchedulableJob(trigger, dataMap);
                break;
            case DAY_OF_WEEK:
                job = buildDayOfWeekSchedulableJob(trigger, dataMap);
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

    private static SchedulableJob buildCronSchedulableJob(Trigger trigger, JobDataMap dataMap) {
        CronTrigger cronTrigger = (CronTrigger) trigger;
        CronSchedulableJob job = new CronSchedulableJob();
        job.setEndDate(getEndDate(cronTrigger));
        job.setCronExpression(cronTrigger.getCronExpression());
        job.setIgnorePastFiresAtStart(dataMap.getBoolean(IGNORE_PAST_FIRES_AT_START));
        return job;
    }

    private static SchedulableJob buildRepeatingSchedulableJob(Trigger trigger, JobDataMap dataMap) {
        SimpleTrigger simpleTrigger = (SimpleTrigger) trigger;
        RepeatingSchedulableJob job = new RepeatingSchedulableJob();
        job.setEndDate(getEndDate(simpleTrigger));
        job.setRepeatCount(simpleTrigger.getRepeatCount());
        job.setRepeatIntervalInSeconds((int) simpleTrigger.getRepeatInterval() / SECOND);
        job.setIgnorePastFiresAtStart(dataMap.getBoolean(IGNORE_PAST_FIRES_AT_START));
        job.setUseOriginalFireTimeAfterMisfire(dataMap.getBoolean(USE_ORIGINAL_FIRE_TIME_AFTER_MISFIRE));
        return job;
    }

    private static SchedulableJob buildRepeatingPeriodSchedulableJob(Trigger trigger, JobDataMap dataMap) {
        PeriodIntervalTrigger periodTrigger = (PeriodIntervalTrigger) trigger;
        RepeatingPeriodSchedulableJob job = new RepeatingPeriodSchedulableJob();
        job.setEndDate(getEndDate(periodTrigger));
        job.setRepeatPeriod(periodTrigger.getRepeatPeriod());
        job.setIgnorePastFiresAtStart(dataMap.getBoolean(IGNORE_PAST_FIRES_AT_START));
        job.setUseOriginalFireTimeAfterMisfire(dataMap.getBoolean(USE_ORIGINAL_FIRE_TIME_AFTER_MISFIRE));
        return job;
    }

    private static SchedulableJob buildDayOfWeekSchedulableJob(Trigger trigger, JobDataMap dataMap) {
        CronTrigger cronTrigger = (CronTrigger) trigger;
        DayOfWeekSchedulableJob job = new DayOfWeekSchedulableJob();
        job.setIgnorePastFiresAtStart(dataMap.getBoolean(IGNORE_PAST_FIRES_AT_START));

        CronExpressionUtil cronExpressionUtil = new CronExpressionUtil(cronTrigger.getCronExpression());
        job.setTime(cronExpressionUtil.getTime());
        job.setDays(cronExpressionUtil.getDaysOfWeek());

        return job;
    }

    private static SchedulableJob buildRunOnceSchedulableJob() {
        return new RunOnceSchedulableJob();
    }

    private static DateTime getEndDate(Trigger trigger) {
        return trigger.getEndTime() == null ? null : new DateTime(trigger.getEndTime());
    }

    private static String getJobType(JobKey jobKey, JobDataMap dataMap) throws SchedulerException {
        if (jobKey.getName().endsWith(RunOnceJobId.SUFFIX_RUNONCEJOBID)) {
            return RUN_ONCE;
        } else if (jobKey.getName().endsWith(RepeatingJobId.SUFFIX_REPEATJOBID)) {
            return REPEATING;
        } else if (jobKey.getName().endsWith(RepeatingPeriodJobId.SUFFIX_REPEATPERIODJOBID)) {
            return REPEATING_PERIOD;
        } else if (dataMap.getBoolean(IS_DAY_OF_WEEK)) {
            return DAY_OF_WEEK;
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
