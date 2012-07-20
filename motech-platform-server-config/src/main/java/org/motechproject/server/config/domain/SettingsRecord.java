package org.motechproject.server.config.domain;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.server.config.settings.MotechSettings;

import java.util.Properties;

@TypeDiscriminator("doc.type === 'SettingsRecord'")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SettingsRecord extends MotechBaseDataObject implements MotechSettings {

    private String language;
    private boolean cluster;
    private DateTime lastRun;
    private byte[] configFileChecksum;
    private Properties activemqProperties;
    private Properties quartzProperties;

    @Override
    public String getLanguage() {
        return language;
    }

    @Override
    public Properties getActivemqProperties() {
        return activemqProperties;
    }

    @Override
    public Properties getQuartzProperties() {
        return quartzProperties;
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
        setActivemqProperties(settings.getActivemqProperties());
        setQuartzProperties(settings.getQuartzProperties());
    }

}
