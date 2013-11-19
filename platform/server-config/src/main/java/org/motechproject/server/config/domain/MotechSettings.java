package org.motechproject.server.config.domain;

import org.joda.time.DateTime;

import java.io.IOException;
import java.security.DigestInputStream;
import java.util.Properties;

/**
 * Interface for main motech settings managment
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

    Properties getActivemqProperties();

    DateTime getLastRun();

    Properties getPlatformSettings();

    String getFilePath();

    byte[] getConfigFileChecksum();

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

    void setConfigFileChecksum(byte[] configFileChecksum);

    void updateFromProperties(Properties props);

    void savePlatformSetting(String key, String value);

    void load(DigestInputStream dis) throws IOException;

    void updateSettings(SettingsRecord settingsRecord);

    void setFilePath(String filePath);
}
