package org.motechproject.scheduler;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Map;
import java.util.Set;

/**
 *
 */
public class MotechScheduledJob implements Job{

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //TODO - implement properly

        System.out.println("job executed");
        JobDataMap data = jobExecutionContext.getJobDetail().getJobDataMap();


        for (Object entry: data.entrySet()) {

            Map.Entry mapEntry = (Map.Entry) entry;
                System.out.println(mapEntry.getKey() +" = " + mapEntry.getValue());

        }
    }
}
