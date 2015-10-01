package org.motechproject.security.passwordreminder;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Waits for the scheduler module to start and then schedules password expiration checker job.
 */
@Component
public class PasswordExpirationChecker implements ServiceTrackerCustomizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordExpirationChecker.class);

    private static final String SCHEDULER_SERVICE_CLASS = "org.motechproject.scheduler.service.MotechSchedulerService";

    private PasswordExpirationCheckerInternal internal;

    @Autowired
    private BundleContext bundleContext;

    @PostConstruct
    public void init() {
        ServiceTracker schedulerTracker = new ServiceTracker(bundleContext, SCHEDULER_SERVICE_CLASS, this);
        schedulerTracker.open();
    }

    @Override
    public Object addingService(ServiceReference reference) {
        Object service = bundleContext.getService(reference);

        LOGGER.info("Scheduler service bound");
        internal = new PasswordExpirationCheckerInternal(service);
        internal.schedulePasswordReminderJob();

        return service;
    }

    @Override
    public void modifiedService(ServiceReference reference, Object service) {
    }

    @Override
    public void removedService(ServiceReference reference, Object service) {
        LOGGER.info("Scheduler service unbound");
        internal = null;
    }

}
