package org.motechproject.server.config.domain;

import org.joda.time.DateTime;

import java.io.IOException;
import java.security.DigestInputStream;
import java.util.Properties;

/**
 * Interface for main MOTECH settings management.
 */
public interface MotechSettings {

    String getLanguage();

    String getStatusMsgTimeout();

    LoginMode getLoginMode();

    String getProviderName();

    String getProviderUrl();

    String getServerUrl();

    String getServerHost();

    String getUploadSize();

    DateTime getLastRun();

    /**
     * Converts this MOTECH setting to {@code Properties}.
     *
     * @return this object as {@code Properties}
     */
    Properties asProperties();

    String getFilePath();

    String getConfigFileChecksum();

    /**
     * Checks whether platform is initialized.
     *
     * @return true if platform is initialized, false otherwise
     */
    boolean isPlatformInitialized();

    void setPlatformInitialized(boolean platformInitialized);

    void setLanguage(String language);

    void setLoginModeValue(String loginMode);

    void setProviderName(String providerName);

    void setProviderUrl(String providerUrl);

    void setStatusMsgTimeout(String statusMsgTimeout);

    void setLastRun(DateTime lastRun);

    void setServerUrl(String serverUrl);

    void setUploadSize(String uploadSize);

    void setConfigFileChecksum(String configFileChecksum);

    /**
     * Updates this object with given properties.
     *
     * @param props  properties to be applied
     */
    void updateFromProperties(Properties props);

    /**
     * Adds or updates given key-value pair within this object.
     *
     * @param key  the key of the pair
     * @param value  the value of the pair
     */
    void savePlatformSetting(String key, String value);

    /**
     * Loads the properties from given stream and stores them withing this object.
     *
     * @param dis  the source stream
     * @throws IOException when I/O error occurs
     */
    void load(DigestInputStream dis) throws IOException;

    /**
     * Updates settings with given information.
     *
     * @param configFileChecksum  the configuration file checksum to be set
     * @param filePath  the file path to be set
     * @param platformSettings  the platform settings to be add
     */
    void updateSettings(final String configFileChecksum, String filePath, Properties platformSettings);

    void setFilePath(String filePath);
}
