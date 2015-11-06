package org.motechproject.mds.service.impl;

import org.motechproject.bundle.extender.MotechOsgiConfigurableApplicationContext;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.repository.AllEntities;
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

import java.util.List;

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

                AllEntities allEntities = applicationContext.getBean(AllEntities.class);
                List<Entity> entities = allEntities.getActualEntities();

                trashService.emptyTrash(entities);
            } else {
                LOGGER.warn("TrashService is unavailable, unable to empty trash");
            }
        } catch (Exception e) {
            LOGGER.error("Job execution failed.", e);
        }
    }
}
