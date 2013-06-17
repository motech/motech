package org.motechproject.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class TestJobHandler implements Job {

    static boolean invoked = false;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
       invoked = true;
    }

    public static boolean isInvoked() {
        return invoked;
    }
}
