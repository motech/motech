package org.motechproject.scheduler.impl;

import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.gateway.SchedulerFireEventGateway;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 *
 */
public class MotechScheduledJob implements Job {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    @SuppressWarnings("unchecked")
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        log.info("executing...");

        try {
            JobDetail jobDetail = jobExecutionContext.getJobDetail();
            JobDataMap jobDataMap = jobDetail.getJobDataMap();

            String jobId = jobDetail.getKey().getName();
            String eventType = jobDataMap.getString(MotechEvent.EVENT_TYPE_KEY_NAME);
            Map<String, Object> params = jobDataMap.getWrappedMap();
            params.remove(MotechEvent.EVENT_TYPE_KEY_NAME);
            params.put("JobID", jobId);

            MotechEvent motechEvent = new MotechEvent(eventType, params);
            Trigger trigger = jobExecutionContext.getTrigger();
            motechEvent.setEndTime(trigger.getEndTime())
                    .setLastEvent(!trigger.mayFireAgain());

            log.info("Sending Motech Event Message: " + motechEvent);

            SchedulerContext schedulerContext;
            try {
                schedulerContext = jobExecutionContext.getScheduler().getContext();
            } catch (SchedulerException e) {
                log.error("Can not execute job. Can not get Scheduler Context", e);
                return;
            }

            ApplicationContext applicationContext = (ApplicationContext) schedulerContext.get("applicationContext");

            SchedulerFireEventGateway schedulerFiredEventGateway =
                    (SchedulerFireEventGateway) applicationContext.getBean("schedulerFireEventGateway");

            schedulerFiredEventGateway.sendEventMessage(motechEvent);
        } catch (Exception e) {
            log.error("Job execution failed.", e);
        }
    }
}
