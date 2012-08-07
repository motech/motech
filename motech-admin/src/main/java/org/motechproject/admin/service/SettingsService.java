package org.motechproject.admin.service;

import org.motechproject.admin.settings.Settings;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface SettingsService {

    List<Settings> getSettings();

    List<Settings> getBundleSettings(long bundleId) throws IOException;

    void saveBundleSettings(Settings settings, long bundleId);

    void savePlatformSettings(Settings settings);

    void savePlatformSettings(List<Settings> settings);

    void saveSettingsFile(MultipartFile configFile);

    void addSettingsPath(String path);

    List<String> retrieveRegisteredBundleNames();

    List<String> getRawFilenames(long bundleId);

    void saveRawFile(MultipartFile file, String filename, long bundleId);
}
