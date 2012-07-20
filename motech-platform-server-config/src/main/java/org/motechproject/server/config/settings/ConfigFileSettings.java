package org.motechproject.server.config.settings;

import java.io.IOException;
import java.net.URL;
import java.security.DigestInputStream;
import java.util.Properties;

public class ConfigFileSettings extends Properties implements MotechSettings {

    private byte[] md5checkSum;
    private URL fileURL;

    @Override
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

    public String getPath() {
        return getFileURL().getPath();
    }

    public synchronized void load(DigestInputStream inStream) throws IOException {
        super.load(inStream);
        md5checkSum = inStream.getMessageDigest().digest();
    }

    @Override
    public Properties getCouchDBProperties() {
        Properties couchProperties = new Properties();

        putPropertyIfNotNull(couchProperties, "host", getProperty(DB_HOST));
        putPropertyIfNotNull(couchProperties, "port", (getProperty(DB_PORT)));
        putPropertyIfNotNull(couchProperties, "username", getProperty(DB_USERNAME));
        putPropertyIfNotNull(couchProperties, "password", getProperty(DB_PASSWORD));
        putPropertyIfNotNull(couchProperties, "maxConnections", getProperty(DB_MAX_CONNECTIONS));
        putPropertyIfNotNull(couchProperties, "connectionTimeout", getProperty(DB_CONNECTION_TIMEOUT));
        putPropertyIfNotNull(couchProperties, "socketTimeout", getProperty(DB_SOCKET_TIMEOUT));

        return couchProperties;
    }

    @Override
    public Properties getActivemqProperties() {
        Properties activemqProperties = new Properties();

        putPropertyIfNotNull(activemqProperties, "queue.for.events", getProperty(AMQ_QUEUE_EVENTS));
        putPropertyIfNotNull(activemqProperties, "queue.for.scheduler", getProperty(AMQ_QUEUE_SCHEDULER));
        putPropertyIfNotNull(activemqProperties, "broker.url", getProperty(AMQ_BROKER_URL));
        putPropertyIfNotNull(activemqProperties, "maximumRedeliveries", getProperty(AMQ_MAX_REDELIVERIES));
        putPropertyIfNotNull(activemqProperties, "redeliveryDelayInMillis", getProperty(AMQ_REDELIVERY_DELAY_IN_MILLIS));
        putPropertyIfNotNull(activemqProperties, "concurrentConsumers", getProperty(AMQ_CONCURRENT_CONSUMERS));
        putPropertyIfNotNull(activemqProperties, "maxConcurrentConsumers", getProperty(AMQ_MAX_CONCURRENT_CONSUMERS));

        return activemqProperties;
    }

    @Override
    public Properties getQuartzProperties() {
        Properties quartzProperties = new Properties();

        putPropertyIfNotNull(quartzProperties, "org.quartz.scheduler.instanceName", getProperty(QUARTZ_SCHEDULER_NAME));
        putPropertyIfNotNull(quartzProperties, "org.quartz.threadPool.class", getProperty(QUARTZ_THREAD_POOL_CLASS));
        putPropertyIfNotNull(quartzProperties, "org.quartz.threadPool.threadCount", getProperty(QUARTZ_THREAD_POOL_THREAD_COUNT));
        putPropertyIfNotNull(quartzProperties, "org.quartz.jobStore.class", getProperty(QUARTZ_JOB_STORE_CLASS));

        return quartzProperties;
    }

    private static void putPropertyIfNotNull(Properties properties, String key, Object value) {
        if (value != null) {
            properties.put(key, value);
        }
    }
}
