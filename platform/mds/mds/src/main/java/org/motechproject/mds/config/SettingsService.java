package org.motechproject.mds.config;

/**
 * The <code>SettingsService</code> is a generic class, allowing access to all
 * MDS settings, as well as providing an ability to easily change these settings.
 */
public interface SettingsService {

    /**
     * Returns current setting of the Delete mode. Depending on its setting, deleting an MDS instance either
     * moves it to trash, or removes it permanently.
     *
     * @return current delete mode setting
     */
    DeleteMode getDeleteMode();

    /**
     * Returns current setting of the Empty trash, which informs whether automatic removal of instances is enabled.
     *
     * @return true, if setting is enabled, false otherwise
     */
    Boolean isEmptyTrash();

    /**
     * Together with {@link #getTimeUnit()} specifies frequency of the automatic removal of the instances.
     *
     * @return value as an integer
     */
    Integer getTimeValue();

    /**
     * Together with {@link #getTimeValue()} specifies frequency of the automatic removal of the instances.
     *
     * @return selected unit of time
     */
    TimeUnit getTimeUnit();

    /**
     * Returns current setting of the grid size.
     *
     * @return the size of the grid
     */
    Integer getGridSize();

    /**
     * Updates all MDS settings and performs necessary actions if required (eg. scheduling jobs, that remove
     * instances from trash).
     *
     * @param settings settings to save
     */
    void saveModuleSettings(ModuleSettings settings);

    /**
     * Retrieves all MDS settings.
     *
     * @return MDS settings
     */
    ModuleSettings getModuleSettings();
}
