package org.motechproject.scheduler;

import org.motech.scheduler.exception.MotechSchedulerException;
import org.motechproject.model.MotechScheduledEvent;
import org.motechproject.model.RunOnceSchedulableJob;
import org.motechproject.model.SchedulableJob;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sound.midi.Track;
import java.text.ParseException;
import java.util.Date;

/**
 *
 */
public class MotechSchedulerServiceImpl implements MotechSchedulerService {

    public static final String JOB_GROUP_NAME = "default";

    final Logger log = LoggerFactory.getLogger(MotechSchedulerServiceImpl.class);


    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Override
    public void scheduleJob(SchedulableJob schedulableJob) {

        log.info("Scheduling the job: " + schedulableJob);

        if (schedulableJob == null ) {
            throw new IllegalArgumentException("SchedulableJob can not be null");
        }

        MotechScheduledEvent motechScheduledEvent = schedulableJob.getMotechScheduledEvent();
        if (motechScheduledEvent == null) {
            throw new IllegalArgumentException("Invalid SchedulableJob. MotechScheduledEvent of the SchedulableJob can not be null");
        }

        String jobId = motechScheduledEvent.getJobId();
        JobDetail jobDetail = new JobDetail(jobId, JOB_GROUP_NAME, MotechScheduledJob.class);
        putMotechScheduledEventDataToJobDataMap(jobDetail.getJobDataMap(), motechScheduledEvent);

        Trigger trigger;

        try {
            trigger = new CronTrigger(jobId, JOB_GROUP_NAME, schedulableJob.getCronExpression());
        } catch (ParseException e) {
            throw new MotechSchedulerException("Can not schedule the job: " + jobId + "\n invalid Cron expression: " +
                                                schedulableJob.getCronExpression());
        }

        scheduleJob(jobDetail, trigger);

    }

    @Override
    public void updateScheduledJob(MotechScheduledEvent motechScheduledEvent) {

        log.info("Updating the scheduled job: " + motechScheduledEvent);

        if (motechScheduledEvent == null) {
            throw new IllegalArgumentException("MotechScheduledEvent can not be null");
        }

        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        String jobId = motechScheduledEvent.getJobId();
        Trigger trigger;



        try {
            trigger =  scheduler.getTrigger(jobId, JOB_GROUP_NAME);

            if (trigger == null) {
                throw new MotechSchedulerException("Can not update the job: " + jobId + " The job does not exist (not scheduled)");
            }

        } catch (SchedulerException e) {
            throw new MotechSchedulerException("Can not update the job: " + jobId +
                    ".\n Can not get a trigger associated with that job " + e.getMessage(), e);
        }

        try {
            scheduler.deleteJob(jobId, JOB_GROUP_NAME);
        } catch (SchedulerException e) {
            throw new MotechSchedulerException("Can not update the job: " + jobId +
                    ".\n Can not delete old instance of the job " + e.getMessage(), e);
        }

        JobDetail jobDetail = new JobDetail(jobId, JOB_GROUP_NAME, MotechScheduledJob.class);
        putMotechScheduledEventDataToJobDataMap(jobDetail.getJobDataMap(), motechScheduledEvent);


        scheduleJob(jobDetail, trigger);

    }

    @Override
    public void rescheduleJob(String jobId, String cronExpression) {

        log.info("Rescheduling the Job: " + jobId + " new cron expression: " + cronExpression);

        if (jobId == null ) {
            throw new IllegalArgumentException("Job ID can not be null");
        }

        if (cronExpression == null) {
            throw new IllegalArgumentException("Cron expression can not be null");
        }

        Scheduler scheduler = schedulerFactoryBean.getScheduler();

        CronTrigger trigger;


        try {
            trigger = (CronTrigger) scheduler.getTrigger(jobId, JOB_GROUP_NAME);

            if (trigger == null) {
                throw new MotechSchedulerException("Can not reschedule the job: " + jobId + " The job does not exist (not scheduled)");
            }

        } catch (SchedulerException e) {
            throw new MotechSchedulerException("Can not reschedule the job: " + jobId +
                    ".\n Can not get a trigger associated with that job " + e.getMessage(), e);
        } catch (ClassCastException e) {
            throw new MotechSchedulerException("Can not reschedule the job: " + jobId +
                    ".\n The trigger associated with that job is not a CronTrigger");
        }

        try {
            trigger.setCronExpression(cronExpression);
        } catch (ParseException e) {
             throw new MotechSchedulerException("Can not reschedule the job: " + jobId + " Invalid Cron expression: " +
                                                cronExpression);
        }

        try {
            schedulerFactoryBean.getScheduler().rescheduleJob(jobId, JOB_GROUP_NAME, trigger);
        } catch (SchedulerException e) {
            throw new MotechSchedulerException("Can not reschedule the job: " + jobId + " " + e.getMessage(), e);
        }
    }

    @Override
    public void  scheduleRunOnceJob(RunOnceSchedulableJob schedulableJob) {

        log.info("Scheduling the Job: " + schedulableJob);

        if (schedulableJob == null ) {
            throw new IllegalArgumentException("SchedulableJob can not be null");
        }

        MotechScheduledEvent motechScheduledEvent = schedulableJob.getMotechScheduledEvent();
        if (motechScheduledEvent == null) {
            throw new IllegalArgumentException("Invalid SchedulableJob. MotechScheduledEvent of the SchedulableJob can not be null");
        }

        Date jobStartDate = schedulableJob.getStartDate();
        if (jobStartDate == null ) {
             throw new IllegalArgumentException("Invalid RunOnceSchedulableJob. The job start date can not be null");
        }
        Date currentDate = new Date();
        if (jobStartDate.before(currentDate) ) {
             throw new IllegalArgumentException("Invalid RunOnceSchedulableJob. The job start date can not be in the past. \n" +
                                                " Job start date: " + jobStartDate.toString() +
                                                " Attempted to schedule at:" + currentDate.toString());
        }

        String jobId = motechScheduledEvent.getJobId();
        JobDetail jobDetail = new JobDetail(jobId, JOB_GROUP_NAME, MotechScheduledJob.class);
        putMotechScheduledEventDataToJobDataMap(jobDetail.getJobDataMap(), motechScheduledEvent);

        Trigger trigger = new SimpleTrigger(jobId, JOB_GROUP_NAME, jobStartDate);

        scheduleJob(jobDetail, trigger);

    }

    @Override
    public void unscheduleJob(String jobId) {

        log.info("Unscheduling the Job: " + jobId);

        if (jobId == null) {
            throw new IllegalArgumentException("Scheduled Job ID can not be null");
        }

        try {
            schedulerFactoryBean.getScheduler().unscheduleJob(jobId, JOB_GROUP_NAME);
        } catch (SchedulerException e) {
            throw new MotechSchedulerException("Can not unschedule the job: " + jobId + " " + e.getMessage(), e);
        }

    }

    private void scheduleJob(JobDetail jobDetail, Trigger trigger) {

         try {
            schedulerFactoryBean.getScheduler().scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
             throw new MotechSchedulerException("Can not schedule the job:\n " +
                                                jobDetail.toString() +"\n"+ trigger.toString() +
                                                "\n" + e.getMessage(), e);
        }
    }

    private void putMotechScheduledEventDataToJobDataMap(JobDataMap jobDataMap, MotechScheduledEvent motechScheduledEvent) {
        jobDataMap.putAll(motechScheduledEvent.getParameters());
        jobDataMap.put(MotechScheduledEvent.EVENT_TYPE_KEY_NAME, motechScheduledEvent.getEventType());
    }
}
