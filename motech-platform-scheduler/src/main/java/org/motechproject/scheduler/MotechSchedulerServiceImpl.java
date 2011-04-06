/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2011 Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, nor its respective contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA AND ITS CONTRIBUTORS
 * “AS IS” AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION USA OR ITS CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package org.motechproject.scheduler;

import org.motechproject.model.MotechEvent;
import org.motechproject.model.RunOnceSchedulableJob;
import org.motechproject.model.SchedulableJob;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.text.ParseException;
import java.util.Date;

/**
 * Motech Scheduler Service implementation
 * @see MotechSchedulerService
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

            String errorMessage = "SchedulableJob can not be null";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        MotechEvent motechEvent = schedulableJob.getMotechEvent();
        if (motechEvent == null) {
            String errorMessage = "Invalid SchedulableJob. MotechEvent of the SchedulableJob can not be null";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        String jobId = motechEvent.getJobId();
        JobDetail jobDetail = new JobDetail(jobId, JOB_GROUP_NAME, MotechScheduledJob.class);
        putMotechEventDataToJobDataMap(jobDetail.getJobDataMap(), motechEvent);

        Trigger trigger;

        try {
            trigger = new CronTrigger(jobId, JOB_GROUP_NAME, schedulableJob.getCronExpression());
            Date startTime = schedulableJob.getStartTime();
            Date endTime = schedulableJob.getEndTime();
			if(startTime!=null) {
				trigger.setStartTime(startTime);
			}
			if(endTime!=null) {
				trigger.setEndTime(endTime);
			}
        } catch (ParseException e) {
            String errorMessage = "Can not schedule the job: " + jobId + "\n invalid Cron expression: " +
                                                schedulableJob.getCronExpression();
            log.error(errorMessage);
            throw new MotechSchedulerException(errorMessage);
        }

        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        Trigger existingTrigger = null;
        try {
            existingTrigger = scheduler.getTrigger(jobId, JOB_GROUP_NAME);
        } catch (SchedulerException e) {
            String errorMessage = "Schedule or reschedule the job: " + jobId +
                    ".\n  " + e.getMessage();
            log.error(errorMessage, e);
            throw new MotechSchedulerException(errorMessage);
        }

        if (existingTrigger != null) {
            unscheduleJob(jobId);
        }

        scheduleJob(jobDetail, trigger);

    }


    @Override
    public void updateScheduledJob(MotechEvent motechEvent) {

        log.info("Updating the scheduled job: " + motechEvent);

        if (motechEvent == null) {
            String errorMessage = "MotechEvent can not be null";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        String jobId = motechEvent.getJobId();
        Trigger trigger;



        try {
            trigger =  scheduler.getTrigger(jobId, JOB_GROUP_NAME);

            if (trigger == null) {
                String errorMessage = "Can not update the job: " + jobId + " The job does not exist (not scheduled)";
                log.error(errorMessage);
                throw new MotechSchedulerException(errorMessage);
            }

        } catch (SchedulerException e) {
            String errorMessage = "Can not update the job: " + jobId +
                    ".\n Can not get a trigger associated with that job " + e.getMessage();
            log.error(errorMessage, e);
            throw new MotechSchedulerException(errorMessage);
        }

        try {
            scheduler.deleteJob(jobId, JOB_GROUP_NAME);
        } catch (SchedulerException e) {
            String errorMessage = "Can not update the job: " + jobId +
                    ".\n Can not delete old instance of the job " + e.getMessage();
            log.error(errorMessage, e);
            throw new MotechSchedulerException(errorMessage);
        }

        JobDetail jobDetail = new JobDetail(jobId, JOB_GROUP_NAME, MotechScheduledJob.class);
        putMotechEventDataToJobDataMap(jobDetail.getJobDataMap(), motechEvent);


        scheduleJob(jobDetail, trigger);

    }


    @Override
    public void rescheduleJob(String jobId, String cronExpression) {

        log.info("Rescheduling the Job: " + jobId + " new cron expression: " + cronExpression);

        if (jobId == null ) {
            String errorMessage = "Job ID can not be null";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage );
        }

        if (cronExpression == null) {
            String errorMessage = "Cron expression can not be null";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        Scheduler scheduler = schedulerFactoryBean.getScheduler();

        CronTrigger trigger;


        try {
            trigger = (CronTrigger) scheduler.getTrigger(jobId, JOB_GROUP_NAME);

            if (trigger == null) {
                String errorMessage = "Can not reschedule the job: " + jobId + " The job does not exist (not scheduled)";
                log.error(errorMessage);
                throw new MotechSchedulerException();
            }

        } catch (SchedulerException e) {
            String errorMessage = "Can not reschedule the job: " + jobId +
                    ".\n Can not get a trigger associated with that job " + e.getMessage();
            log.error(errorMessage, e);
            throw new MotechSchedulerException(errorMessage);
        } catch (ClassCastException e) {
            String errorMessage = "Can not reschedule the job: " + jobId +
                    ".\n The trigger associated with that job is not a CronTrigger";
            log.error(errorMessage);
            throw new MotechSchedulerException();
        }

        try {
            trigger.setCronExpression(cronExpression);
        } catch (ParseException e) {
            String errorMessage = "Can not reschedule the job: " + jobId + " Invalid Cron expression: " +
                                                cronExpression;
            log.error(errorMessage, e);
             throw new MotechSchedulerException(errorMessage);
        }

        try {
            schedulerFactoryBean.getScheduler().rescheduleJob(jobId, JOB_GROUP_NAME, trigger);
        } catch (SchedulerException e) {
            String errorMessage = "Can not reschedule the job: " + jobId + " " + e.getMessage();
            log.error(errorMessage, e);
            throw new MotechSchedulerException(errorMessage);
        }
    }

    @Override
    public void  scheduleRunOnceJob(RunOnceSchedulableJob schedulableJob) {

        log.info("Scheduling the Job: " + schedulableJob);

        if (schedulableJob == null ) {
            String errorMessage = "SchedulableJob can not be null";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        MotechEvent motechEvent = schedulableJob.getMotechEvent();
        if (motechEvent == null) {
            String errorMessage = "Invalid SchedulableJob. MotechEvent of the SchedulableJob can not be null";
            log.error(errorMessage);
            throw new IllegalArgumentException();
        }

        Date jobStartDate = schedulableJob.getStartDate();
        if (jobStartDate == null ) {
            String errorMessage = "Invalid RunOnceSchedulableJob. The job start date can not be null";
            log.error(errorMessage);
             throw new IllegalArgumentException(errorMessage);
        }
        Date currentDate = new Date();
        if (jobStartDate.before(currentDate) ) {
            String errorMessage = "Invalid RunOnceSchedulableJob. The job start date can not be in the past. \n" +
                                                " Job start date: " + jobStartDate.toString() +
                                                " Attempted to schedule at:" + currentDate.toString();
            log.error(errorMessage);
             throw new IllegalArgumentException();
        }

        String jobId = motechEvent.getJobId();
        JobDetail jobDetail = new JobDetail(jobId, JOB_GROUP_NAME, MotechScheduledJob.class);
        putMotechEventDataToJobDataMap(jobDetail.getJobDataMap(), motechEvent);

        Trigger trigger = new SimpleTrigger(jobId, JOB_GROUP_NAME, jobStartDate);

        scheduleJob(jobDetail, trigger);

    }

    @Override
    public void unscheduleJob(String jobId) {

        log.info("Unscheduling the Job: " + jobId);

        if (jobId == null) {
            String errorMessage = "Scheduled Job ID can not be null";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        try {
            schedulerFactoryBean.getScheduler().unscheduleJob(jobId, JOB_GROUP_NAME);
        } catch (SchedulerException e) {
            String errorMessage = "Can not unschedule the job: " + jobId + " " + e.getMessage();
            log.error(errorMessage, e);
            throw new MotechSchedulerException(errorMessage);
        }

    }

    private void scheduleJob(JobDetail jobDetail, Trigger trigger) {

         try {
            schedulerFactoryBean.getScheduler().scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
             String errorMessage = "Can not schedule the job:\n " +
                                                jobDetail.toString() +"\n"+ trigger.toString() +
                                                "\n" + e.getMessage();
             log.error(errorMessage, e);
             throw new MotechSchedulerException(errorMessage);
        }
    }

    private void putMotechEventDataToJobDataMap(JobDataMap jobDataMap, MotechEvent motechEvent) {
        jobDataMap.putAll(motechEvent.getParameters());
        jobDataMap.put(MotechEvent.EVENT_TYPE_KEY_NAME, motechEvent.getEventType());
    }
}
