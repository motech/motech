package org.motechproject.server.config.service;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.server.config.domain.ConfigFileSettings;
import org.motechproject.server.config.domain.SettingsRecord;
import org.motechproject.server.config.monitor.ConfigFileMonitor;
import org.motechproject.server.config.repository.AllSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class handles events with settings file creation or modification.
 * Then, the new settings are loaded and stored in the database.
 */

@Component
public class SettingsFileChangeEventHandler {

    @Autowired
    private ConfigLoader configLoader;

    @Autowired
    private AllSettings allSettings;

    @Autowired
    private PlatformSettingsService platformSettingsService;

    @MotechListener(subjects = { ConfigFileMonitor.FILE_CHANGED_EVENT_SUBJECT, ConfigFileMonitor.FILE_CREATED_EVENT_SUBJECT })
    public void reloadSettings(MotechEvent event) {
        ConfigFileSettings configFileSettings = configLoader.loadConfig();
        SettingsRecord dbSettings = allSettings.getSettings();
        dbSettings.updateSettings(configFileSettings);
        allSettings.addOrUpdateSettings(dbSettings);

        platformSettingsService.evictMotechSettingsCache();
        platformSettingsService.evictActiveMqSettingsCache();
    }
}
