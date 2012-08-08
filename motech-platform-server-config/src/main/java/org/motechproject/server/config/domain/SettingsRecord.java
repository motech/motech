package org.motechproject.server.config.domain;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.server.config.settings.MotechSettings;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

@TypeDiscriminator("doc.type === 'SettingsRecord'")
@JsonIgnoreProperties(ignoreUnknown = true, value = { "couchDbProperties" })
public class SettingsRecord extends MotechBaseDataObject implements MotechSettings {

    private String language;
    private String statusMsgTimeout;

    private boolean cluster;
    private DateTime lastRun;
    private byte[] configFileChecksum;

    private Properties couchDbProperties;
    private Properties activemqProperties;
    private Properties quartzProperties;
    private Properties systemProperties;

    @Override
    public String getLanguage() {
        return language;
    }

    @Override
    public String getStatusMsgTimeout() {
        return statusMsgTimeout;
    }

    @Override
    public Properties getCouchDBProperties() {
        return couchDbProperties;
    }

    @Override
    public Properties getActivemqProperties() {
        return activemqProperties;
    }

    @Override
    public Properties getQuartzProperties() {
        return quartzProperties;
    }

    public void setCouchDbProperties(final Properties couchDbProperties) {
        this.couchDbProperties = couchDbProperties;
    }

    public void setActivemqProperties(final Properties activemqProperties) {
        this.activemqProperties = activemqProperties;
    }

    public void setQuartzProperties(final Properties quartzProperties) {
        this.quartzProperties = quartzProperties;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    public void setStatusMsgTimeout(final String statusMsgTimeout) {
        this.statusMsgTimeout = statusMsgTimeout;
    }

    public boolean isCluster() {
        return cluster;
    }

    public void setCluster(final boolean cluster) {
        this.cluster = cluster;
    }

    public DateTime getLastRun() {
        return lastRun;
    }

    public void setLastRun(final DateTime lastRun) {
        this.lastRun = lastRun;
    }

    public byte[] getConfigFileChecksum() {
        return configFileChecksum;
    }

    public void setConfigFileChecksum(final byte[] configFileChecksum) {
        this.configFileChecksum = configFileChecksum;
    }

    public void updateSettings(final MotechSettings settings) {
        setLanguage(settings.getLanguage());
        setStatusMsgTimeout(settings.getStatusMsgTimeout());
        setActivemqProperties(settings.getActivemqProperties());
        setQuartzProperties(settings.getQuartzProperties());
    }

    public void updateFromProperties(final Properties props) {
        if (activemqProperties == null || activemqProperties.isEmpty()) {
            activemqProperties = emptyActivemqProperties();
        }
        if (quartzProperties == null || quartzProperties.isEmpty()) {
            quartzProperties = emptyQuartzProperties();
        }

        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            String key = (String)entry.getKey();
            String value = (String)entry.getValue();

            if (MotechSettings.LANGUAGE.equals(key)) {
                setLanguage(value);
            } else if (MotechSettings.STATUS_MSG_TIMEOUT.equals(key)) {
                setStatusMsgTimeout(value);
            } else {
                for (Properties p : Arrays.asList(getActivemqProperties(), getQuartzProperties())) {
                    if (p.containsKey(key)) {
                        p.put(key, value);
                        break;
                    }
                }
            }
        }
    }

    private Properties emptyActivemqProperties() {
        Properties props = new Properties();
        props.put(AMQ_BROKER_URL, "");
        props.put(AMQ_CONCURRENT_CONSUMERS, "");
        props.put(AMQ_MAX_CONCURRENT_CONSUMERS, "");
        props.put(AMQ_MAX_REDELIVERIES, "");
        props.put(AMQ_QUEUE_EVENTS, "");
        props.put(AMQ_QUEUE_SCHEDULER, "");
        props.put(AMQ_REDELIVERY_DELAY_IN_MILLIS, "");
        return props;
    }

    private Properties emptyQuartzProperties() {
        Properties props = new Properties();
        props.put(QUARTZ_JOB_STORE_CLASS, "");
        props.put(QUARTZ_SCHEDULER_NAME, "");
        props.put(QUARTZ_THREAD_POOL_CLASS, "");
        props.put(QUARTZ_THREAD_POOL_THREAD_COUNT, "");
        return props;
    }
}
