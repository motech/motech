package org.motechproject.mds.service.impl;

import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.mds.exception.scheduler.MdsSchedulerException;
import org.motechproject.mds.service.MdsSchedulerService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.quartz.JobDetail;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import static org.motechproject.mds.util.Constants.Config.EMPTY_TRASH_JOB;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.TriggerKey.triggerKey;

/**
 * Default implementation of the <code>MdsSchedulerService</code>.
 */
@Service("mdsSchedulerService")
public class MdsSchedulerServiceImpl implements MdsSchedulerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MdsSchedulerServiceImpl.class);

    public static final String JOB_GROUP_NAME = "default";
    public static final int MAX_REPEAT_COUNT = 999999;
    public static final int DEFAULT_WAIT_TIME = 5000;
    public static final int RETRIEVAL_RETRIES_COUNT = 10;
    public static final String SCHEDULER_SYMBOLIC_NAME = "org.motechproject.motech-scheduler";

    private BundleContext bundleContext;
    private Scheduler scheduler;
    private WebApplicationContext webApplicationContext;

    @Autowired
    public MdsSchedulerServiceImpl(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    public void scheduleRepeatingJob(long interval) {
        Date jobStartTime = DateUtil.nowUTC().toDate();

        if (interval == 0) {
            throw new IllegalArgumentException("Invalid RepeatingSchedulableJob. The job repeat interval can not be 0");
        }

        JobDetail jobDetail = newJob(MdsScheduledJob.class)
                .withIdentity(jobKey(EMPTY_TRASH_JOB, JOB_GROUP_NAME))
                .build();

        ScheduleBuilder scheduleBuilder = simpleSchedule()
                .withIntervalInMilliseconds(interval)
                .withRepeatCount(MAX_REPEAT_COUNT);

        Trigger trigger = buildJobDetail(jobStartTime, jobDetail, scheduleBuilder);
        scheduleJob(jobDetail, trigger);
    }

    @Override
    public void unscheduleRepeatingJob() {
        try {
            if (scheduler == null) {
                findMotechSchedulerFactoryBean();
            }
            if (scheduler != null) {
                scheduler.unscheduleJob(triggerKey(EMPTY_TRASH_JOB, JOB_GROUP_NAME));
            }
        } catch (SchedulerException e) {
            throw new MdsSchedulerException(String.format("Can not unschedule the job: %s %s",
                    EMPTY_TRASH_JOB, e.getMessage()), e);
        }
    }

    private Trigger buildJobDetail(Date jobStartTime, JobDetail jobDetail, ScheduleBuilder scheduleBuilder) {
        return newTrigger()
                .withIdentity(triggerKey(EMPTY_TRASH_JOB, JOB_GROUP_NAME))
                .forJob(jobDetail)
                .withSchedule(scheduleBuilder)
                .startAt(jobStartTime)
                .build();
    }

    private void scheduleJob(JobDetail jobDetail, Trigger trigger) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Scheduling job:" + jobDetail);
        }
        try {
            if (scheduler == null) {
                findMotechSchedulerFactoryBean();
            }
            if (scheduler != null) {
                scheduler.scheduleJob(jobDetail, trigger);
            }
        } catch (SchedulerException e) {
            throw new MdsSchedulerException(String.format("Can not schedule the job:\n %s\n%s\n%s",
                    jobDetail.toString(), trigger.toString(), e.getMessage()), e);
        }
    }

    private void findMotechSchedulerFactoryBean() {
        ServiceReference[] references;
        int tries = 0;

        try {
            do {
                references = bundleContext.getAllServiceReferences(WebApplicationContext.class.getName(), null);

                if (references != null) {
                    for (ServiceReference ref : references) {
                        if (SCHEDULER_SYMBOLIC_NAME.equals(ref.getBundle().getSymbolicName())) {
                            webApplicationContext = (WebApplicationContext) bundleContext.getService(ref);
                            break;
                        }
                    }
                }
                ++tries;
                Thread.sleep(DEFAULT_WAIT_TIME);
            } while (webApplicationContext == null && tries < RETRIEVAL_RETRIES_COUNT);

            if (webApplicationContext != null) {
                Object motechSchedulerFactoryBean = webApplicationContext.getBean("motechSchedulerFactoryBean");
                Method method = motechSchedulerFactoryBean.getClass().getMethod("getQuartzScheduler");
                scheduler = (Scheduler) method.invoke(motechSchedulerFactoryBean);
            }
        } catch (InterruptedException | NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                InvalidSyntaxException e) {
            throw new MdsSchedulerException("Can't find motechSchedulerFactoryBean", e);
        }
    }
}
