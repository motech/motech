package org.motechproject.email;

import org.eclipse.gemini.blueprint.service.importer.OsgiServiceLifecycleListener;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.email.service.impl.PurgeEmailEventHandlerImpl;
import org.motechproject.email.settings.SettingsDto;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.contract.CronSchedulableJob;
import org.motechproject.scheduler.service.MotechSchedulerService;
import org.motechproject.server.config.SettingsFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * The <code>EmailPurger</code> class is responsible for handling changes in mail purging settings and
 * adjusting/deleting the scheduler job responsible for it, in case the settings were changed.
 */
@Component
public class EmailPurger implements OsgiServiceLifecycleListener {

    private static final Logger LOG = LoggerFactory.getLogger(EmailPurger.class);

    private MotechSchedulerService motechSchedulerService;
    private boolean configurationServiceAvailable;
    private final Object lock = new Object();

    @Autowired
    @Qualifier("emailSettings")
    private SettingsFacade settingsFacade;

    public void handleSettingsChange() {
        synchronized (lock) {
            if (configurationServiceAvailable && motechSchedulerService != null) {
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

        unscheduleMailPurging();
        Map<String, Object> params = new HashMap<>();
        params.put("purgeTime", time);
        params.put("purgeMultiplier", multiplier);
        motechSchedulerService.safeScheduleJob(new CronSchedulableJob(
                new MotechEvent(PurgeEmailEventHandlerImpl.PURGE_EMAIL_SUBJECT, params), "0 0 1 * * ?"
        ));

        LOG.info("Email purge job scheduled");
    }

    private void unscheduleMailPurging() {
        motechSchedulerService.unscheduleAllJobs(PurgeEmailEventHandlerImpl.PURGE_EMAIL_SUBJECT);
        LOG.info("Unscheduled email purge job");
    }

    @Override
    public void bind(Object service, Map properties) {
        if (service instanceof MotechSchedulerService) {
            LOG.info("Scheduler service bound");
            motechSchedulerService = (MotechSchedulerService) service;
            handleSettingsChange();
        } else if (service instanceof ConfigurationService) {
            LOG.info("Configuration service bound");
            configurationServiceAvailable = true;
            handleSettingsChange();
        }
    }

    @Override
    public void unbind(Object service, Map properties) {
        if (service instanceof MotechSchedulerService) {
            LOG.info("Scheduler service unbound");
            motechSchedulerService = null;
        } else if (service instanceof ConfigurationService) {
            configurationServiceAvailable = false;
            LOG.info("Configuration service unbound");
        }
    }
}
