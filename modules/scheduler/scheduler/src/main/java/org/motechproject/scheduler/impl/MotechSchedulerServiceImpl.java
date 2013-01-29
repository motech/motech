package org.motechproject.scheduler.impl;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.MotechObject;
import org.motechproject.event.MotechEvent;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.*;
import org.motechproject.scheduler.exception.MotechSchedulerException;
import org.motechproject.util.DateUtil;
import org.quartz.*;
import org.quartz.impl.calendar.BaseCalendar;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.spi.OperableTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.motechproject.util.DateUtil.newDateTime;
import static org.motechproject.util.DateUtil.now;
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
public class MotechSchedulerServiceImpl extends MotechObject implements MotechSchedulerService {
    public static final String JOB_GROUP_NAME = "default";
    private static final int MAX_REPEAT_COUNT = 999999;
    private static final int MILLISECOND = 1000;
    private SchedulerFactoryBean schedulerFactoryBean;

    @Value("#{quartzProperties['org.quartz.scheduler.cron.trigger.misfire.policy']}")
    private String cronTriggerMisfirePolicy;

    @Value("#{quartzProperties['org.quartz.scheduler.repeating.trigger.misfire.policy']}")
    private String repeatingTriggerMisfirePolicy;
    private Scheduler scheduler;

    @Autowired
    private Properties quartzProperties;

    private MotechSchedulerServiceImpl() {
    }

    @Autowired
    public MotechSchedulerServiceImpl(SchedulerFactoryBean schedulerFactoryBean) {
        this.schedulerFactoryBean = schedulerFactoryBean;
        this.scheduler = schedulerFactoryBean.getScheduler();
    }

    @Override
    public void scheduleJob(CronSchedulableJob cronSchedulableJob) {
        MotechEvent motechEvent = assertCronJob(cronSchedulableJob);

        JobId jobId = new CronJobId(motechEvent);

        String jobGroupName = getJobGroupName(cronSchedulableJob.getMotechEvent().getSubject());
        JobDetail jobDetail = newJob(MotechScheduledJob.class)
                .withIdentity(jobKey(jobId.value(), jobGroupName))
                .build();

        putMotechEventDataToJobDataMap(jobDetail.getJobDataMap(), motechEvent);

        CronScheduleBuilder cronSchedule;
        try {
            cronSchedule = cronSchedule(cronSchedulableJob.getCronExpression());
        } catch (Exception e) {
            String errorMessage = format("Can not schedule job %s; invalid Cron expression: %s", jobId, cronSchedulableJob.getCronExpression());
            logError(errorMessage);
            throw new MotechSchedulerException(errorMessage);
        }

        cronSchedule = setMisfirePolicyForCronTrigger(cronSchedule, cronTriggerMisfirePolicy);

        CronTrigger trigger = newTrigger()
                .withIdentity(triggerKey(jobId.value(), jobGroupName))
                .forJob(jobDetail)
                .withSchedule(cronSchedule)
                .startAt(cronSchedulableJob.getStartTime() != null ? cronSchedulableJob.getStartTime() : new Date())
                .endAt(cronSchedulableJob.getEndTime())
                .build();

        Trigger existingTrigger;
        try {
            existingTrigger = scheduler.getTrigger(triggerKey(jobId.value(), jobGroupName));
        } catch (SchedulerException e) {
            String errorMessage = format("Schedule or reschedule the job: %s.\n%s", jobId, e.getMessage());
            logError(errorMessage, e);
            throw new MotechSchedulerException(errorMessage);
        }
        if (existingTrigger != null) {
            unscheduleJobByJobIdAndJobGroupName(jobId.value(), jobGroupName);
        }

        scheduleJob(jobDetail, trigger);
    }

    private MotechEvent assertCronJob(CronSchedulableJob cronSchedulableJob) {
        assertArgumentNotNull("SchedulableJob", cronSchedulableJob);
        logInfo("Scheduling the job: %s", cronSchedulableJob);

        MotechEvent motechEvent = cronSchedulableJob.getMotechEvent();
        assertArgumentNotNull("MotechEvent of the SchedulableJob", motechEvent);
        return motechEvent;
    }

    private CronScheduleBuilder setMisfirePolicyForCronTrigger(CronScheduleBuilder cronSchedule, String misfirePolicy) {
        if (isEmpty(misfirePolicy)) {
            return cronSchedule;
        }
        Integer intMisfirePolicy = Integer.valueOf(misfirePolicy);
        if (intMisfirePolicy.equals(CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING)) {
            return cronSchedule.withMisfireHandlingInstructionDoNothing();
        }
        if (intMisfirePolicy.equals(CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW)) {
            return cronSchedule.withMisfireHandlingInstructionFireAndProceed();
        }
        if (intMisfirePolicy.equals(CronTrigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY)) {
            return cronSchedule.withMisfireHandlingInstructionIgnoreMisfires();
        }
        return cronSchedule;
    }

    @Override
    public void safeScheduleJob(CronSchedulableJob cronSchedulableJob) {
        assertCronJob(cronSchedulableJob);
        JobId jobId = new CronJobId(cronSchedulableJob.getMotechEvent());
        String subject = cronSchedulableJob.getMotechEvent().getSubject();
        try {
            unscheduleJobByJobIdAndJobGroupName(jobId.value(), getJobGroupName(subject));
        } catch (MotechSchedulerException e) {
            logError(e.getMessage());
        }
        scheduleJob(cronSchedulableJob);
    }

    @Override
    public void updateScheduledJob(MotechEvent motechEvent) {
        logInfo("Updating the scheduled job: %s", motechEvent);
        assertArgumentNotNull("MotechEvent", motechEvent);

        JobId jobId = new CronJobId(motechEvent);
        Trigger trigger;
        String jobGroupName = getJobGroupName(motechEvent.getSubject());

        try {
            trigger = scheduler.getTrigger(triggerKey(jobId.value(), jobGroupName));

            if (trigger == null) {
                String errorMessage = "Can not update the job: " + jobId + " The job does not exist (not scheduled)";
                logError(errorMessage);
                throw new MotechSchedulerException(errorMessage);
            }

        } catch (SchedulerException e) {
            String errorMessage = "Can not update the job: " + jobId +
                    ".\n Can not get a trigger associated with that job " + e.getMessage();
            logError(errorMessage, e);
            throw new MotechSchedulerException(errorMessage);
        }

        try {
            scheduler.deleteJob(jobKey(jobId.value(), jobGroupName));
        } catch (SchedulerException e) {
            handleException(String.format("Can not update the job: %s.\n Can not delete old instance of the job %s", jobId, e.getMessage()), e);
        }

        JobDetail jobDetail = newJob(MotechScheduledJob.class).withIdentity(jobId.value(), jobGroupName).build();
        putMotechEventDataToJobDataMap(jobDetail.getJobDataMap(), motechEvent);

        scheduleJob(jobDetail, trigger);
    }

    @Override
    public void rescheduleJob(String subject, String externalId, String cronExpression) {
        assertArgumentNotNull("Subject", subject);
        assertArgumentNotNull("ExternalId", externalId);
        assertArgumentNotNull("Cron expression", cronExpression);

        JobId jobId = new CronJobId(subject, externalId);
        logInfo("Rescheduling the Job: %s new cron expression: %s", jobId, cronExpression);

        CronTrigger trigger = null;
        JobDetail job = null;
        String jobGroupName = getJobGroupName(subject);
        try {
            trigger = (CronTrigger) scheduler.getTrigger(triggerKey(jobId.value(), jobGroupName));
            if (trigger == null) {
                logError("Can not reschedule the job: %s The job does not exist (not scheduled)", jobId);
                throw new MotechSchedulerException();
            }
            job = scheduler.getJobDetail(trigger.getJobKey());
        } catch (SchedulerException e) {
            handleException(String.format("Can not reschedule the job: %s.\n Can not get a trigger associated with that job %s", jobId, e.getMessage()), e);
        } catch (ClassCastException e) {
            handleException(String.format("Can not reschedule the job: %s.\n The trigger associated with that job is not a CronTrigger", jobId), e);
        }

        CronScheduleBuilder newCronSchedule = null;
        try {
            newCronSchedule = cronSchedule(cronExpression);
        } catch (Exception e) {
            handleException(String.format("Can not reschedule the job: %s Invalid Cron expression: %s", jobId, cronExpression), e);
        }

        CronTrigger newTrigger = newTrigger()
                .withIdentity(trigger.getKey())
                .forJob(job)
                .withSchedule(newCronSchedule)
                .startAt(trigger.getStartTime())
                .endAt(trigger.getEndTime())
                .build();

        try {
            scheduler.rescheduleJob(triggerKey(jobId.value(), jobGroupName), newTrigger);
        } catch (SchedulerException e) {
            handleException(String.format("Can not reschedule the job: %s %s", jobId, e.getMessage()), e);
        }
    }

    private void handleException(String errorMessage, Exception e) {
        logError(errorMessage, e);
        throw new MotechSchedulerException(errorMessage);
    }

    @Override
    public void scheduleRepeatingJob(RepeatingSchedulableJob repeatingSchedulableJob) {
        MotechEvent motechEvent = assertArgumentNotNull(repeatingSchedulableJob);

        Date jobStartDate = repeatingSchedulableJob.getStartTime();
        Date jobEndDate = repeatingSchedulableJob.getEndTime();
        assertArgumentNotNull("Job start date", jobStartDate);

        long repeatIntervalInMilliSeconds = repeatingSchedulableJob.getRepeatIntervalInMilliSeconds();
        if (repeatIntervalInMilliSeconds == 0) {
            String errorMessage = "Invalid RepeatingSchedulableJob. The job repeat interval can not be 0";
            logError(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        Integer jobRepeatCount = repeatingSchedulableJob.getRepeatCount();
        if (null == jobRepeatCount) {
            jobRepeatCount = MAX_REPEAT_COUNT;
        }

        JobId jobId = new RepeatingJobId(motechEvent);
        String jobGroupName = getJobGroupName(motechEvent.getSubject());
        JobDetail jobDetail = newJob(MotechScheduledJob.class)
                .withIdentity(jobKey(jobId.value(), jobGroupName))
                .build();

        putMotechEventDataToJobDataMap(jobDetail.getJobDataMap(), motechEvent);

        ScheduleBuilder scheduleBuilder;
        if (!repeatingSchedulableJob.isUseOriginalFireTimeAfterMisfire()) {
            SimpleScheduleBuilder simpleSchedule = simpleSchedule()
                    .withIntervalInMilliseconds(repeatIntervalInMilliSeconds)
                    .withRepeatCount(jobRepeatCount);
            simpleSchedule = setMisfirePolicyForSimpleTrigger(simpleSchedule, repeatingTriggerMisfirePolicy);
            scheduleBuilder = simpleSchedule;
        } else {
            if (repeatingSchedulableJob.getRepeatCount() != null) {
                final double half = 0.5;
                jobEndDate = new Date((long) (repeatingSchedulableJob.getStartTime().getTime() + repeatIntervalInMilliSeconds * (repeatingSchedulableJob.getRepeatCount() + half)));
            }
            scheduleBuilder = CalendarIntervalScheduleBuilder.calendarIntervalSchedule()
                    .withIntervalInSeconds((int) (repeatIntervalInMilliSeconds / MILLISECOND))
                    .withMisfireHandlingInstructionFireAndProceed();
        }

        Trigger trigger = newTrigger()
                .withIdentity(triggerKey(jobId.value(), jobGroupName))
                .forJob(jobDetail)
                .withSchedule(scheduleBuilder)
                .startAt(jobStartDate)
                .endAt(jobEndDate)
                .build();

        if (repeatingSchedulableJob.isIgnorePastFiresAtStart()) {
            List<Date> triggers = TriggerUtils.computeFireTimes((OperableTrigger) trigger, null, Integer.MAX_VALUE);
            int nextTrigger = getFirstTriggerNotInPast(triggers);
            if (nextTrigger != -1) {

                if (scheduleBuilder instanceof SimpleScheduleBuilder) {
                    ((SimpleScheduleBuilder) scheduleBuilder).withRepeatCount(triggers.size() - nextTrigger - 1);
                }

                ((OperableTrigger) trigger).setStartTime(triggers.get(nextTrigger));
                trigger = newTrigger()
                        .withIdentity(triggerKey(jobId.value(), jobGroupName))
                        .forJob(jobDetail)
                        .withSchedule(scheduleBuilder)
                        .startAt(triggers.get(nextTrigger))
                        .endAt(jobEndDate)
                        .build();
            }
        }

        scheduleJob(jobDetail, trigger);
    }


    private SimpleScheduleBuilder setMisfirePolicyForSimpleTrigger(SimpleScheduleBuilder simpleSchedule, String newMisfirePolicy) {
        Integer misfirePolicy = SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT;
        if (!isEmpty(newMisfirePolicy)) {
            misfirePolicy = Integer.valueOf(newMisfirePolicy);
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

    private int getFirstTriggerNotInPast(List<Date> dates) {
        DateTime now = DateUtil.now();
        for (int i = 0; i < dates.size(); i++) {
            Date date = dates.get(i);

            if (newDateTime(date).isAfter(now)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void safeScheduleRepeatingJob(RepeatingSchedulableJob repeatingSchedulableJob) {
        assertArgumentNotNull(repeatingSchedulableJob);
        String subject = repeatingSchedulableJob.getMotechEvent().getSubject();
        try {
            unscheduleJobByJobIdAndJobGroupName(new RepeatingJobId(repeatingSchedulableJob.getMotechEvent()).value(), getJobGroupName(subject));
        } catch (MotechSchedulerException e) {
            logError(e.getMessage());
        }
        scheduleRepeatingJob(repeatingSchedulableJob);
    }

    @Override
    public void scheduleRunOnceJob(RunOnceSchedulableJob schedulableJob) {
        MotechEvent motechEvent = assertArgumentNotNull(schedulableJob);

        Date jobStartDate = schedulableJob.getStartDate();
        assertArgumentNotNull("Job start date", jobStartDate);
        Date currentDate = DateUtil.today().toDate();
        if (jobStartDate.before(currentDate)) {
            String errorMessage = "Invalid RunOnceSchedulableJob. The job start date can not be in the past. \n" +
                    " Job start date: " + jobStartDate.toString() +
                    " Attempted to schedule at:" + currentDate.toString();
            logError(errorMessage);
            throw new IllegalArgumentException();
        }

        JobId jobId = new RunOnceJobId(motechEvent);
        String jobGroupName = getJobGroupName(motechEvent.getSubject());
        JobDetail jobDetail = newJob(MotechScheduledJob.class)
                .withIdentity(jobId.value(), jobGroupName)
                .build();

        putMotechEventDataToJobDataMap(jobDetail.getJobDataMap(), motechEvent);

        SimpleScheduleBuilder simpleSchedule = simpleSchedule()
                .withRepeatCount(0)
                .withIntervalInSeconds(0)
                .withMisfireHandlingInstructionFireNow();

        Trigger trigger = newTrigger()
                .withIdentity(triggerKey(jobId.value(), jobGroupName))
                .forJob(jobDetail)
                .withSchedule(simpleSchedule)
                .startAt(jobStartDate)
                .build();

        scheduleJob(jobDetail, trigger);
    }

    private MotechEvent assertArgumentNotNull(RepeatingSchedulableJob repeatingSchedulableJob) {
        assertArgumentNotNull("SchedulableJob", repeatingSchedulableJob);
        logInfo("Scheduling the Job: %s", repeatingSchedulableJob);
        MotechEvent motechEvent = repeatingSchedulableJob.getMotechEvent();
        assertArgumentNotNull("Invalid SchedulableJob. MotechEvent of the SchedulableJob", motechEvent);
        return motechEvent;
    }

    private MotechEvent assertArgumentNotNull(RunOnceSchedulableJob schedulableJob) {
        assertArgumentNotNull("SchedulableJob", schedulableJob);
        logInfo("Scheduling the Job: %s", schedulableJob);

        MotechEvent motechEvent = schedulableJob.getMotechEvent();
        assertArgumentNotNull("MotechEvent of the SchedulableJob", motechEvent);
        return motechEvent;
    }

    public void safeScheduleRunOnceJob(RunOnceSchedulableJob schedulableJob) {
        assertArgumentNotNull(schedulableJob);
        JobId jobId = new RunOnceJobId(schedulableJob.getMotechEvent());
        String jobGroupName = getJobGroupName(schedulableJob.getMotechEvent().getSubject());
        try {
            unscheduleJobByJobIdAndJobGroupName(jobId.value(), jobGroupName);
        } catch (MotechSchedulerException e) {
            logError(e.getMessage());
        }
        scheduleRunOnceJob(schedulableJob);
    }

    @Override
    public void scheduleDayOfWeekJob(DayOfWeekSchedulableJob dayOfWeekSchedulableJob) {
        MotechEvent motechEvent = dayOfWeekSchedulableJob.getMotechEvent();
        LocalDate start = dayOfWeekSchedulableJob.getStartDate();
        LocalDate end = dayOfWeekSchedulableJob.getEndDate();
        Time time = dayOfWeekSchedulableJob.getTime();
        DateTime now = now();

        if (dayOfWeekSchedulableJob.isIntervening() && newDateTime(start, time).isBefore(now)) {
            start = now.toLocalDate();
        }

        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.atHourAndMinuteOnGivenDaysOfWeek(time.getHour(), time.getMinute(), dayOfWeekSchedulableJob.getCronDays().toArray(new Integer[0]));
        CronTriggerImpl cronTrigger = (CronTriggerImpl) cronScheduleBuilder.build();
        CronSchedulableJob cronSchedulableJob = new CronSchedulableJob(motechEvent, cronTrigger.getCronExpression(), start.toDate(), end.toDate());

        scheduleJob(cronSchedulableJob);
    }

    @Override
    public void unscheduleRepeatingJob(String subject, String externalId) {
        JobId jobId = new RepeatingJobId(subject, externalId);
        logInfo("Unscheduling repeating the Job: %s", jobId);
        unscheduleJobByJobIdAndJobGroupName(jobId.value(), subject);
    }

    @Override
    public void safeUnscheduleRepeatingJob(String subject, String externalId) {
        try {
            unscheduleRepeatingJob(subject, externalId);
        } catch (Exception e) {
            logError(e.getMessage());
        }
    }

    @Override
    public void unscheduleRunOnceJob(String subject, String externalId) {
        JobId jobId = new RunOnceJobId(subject, externalId);
        logInfo("Unscheduling run once Job: %s", jobId);
        unscheduleJobByJobIdAndJobGroupName(jobId.value(), getJobGroupName(subject));
    }

    @Override
    public void safeUnscheduleRunOnceJob(String subject, String externalId) {
        try {
            unscheduleRunOnceJob(subject, externalId);
        } catch (Exception e) {
            logError(e.getMessage());
        }
    }

    @Override
    public void unscheduleJob(String subject, String externalId) {
        unscheduleJob(new CronJobId(subject, externalId), getJobGroupName(subject));
    }

    @Override
    public void unscheduleJob(JobId job, String jobGroupName) {
        logInfo("Unscheduling the Job: %s", job);
        unscheduleJobByJobIdAndJobGroupName(job.value(), jobGroupName);
    }

    @Override
    public void safeUnscheduleJob(String subject, String externalId) {
        try {
            unscheduleJob(subject, externalId);
        } catch (Exception e) {
            logError(e.getMessage());
        }
    }

    private void unscheduleJobByJobIdAndJobGroupName(String jobId, String jobGroupName) {
        try {
            assertArgumentNotNull("ScheduledJobID", jobId);
            scheduler.unscheduleJob(triggerKey(jobId, jobGroupName));
        } catch (SchedulerException e) {
            handleException(String.format("Can not unschedule the job: %s %s", jobId, e.getMessage()), e);
        }
    }

    private void safeUnscheduleJob(String jobId) {
        try {
            assertArgumentNotNull("ScheduledJobID", jobId);
            scheduler.unscheduleJob(triggerKey(jobId, JOB_GROUP_NAME));
        } catch (SchedulerException e) {
            logError(e.getMessage());
        }
    }

    @Override
    public void safeUnscheduleAllJobs(String jobIdPrefix) {
        try {
            logInfo("Safe unscheduling the Jobs given jobIdPrefix: %s", jobIdPrefix);
            List<TriggerKey> triggerKeys = new ArrayList<TriggerKey>(scheduler.getTriggerKeys(GroupMatcher.triggerGroupContains(JOB_GROUP_NAME)));
            List<String> triggerNames = extractTriggerNames(triggerKeys);
            for (String triggerName : triggerNames) {
                if (StringUtils.isNotEmpty(jobIdPrefix) && triggerName.contains(jobIdPrefix)) {
                    safeUnscheduleJob(triggerName);
                }
            }
        } catch (SchedulerException e) {
            logError(e.getMessage());
        }
    }

    private List<Date> computeFireTimesForTrigger(String name, String group, Date startDate, Date endDate)
            throws SchedulerException {
        Trigger trigger;
        List<Date> messageTimings;

        trigger = scheduler.getTrigger(triggerKey(name, group));
        messageTimings = TriggerUtils.computeFireTimesBetween(
                (OperableTrigger) trigger, new BaseCalendar(), startDate, endDate);

        return messageTimings;
    }

    /*
     * Assumes that the externalJobId is non-repeating in nature. Thus the fetch is for jobId.value() and not
     * jobId.repeatingId()
     * Uses quartz API to fetch the exact triggers. Fast
     */
    @Override
    public List<Date> getScheduledJobTimings(String subject, String externalJobId, Date startDate, Date endDate) {
        Scheduler localScheduler = schedulerFactoryBean.getScheduler();
        JobId jobId = new CronJobId(subject, externalJobId);
        Trigger trigger;
        List<Date> messageTimings = null;
        try {
            trigger = localScheduler.getTrigger(triggerKey(jobId.value(), JOB_GROUP_NAME));
            messageTimings = TriggerUtils.computeFireTimesBetween(
                    (OperableTrigger) trigger, new BaseCalendar(), startDate, endDate);

        } catch (SchedulerException e) {
            handleException(String.format(
                    "Can not get scheduled job timings given subject and externalJobId for dates : %s %s %s %s %s",
                    subject, externalJobId, startDate.toString(), endDate.toString(), e.getMessage()), e);
        }
        return messageTimings;
    }

    /*
     * Loads all triggers and then loops over them to find the applicable trigger using string comparison. This
     * will work regardless of the jobId being cron or repeating.
     */
    @Override
    public List<Date> getScheduledJobTimingsWithPrefix(
            String subject, String externalJobIdPrefix, Date startDate, Date endDate) {

        JobId jobId = new CronJobId(subject, externalJobIdPrefix);
        List<Date> messageTimings = new ArrayList<>();
        try {
            List<TriggerKey> triggerKeys = new ArrayList<TriggerKey>(
                    scheduler.getTriggerKeys(GroupMatcher.triggerGroupContains(JOB_GROUP_NAME)));
            for (TriggerKey triggerKey : triggerKeys) {
                if (StringUtils.isNotEmpty(externalJobIdPrefix) && triggerKey.getName().contains(jobId.value())) {
                    Trigger trigger = scheduler.getTrigger(triggerKey);
                    messageTimings.addAll(TriggerUtils.computeFireTimesBetween(
                            (OperableTrigger) trigger, new BaseCalendar(), startDate, endDate));
                }
            }

        } catch (SchedulerException e) {
            handleException(String.format(
                    "Can not get scheduled job timings given subject and externalJobIdPrefix for dates : %s %s %s %s %s",
                    subject, externalJobIdPrefix, startDate.toString(), endDate.toString(), e.getMessage()), e);
        }

        return messageTimings;
    }

    @Override
    public void unscheduleAllJobs(String jobIdPrefix) {
        try {
            logInfo("Unscheduling the Jobs given jobIdPrefix: %s", jobIdPrefix);
            List<String> triggerGroupNames = scheduler.getTriggerGroupNames();
            for(String triggerGroupName : triggerGroupNames){
                List<TriggerKey> triggerKeys = new ArrayList(scheduler.getTriggerKeys(GroupMatcher.triggerGroupContains(triggerGroupName)));
                List<String> triggerNames = extractTriggerNames(triggerKeys);
                for (String triggerName : triggerNames) {
                    if (StringUtils.isNotEmpty(jobIdPrefix) && triggerName.contains(jobIdPrefix)) {
                        unscheduleJobByJobIdAndJobGroupName(triggerName, triggerGroupName);
                    }
                }
            }
        } catch (SchedulerException e) {
            handleException(String.format("Can not unschedule jobs given jobIdPrefix: %s %s", jobIdPrefix, e.getMessage()), e);
        }
    }

    @Override
    public void shutdownScheduler() {
        try {
            scheduler.shutdown(true);
        } catch (SchedulerException e) {
            logError("scheduler did not shutdown.", e);
        }
    }

    @Override
    public String getJobGroupName(String subject) {
        String givenJobGroupName = quartzProperties.getProperty(String.format("%s.%s", subject, "jobGroupName"));
        return givenJobGroupName != null ? givenJobGroupName : JOB_GROUP_NAME;
    }

    private void scheduleJob(JobDetail jobDetail, Trigger trigger) {
        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            handleException(String.format("Can not schedule the job:\n %s\n%s\n%s", jobDetail.toString(), trigger.toString(), e.getMessage()), e);
        }
    }

    private void putMotechEventDataToJobDataMap(JobDataMap jobDataMap, MotechEvent motechEvent) {
        jobDataMap.putAll(motechEvent.getParameters());
        jobDataMap.put(MotechEvent.EVENT_TYPE_KEY_NAME, motechEvent.getSubject());
    }

    private List<String> extractTriggerNames(List<TriggerKey> triggerKeys) {
        List<String> names = new ArrayList<String>();
        for (TriggerKey key : triggerKeys) {
            names.add(key.getName());
        }
        return names;
    }
}
