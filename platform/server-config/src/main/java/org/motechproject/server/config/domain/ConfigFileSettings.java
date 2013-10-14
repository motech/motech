package org.motechproject.server.config.domain;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.DigestInputStream;
import java.util.Arrays;
import java.util.Properties;

/**
 * Holder class for config settings
 */
public class ConfigFileSettings implements MotechSettings {

    private byte[] md5checkSum;
    private URL fileURL;
    private Properties motechSettings = new Properties();
    private Properties activemq = new Properties();

    public ConfigFileSettings() {
    }

    public ConfigFileSettings(Properties motechSettings, Properties activemqSettings) {
        this.motechSettings.putAll(motechSettings);
        this.activemq.putAll(activemqSettings);
    }

    @Override
    public String getLanguage() {
        return motechSettings.getProperty(LANGUAGE);
    }

    @Override
    public String getStatusMsgTimeout() {
        return motechSettings.getProperty(STATUS_MSG_TIMEOUT);
    }

    @Override
    public LoginMode getLoginMode() {
        return LoginMode.valueOf(motechSettings.getProperty(LOGINMODE));
    }

    @Override
    public String getProviderName() {
        return motechSettings.getProperty(PROVIDER_NAME);
    }

    @Override
    public String getProviderUrl() {
        return motechSettings.getProperty(PROVIDER_URL);
    }

    @Override
    public String getServerUrl() {
        return new MotechURL(motechSettings.getProperty(SERVER_URL)).toString();
    }


    @Override
    public String getServerHost() {
        return new MotechURL(motechSettings.getProperty(SERVER_URL)).getHost();
    }


    public String getUploadSize() {
        return motechSettings.getProperty(UPLOAD_SIZE);
    }

    public byte[] getMd5checkSum() {
        return Arrays.copyOf(md5checkSum, md5checkSum.length);
    }

    public URL getFileURL() {
        return fileURL;
    }

    public void setFileURL(URL fileURL) {
        this.fileURL = fileURL;
    }

    public String getPath() {
        return getFileURL().getPath();
    }

    public synchronized void load(DigestInputStream inStream) throws IOException {
        motechSettings.load(inStream);
    }

    public synchronized void loadActiveMq(InputStream is) throws IOException {
        activemq.load(is);
    }

    @Override
    public Properties getActivemqProperties() {
        Properties activemqProperties = new Properties();
        activemqProperties.putAll(activemq);
        return activemqProperties;
    }

    @Override
    public Properties getSchedulerProperties() {
        Properties schedulerProperties = new Properties();

        putPropertyIfNotNull(schedulerProperties, SCHEDULER_URL, motechSettings.getProperty(SCHEDULER_URL));

        return schedulerProperties;
    }

    private static void putPropertyIfNotNull(Properties properties, String key, Object value) {
        if (value != null) {
            properties.put(key, value);
        }
    }

    public void setMd5checksum(byte[] digest) {
        this.md5checkSum = Arrays.copyOf(digest, digest.length);
    }

    public void saveMotechSetting(String key, String value) {
        if (value != null) {
            motechSettings.put(key, value);
        }
    }

    public void storeMotechSettings() throws IOException {
        File file = new File(getPath() + File.separator + MotechSettings.SETTINGS_FILE_NAME);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            motechSettings.store(fileOutputStream, null);
        }
    }

    public Properties getAll() {
        Properties copy = new Properties();
        copy.putAll(motechSettings);
        copy.putAll(activemq);

        return copy;
    }

    public void saveActiveMqSetting(String key, String value) {
        activemq.put(key, value);
    }

    public Properties getMotechSettings() {
        return motechSettings;
    }

    public void storeActiveMqSettings() throws IOException {
        File file = new File(getPath() + File.separator + MotechSettings.ACTIVEMQ_FILE_NAME);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            activemq.store(fileOutputStream, null);
        }


    }
}
