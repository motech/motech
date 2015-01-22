package org.motechproject.mds.service.impl;

import org.motechproject.bundle.extender.MotechOsgiConfigurableApplicationContext;
import org.motechproject.mds.service.TrashService;
import org.motechproject.osgi.web.util.OSGiServiceUtils;
import org.osgi.framework.BundleContext;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 *  Job responsible for emptying MDS trash.
 */
public class MdsScheduledJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(MdsScheduledJob.class);

    @Override
    @SuppressWarnings("unchecked")
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        LOGGER.info("executing...");

        try {
            SchedulerContext schedulerContext;
            try {
                schedulerContext = jobExecutionContext.getScheduler().getContext();
            } catch (SchedulerException e) {
                LOGGER.error("Can not execute job. Can not get Scheduler Context", e);
                return;
            }
            ApplicationContext applicationContext = (ApplicationContext) schedulerContext.get("applicationContext");
            BundleContext bundleContext = ((MotechOsgiConfigurableApplicationContext) applicationContext).getBundleContext();

            TrashService trashService = OSGiServiceUtils.findService(bundleContext, TrashService.class);

            if (trashService != null) {
                trashService.emptyTrash();
            }
        } catch (Exception e) {
            LOGGER.error("Job execution failed.", e);
        }
    }
}
