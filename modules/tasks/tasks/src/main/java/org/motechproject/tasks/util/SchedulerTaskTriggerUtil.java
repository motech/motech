package org.motechproject.tasks.util;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.commons.date.model.DayOfWeek;
import org.motechproject.commons.date.model.Time;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.contract.CronJobId;
import org.motechproject.scheduler.contract.CronSchedulableJob;
import org.motechproject.scheduler.contract.DayOfWeekSchedulableJob;
import org.motechproject.scheduler.contract.JobId;
import org.motechproject.scheduler.contract.RepeatingJobId;
import org.motechproject.scheduler.contract.RepeatingPeriodJobId;
import org.motechproject.scheduler.contract.RepeatingPeriodSchedulableJob;
import org.motechproject.scheduler.contract.RepeatingSchedulableJob;
import org.motechproject.scheduler.contract.RunOnceJobId;
import org.motechproject.scheduler.contract.RunOnceSchedulableJob;
import org.motechproject.scheduler.exception.MotechSchedulerException;
import org.motechproject.scheduler.service.MotechSchedulerService;
import org.motechproject.tasks.domain.SchedulerJobType;
import org.motechproject.tasks.domain.SchedulerTaskTriggerInformation;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This util class is responsible for the handling of scheduling, unscheduling and building
 * scheduler jobs for Scheduler task triggers. Since the Scheduler trigger works differently to
 * all other triggers, as it requires firing a task every specified amount of time, this util
 * class has been created to encapsulate all the required logic in that area.
 */
@Component
public class SchedulerTaskTriggerUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerTaskTriggerUtil.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("yy-MM-dd HH:mm Z");

    @Autowired
    private MotechSchedulerService schedulerService;
    @Autowired
    private TaskService taskService;

    public void scheduleTriggerJob(String subject) {
        MotechEvent motechEvent = prepareSchedulerEvent(subject);
        Task task = getSingleTaskBySubject(subject);

        DateTime now = DateUtil.now();

        DateTime startDate;
        DateTime endDate;
        boolean ignorePastFiresAtStart;
        int interval;
        String cronExpression;
        List<DayOfWeek> days;
        Time time;
        Period repeatPeriod;

        SchedulerTaskTriggerInformation trigger = (SchedulerTaskTriggerInformation) task.getTrigger();
        SchedulerJobType triggerType = trigger.getType();

        switch (triggerType) {
            case RUN_ONCE_JOB:
                LOGGER.info("Scheduling run once job for task: {}", task);
                startDate = DATE_FORMAT.parseDateTime(trigger.getStartDate());
                if (now.isBefore(startDate)) {
                    schedulerService.safeScheduleRunOnceJob(
                            new RunOnceSchedulableJob(motechEvent, startDate.toDate()));
                } else {
                    throw new MotechSchedulerException("The run once job has not been scheduled, because provided date ("
                            + startDate + ") is in the past.");
                }
                break;

            case REPEATING_JOB:
                LOGGER.info("Scheduling repeating job for task: {}", task);
                startDate = DATE_FORMAT.parseDateTime(trigger.getStartDate());
                endDate = DATE_FORMAT.parseDateTime(trigger.getEndDate());
                interval = trigger.getInterval();
                ignorePastFiresAtStart = trigger.isIgnoreFiresignorePastFiresAtStart();
                schedulerService.safeScheduleRepeatingJob(
                        new RepeatingSchedulableJob(motechEvent, interval, startDate.toDate(), endDate.toDate(),
                                ignorePastFiresAtStart));
                break;

            case CRON_JOB:
                LOGGER.info("Scheduling cron job for task: {}", task);
                startDate = DATE_FORMAT.parseDateTime(trigger.getStartDate());
                endDate = DATE_FORMAT.parseDateTime(trigger.getEndDate());
                cronExpression = trigger.getCronExpression();
                ignorePastFiresAtStart = trigger.isIgnoreFiresignorePastFiresAtStart();
                schedulerService.safeScheduleJob(
                        new CronSchedulableJob(motechEvent, cronExpression, startDate.toDate(), endDate.toDate(),
                                ignorePastFiresAtStart));
                break;

            case DAY_OF_WEEK_JOB:
                LOGGER.info("Scheduling day of week job for task: {}", task);
                startDate = DATE_FORMAT.parseDateTime(trigger.getStartDate());
                endDate = DATE_FORMAT.parseDateTime(trigger.getEndDate());
                days = trigger.getDays();
                time = trigger.getTime();
                ignorePastFiresAtStart = trigger.isIgnoreFiresignorePastFiresAtStart();
                schedulerService.scheduleDayOfWeekJob(
                        new DayOfWeekSchedulableJob(motechEvent, startDate.toLocalDate(), endDate.toLocalDate(),
                                days, time, ignorePastFiresAtStart));
                break;

            case REPEATING_JOB_WITH_PERIOD_INTERVAL:
                LOGGER.info("Scheduling repeating job with period interval for task: {}", task);
                startDate = DATE_FORMAT.parseDateTime(trigger.getStartDate());
                endDate = DATE_FORMAT.parseDateTime(trigger.getEndDate());
                repeatPeriod = trigger.getRepeatPeriod();
                ignorePastFiresAtStart = trigger.isIgnoreFiresignorePastFiresAtStart();
                schedulerService.safeScheduleRepeatingPeriodJob(
                        new RepeatingPeriodSchedulableJob(motechEvent, startDate.toDate(), endDate.toDate(),
                                repeatPeriod, ignorePastFiresAtStart));
            default:
                break;
        }
    }

    public void setSchedulerTaskTriggerType(Task task) {
        String[] name = task.getTrigger().getSubject().split("\\.");
        switch (name[name.length-1]) {
            case "runOnceJob":
                ((SchedulerTaskTriggerInformation) task.getTrigger()).setType(SchedulerJobType.RUN_ONCE_JOB);
                break;

            case "repeatingJob":
                ((SchedulerTaskTriggerInformation) task.getTrigger()).setType(SchedulerJobType.REPEATING_JOB);
                break;

            case "cronJob":
                ((SchedulerTaskTriggerInformation) task.getTrigger()).setType(SchedulerJobType.CRON_JOB);
                break;

            case "dayOfWeekJob":
                ((SchedulerTaskTriggerInformation) task.getTrigger()).setType(SchedulerJobType.DAY_OF_WEEK_JOB);
                break;

            case "repeatingJobWithPeriodInterval":
                ((SchedulerTaskTriggerInformation) task.getTrigger()).setType(SchedulerJobType.REPEATING_JOB_WITH_PERIOD_INTERVAL);
                break;

            default:
                break;
        }
    }

    private MotechEvent prepareSchedulerEvent(String subject) {
        Map<String, Object> values = new HashMap<>();

        return new MotechEvent(subject, values);
    }

    public Task getSingleTaskBySubject(String subject) {
        List<Task> tasks;
        String[] name = subject.split("\\.");
        tasks = taskService.findTasksByName(name[name.length-1]);

        if (tasks.size() == 1) {
            return tasks.get(0);
        } else {
            return null;
        }
    }

    public void unscheduleTaskTrigger(Task task){
        JobId jobId = null;

        if (task.getTrigger() instanceof SchedulerTaskTriggerInformation) {
            switch (((SchedulerTaskTriggerInformation) task.getTrigger()).getType()) {
                case RUN_ONCE_JOB:
                    jobId = new RunOnceJobId(task.getTrigger().getEffectiveListenerSubject(), "null");
                    break;
                case REPEATING_JOB:
                    jobId = new RepeatingJobId(task.getTrigger().getEffectiveListenerSubject(), "null");
                    break;
                case CRON_JOB:
                    jobId = new CronJobId(task.getTrigger().getEffectiveListenerSubject(), "null");
                    break;
                case DAY_OF_WEEK_JOB:
                    jobId = new CronJobId(task.getTrigger().getEffectiveListenerSubject(), "null");
                    break;
                case REPEATING_JOB_WITH_PERIOD_INTERVAL:
                    jobId = new RepeatingPeriodJobId(task.getTrigger().getEffectiveListenerSubject(), "null");
                    break;
            }
        }
        schedulerService.unscheduleJob(jobId);
    }

}