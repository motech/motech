package org.motechproject.scheduler;

import org.motechproject.model.MotechScheduledEvent;
import org.motechproject.model.SchedulableJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 */
public class MotechScheduler {



    public static void main(String[] args) {


        AbstractApplicationContext ctx
                = new ClassPathXmlApplicationContext(new String[]{"/applicationContext.xml"});

        // add a shutdown hook for the above context...
        ctx.registerShutdownHook();


    }


    //The following code is for test purposes only
     @Autowired
     private MotechSchedulerService schedulerService;
    private void init() {



        MotechScheduledEvent scheduledEvent = new MotechScheduledEvent("1", "test", null);
        SchedulableJob schedulableJob = new SchedulableJob(scheduledEvent, "* * * * ? *");

        schedulerService.scheduleJob(schedulableJob);


    }


}
