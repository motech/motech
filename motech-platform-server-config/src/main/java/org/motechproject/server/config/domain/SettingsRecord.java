package org.motechproject.server.config.domain;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.server.config.settings.MotechSettings;

@TypeDiscriminator("doc.type === 'SettingsRecord'")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SettingsRecord extends MotechBaseDataObject implements MotechSettings {

    private String language;
    private boolean cluster;
    private DateTime lastRun;
    private byte[] configFileChecksum;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isCluster() {
        return cluster;
    }

    public void setCluster(boolean cluster) {
        this.cluster = cluster;
    }

    public DateTime getLastRun() {
        return lastRun;
    }

    public void setLastRun(DateTime lastRun) {
        this.lastRun = lastRun;
    }

    public byte[] getConfigFileChecksum() {
        return configFileChecksum;
    }

    public void setConfigFileChecksum(byte[] configFileChecksum) {
        this.configFileChecksum = configFileChecksum;
    }

    public void updateSettings(MotechSettings settings) {
        setLanguage(settings.getLanguage());
    }
}
