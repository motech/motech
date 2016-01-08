package org.motechproject.admin.settings;

import java.util.List;

/**
 * Class for storing settings values, it is representing one settings section
 * on Admin settings UI.
 */
public class Settings {

    private String section;
    private List<SettingsOption> settings;

    /**
     * @return the name of this settings section
     */
    public String getSection() {
        return section;
    }

    /**
     * @param section the name of this settings section
     */
    public void setSection(String section) {
        this.section = section;
    }

    /**
     * @return list of settings options in this section
     */
    public List<SettingsOption> getSettings() {
        return settings;
    }

    /**
     * @param settings list of settings options in this section
     */
    public void setSettings(List<SettingsOption> settings) {
        this.settings = settings;
    }

    public Settings() {
        this(null, null);
    }

    /**
     * @param section the name of this settings section
     * @param settings list of settings options in this section
     */
    public Settings(String section, List<SettingsOption> settings) {
        this.section = section;
        this.settings = settings;
    }
}
