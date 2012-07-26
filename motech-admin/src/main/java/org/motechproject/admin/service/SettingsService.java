package org.motechproject.admin.service;

import org.motechproject.admin.settings.BundleSettings;
import org.motechproject.admin.settings.SettingsOption;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface SettingsService {

    List<SettingsOption> getSettings();

    List<BundleSettings> getBundleSettings(long bundleId) throws IOException;

    void saveBundleSettings(BundleSettings settings, long bundleId);

    void savePlatformSettings(List<SettingsOption> settingsOptions);

    void saveSettingsFile(MultipartFile configFile);

    void addSettingsPath(String path);

    List<String> retrieveRegisteredBundleNames();
}
