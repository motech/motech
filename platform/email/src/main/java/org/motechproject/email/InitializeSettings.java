package org.motechproject.email;

import org.motechproject.email.model.SettingsDto;
import org.motechproject.email.service.impl.PurgeEmailEventHandlerImpl;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.CronSchedulableJob;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashMap;
import java.util.Map;

/**
 * The <code>InitializeSettings</code> class is responsible for handling changes in mail purging settings and
 * adjusting/deleting the scheduler job responsible for it, in case the settings were changed
 */

public class InitializeSettings {

    private MotechSchedulerService motechSchedulerService;
    private SettingsFacade settingsFacade;

    @Autowired
    public InitializeSettings(@Qualifier("emailSettings") SettingsFacade settingsFacade, MotechSchedulerService motechSchedulerService) {
        this.settingsFacade = settingsFacade;
        this.motechSchedulerService = motechSchedulerService;
    }

    public InitializeSettings() {
        this(null, null);
    }

    public void handleSettingsChange() {
        SettingsDto settings = new SettingsDto(settingsFacade);
        if ("true".equals(settings.getLogPurgeEnable())) {
            scheduleMailPurging(settings.getLogPurgeTime(), settings.getLogPurgeTimeMultiplier());
        } else {
            unscheduleMailPurging();
        }
    }

    private void scheduleMailPurging(String time, String multiplier) {
        unscheduleMailPurging();
        Map<String,Object> params = new HashMap<>();
        params.put("purgeTime", time);
        params.put("purgeMultiplier", multiplier);
        motechSchedulerService.safeScheduleJob(new CronSchedulableJob(
                new MotechEvent(PurgeEmailEventHandlerImpl.PURGE_EMAIL_SUBJECT, params), "0 0 1 * * ?"
        ));
    }

    private void unscheduleMailPurging() {
        motechSchedulerService.unscheduleAllJobs(PurgeEmailEventHandlerImpl.PURGE_EMAIL_SUBJECT);
    }
}
