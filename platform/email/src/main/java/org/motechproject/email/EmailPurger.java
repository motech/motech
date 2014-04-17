package org.motechproject.email;

import org.eclipse.gemini.blueprint.service.importer.OsgiServiceLifecycleListener;
import org.motechproject.email.model.SettingsDto;
import org.motechproject.email.service.impl.PurgeEmailEventHandlerImpl;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.motechproject.server.config.SettingsFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * The <code>EmailPurger</code> class is responsible for handling changes in mail purging settings and
 * adjusting/deleting the scheduler job responsible for it, in case the settings were changed.
 */
@Component("emailPurger")
public class EmailPurger implements OsgiServiceLifecycleListener {

    private static final Logger LOG = LoggerFactory.getLogger(EmailPurger.class);

    private MotechSchedulerService motechSchedulerService;
    private SettingsFacade settingsFacade;

    @Autowired
    public void setSettingsFacade(SettingsFacade settingsFacade) {
        this.settingsFacade = settingsFacade;
    }


    public void handleSettingsChange() {
        if (motechSchedulerService == null) {
            throw new IllegalStateException("Scheduler service unavailable - cannot schedule job for purging emails");
        }

        SettingsDto settings = new SettingsDto(settingsFacade);
        if ("true".equals(settings.getLogPurgeEnable())) {
            scheduleMailPurging(settings.getLogPurgeTime(), settings.getLogPurgeTimeMultiplier());
        } else {
            unscheduleMailPurging();
        }
    }

    private void scheduleMailPurging(String time, String multiplier) {
        unscheduleMailPurging();
        Map<String, Object> params = new HashMap<>();
        params.put("purgeTime", time);
        params.put("purgeMultiplier", multiplier);
        motechSchedulerService.safeScheduleJob(new CronSchedulableJob(
                new MotechEvent(PurgeEmailEventHandlerImpl.PURGE_EMAIL_SUBJECT, params), "0 0 1 * * ?"
        ));
    }

    private void unscheduleMailPurging() {
        motechSchedulerService.unscheduleAllJobs(PurgeEmailEventHandlerImpl.PURGE_EMAIL_SUBJECT);
    }

    @Override
    public void bind(Object service, Map properties) throws Exception {
        LOG.debug("Scheduler service bound");
        motechSchedulerService = (MotechSchedulerService) service;
        handleSettingsChange();
    }

    @Override
    public void unbind(Object service, Map properties) throws Exception {
        LOG.debug("Scheduler service unbound");
    }
}
