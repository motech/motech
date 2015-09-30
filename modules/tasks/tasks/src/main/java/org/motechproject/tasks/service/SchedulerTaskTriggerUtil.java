package org.motechproject.tasks.service;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.commons.date.model.DayOfWeek;
import org.motechproject.commons.date.model.Time;
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
import org.motechproject.scheduler.service.MotechSchedulerService;
import org.motechproject.tasks.domain.SchedulerTaskTriggerInformation;
import org.motechproject.tasks.domain.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SchedulerTaskTriggerUtil {

    @Autowired
    private MotechSchedulerService schedulerService;
    @Autowired
    private TaskService taskService;


    public void scheduleTriggerJob(String subject) {

        MotechEvent motechEvent = prepareSchedulerEvent(subject);

        Task task = getSingleTaskBySubject(subject);

        DateTime now = new DateTime();
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yy-MM-dd HH:mm Z");
        DateTime startDate;
        DateTime endDate;
        boolean ignorePastFiresAtStart;
        int interval;
        String cronExpression;
        List<DayOfWeek> days;
        Time time;
        Period repeatPeriod;

        SchedulerTaskTriggerInformation trigger = (SchedulerTaskTriggerInformation) task.getTrigger();

        SchedulerTaskTriggerInformation.SchedulerJobType triggerType = trigger.getType();

        switch (triggerType) {
            case RUN_ONCE_JOB:
                startDate = formatter.parseDateTime(trigger.getStartDate());
                if (now.isBefore(startDate)) {
                    schedulerService.safeScheduleRunOnceJob(
                            new RunOnceSchedulableJob(motechEvent, startDate.toDate()));
                } else {
                    //todo add throwing a message box here informing that start date cannot be in past
                }
                break;

            case REPEATING_JOB:
                startDate = formatter.parseDateTime(trigger.getStartDate());
                endDate = formatter.parseDateTime(trigger.getEndDate());
                interval = trigger.getInterval();
                ignorePastFiresAtStart = trigger.isIgnoreFiresignorePastFiresAtStart();
                schedulerService.safeScheduleRepeatingJob(
                        new RepeatingSchedulableJob(motechEvent, interval, startDate.toDate(), endDate.toDate(),
                                ignorePastFiresAtStart));
                break;

            case CRON_JOB:
                startDate = formatter.parseDateTime(trigger.getStartDate());
                endDate = formatter.parseDateTime(trigger.getEndDate());
                cronExpression = trigger.getCronExpression();
                ignorePastFiresAtStart = trigger.isIgnoreFiresignorePastFiresAtStart();
                schedulerService.safeScheduleJob(
                        new CronSchedulableJob(motechEvent, cronExpression, startDate.toDate(), endDate.toDate(),
                                ignorePastFiresAtStart));
                break;

            case DAY_OF_WEEK_JOB:
                startDate = formatter.parseDateTime(trigger.getStartDate());
                endDate = formatter.parseDateTime(trigger.getEndDate());
                days = trigger.getDays();
                time = trigger.getTime();
                ignorePastFiresAtStart = trigger.isIgnoreFiresignorePastFiresAtStart();
                schedulerService.scheduleDayOfWeekJob(
                        new DayOfWeekSchedulableJob(motechEvent, startDate.toLocalDate(), endDate.toLocalDate(),
                                days, time, ignorePastFiresAtStart));
                break;

            case REPEATING_JOB_WITH_PERIOD_INTERVAL:
                startDate = formatter.parseDateTime(trigger.getStartDate());
                endDate = formatter.parseDateTime(trigger.getEndDate());
                repeatPeriod = trigger.getRepeatPeriod();
                ignorePastFiresAtStart = trigger.isIgnoreFiresignorePastFiresAtStart();
                schedulerService.safeScheduleRepeatingPeriodJob(
                        new RepeatingPeriodSchedulableJob(motechEvent, startDate.toDate(), endDate.toDate(),
                                repeatPeriod, ignorePastFiresAtStart));
                break;

            default:
                break;
        }
    }

    public void setSchedulerTaskTriggerType(Task task) {
        String[] name = task.getTrigger().getSubject().split("\\.");
        switch (name[name.length-1]) {
            case "runOnceJob":
                ((SchedulerTaskTriggerInformation) task.getTrigger()).setType(SchedulerTaskTriggerInformation.SchedulerJobType.RUN_ONCE_JOB);
                break;

            case "repeatingJob":
                ((SchedulerTaskTriggerInformation) task.getTrigger()).setType(SchedulerTaskTriggerInformation.SchedulerJobType.REPEATING_JOB);
                break;

            case "cronJob":
                ((SchedulerTaskTriggerInformation) task.getTrigger()).setType(SchedulerTaskTriggerInformation.SchedulerJobType.CRON_JOB);
                break;

            case "dayOfWeekJob":
                ((SchedulerTaskTriggerInformation) task.getTrigger()).setType(SchedulerTaskTriggerInformation.SchedulerJobType.DAY_OF_WEEK_JOB);
                break;

            case "repeatingJobWithPeriodInterval":
                ((SchedulerTaskTriggerInformation) task.getTrigger()).setType(SchedulerTaskTriggerInformation.SchedulerJobType.REPEATING_JOB_WITH_PERIOD_INTERVAL);
                break;

            default:
                break;
        }
    }

    public MotechEvent prepareSchedulerEvent(String subject) {
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
        // Since no jobId is assigned when creating motechEvent "null" is put here. Fix when event jobId will be used.
        // todo actually we can use task name as a jobID
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