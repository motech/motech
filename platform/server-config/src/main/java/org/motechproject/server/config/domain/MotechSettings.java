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

    String getJmxHost();

    String getJmxBroker();

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

    void setJmxHost(String jmxHost);

    void setJmxBroker(String jmxBroker);

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
     * @param platformSettings the platform settings to be add
     */
    void updateSettings(final String configFileChecksum, String filePath, Properties platformSettings);

    void setFilePath(String filePath);

    boolean getEmailRequired();

    void setEmailRequired(String emailRequired);

    /**
     * Gets the http session timeout for Motech users. Users will be logged out after reaching this timeout.
     * This value is specified in seconds. A negative value specifies that sessions should never time out.
     * @return the http session timeout, in seconds
     */
    Integer getSessionTimeout();

    /**
     * Sets the http session timeout for Motech users. Users will be logged out after reaching this timeout.
     * This value is specified in seconds. A negative value specifies that sessions should never time out.
     * @param sessionTimeout the http session timeout, in seconds
     */
    void setSessionTimeout(Integer sessionTimeout);

    /**
     * Returns the name of the password validator. The validator with that name will be retrieved by web-security
     * for validation of new password.
     * @return the name of the validator
     */
    String getPasswordValidator();

    /**
     * Sets the name of the password validator. The validator with that name will be retrieved by web-security
     * for validation of new password.
     * @param validator the name of the validator
     */
    void setPasswordValidator(String validator);

    /**
     * Returns the minimal length of user passwords in MOTECH.
     * @return the minimal length of the password, 0 or less means no minimal length
     */
    Integer getMinPasswordLength();

    /**
     * Sets the minimal length of user passwords in MOTECH.
     * @param minPasswordLength the minimal length of the password, 0 or less means no minimal length
     */
    void setMinPasswordLength(Integer minPasswordLength);
}
