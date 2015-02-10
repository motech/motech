package org.motechproject.admin.internal.service;

import org.motechproject.admin.settings.AdminSettings;
import org.motechproject.admin.settings.Settings;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Settings service used to manage platform setting changes from the Admin UI.
 */
public interface SettingsService {

    /**
     * Retrieves platform settings.
     *
     * @return platform settings
     */
    AdminSettings getSettings();

    /**
     * Retrieves settings for bundle with given id.
     *
     * @param bundleId the id of the bundle we wish to load settings for
     * @return bundle settings
     * @throws IOException if any of the bundle properties files cannot be read
     */
    List<Settings> getBundleSettings(long bundleId) throws IOException;

    /**
     * Saves settings for bundle with the given id.
     *
     * @param settings the settings to be saved
     * @param bundleId the id of bundle we wish to save settings for
     */
    void saveBundleSettings(Settings settings, long bundleId);

    /**
     * Creates a file with platform settings and pack it into av zip file.
     *
     * @param fileName the name of zip file
     * @return InputStream that contains zip file
     * @throws IOException if platform settings file cannot be read
     */
    InputStream exportConfig(String fileName) throws IOException;

    /**
     * Saves given platform settings to the settings service.
     *
     * @param settings the settings to be saved
     */
    void savePlatformSettings(Settings settings);

    /**
     * Saves given list of platform settings to the settings service.
     *
     * @param settings the list of setting to be saved
     */
    void savePlatformSettings(List<Settings> settings);

    /**
     * Saves platform settings from given file.
     *
     * @param configFile the file with settings
     */
    void saveSettingsFile(MultipartFile configFile);

    /**
     * Adds a new config location and restarts the monitor.
     *
     * @param path the new config location
     * @throws IOException if cannot add file monitoring location
     */
    void addSettingsPath(String path) throws IOException;

    /**
     * Retrieves list of bundle names which have settings.
     *
     * @return list of bundle names
     */
    List<String> retrieveRegisteredBundleNames();

    /**
     * Looks for all registered raw data properties within bundle.
     *
     * @param bundleId the id of bundle we wish to check
     * @return list of filenames that register raw config for specified bundle
     */
    List<String> getRawFilenames(long bundleId);

    /**
     * Saves raw file to the setting of bundle with given id.
     *
     * @param file the file with Raw JSON data to persist
     * @param filename resource filename
     * @param bundleId the id of the bundle for which we wish
     */
    void saveRawFile(MultipartFile file, String filename, long bundleId);
}
