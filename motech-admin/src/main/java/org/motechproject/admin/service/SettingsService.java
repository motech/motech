package org.motechproject.admin.service;

import org.motechproject.admin.settings.BundleSettings;
import org.motechproject.admin.settings.SettingsOption;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface SettingsService {
    public List<SettingsOption> getSettings();

    List<BundleSettings> getBundleSettings(long bundleId) throws IOException;

    void saveBundleSettings(List<SettingsOption> options, long bundleId) throws IOException;

    void savePlatformSettings(List<SettingsOption> settingsOptions);

    void saveSetting(SettingsOption option);
}
