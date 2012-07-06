package org.motechproject.server.startup.service.impl;

import org.motechproject.server.startup.service.PlatformSettingsService;
import org.motechproject.server.startup.settings.MotechSettings;
import org.motechproject.server.startup.StartupManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class PlatformSettingsServiceImpl implements PlatformSettingsService {

    @Autowired
    StartupManager startupManager;

    @Override
    public MotechSettings getPlatformSettings() {
        return startupManager.getSettings();
    }

    @Override
    public String getPlatformLanguage() {
        MotechSettings settings = startupManager.getSettings();
        return (settings == null ? null : settings.getLanguage());
    }

    @Override
    public String getPlatformLanguage(String defaultValue) {
        String language = getPlatformLanguage();
        return (language == null ? defaultValue : language);
    }

    @Override
    public Locale getPlatformLocale() {
        String language = getPlatformLanguage();
        return (language == null ? Locale.getDefault() : new Locale(language));
    }
}
