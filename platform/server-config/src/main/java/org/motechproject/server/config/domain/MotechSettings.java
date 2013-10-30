package org.motechproject.server.config.domain;

import org.joda.time.DateTime;

import java.io.IOException;
import java.security.DigestInputStream;
import java.util.Properties;

/**
 * Interface for main motech settings managment
 */

public interface MotechSettings {
    String SETTINGS_FILE_NAME = "motech-settings.conf";

    String AMQ_QUEUE_EVENTS = "jms.queue.for.events";
    String AMQ_QUEUE_SCHEDULER = "jms.queue.for.scheduler";
    String AMQ_BROKER_URL = "jms.broker.url";
    String AMQ_MAX_REDELIVERIES = "jms.maximumRedeliveries";
    String AMQ_REDELIVERY_DELAY_IN_MILLIS = "jms.redeliveryDelayInMillis";
    String AMQ_CONCURRENT_CONSUMERS = "jms.concurrentConsumers";
    String AMQ_MAX_CONCURRENT_CONSUMERS = "jms.maxConcurrentConsumers";

    String LANGUAGE = "system.language";
    String STATUS_MSG_TIMEOUT = "statusmsg.timeout";
    String SERVER_URL = "server.url";
    String UPLOAD_SIZE = "upload.size";

    String PROVIDER_NAME = "provider.name";
    String PROVIDER_URL = "provider.url";
    String LOGINMODE = "login.mode";

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
