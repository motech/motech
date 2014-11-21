package org.motechproject.email.purging;

import org.eclipse.gemini.blueprint.service.importer.OsgiServiceLifecycleListener;
import org.motechproject.email.settings.SettingsDto;
import org.motechproject.server.config.SettingsFacade;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * The <code>EmailPurger</code> class is responsible for handling changes in mail purging settings and
 * adjusting/deleting the scheduler job responsible for it, in case the settings were changed.
 * This class cannot have a dependency on Scheduler class, since that import is optional and this a Spring bean
 * that always gets instantiated. This is a blueprint lifecycle listener for the
 * {@link org.motechproject.config.service.ConfigurationService} and uses a service tracker to track
 * the Scheduler Service.
 */
@Component
public class EmailPurger implements OsgiServiceLifecycleListener, ServiceTrackerCustomizer {

    private static final Logger LOG = LoggerFactory.getLogger(EmailPurger.class);

    private static final String SCHEDULER_SERVICE_CLASS = "org.motechproject.scheduler.service.MotechSchedulerService";

    private EmailPurgerInternal internal;
    private boolean configurationServiceAvailable;
    private final Object lock = new Object();

    @Autowired
    @Qualifier("emailSettings")
    private SettingsFacade settingsFacade;

    @Autowired
    private BundleContext bundleContext;

    @PostConstruct
    public void init() {
        ServiceTracker schedulerTracker = new ServiceTracker(bundleContext, SCHEDULER_SERVICE_CLASS, this);
        schedulerTracker.open();
    }

    public void handleSettingsChange() {
        synchronized (lock) {
            if (!configurationServiceAvailable) {
                LOG.warn("Configuration service unavailable, cannot schedule purge job");
            } else if (internal == null) {
                LOG.warn("Scheduler service unavailable, cannot schedule purge job");
            } else {
                SettingsDto settings = new SettingsDto(settingsFacade);
                if ("true".equals(settings.getLogPurgeEnable())) {
                    scheduleMailPurging(settings.getLogPurgeTime(), settings.getLogPurgeTimeMultiplier());
                } else {
                    unscheduleMailPurging();
                }
            }
        }
    }

    private void scheduleMailPurging(String time, String multiplier) {
        LOG.info("Scheduling email purge job");

        internal.unschedulePurgingJob();
        internal.schedulePurgingJob(time, multiplier);

        LOG.info("Email purge job scheduled");
    }

    private void unscheduleMailPurging() {
        internal.unschedulePurgingJob();
        LOG.info("Unscheduled email purge job");
    }

    @Override
    public void bind(Object service, Map properties) {
        LOG.info("Configuration service bound");
        configurationServiceAvailable = true;
        handleSettingsChange();
    }

    @Override
    public void unbind(Object service, Map properties) {
        configurationServiceAvailable = false;
        LOG.info("Configuration service unbound");
    }

    @Override
    public Object addingService(ServiceReference reference) {
        Object service = bundleContext.getService(reference);

        LOG.info("Scheduler service bound");
        internal = new EmailPurgerInternal(service);
        handleSettingsChange();

        return service;
    }

    @Override
    public void modifiedService(ServiceReference reference, Object service) {
    }

    @Override
    public void removedService(ServiceReference reference, Object service) {
        LOG.info("Scheduler service unbound");
        internal = null;
    }
}
