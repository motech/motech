package org.motechproject.mds.config.impl;

import org.motechproject.mds.config.DeleteMode;
import org.motechproject.mds.config.MdsConfig;
import org.motechproject.mds.config.ModuleSettings;
import org.motechproject.mds.config.SettingsService;
import org.motechproject.mds.config.TimeUnit;
import org.motechproject.mds.domain.ConfigSettings;
import org.motechproject.mds.repository.AllConfigSettings;
import org.motechproject.mds.service.TrashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Properties;

import static org.motechproject.mds.util.Constants.Config.MDS_DEFAULT_GRID_SIZE;
import static org.motechproject.mds.util.Constants.Config.MDS_DELETE_MODE;
import static org.motechproject.mds.util.Constants.Config.MDS_EMPTY_TRASH;
import static org.motechproject.mds.util.Constants.Config.MDS_TIME_UNIT;
import static org.motechproject.mds.util.Constants.Config.MDS_TIME_VALUE;
import static org.motechproject.mds.util.Constants.Config.MODULE_FILE;

/**
 * Default implementation of {@link org.motechproject.mds.config.SettingsService} interface.
 */
@Service("settingsService")
public class SettingsServiceImpl implements SettingsService {

    private AllConfigSettings allConfigSettings;
    private MdsConfig mdsConfig;
    private TrashService trashService;

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
    public Integer getGridSize() {
        return getModuleSettings().getGridSize();
    }

    @Override
    @Transactional
    public void saveModuleSettings(ModuleSettings settings) {
        ConfigSettings configSetting = new ConfigSettings();
        configSetting.setEmptyTrash(settings.isEmptyTrash());
        configSetting.setAfterTimeUnit(settings.getTimeUnit());
        configSetting.setDeleteMode(settings.getDeleteMode());
        configSetting.setDefaultGridSize(settings.getGridSize());

        if (settings.getProperty(MDS_TIME_VALUE) != null) {
            configSetting.setAfterTimeValue(Integer.parseInt(settings.getProperty(MDS_TIME_VALUE)));
        }

        allConfigSettings.addOrUpdate(configSetting);

        if (trashService != null) {
            trashService.scheduleEmptyTrashJob();
        }
    }

    @Override
    @Transactional
    public ModuleSettings getModuleSettings() {

        ModuleSettings moduleSettings = new ModuleSettings();
        moduleSettings.putAll(getProperties());

        return moduleSettings;
    }

    @Override
    @Transactional
    public Properties getProperties() {
        ConfigSettings configSettings = allConfigSettings.retrieve("id", 1);
        Properties props = new Properties();
        if (configSettings != null) {
            props.put(MDS_TIME_VALUE, configSettings.getAfterTimeValue());
            props.put(MDS_EMPTY_TRASH, configSettings.getEmptyTrash());
            props.put(MDS_TIME_UNIT, configSettings.getAfterTimeUnit());
            props.put(MDS_DELETE_MODE, configSettings.getDeleteMode());
            props.put(MDS_DEFAULT_GRID_SIZE, configSettings.getDefaultGridSize());
        } else {
            props = mdsConfig.getProperties(MODULE_FILE);
        }
        return props;
    }

    @Autowired
    public void setAllConfigSettings(AllConfigSettings allConfigSettings) {
        this.allConfigSettings = allConfigSettings;
    }

    @Autowired
    public void setMdsConfig(MdsConfig mdsConfig) {
        this.mdsConfig = mdsConfig;
    }

    @Autowired(required = false)
    public void setTrashService(TrashService trashService) {
        this.trashService = trashService;
    }
}
