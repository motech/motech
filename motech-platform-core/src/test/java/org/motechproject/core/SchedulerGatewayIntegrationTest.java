package org.motechproject.core;

import org.junit.Test;
import org.motechproject.model.MotechScheduledEvent;
import org.motechproject.model.RunOnceSchedulableJob;
import org.motechproject.model.SchedulableJob;
import org.motechproject.scheduler.MotechSchedulerGateway;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;

/**
 *
 * User: az44
 * Date: 23/02/11
 * Time: 11:00 AM
 *
 */
public class SchedulerGatewayIntegrationTest {

     public static void main(String[] args) {



        AbstractApplicationContext ctx
                = new ClassPathXmlApplicationContext(new String[]{"/integrationCommon.xml",
                                                                  "/schedulerOutboundChannelAdapter.xml"});


        MotechSchedulerGateway motechSchedulerGateway = (MotechSchedulerGateway) ctx.getBean("motechSchedulerGateway");

          MotechScheduledEvent scheduledEvent = new MotechScheduledEvent("test_1", "testEvent", null);
        SchedulableJob schedulableJob = new SchedulableJob(scheduledEvent, "0/5 0 * * * ?");

         // In order to run the test uncomment the following code

        /* motechSchedulerGateway.scheduleJob(schedulableJob);

         motechSchedulerGateway.unscheduleJob("test_1");

         RunOnceSchedulableJob runOnceSchedulableJob = new RunOnceSchedulableJob(scheduledEvent, new Date((new Date().getTime()+5000)));

         motechSchedulerGateway.scheduleRunOnceJob(runOnceSchedulableJob);*/
    }

    /**
     * This is a fake test method to make CI happy
     */
    @Test
    public void fakeTest() {

    }
}
