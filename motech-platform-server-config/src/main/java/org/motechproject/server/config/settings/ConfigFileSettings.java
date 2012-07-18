package org.motechproject.server.config.settings;

import java.io.IOException;
import java.net.URL;
import java.security.DigestInputStream;
import java.util.Properties;

public class ConfigFileSettings extends Properties implements MotechSettings {

    public static final String DB_HOST = "db.host";
    public static final String DB_PORT = "db.port";
    public static final String DB_USERNAME = "db.user";
    public static final String DB_PASSWORD = "db.password";
    public static final String LANGUAGE = "language";

    private byte[] md5checkSum;
    private URL fileURL;

    public String getDbHost() {
        return getProperty(DB_HOST);
    }

    public String getDbPort() {
        return (getProperty(DB_PORT));
    }

    public String getDbUsername() {
        return getProperty(DB_USERNAME);
    }

    public String getDbPassword() {
        return getProperty(DB_PASSWORD);
    }

    public String getLanguage() {
        return getProperty(LANGUAGE);
    }

    public byte[] getMd5checkSum() {
        return md5checkSum;
    }

    public URL getFileURL() {
        return fileURL;
    }

    public void setFileURL(URL fileURL) {
        this.fileURL = fileURL;
    }

    public synchronized void load(DigestInputStream inStream) throws IOException {
        super.load(inStream);
        md5checkSum = inStream.getMessageDigest().digest();
    }

    public Properties getCouchProperties() {
        Properties couchProperties = new Properties();

        putPropertyIfNotNull(couchProperties, "host", getDbHost());
        putPropertyIfNotNull(couchProperties, "port", getDbPort());
        putPropertyIfNotNull(couchProperties, "username", getDbUsername());
        putPropertyIfNotNull(couchProperties, "password", getDbPassword());
        // TODO: more props

        return couchProperties;
    }

    private static void putPropertyIfNotNull(Properties properties, String key, Object value) {
        if (value != null) {
            properties.put(key, value);
        }
    }
}
