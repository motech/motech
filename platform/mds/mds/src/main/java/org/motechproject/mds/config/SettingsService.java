package org.motechproject.mds.config;

import java.util.Properties;

/**
 * The <code>SettingsService</code> is a service class responsible for handling MDS settings.
 * MDS cannot use the regular settings system, since it is based on MDS and would cause circular reference
 * issues..
 */
public interface SettingsService {

    DeleteMode getDeleteMode();

    Boolean isEmptyTrash();

    Integer getTimeValue();

    TimeUnit getTimeUnit();

    void saveModuleSettings(ModuleSettings settings);

    ModuleSettings getModuleSettings();

    Properties getProperties();

}
