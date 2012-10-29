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
    private byte[] configFileChecksum = new byte[0];

    private Properties couchDbProperties;
    private Properties activemqProperties = new Properties();
    private Properties quartzProperties;
    private Properties metricsProperties;
    private Properties schedulerProperties;

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

    @Override
    public Properties getMetricsProperties() {
        return metricsProperties;
    }

    public Properties getSchedulerProperties() {
        return schedulerProperties;
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

    public void setMetricsProperties(final Properties metricsProperties) {
        this.metricsProperties = metricsProperties;
    }

    public void setSchedulerProperties(final Properties schedulerProperties) {
        this.schedulerProperties = schedulerProperties;
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
        return Arrays.copyOf(configFileChecksum, configFileChecksum.length);
    }

    public void setConfigFileChecksum(final byte[] configFileChecksum) {
        this.configFileChecksum = Arrays.copyOf(configFileChecksum, configFileChecksum.length);
    }

    public void updateSettings(final MotechSettings settings) {
        setLanguage(settings.getLanguage());
        setStatusMsgTimeout(settings.getStatusMsgTimeout());
        setActivemqProperties(settings.getActivemqProperties());
        setQuartzProperties(settings.getQuartzProperties());
        setMetricsProperties(settings.getMetricsProperties());
        setSchedulerProperties(settings.getSchedulerProperties());
    }

    public void updateFromProperties(final Properties props) {
        if (quartzProperties == null || quartzProperties.isEmpty()) {
            quartzProperties = emptyQuartzProperties();
        }
        if (metricsProperties == null || metricsProperties.isEmpty()) {
            metricsProperties = emptyMetricsProperties();
        }
        if (schedulerProperties == null || schedulerProperties.isEmpty()) {
            schedulerProperties = emptySchedulerProperties();
        }

        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            String key = (String)entry.getKey();
            String value = (String)entry.getValue();

            switch (key) {
                case MotechSettings.LANGUAGE:
                    setLanguage(value);
                    break;
                case MotechSettings.STATUS_MSG_TIMEOUT:
                    setStatusMsgTimeout(value);
                    break;
                default:
                    for (Properties p : Arrays.asList(getQuartzProperties(), getMetricsProperties(), getSchedulerProperties())) {
                        if (p.containsKey(key)) {
                            p.put(key, value);
                            break;
                        }
                    }
                    break;
            }
        }
    }

    private Properties emptyQuartzProperties() {
        Properties props = new Properties();
        props.put(QUARTZ_JOB_STORE_CLASS, "");
        props.put(QUARTZ_SCHEDULER_NAME, "");
        props.put(QUARTZ_THREAD_POOL_CLASS, "");
        props.put(QUARTZ_THREAD_POOL_THREAD_COUNT, "");
        return props;
    }

    private Properties emptyMetricsProperties() {
        Properties props = new Properties();
        props.put(GRAPHITE_URL, "");
        return props;
    }

    private Properties emptySchedulerProperties() {
        Properties props = new Properties();
        props.put(SCHEDULER_URL, "");
        return props;
    }
}
