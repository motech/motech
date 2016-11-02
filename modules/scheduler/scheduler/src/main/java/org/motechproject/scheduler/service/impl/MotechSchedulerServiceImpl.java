package org.motechproject.scheduler.service.impl;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.commons.date.model.Time;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.config.SettingsFacade;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.builder.SchedulableJobBuilder;
import org.motechproject.scheduler.contract.CronJobId;
import org.motechproject.scheduler.contract.CronSchedulableJob;
import org.motechproject.scheduler.contract.DayOfWeekSchedulableJob;
import org.motechproject.scheduler.contract.JobBasicInfo;
import org.motechproject.scheduler.contract.JobId;
import org.motechproject.scheduler.contract.MisfireSchedulableJob;
import org.motechproject.scheduler.contract.RepeatingJobId;
import org.motechproject.scheduler.contract.RepeatingPeriodJobId;
import org.motechproject.scheduler.contract.RepeatingPeriodSchedulableJob;
import org.motechproject.scheduler.contract.RepeatingSchedulableJob;
import org.motechproject.scheduler.contract.RunOnceJobId;
import org.motechproject.scheduler.contract.RunOnceSchedulableJob;
import org.motechproject.scheduler.contract.SchedulableJob;
import org.motechproject.scheduler.exception.MotechSchedulerException;
import org.motechproject.scheduler.factory.MotechSchedulerFactoryBean;
import org.motechproject.scheduler.service.MotechScheduledJob;
import org.motechproject.scheduler.service.MotechSchedulerService;
import org.motechproject.scheduler.trigger.PeriodIntervalScheduleBuilder;
import org.quartz.CalendarIntervalScheduleBuilder;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.TriggerUtils;
import org.quartz.impl.calendar.BaseCalendar;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.spi.OperableTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;
import static org.motechproject.commons.date.util.DateUtil.newDateTime;
import static org.motechproject.commons.date.util.DateUtil.now;
import static org.motechproject.scheduler.constants.SchedulerConstants.EVENT_METADATA;
import static org.motechproject.scheduler.constants.SchedulerConstants.EVENT_TYPE_KEY_NAME;
import static org.motechproject.scheduler.constants.SchedulerConstants.IGNORE_PAST_FIRES_AT_START;
import static org.motechproject.scheduler.constants.SchedulerConstants.IS_DAY_OF_WEEK;
import static org.motechproject.scheduler.constants.SchedulerConstants.UI_DEFINED;
import static org.motechproject.scheduler.constants.SchedulerConstants.USE_ORIGINAL_FIRE_TIME_AFTER_MISFIRE;
import static org.motechproject.scheduler.validation.SchedulableJobValidator.validateCronSchedulableJob;
import static org.motechproject.scheduler.validation.SchedulableJobValidator.validateDayOfWeekSchedulableJob;
import static org.motechproject.scheduler.validation.SchedulableJobValidator.validateRepeatingPeriodSchedulableJob;
import static org.motechproject.scheduler.validation.SchedulableJobValidator.validateRepeatingSchedulableJob;
import static org.motechproject.scheduler.validation.SchedulableJobValidator.validateRunOnceSchedulableJob;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.TriggerKey.triggerKey;

/**
 * Motech Scheduler Service implementation
 *
 * @see MotechSchedulerService
 */
@Service("schedulerService")
public class MotechSchedulerServiceImpl implements MotechSchedulerService {

    public static final String JOB_GROUP_NAME = "default";
    private static final int MAX_REPEAT_COUNT = 999999;
    private static final int MILLISECOND = 1000;
    private static final String LOG_SUBJECT_EXTERNAL_ID = "subject: %s, externalId: %s";

    private SettingsFacade schedulerSettings;

    private Scheduler scheduler;

    private Map<String, Integer> cronTriggerMisfirePolicies;
    private Map<String, Integer> simpleTriggerMisfirePolicies;

    private static final Logger LOGGER = LoggerFactory.getLogger(MotechSchedulerServiceImpl.class);

    @Autowired
    public MotechSchedulerServiceImpl(MotechSchedulerFactoryBean motechSchedulerFactoryBean, SettingsFacade schedulerSettings) {
        this.schedulerSettings = schedulerSettings;
        this.scheduler = motechSchedulerFactoryBean.getQuartzScheduler();
        constructMisfirePoliciesMaps();
    }

    @Override
    public void scheduleJob(CronSchedulableJob cronSchedulableJob) {
        scheduleCronJob(cronSchedulableJob, false, false);
    }

    @Override
    public void scheduleRepeatingJob(RepeatingSchedulableJob repeatingSchedulableJob) {
        scheduleRepeatingJob(repeatingSchedulableJob, false);
    }

    @Override
    public void scheduleRepeatingPeriodJob(RepeatingPeriodSchedulableJob repeatingPeriodSchedulableJob) {
        scheduleRepeatingPeriodJob(repeatingPeriodSchedulableJob, false);
    }

    @Override
    public void scheduleRunOnceJob(RunOnceSchedulableJob schedulableJob) {
        scheduleRunOnceJob(schedulableJob, false);
    }

    @Override
    public void scheduleDayOfWeekJob(DayOfWeekSchedulableJob dayOfWeekSchedulableJob) {
        scheduleDayOfWeekJob(dayOfWeekSchedulableJob, false);
    }

    @Override
    public void scheduleJob(SchedulableJob job) {
        scheduleJob(job, false);
    }

    @Override
    public void safeScheduleJob(CronSchedulableJob cronSchedulableJob) {
        logObjectIfNotNull(cronSchedulableJob);

        assertCronJob(cronSchedulableJob);

        JobId jobId = new CronJobId(cronSchedulableJob.getMotechEvent());
        try {
            unscheduleJob(jobId.value());
        } catch (MotechSchedulerException e) {
            LOGGER.error("Error while unscheduling job with id {}", jobId.value(), e);
        }
        scheduleJob(cronSchedulableJob);
    }

    @Override
    public void safeScheduleRepeatingPeriodJob(RepeatingPeriodSchedulableJob repeatingPeriodSchedulableJob) {
        logObjectIfNotNull(repeatingPeriodSchedulableJob);

        assertArgumentNotNull(repeatingPeriodSchedulableJob);

        JobId jobId = new RepeatingJobId(repeatingPeriodSchedulableJob.getMotechEvent());

        try {
            unscheduleJob(jobId);
        } catch (MotechSchedulerException e) {
            LOGGER.error("Unable to unschedule repeating job with id {}", jobId.value(), e);
        }
        scheduleRepeatingPeriodJob(repeatingPeriodSchedulableJob);
    }

    @Override
    public void safeScheduleRepeatingJob(RepeatingSchedulableJob repeatingSchedulableJob) {
        logObjectIfNotNull(repeatingSchedulableJob);
        assertArgumentNotNull(repeatingSchedulableJob);

        JobId jobId = new RepeatingJobId(repeatingSchedulableJob.getMotechEvent());

        try {
            unscheduleJob(jobId);
        } catch (MotechSchedulerException e) {
            LOGGER.error("Unable to unschedule repeating job with ID {}", jobId.value(), e);
        }
        scheduleRepeatingJob(repeatingSchedulableJob);
    }

    @Override
    public void safeScheduleRunOnceJob(RunOnceSchedulableJob schedulableJob) {
        logObjectIfNotNull(schedulableJob);
        assertArgumentNotNull("RunOnceSchedulableJob", schedulableJob);

        JobId jobId = new RunOnceJobId(schedulableJob.getMotechEvent());
        try {
            unscheduleJob(jobId);
        } catch (MotechSchedulerException e) {
            LOGGER.error("Unable to unschedule run once job with ID {}", jobId.value(), e);
        }
        scheduleRunOnceJob(schedulableJob);
    }

    @Override
    public void updateJob(SchedulableJob job) {
        scheduleJob(job, true);
    }

    @Override
    public void rescheduleJob(String subject, String externalId, String cronExpression) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(subject + " " + externalId + " " + cronExpression);
        }
        assertArgumentNotNull("Subject", subject);
        assertArgumentNotNull("ExternalId", externalId);
        assertArgumentNotNull("Cron expression", cronExpression);

        JobId jobId = new CronJobId(subject, externalId);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(format("Rescheduling the Job: %s new cron expression: %s", jobId, cronExpression));
        }

        CronTrigger trigger;
        JobDetail job;
        try {
            trigger = (CronTrigger) scheduler.getTrigger(triggerKey(jobId.value(), JOB_GROUP_NAME));
            if (trigger == null) {
                throw new MotechSchedulerException(format("Can not reschedule the job: %s The job does not exist (not scheduled)", jobId));
            }
            job = scheduler.getJobDetail(trigger.getJobKey());
        } catch (SchedulerException e) {
            throw new MotechSchedulerException(String.format("Can not reschedule the job: %s.\n Can not get a trigger associated with that job %s", jobId, e.getMessage()), e);
        } catch (ClassCastException e) {
            throw new MotechSchedulerException(String.format("Can not reschedule the job: %s.\n The trigger associated with that job is not a CronTrigger", jobId), e);
        }

        CronScheduleBuilder newCronSchedule;
        try {
            newCronSchedule = cronSchedule(cronExpression);
        } catch (RuntimeException e) {
            throw new MotechSchedulerException(String.format("Can not reschedule the job: %s Invalid Cron expression: %s", jobId, cronExpression), e);
        }

        CronTrigger newTrigger = newTrigger()
                .withIdentity(trigger.getKey())
                .forJob(job)
                .withSchedule(newCronSchedule)
                .startAt(trigger.getStartTime())
                .endAt(trigger.getEndTime())
                .build();

        try {
            scheduler.rescheduleJob(triggerKey(jobId.value(), JOB_GROUP_NAME), newTrigger);
        } catch (SchedulerException e) {
            throw new MotechSchedulerException(String.format("Can not reschedule the job: %s %s", jobId, e.getMessage()), e);
        }
    }

    @Override
    public void unscheduleJob(String subject, String externalId) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(format("unscheduling cron job: " + LOG_SUBJECT_EXTERNAL_ID, subject, externalId));
        }
        unscheduleJob(new CronJobId(subject, externalId));
    }

    @Override
    public void unscheduleJob(JobId job) {
        logObjectIfNotNull(job);
        unscheduleJob(job.value());
    }

    @Override
    public void unscheduleRepeatingJob(String subject, String externalId) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(format("unscheduling repeating job: " + LOG_SUBJECT_EXTERNAL_ID, subject, externalId));
        }

        JobId jobId = new RepeatingJobId(subject, externalId);
        logObjectIfNotNull(jobId);

        unscheduleJob(jobId.value());
    }

    @Override
    public void unscheduleRunOnceJob(String subject, String externalId) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(format("unscheduling run once job: " + LOG_SUBJECT_EXTERNAL_ID, subject, externalId));
        }

        JobId jobId = new RunOnceJobId(subject, externalId);
        logObjectIfNotNull(jobId);

        unscheduleJob(jobId.value());
    }

    @Override
    public void unscheduleAllJobs(String jobIdPrefix) {
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Unscheduling jobs with prefix: ", jobIdPrefix);
            }
            List<TriggerKey> triggerKeys = new ArrayList<>(scheduler.getTriggerKeys(GroupMatcher.triggerGroupContains(JOB_GROUP_NAME)));
            List<String> triggerNames = extractTriggerNames(triggerKeys);
            for (String triggerName : triggerNames) {
                if (StringUtils.isNotEmpty(jobIdPrefix) && triggerName.contains(jobIdPrefix)) {
                    unscheduleJob(triggerName);
                }
            }
        } catch (SchedulerException e) {
            throw new MotechSchedulerException(String.format("Can not unschedule jobs given jobIdPrefix: %s %s",
                    jobIdPrefix, e.getMessage()), e);
        }
    }

    @Override
    public void safeUnscheduleRepeatingJob(String subject, String externalId) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(format("safe unscheduling repeating job: " + LOG_SUBJECT_EXTERNAL_ID, subject, externalId));
        }
        try {
            unscheduleRepeatingJob(subject, externalId);
        } catch (RuntimeException e) {
            LOGGER.error("Unable to unschedule the repeating job with subject {} and externalId {}",
                    subject, externalId, e);
        }
    }

    @Override
    public void safeUnscheduleRunOnceJob(String subject, String externalId) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(format("safe unscheduling run once job: " + LOG_SUBJECT_EXTERNAL_ID, subject, externalId));
        }
        try {
            unscheduleRunOnceJob(subject, externalId);
        } catch (RuntimeException e) {
            LOGGER.error("Unable to unschedule the run once job with subject {} and externalId {}",
                    subject, externalId, e);
        }
    }

    @Override
    public void safeUnscheduleJob(String subject, String externalId) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(format("safe unscheduling cron job: " + LOG_SUBJECT_EXTERNAL_ID, subject, externalId));
        }
        try {
            unscheduleJob(subject, externalId);
        } catch (RuntimeException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void safeUnscheduleAllJobs(String jobIdPrefix) {
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(format("Safe unscheduling the Jobs given jobIdPrefix: %s", jobIdPrefix));
            }
            List<TriggerKey> triggerKeys = new ArrayList<>(scheduler.getTriggerKeys(GroupMatcher.triggerGroupContains(JOB_GROUP_NAME)));
            List<String> triggerNames = extractTriggerNames(triggerKeys);
            for (String triggerName : triggerNames) {
                if (StringUtils.isNotEmpty(jobIdPrefix) && triggerName.contains(jobIdPrefix)) {
                    safeUnscheduleJob(triggerName);
                }
            }
        } catch (SchedulerException e) {
            LOGGER.error("Unable to unschedule all jobs with jobIdPrefix {}", jobIdPrefix, e);
        }
    }

    @Override
    public JobBasicInfo pauseJob(JobBasicInfo info) {
        try {
            JobKey key = new JobKey(info.getName(), info.getGroup());
            validateJob(key);
            scheduler.pauseJob(key);
            info.setStatus(JobBasicInfo.STATUS_PAUSED);
            return info;
        } catch (MotechSchedulerException | SchedulerException e) {
            throw new MotechSchedulerException(String.format("Can not pause the job:\n %s\n%s\n%s",
                    info.getName(), info.getGroup(), e.getMessage()), e);
        }
    }

    @Override
    public JobBasicInfo resumeJob(JobBasicInfo info) {
        try {
            JobKey key = new JobKey(info.getName(), info.getGroup());
            validateJob(key);
            scheduler.resumeJob(key);
            info.setStatus(JobBasicInfo.STATUS_OK);
            return info;
        } catch (MotechSchedulerException | SchedulerException e) {
            throw new MotechSchedulerException(String.format("Can not resume the job:\n %s\n%s\n%s",
                    info.getName(), info.getGroup(), e.getMessage()), e);
        }
    }

    @Override
    public void deleteJob(JobBasicInfo info) {
        try {
            JobKey key = new JobKey(info.getName(), info.getGroup());
            validateJob(key);
            scheduler.deleteJob(key);
        } catch (MotechSchedulerException | SchedulerException e) {
            throw new MotechSchedulerException(String.format("Can not delete the job:\n %s\n%s\n%s",
                    info.getName(), info.getGroup(), e.getMessage()), e);
        }
    }

    @Override
    public SchedulableJob getJob(JobBasicInfo info) {
        try {
            JobKey key = jobKey(info.getName(), info.getGroup());
            return SchedulableJobBuilder.buildJob(key, scheduler.getJobDetail(key).getJobDataMap(),
                    scheduler.getTriggersOfJob(key).get(0));
        } catch (SchedulerException e) {
            throw new MotechSchedulerException(String.format("Can not retrieve the job:\n %s\n %s\n %s", info.getName(),
                    info.getGroup(), e.getMessage()), e);
        }
    }

    @Override
    public DateTime getPreviousFireDate(JobId jobId) {
        Date previousFireTime = null;
        try {
            Trigger trigger = scheduler.getTrigger(triggerKey(jobId.value(), JOB_GROUP_NAME));
            if (trigger != null) {
                previousFireTime = trigger.getPreviousFireTime();
            }
        } catch (SchedulerException e) {
            throw new MotechSchedulerException(String.format("Can not find next fire date for the job: %s %s", jobId,
                    e.getMessage()), e);
        }
        return (previousFireTime == null) ? null : new DateTime(previousFireTime);
    }

    @Override
    public DateTime getNextFireDate(JobId jobId) {
        Date nextFireTime = null;
        try {
            Trigger trigger = scheduler.getTrigger(triggerKey(jobId.value(), JOB_GROUP_NAME));
            if (trigger != null) {
                nextFireTime = trigger.getNextFireTime();
            }
        } catch (SchedulerException e) {
            throw new MotechSchedulerException(String.format("Can not find next fire date for the job: %s %s",
                    jobId, e.getMessage()), e);
        }
        return (nextFireTime == null) ? null : new DateTime(nextFireTime);
    }

    /*
     * Assumes that the externalJobId is non-repeating in nature. Thus the fetch is for jobId.value() and not
     * jobId.repeatingId()
     * Uses quartz API to fetch the exact triggers. Fast
     */
    @Override
    public List<DateTime> getScheduledJobTimings(String subject, String externalJobId, DateTime startDate, DateTime endDate) {
        JobId jobId = new CronJobId(subject, externalJobId);
        Trigger trigger;
        try {
            trigger = scheduler.getTrigger(triggerKey(jobId.value(), JOB_GROUP_NAME));
            return DateUtil.datesToDateTimes(TriggerUtils.computeFireTimesBetween(
                    (OperableTrigger) trigger, new BaseCalendar(), DateUtil.toDate(startDate), DateUtil.toDate(endDate)));

        } catch (SchedulerException e) {
            throw new MotechSchedulerException(String.format(
                    "Can not get scheduled job timings given subject and externalJobId for dates : %s %s %s %s %s",
                    subject, externalJobId, startDate.toString(), endDate.toString(), e.getMessage()), e);
        }
    }

    /*
     * Loads all triggers and then loops over them to find the applicable trigger using string comparison. This
     * will work regardless of the jobId being cron or repeating.
     */
    @Override
    public List<DateTime> getScheduledJobTimingsWithPrefix(
            String subject, String externalJobIdPrefix, DateTime startDate, DateTime endDate) {

        JobId jobId = new CronJobId(subject, externalJobIdPrefix);
        List<Date> messageTimings = new ArrayList<>();
        try {
            List<TriggerKey> triggerKeys = new ArrayList<TriggerKey>(
                    scheduler.getTriggerKeys(GroupMatcher.triggerGroupContains(JOB_GROUP_NAME)));
            for (TriggerKey triggerKey : triggerKeys) {
                if (StringUtils.isNotEmpty(externalJobIdPrefix) && triggerKey.getName().contains(jobId.value())) {
                    Trigger trigger = scheduler.getTrigger(triggerKey);
                    messageTimings.addAll(TriggerUtils.computeFireTimesBetween(
                            (OperableTrigger) trigger, new BaseCalendar(), DateUtil.toDate(startDate), DateUtil.toDate(endDate)));
                }
            }

        } catch (SchedulerException e) {
            throw new MotechSchedulerException(String.format(
                    "Can not get scheduled job timings given subject and externalJobIdPrefix for dates : %s %s %s %s %s",
                    subject, externalJobIdPrefix, startDate.toString(), endDate.toString(), e.getMessage()), e);
        }

        return DateUtil.datesToDateTimes(messageTimings);
    }

    /**
     * Asserts that given object is not null.
     *
     * @param objectName  the objects name
     * @param object  the object to be checked for being null
     * @throws IllegalArgumentException if object is null
     */
    protected void assertArgumentNotNull(String objectName, Object object) {
        if (object == null) {
            throw new IllegalArgumentException(String.format("%s cannot be null", objectName));
        }
    }

    /**
     * Logs object if it is not null.
     *
     * @param obj  the object to be checked for being null
     */
    protected void logObjectIfNotNull(Object obj) {
        if (LOGGER.isDebugEnabled() && obj != null) {
            LOGGER.debug(obj.toString());
        }
    }

    private void scheduleCronJob(CronSchedulableJob job, boolean isDayOfWeek, boolean update) {
        logObjectIfNotNull(job);

        validateCronSchedulableJob(job);

        MotechEvent motechEvent = job.getMotechEvent();

        JobId jobId = new CronJobId(motechEvent);

        JobDetail jobDetail = newJob(MotechScheduledJob.class)
                .withIdentity(jobKey(jobId.value(), JOB_GROUP_NAME))
                .build();

        putMotechEventDataToJobDataMap(jobDetail.getJobDataMap(), motechEvent);
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(IS_DAY_OF_WEEK, isDayOfWeek);
        metadata.put(UI_DEFINED, job.isUiDefined());
        metadata.put(IGNORE_PAST_FIRES_AT_START, job.isIgnorePastFiresAtStart());
        metadata.putAll(motechEvent.getMetadata());
        jobDetail.getJobDataMap().put(EVENT_METADATA, metadata);

        CronScheduleBuilder cronSchedule = cronSchedule(job.getCronExpression());

        // TODO: should take readable names rather than integers
        cronSchedule = setMisfirePolicyForCronTrigger(cronSchedule,  schedulerSettings.getProperty("scheduler.cron.trigger.misfire.policy"));

        CronTrigger trigger = newTrigger()
                .withIdentity(triggerKey(jobId.value(), JOB_GROUP_NAME))
                .forJob(jobDetail)
                .withSchedule(cronSchedule)
                .startAt(job.getStartDate() != null ? job.getStartDate().toDate() : now().toDate())
                .endAt(DateUtil.toDate(job.getEndDate()))
                .build();

        Trigger existingTrigger;
        try {
            existingTrigger = scheduler.getTrigger(triggerKey(jobId.value(), JOB_GROUP_NAME));
        } catch (SchedulerException e) {
            throw new MotechSchedulerException(format("Schedule or reschedule the job: %s.\n%s", jobId, e.getMessage()),
                    "scheduler.error.cantRescheduleJob", Arrays.asList(jobId.value(), e.getMessage()), e);
        }
        if (existingTrigger != null) {
            unscheduleJob(jobId.value());
        }

        DateTime now = now();

        if (job.isIgnorePastFiresAtStart() && (job.getStartDate() == null || job.getStartDate().isBefore(now))) {

            Date newStartTime = trigger.getFireTimeAfter(now.toDate());
            if (newStartTime == null) {
                newStartTime = now.toDate();
            }

            trigger = newTrigger()
                    .withIdentity(triggerKey(jobId.value(), JOB_GROUP_NAME))
                    .forJob(jobDetail)
                    .withSchedule(cronSchedule)
                    .startAt(newStartTime)
                    .endAt(DateUtil.toDate(job.getEndDate()))
                    .build();
        }

        scheduleJob(jobDetail, trigger, update);
    }

    private void scheduleRepeatingJob(RepeatingSchedulableJob job, boolean update) {
        logObjectIfNotNull(job);

        validateRepeatingSchedulableJob(job);

        MotechEvent motechEvent = job.getMotechEvent();

        DateTime jobStartTime = job.getStartDate();
        DateTime jobEndTime = job.getEndDate();

        Integer repeatIntervalInSeconds = job.getRepeatIntervalInSeconds();

        Integer jobRepeatCount = job.getRepeatCount();
        if (null == jobRepeatCount) {
            jobRepeatCount = MAX_REPEAT_COUNT;
        }

        JobId jobId = new RepeatingJobId(motechEvent);
        JobDetail jobDetail = newJob(MotechScheduledJob.class)
                .withIdentity(jobKey(jobId.value(), JOB_GROUP_NAME))
                .build();

        putMotechEventDataToJobDataMap(jobDetail.getJobDataMap(), motechEvent);

        jobDetail.getJobDataMap().put(EVENT_METADATA, createMetadataForMisfireSchedulableJob(job, motechEvent));

        try {
            if (scheduler.getTrigger(triggerKey(jobId.value(), JOB_GROUP_NAME)) != null) {
                unscheduleJob(jobId);
            }
        } catch (SchedulerException e) {
            throw new MotechSchedulerException(format("Schedule or reschedule the job: %s.\n%s", jobId, e.getMessage()),
                    "scheduler.error.cantRescheduleJob", Arrays.asList(jobId.value(), e.getMessage()), e);
        }

        ScheduleBuilder scheduleBuilder;
        if (!job.isUseOriginalFireTimeAfterMisfire()) {
            SimpleScheduleBuilder simpleSchedule = simpleSchedule()
                    .withIntervalInSeconds(repeatIntervalInSeconds)
                    .withRepeatCount(jobRepeatCount);

            simpleSchedule = setMisfirePolicyForSimpleTrigger(simpleSchedule, schedulerSettings.getProperty("scheduler.repeating.trigger.misfire.policy"));

            scheduleBuilder = simpleSchedule;
        } else {
            if (job.getRepeatCount() != null) {
                final double half = 0.5;
                jobEndTime = new DateTime((long) (job.getStartDate().getMillis() + repeatIntervalInSeconds * MILLISECOND * (job.getRepeatCount() + half)));
            }
            scheduleBuilder = CalendarIntervalScheduleBuilder.calendarIntervalSchedule()
                    .withIntervalInSeconds(repeatIntervalInSeconds)
                    .withMisfireHandlingInstructionFireAndProceed();
        }

        Trigger trigger = buildJobDetail(job, DateUtil.toDate(jobStartTime),
                DateUtil.toDate(jobEndTime), jobId, jobDetail, scheduleBuilder);
        scheduleJob(jobDetail, trigger, update);
    }

    private Map<String, Object> createMetadataForMisfireSchedulableJob(MisfireSchedulableJob job, MotechEvent event) {

        Map<String, Object> metadata = new HashMap<>();
        metadata.put(UI_DEFINED, job.isUiDefined());
        metadata.put(IGNORE_PAST_FIRES_AT_START, job.isIgnorePastFiresAtStart());
        metadata.put(USE_ORIGINAL_FIRE_TIME_AFTER_MISFIRE, job.isUseOriginalFireTimeAfterMisfire());
        metadata.putAll(event.getMetadata());

        return metadata;
    }

    private void scheduleRepeatingPeriodJob(RepeatingPeriodSchedulableJob job, boolean update) {
        logObjectIfNotNull(job);

        validateRepeatingPeriodSchedulableJob(job);

        MotechEvent motechEvent = job.getMotechEvent();

        JobId jobId = new RepeatingPeriodJobId(motechEvent);
        JobDetail jobDetail = newJob(MotechScheduledJob.class)
                .withIdentity(jobKey(jobId.value(), JOB_GROUP_NAME))
                .build();

        putMotechEventDataToJobDataMap(jobDetail.getJobDataMap(), motechEvent);

        jobDetail.getJobDataMap().put(EVENT_METADATA, createMetadataForMisfireSchedulableJob(job, motechEvent));

        ScheduleBuilder scheduleBuilder = PeriodIntervalScheduleBuilder.periodIntervalSchedule()
                .withRepeatPeriod(job.getRepeatPeriod())
                .withMisfireHandlingInstructionFireAndProceed();

        Trigger trigger = buildJobDetail(job, DateUtil.toDate(job.getStartDate()),
                DateUtil.toDate(job.getEndDate()), jobId, jobDetail, scheduleBuilder);
        scheduleJob(jobDetail, trigger, update);
    }

    private void scheduleRunOnceJob(RunOnceSchedulableJob job, boolean update) {
        logObjectIfNotNull(job);

        validateRunOnceSchedulableJob(job);

        MotechEvent motechEvent = job.getMotechEvent();

        JobId jobId = new RunOnceJobId(motechEvent);
        JobDetail jobDetail = newJob(MotechScheduledJob.class)
                .withIdentity(jobId.value(), JOB_GROUP_NAME)
                .build();

        putMotechEventDataToJobDataMap(jobDetail.getJobDataMap(), motechEvent);
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(UI_DEFINED, job.isUiDefined());
        metadata.putAll(motechEvent.getMetadata());
        jobDetail.getJobDataMap().put(EVENT_METADATA, metadata);

        SimpleScheduleBuilder simpleSchedule = simpleSchedule()
                .withRepeatCount(0)
                .withIntervalInSeconds(0)
                .withMisfireHandlingInstructionFireNow();

        Trigger trigger = newTrigger()
                .withIdentity(triggerKey(jobId.value(), JOB_GROUP_NAME))
                .forJob(jobDetail)
                .withSchedule(simpleSchedule)
                .startAt(DateUtil.toDate(job.getStartDate()))
                .build();

        scheduleJob(jobDetail, trigger, update);
    }

    private void scheduleDayOfWeekJob(DayOfWeekSchedulableJob job, boolean update) {
        logObjectIfNotNull(job);

        validateDayOfWeekSchedulableJob(job);

        MotechEvent motechEvent = job.getMotechEvent();
        Time time = job.getTime();

        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.atHourAndMinuteOnGivenDaysOfWeek(time.getHour(),
                time.getMinute(), job.getCronDays()
                        .toArray(new Integer[job.getCronDays().size()]));

        CronTriggerImpl cronTrigger = (CronTriggerImpl) cronScheduleBuilder.build();
        CronSchedulableJob cronSchedulableJob = new CronSchedulableJob(motechEvent, cronTrigger.getCronExpression(),
                job.getStartDate(), job.getEndDate(), job.isIgnorePastFiresAtStart(), job.isUiDefined());

        scheduleCronJob(cronSchedulableJob, true, update);
    }

    private void scheduleJob(SchedulableJob job, boolean update) {
        if (job instanceof CronSchedulableJob) {
            scheduleCronJob((CronSchedulableJob) job, false, update);
        } else if (job instanceof DayOfWeekSchedulableJob) {
            scheduleDayOfWeekJob((DayOfWeekSchedulableJob) job, update);
        } else if (job instanceof RepeatingSchedulableJob) {
            scheduleRepeatingJob((RepeatingSchedulableJob) job, update);
        } else if (job instanceof RepeatingPeriodSchedulableJob) {
            scheduleRepeatingPeriodJob((RepeatingPeriodSchedulableJob) job, update);
        } else if (job instanceof RunOnceSchedulableJob) {
            scheduleRunOnceJob((RunOnceSchedulableJob) job, update);
        }
    }

    private void scheduleJob(JobDetail jobDetail, Trigger trigger) {
        scheduleJob(jobDetail, trigger, false);
    }

    private void scheduleJob(JobDetail jobDetail, Trigger trigger, boolean update) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Scheduling job:" + jobDetail);
        }
        try {
            Set<Trigger> triggerSet = new HashSet<>();
            triggerSet.add(trigger);
            scheduler.scheduleJob(jobDetail, triggerSet, update);
        } catch (SchedulerException e) {
            throw new MotechSchedulerException(String.format("Can not schedule the job:\n %s\n%s\n%s",
                    jobDetail.toString(), trigger.toString(), e.getMessage()),
                    "scheduler.error.schedulerError", Arrays.asList(e.getMessage()), e);
        }
    }

    private void unscheduleJob(String jobId) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(jobId);
        }
        try {
            assertArgumentNotNull("ScheduledJobID", jobId);
            scheduler.unscheduleJob(triggerKey(jobId, JOB_GROUP_NAME));
        } catch (SchedulerException e) {
            throw new MotechSchedulerException(String.format("Can not unschedule the job: %s %s",
                    jobId, e.getMessage()), e);
        }
    }

    private void safeUnscheduleJob(String jobId) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(jobId);
        }
        try {
            assertArgumentNotNull("ScheduledJobID", jobId);
            scheduler.unscheduleJob(triggerKey(jobId, JOB_GROUP_NAME));
        } catch (SchedulerException e) {
            LOGGER.error("Unable to unschedule job with ID {}", jobId, e);
        }
    }

    private void validateJob(JobKey key) throws SchedulerException {
        JobDetail detail = scheduler.getJobDetail(key);

        if (detail == null) {
            throw new MotechSchedulerException(String.format("Job doesn't exist:\n %s\n %s", key.getName(),
                    key.getGroup()));
        }

        JobDataMap map = detail.getJobDataMap();

        if (map != null && !isJobUIDefined(map)) {
            throw new MotechSchedulerException(String.format("Job is not ui defined:\n %s\n %s", key.getName(),
                    key.getGroup()));
        }
    }

    private boolean isJobUIDefined(JobDataMap jobDataMap) {
        return jobDataMap.get(EVENT_METADATA) != null &&  (Boolean) ((Map<String, Object>) jobDataMap.get(EVENT_METADATA)).get(UI_DEFINED);
    }

    private MotechEvent assertCronJob(CronSchedulableJob cronSchedulableJob) {
        assertArgumentNotNull("SchedulableJob", cronSchedulableJob);
        MotechEvent motechEvent = cronSchedulableJob.getMotechEvent();
        assertArgumentNotNull("MotechEvent of the SchedulableJob", motechEvent);
        return motechEvent;
    }

    private CronScheduleBuilder setMisfirePolicyForCronTrigger(CronScheduleBuilder cronSchedule, String motechMisfireProperty) {
        Integer misfirePolicyAsInt = cronTriggerMisfirePolicies.get(motechMisfireProperty);
        if (misfirePolicyAsInt == null || misfirePolicyAsInt.equals(CronTrigger.MISFIRE_INSTRUCTION_SMART_POLICY)) {
            return cronSchedule;
        }
        if (misfirePolicyAsInt.equals(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING)) {
            return cronSchedule.withMisfireHandlingInstructionDoNothing();
        }
        if (misfirePolicyAsInt.equals(CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW)) {
            return cronSchedule.withMisfireHandlingInstructionFireAndProceed();
        }
        if (misfirePolicyAsInt.equals(CronTrigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY)) {
            return cronSchedule.withMisfireHandlingInstructionIgnoreMisfires();
        }
        return cronSchedule;
    }

    private Trigger buildJobDetail(SchedulableJob schedulableJob, Date jobStartTime, Date jobEndTime,
                                   JobId jobId, JobDetail jobDetail, ScheduleBuilder scheduleBuilder) {
        Trigger trigger = newTrigger()
                .withIdentity(triggerKey(jobId.value(), JOB_GROUP_NAME))
                .forJob(jobDetail)
                .withSchedule(scheduleBuilder)
                .startAt(jobStartTime)
                .endAt(jobEndTime)
                .build();
        DateTime now = now();

        if (schedulableJob.isIgnorePastFiresAtStart() && newDateTime(jobStartTime).isBefore(now)) {

            List<Date> pastTriggers = TriggerUtils.computeFireTimesBetween((OperableTrigger) trigger, null, jobStartTime, now.toDate());

            if (pastTriggers.size() > 0) {
                if (scheduleBuilder instanceof SimpleScheduleBuilder && ((RepeatingSchedulableJob) schedulableJob).getRepeatCount() != null) {
                    ((SimpleScheduleBuilder) scheduleBuilder)
                            .withRepeatCount(((RepeatingSchedulableJob) schedulableJob).getRepeatCount() - pastTriggers.size());
                }
                Date newStartTime = getFirstTriggerInFuture(trigger, now);
                trigger = newTrigger()
                        .withIdentity(triggerKey(jobId.value(), JOB_GROUP_NAME))
                        .forJob(jobDetail)
                        .withSchedule(scheduleBuilder)
                        .startAt(newStartTime)
                        .endAt(jobEndTime)
                        .build();
            }
        }
        return trigger;
    }

    private Date getFirstTriggerInFuture(Trigger trigger, DateTime now) {   // extracted away because of checkstyle :(
        Date newStartTime = trigger.getFireTimeAfter(now.toDate());
        if (newStartTime == null) {
            newStartTime = now.toDate();
        }
        return newStartTime;
    }

    private SimpleScheduleBuilder setMisfirePolicyForSimpleTrigger(SimpleScheduleBuilder simpleSchedule, String motechMisfireProperty) {
        Integer misfirePolicy = simpleTriggerMisfirePolicies.get(motechMisfireProperty);
        if (misfirePolicy == null) {
            misfirePolicy = SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT;
        }
        if (misfirePolicy.equals(SimpleTrigger.MISFIRE_INSTRUCTION_SMART_POLICY)) {
            return simpleSchedule;
        }
        if (misfirePolicy.equals(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW)) {
            return simpleSchedule.withMisfireHandlingInstructionFireNow();
        }
        if (misfirePolicy.equals(SimpleTrigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY)) {
            return simpleSchedule.withMisfireHandlingInstructionIgnoreMisfires();
        }
        if (misfirePolicy.equals(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT)) {
            return simpleSchedule.withMisfireHandlingInstructionNextWithExistingCount();
        }
        if (misfirePolicy.equals(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT)) {
            return simpleSchedule.withMisfireHandlingInstructionNextWithRemainingCount();
        }
        if (misfirePolicy.equals(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT)) {
            return simpleSchedule.withMisfireHandlingInstructionNowWithExistingCount();
        }
        if (misfirePolicy.equals(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT)) {
            return simpleSchedule.withMisfireHandlingInstructionNowWithRemainingCount();
        }
        return simpleSchedule;
    }

    private MotechEvent assertArgumentNotNull(SchedulableJob schedulableJob) {
        assertArgumentNotNull("SchedulableJob", schedulableJob);
        MotechEvent motechEvent = schedulableJob.getMotechEvent();
        assertArgumentNotNull("Invalid SchedulableJob. MotechEvent of the SchedulableJob", motechEvent);
        return motechEvent;
    }

    private void putMotechEventDataToJobDataMap(JobDataMap jobDataMap, MotechEvent motechEvent) {
        jobDataMap.putAll(motechEvent.getParameters());
        jobDataMap.put(EVENT_TYPE_KEY_NAME, motechEvent.getSubject());
    }

    private List<String> extractTriggerNames(List<TriggerKey> triggerKeys) {
        List<String> names = new ArrayList<>();
        for (TriggerKey key : triggerKeys) {
            names.add(key.getName());
        }
        return names;
    }

    private void constructMisfirePoliciesMaps() {
        cronTriggerMisfirePolicies = new HashMap<>();
        cronTriggerMisfirePolicies.put("do_nothing", CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING);
        cronTriggerMisfirePolicies.put("fire_once_now", CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW);
        cronTriggerMisfirePolicies.put("ignore", CronTrigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY);

        simpleTriggerMisfirePolicies = new HashMap<>();
        simpleTriggerMisfirePolicies.put("fire_now", SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        simpleTriggerMisfirePolicies.put("ignore", SimpleTrigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY);
        simpleTriggerMisfirePolicies.put("reschedule_next_with_existing_count", SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT);
        simpleTriggerMisfirePolicies.put("reschedule_next_with_remaining_count", SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT);
        simpleTriggerMisfirePolicies.put("reschedule_now_with_existing_count", SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT);
        simpleTriggerMisfirePolicies.put("reschedule_now_with_remaining_count", SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT);
    }
}
