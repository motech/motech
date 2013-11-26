package org.motechproject.server.config.service;

import org.motechproject.config.service.ConfigurationService;
import org.motechproject.server.config.domain.SettingsRecord;
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
    private AllSettings allSettings;

    @Autowired
    private ConfigurationService configurationService;

    public void reloadSettings() {
        SettingsRecord settingsRecord = configurationService.loadConfig();
        SettingsRecord dbSettings = allSettings.getSettings();
        dbSettings.updateSettings(settingsRecord);
        allSettings.addOrUpdateSettings(dbSettings);

        configurationService.evictMotechSettingsCache();
    }
}
