package org.motechproject.scheduler;

import org.motech.scheduler.exception.MotechSchedulerException;
import org.motechproject.model.MotechScheduledEvent;
import org.motechproject.model.RunOnceSchedulableJob;
import org.motechproject.model.SchedulableJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.text.ParseException;
import java.util.Date;

/**
 *
 */
public class MotechSchedulerServiceImpl implements MotechSchedulerService {

    public static final String JOB_GROUP_NAME = "default";

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Override
    public void scheduleJob(SchedulableJob schedulableJob) {

        if (schedulableJob == null ) {
            throw new IllegalArgumentException("SchedulableJob can not be null");
        }

        MotechScheduledEvent motechScheduledEvent = schedulableJob.getMotechScheduledEvent();
        if (motechScheduledEvent == null) {
            throw new IllegalArgumentException("Invalid SchedulableJob. MotechScheduledEvent of the SchedulableJob can not be null");
        }

        String jobId = motechScheduledEvent.getJobId();
        JobDetail jobDetail = new JobDetail(jobId, JOB_GROUP_NAME, MotechScheduledJob.class);

        Trigger trigger = null;

        try {
            trigger = new CronTrigger(jobId, JOB_GROUP_NAME, schedulableJob.getCronExpression());
        } catch (ParseException e) {
            throw new MotechSchedulerException("Can not schedule the job: " + jobId + "\n invalid Cron expression: " +
                                                schedulableJob.getCronExpression());
        }

        scheduleJob(jobDetail, trigger);

    }

    @Override
    public void
    scheduleRunOnceJob(RunOnceSchedulableJob schedulableJob) {

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

        Trigger trigger = null;

        trigger = new SimpleTrigger(jobId, jobStartDate);

        scheduleJob(jobDetail, trigger);

    }

    @Override
    public void unscheduleJob(String jobId) {

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
}
