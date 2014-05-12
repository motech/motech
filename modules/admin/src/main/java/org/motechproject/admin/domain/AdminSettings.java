package org.motechproject.admin.domain;

import org.motechproject.admin.settings.Settings;

import java.util.List;

/**
 * <code>AdminSettings</code> class is used to store settings for Admin module and
 * specifies that these settings are read-only (by checking config source from bootstrap)
 */
public class AdminSettings {
    private List<Settings> settingsList;
    private boolean readOnly;

    public AdminSettings(List<Settings> settingsList, boolean readOnly) {
        this.settingsList = settingsList;
        this.readOnly = readOnly;
    }

    public List<Settings> getSettingsList() {
        return settingsList;
    }

    public void setSettingsList(List<Settings> settingsList) {
        this.settingsList = settingsList;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
}
