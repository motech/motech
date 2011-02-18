package org.motechproject.scheduler;

import org.motech.scheduler.exception.MotechSchedulerException;
import org.motechproject.model.MotechScheduledEvent;
import org.motechproject.model.RunOnceSchedulableJob;
import org.motechproject.model.SchedulableJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.text.ParseException;

/**
 *
 */
public class MotechSchedulerServiceImpl implements MotechSchedulerService {

    private static final String groupName = "default";

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
        JobDetail jobDetail = new JobDetail(jobId, groupName, MotechScheduledJob.class);

        Trigger trigger = null;

        try {
            trigger = new CronTrigger(jobId,groupName, schedulableJob.getCronExpression());
        } catch (ParseException e) {
            throw new MotechSchedulerException("Can not schedule the job: " + jobId + "\n invalid Cron expression: " +
                                                schedulableJob.getCronExpression());
        }

        try {
            schedulerFactoryBean.getScheduler().scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
             throw new MotechSchedulerException("Can not schedule the job:\n " +
                                                jobDetail.toString() +"\n"+ trigger.toString() +
                                                "\n" + e.getMessage(), e);
        }

    }

    @Override
    public void scheduleRunOnceJob(RunOnceSchedulableJob schedulableJob) {

    }

    @Override
    public void unscheduleJob(String jobId) {

    }
}
