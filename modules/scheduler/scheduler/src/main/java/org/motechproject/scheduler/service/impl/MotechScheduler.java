package org.motechproject.scheduler.service.impl;

import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.service.MotechSchedulerService;
import org.motechproject.scheduler.contract.CronJobId;
import org.motechproject.scheduler.contract.CronSchedulableJob;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(MotechScheduler.class);

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
        AbstractApplicationContext ctx = new ClassPathXmlApplicationContext("/META-INF/motech/*.xml");
        schedulerService = ctx.getBean(MotechSchedulerService.class);

        // add a shutdown hook for the above context...
        ctx.registerShutdownHook();

        LOGGER.info("Motech Scheduler started...");

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
                        LOGGER.warn(String.format("Unknown parameter: %s - ignored", args[0]));
                        break;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error: ", e);
        }
    }


    private static void scheduleTestEvent() {
        Map<String, Object> params = new HashMap<>();
        params.put(MotechSchedulerService.JOB_ID_KEY, TEST_EVENT_NAME);
        MotechEvent motechEvent = new MotechEvent(TEST_SUBJECT, params);
        CronSchedulableJob cronSchedulableJob = new CronSchedulableJob(motechEvent, TEST_CRON_EXPRESSION);

        try {
            LOGGER.info("Scheduling test job: " + cronSchedulableJob);
            schedulerService.scheduleJob(cronSchedulableJob);
        } catch (Exception e) {
            LOGGER.warn("Can not schedule test job.", e);
        }
    }

    private static void unscheduleTestEvent() {
        try {
            LOGGER.info("Unscheduling the test job: " + TEST_EVENT_NAME);
            schedulerService.unscheduleJob(new CronJobId(TEST_SUBJECT, TEST_EVENT_NAME));
        } catch (Exception e) {
            LOGGER.warn(String.format("Can not unschedule the test job %s:", TEST_EVENT_NAME), e);
        }
    }

}
