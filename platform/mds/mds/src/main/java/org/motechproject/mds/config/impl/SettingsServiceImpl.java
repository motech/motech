package org.motechproject.mds.config.impl;

import org.apache.commons.lang.StringUtils;
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
        configSetting.setAfterTimeValue(settings.getTimeValue());

        allConfigSettings.addOrUpdate(configSetting);

        if (trashService != null) {
            trashService.scheduleEmptyTrashJob();
        }
    }

    @Override
    @Transactional
    public ModuleSettings getModuleSettings() {
        ConfigSettings configSettings = allConfigSettings.retrieve("id", 1);
        ModuleSettings moduleSettings;

        if (configSettings != null) {
            moduleSettings = new ModuleSettings();
            moduleSettings.setDeleteMode(configSettings.getDeleteMode());
            moduleSettings.setEmptyTrash(configSettings.getEmptyTrash());
            moduleSettings.setTimeValue(configSettings.getAfterTimeValue());
            moduleSettings.setTimeUnit(configSettings.getAfterTimeUnit());
            moduleSettings.setGridSize(configSettings.getDefaultGridSize());
        } else {
            moduleSettings = getSettingsFromFile();
        }

        return moduleSettings;
    }

    private ModuleSettings getSettingsFromFile() {
        Properties props = mdsConfig.getProperties(MODULE_FILE);

        ModuleSettings moduleSettings = new ModuleSettings();
        moduleSettings.setDeleteMode(StringUtils.isNotBlank(props.getProperty(MDS_DELETE_MODE)) ? DeleteMode.fromString(props.getProperty(MDS_DELETE_MODE)) : null);
        moduleSettings.setEmptyTrash(StringUtils.isNotBlank(props.getProperty(MDS_EMPTY_TRASH)) ? Boolean.parseBoolean(props.getProperty(MDS_EMPTY_TRASH)) : null);
        moduleSettings.setTimeValue(StringUtils.isNotBlank(props.getProperty(MDS_TIME_VALUE)) ? Integer.parseInt(props.getProperty(MDS_TIME_VALUE)) : null);
        moduleSettings.setTimeUnit(StringUtils.isNotBlank(props.getProperty(MDS_TIME_UNIT)) ? TimeUnit.fromString(props.getProperty(MDS_TIME_UNIT)) : null);
        moduleSettings.setGridSize(StringUtils.isNotBlank(props.getProperty(MDS_DEFAULT_GRID_SIZE)) ? Integer.parseInt(props.getProperty(MDS_DEFAULT_GRID_SIZE)) : null);

        return moduleSettings;
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
