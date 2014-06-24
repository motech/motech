package org.motechproject.mds.config;

import java.util.Properties;

/**
 * The <code>SettingsService</code> is a service class for
 * {@link org.motechproject.server.config.SettingsFacade}. Its main purpose is to create better
 * access to module settings for developers.
 */
public interface SettingsService {

    DeleteMode getDeleteMode();

    Boolean isEmptyTrash();

    Integer getTimeValue();

    TimeUnit getTimeUnit();

    void saveModuleSettings(ModuleSettings settings);

    ModuleSettings getModuleSettings();

    Properties getDataNucleusProperties();

}
