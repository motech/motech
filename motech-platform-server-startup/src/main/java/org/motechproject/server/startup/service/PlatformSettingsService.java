package org.motechproject.server.startup.service;

import org.motechproject.server.startup.settings.MotechSettings;

import java.util.Locale;

public interface PlatformSettingsService {
    MotechSettings getPlatformSettings();

    String getPlatformLanguage();

    String getPlatformLanguage(String defaultValue);

    Locale getPlatformLocale();
}
