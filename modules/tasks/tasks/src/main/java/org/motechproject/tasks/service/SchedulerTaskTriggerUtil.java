package org.motechproject.tasks.service;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.commons.date.model.DayOfWeek;
import org.motechproject.commons.date.model.Time;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.contract.CronSchedulableJob;
import org.motechproject.scheduler.contract.DayOfWeekSchedulableJob;
import org.motechproject.scheduler.contract.RepeatingPeriodSchedulableJob;
import org.motechproject.scheduler.contract.RepeatingSchedulableJob;
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

        SchedulerTaskTriggerInformation.schedulerJobType triggerType = trigger.getType();

        switch (triggerType) {
            case RUN_ONCE_JOB:
                startDate = formatter.parseDateTime(trigger.getStartDate());
                // todo check if start date is not in the past, or the exception will be thrown, add 'else' scenario
                if (now.isBefore(startDate)) {
                    RunOnceSchedulableJob job = new RunOnceSchedulableJob(motechEvent, startDate.toDate());
                    schedulerService.safeScheduleRunOnceJob(job);
                }
                break;

            case REPEATING_JOB:
                startDate = formatter.parseDateTime(trigger.getStartDate());
                endDate = formatter.parseDateTime(trigger.getEndDate());
                interval = trigger.getInterval();
                ignorePastFiresAtStart = trigger.isIgnoreFiresignorePastFiresAtStart();
                // todo check if start date is not in the past, or the exception will be thrown, add 'else' scenario
                if (now.isBefore(startDate)) {
                    RepeatingSchedulableJob job = new RepeatingSchedulableJob(motechEvent, interval,
                            startDate.toDate(), endDate.toDate(), ignorePastFiresAtStart);
                    schedulerService.safeScheduleRepeatingJob(job);
                }
                break;

            case CRON_JOB:
                startDate = formatter.parseDateTime(trigger.getStartDate());
                endDate = formatter.parseDateTime(trigger.getEndDate());
                cronExpression = trigger.getCronExpression();
                ignorePastFiresAtStart = trigger.isIgnoreFiresignorePastFiresAtStart();
                // todo check if start date is not in the past, or the exception will be thrown, add 'else' scenario
                if (now.isBefore(startDate)) {
                    CronSchedulableJob job = new CronSchedulableJob(motechEvent, cronExpression,
                            startDate.toDate(), endDate.toDate(), ignorePastFiresAtStart);
                    schedulerService.safeScheduleJob(job);
                }
                break;

            case DAY_OF_WEEK_JOB:
                startDate = formatter.parseDateTime(trigger.getStartDate());
                endDate = formatter.parseDateTime(trigger.getEndDate());
                days = trigger.getDays();
                time = trigger.getTime();
                ignorePastFiresAtStart = trigger.isIgnoreFiresignorePastFiresAtStart();
                // todo check if start date is not in the past, or the exception will be thrown, add 'else' scenario
                if (now.isBefore(startDate)) {
                    DayOfWeekSchedulableJob job = new DayOfWeekSchedulableJob(motechEvent, startDate.toLocalDate(),
                            endDate.toLocalDate(), days, time, ignorePastFiresAtStart);
                    schedulerService.scheduleDayOfWeekJob(job);
                }
                break;

            case REPEATING_JOB_WITH_PERIOD_INTERVAL:
                startDate = formatter.parseDateTime(trigger.getStartDate());
                endDate = formatter.parseDateTime(trigger.getEndDate());
                repeatPeriod = trigger.getRepeatPeriod();
                ignorePastFiresAtStart = trigger.isIgnoreFiresignorePastFiresAtStart();
                // todo check if start date is not in the past, or the exception will be thrown, add 'else' scenario
                if (now.isBefore(startDate)) {
                    RepeatingPeriodSchedulableJob job = new RepeatingPeriodSchedulableJob(motechEvent, startDate.toDate(),
                            endDate.toDate(), repeatPeriod, ignorePastFiresAtStart);
                    schedulerService.safeScheduleRepeatingPeriodJob(job);
                }
                break;

            default:
                break;
        }
    }

    public void setSchedulerTaskTriggerType(Task task) {
        String[] name = task.getTrigger().getSubject().split("\\.");
        switch (name[name.length-1]) {
            case "runOnceJob":
                ((SchedulerTaskTriggerInformation) task.getTrigger()).setType(SchedulerTaskTriggerInformation.schedulerJobType.RUN_ONCE_JOB);
                break;

            case "repeatingJob":
                ((SchedulerTaskTriggerInformation) task.getTrigger()).setType(SchedulerTaskTriggerInformation.schedulerJobType.REPEATING_JOB);
                break;

            case "cronJob":
                ((SchedulerTaskTriggerInformation) task.getTrigger()).setType(SchedulerTaskTriggerInformation.schedulerJobType.CRON_JOB);
                break;

            case "dayOfWeekJob":
                ((SchedulerTaskTriggerInformation) task.getTrigger()).setType(SchedulerTaskTriggerInformation.schedulerJobType.DAY_OF_WEEK_JOB);
                break;

            case "repeatingJobWithPeriodInterval":
                ((SchedulerTaskTriggerInformation) task.getTrigger()).setType(SchedulerTaskTriggerInformation.schedulerJobType.REPEATING_JOB_WITH_PERIOD_INTERVAL);
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

}