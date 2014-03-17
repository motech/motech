package org.motechproject.mds.config;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Properties;

import static org.motechproject.mds.util.Constants.Config.DATANUCLEUS_FILE;
import static org.motechproject.mds.util.Constants.Config.MODULE_FILE;
import static org.motechproject.mds.util.Constants.Config.MODULE_SETTINGS_CHANGE;

/**
 * The <code>SettingsWrapper</code> is a wrapper class for
 * {@link org.motechproject.server.config.SettingsFacade}. Its main purpose is to create better
 * access to module settings for developers.
 */
public class SettingsWrapper {
    private SettingsFacade settingsFacade;
    private EventRelay eventRelay;

    public DeleteMode getDeleteMode() {
        return getModuleSettings().getDeleteMode();
    }

    public Boolean isEmptyTrash() {
        return getModuleSettings().isEmptyTrash();
    }

    public Integer getTimeValue() {
        return getModuleSettings().getTimeValue();
    }

    public TimeUnit getTimeUnit() {
        return getModuleSettings().getTimeUnit();
    }

    public void saveModuleSettings(ModuleSettings settings) {
        settingsFacade.saveConfigProperties(MODULE_FILE, settings);
        eventRelay.sendEventMessage(new MotechEvent(MODULE_SETTINGS_CHANGE));
    }

    public ModuleSettings getModuleSettings() {
        Properties properties = settingsFacade.getProperties(MODULE_FILE);

        ModuleSettings moduleSettings = new ModuleSettings();
        moduleSettings.putAll(properties);

        return moduleSettings;
    }

    public Properties getDataNucleusProperties() {
        return settingsFacade.getProperties(DATANUCLEUS_FILE);
    }

    @Autowired
    @Qualifier("mdsSettings")
    public void setSettingsFacade(SettingsFacade settingsFacade) {
        this.settingsFacade = settingsFacade;
    }

    @Autowired
    public void setEventRelay(EventRelay eventRelay) {
        this.eventRelay = eventRelay;
    }
}
