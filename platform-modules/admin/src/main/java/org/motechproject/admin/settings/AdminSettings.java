package org.motechproject.admin.settings;

import java.util.List;

/**
 * <code>AdminSettings</code> class is used to store settings for the Admin module and
 * specifies whether these settings are read-only (by checking config source from bootstrap).
 */
public class AdminSettings {

    private List<Settings> settingsList;
    private boolean readOnly;

    /**
     * Constructor.
     *
     * @param settingsList the list of the Admin Module settings
     * @param readOnly indicates that the settings are read-only
     */
    public AdminSettings(List<Settings> settingsList, boolean readOnly) {
        this.settingsList = settingsList;
        this.readOnly = readOnly;
    }

    /**
     * @return the list of the Admin Module settings
     */
    public List<Settings> getSettingsList() {
        return settingsList;
    }

    /**
     * @param settingsList the list of the Admin Module settings
     */
    public void setSettingsList(List<Settings> settingsList) {
        this.settingsList = settingsList;
    }

    /**
     * @return true if the settings are read-only, false otherwise
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * @param readOnly whether these settings are read-only
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
}
