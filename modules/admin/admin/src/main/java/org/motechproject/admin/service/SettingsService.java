package org.motechproject.admin.service;

import org.motechproject.admin.settings.Settings;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Settings service used to manage platform setting changes from Admin interface.
 */
public interface SettingsService {

    List<Settings> getSettings();

    List<Settings> getBundleSettings(long bundleId) throws IOException;

    void saveBundleSettings(Settings settings, long bundleId);

    InputStream exportConfig(String fileName) throws IOException;

    void savePlatformSettings(Settings settings);

    void savePlatformSettings(List<Settings> settings);

    void saveSettingsFile(MultipartFile configFile);

    void addSettingsPath(String path) throws IOException;

    List<String> retrieveRegisteredBundleNames();

    List<String> getRawFilenames(long bundleId);

    void saveRawFile(MultipartFile file, String filename, long bundleId);
}
