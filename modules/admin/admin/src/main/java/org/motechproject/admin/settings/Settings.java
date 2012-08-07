package org.motechproject.admin.settings;

import java.util.List;

public class Settings {

    private String section;
    private List<SettingsOption> settings;

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public List<SettingsOption> getSettings() {
        return settings;
    }

    public void setSettings(List<SettingsOption> settings) {
        this.settings = settings;
    }

    public Settings() {
        this(null, null);
    }

    public Settings(String section, List<SettingsOption> settings) {
        this.section = section;
        this.settings = settings;
    }
}
