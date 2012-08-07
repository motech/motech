package org.motechproject.scheduler.impl;

import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.CronJobId;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.motechproject.scheduler.domain.MotechEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * \ingroup scheduler
 * <p/>
 * Main class that can bootstrap a Motech Scheduler
 *
 * @author Igor (iopushnyev@2paths.com)
 */
public final class MotechScheduler {
    private static Logger log = LoggerFactory.getLogger(MotechSchedulerServiceImpl.class);

    private static final String SCHEDULE_TEST_INPUT_PARAM = "-t";
    private static final String UNSCHEDULE_TEST_INPUT_PARAM = "-c";

    private static final String TEST_EVENT_NAME = "testEvent";
    private static final String TEST_SUBJECT = "test";
    private static final String TEST_CRON_EXPRESSION = "0/5 * * * * ?";

    private static MotechSchedulerService schedulerService;

    private MotechScheduler() {
        // Utility classes should not have a public or default constructor
    }

    public static void main(final String[] args) {
        AbstractApplicationContext ctx = new ClassPathXmlApplicationContext("/applicationScheduler.xml");
        schedulerService = ctx.getBean(MotechSchedulerService.class);

        // add a shutdown hook for the above context...
        ctx.registerShutdownHook();

        log.info("Motech Scheduler started...");

        try {
            if (args.length > 0) {
                switch (args[0]) {
                    case SCHEDULE_TEST_INPUT_PARAM:
                        scheduleTestEvent();
                        break;
                    case UNSCHEDULE_TEST_INPUT_PARAM:
                        unscheduleTestEvent();
                        break;
                    default:
                        log.warn(String.format("Unknown parameter: %s - ignored", args[0]));
                        break;
                }
            }
        } catch (Exception e) {
            log.error("Error: ", e);
        }
    }


    private static void scheduleTestEvent() {
        Map<String, Object> params = new HashMap<>();
        params.put(MotechSchedulerService.JOB_ID_KEY, TEST_EVENT_NAME);
        MotechEvent motechEvent = new MotechEvent(TEST_SUBJECT, params);
        CronSchedulableJob cronSchedulableJob = new CronSchedulableJob(motechEvent, TEST_CRON_EXPRESSION);

        try {
            log.info("Scheduling test job: " + cronSchedulableJob);
            schedulerService.scheduleJob(cronSchedulableJob);
        } catch (Exception e) {
            log.warn("Can not schedule test job.", e);
        }
    }

    private static void unscheduleTestEvent() {
        try {
            log.info("Unscheduling the test job: " + TEST_EVENT_NAME);
            schedulerService.unscheduleJob(new CronJobId(TEST_SUBJECT, TEST_EVENT_NAME));
        } catch (Exception e) {
            log.warn(String.format("Can not unschedule the test job %s:", TEST_EVENT_NAME), e);
        }
    }

}
