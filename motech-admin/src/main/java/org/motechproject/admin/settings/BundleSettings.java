package org.motechproject.admin.settings;

import java.util.List;

public class BundleSettings {

    private String filename;
    private List<SettingsOption> settings;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public List<SettingsOption> getSettings() {
        return settings;
    }

    public void setSettings(List<SettingsOption> settings) {
        this.settings = settings;
    }

    public BundleSettings() {
        // default constructor
    }

    public BundleSettings(String filename, List<SettingsOption> settings) {
        this.filename = filename;
        this.settings = settings;
    }
}
