package org.motechproject.mds.config.impl;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.mds.config.DeleteMode;
import org.motechproject.mds.config.ModuleSettings;
import org.motechproject.mds.config.SettingsService;
import org.motechproject.mds.config.TimeUnit;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Properties;

import static org.motechproject.mds.util.Constants.Config.DATANUCLEUS_FILE;
import static org.motechproject.mds.util.Constants.Config.MODULE_FILE;
import static org.motechproject.mds.util.Constants.Config.MODULE_SETTINGS_CHANGE;

/**
 * Default implementation of {@link org.motechproject.mds.config.SettingsService} interface.
 */
@Service("settingsService")
public class SettingsServiceImpl implements SettingsService {
    private SettingsFacade settingsFacade;
    private EventRelay eventRelay;

    @Override
    public DeleteMode getDeleteMode() {
        return getModuleSettings().getDeleteMode();
    }

    @Override
    public Boolean isEmptyTrash() {
        return getModuleSettings().isEmptyTrash();
    }

    @Override
    public Integer getTimeValue() {
        return getModuleSettings().getTimeValue();
    }

    @Override
    public TimeUnit getTimeUnit() {
        return getModuleSettings().getTimeUnit();
    }

    @Override
    public void saveModuleSettings(ModuleSettings settings) {
        settingsFacade.saveConfigProperties(MODULE_FILE, settings);
        eventRelay.sendEventMessage(new MotechEvent(MODULE_SETTINGS_CHANGE));
    }

    @Override
    public ModuleSettings getModuleSettings() {
        Properties properties = settingsFacade.getProperties(MODULE_FILE);

        ModuleSettings moduleSettings = new ModuleSettings();
        moduleSettings.putAll(properties);

        return moduleSettings;
    }

    @Override
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
