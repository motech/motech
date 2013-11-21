package org.motechproject.server.config.service.impl;

import org.motechproject.config.core.constants.ConfigurationConstants;
import org.motechproject.server.config.domain.MotechSettings;
import org.motechproject.server.config.domain.SettingsRecord;
import org.motechproject.server.config.repository.AllSettings;
import org.motechproject.server.config.service.ConfigLoader;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * Implementation of {@Link PlatformSettingsService} class for main motech settings managment
 */
@Service("platformSettingsService")
public class PlatformSettingsServiceImpl implements PlatformSettingsService {
/*
 *     Important Note: This class should not be developed further. Please start making
 *     future changes in org.motechproject.config.service.ConfigurationService. When Config management
 *     work completes, this class will be removed.
 */

    @Autowired
    private AllSettings allSettings;

    @Autowired
    private ConfigLoader configLoader;

    @Override
    public Properties exportPlatformSettings() {
        MotechSettings currentSettings = configLoader.loadMotechSettings();
        SettingsRecord dbSettings = allSettings.getSettings();

        Properties export = new Properties();

        if (currentSettings != null) {
            export.putAll(currentSettings.getPlatformSettings());
        }

        if (dbSettings != null) {
            export.putAll(dbSettings.getActivemqProperties());
            export.put(ConfigurationConstants.LANGUAGE, dbSettings.getLanguage());
        }

        return export;
    }
}
