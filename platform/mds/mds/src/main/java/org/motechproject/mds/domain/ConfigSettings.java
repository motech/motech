package org.motechproject.mds.domain;

import org.motechproject.mds.config.DeleteMode;
import org.motechproject.mds.config.TimeUnit;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PersistenceCapable;

import static org.motechproject.mds.util.Constants.Util.TRUE;

/**
 * The <code>ConfigSettings</code> class represents Data Services settings, that
 * can be adjusted by users via UI.
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE, detachable = TRUE)
public class ConfigSettings {

    @PrimaryKey
    @Persistent
    private Long id;

    @Persistent
    private DeleteMode deleteMode;

    @Persistent
    private boolean emptyTrash;

    @Persistent
    private int afterTimeValue;

    @Persistent
    private TimeUnit afterTimeUnit;

    @Persistent
    private int defaultGridSize;

    @Persistent
    private boolean refreshModuleAfterTimeout;

    public ConfigSettings() {
        this(DeleteMode.TRASH, false, 1, TimeUnit.HOURS, 10, false);
    }

    public ConfigSettings(DeleteMode deleteMode, boolean emptyTrash, int afterTimeValue, TimeUnit afterTimeUnit, int defaultGridSize, boolean refreshModuleAfterTimeout) {
        this.deleteMode = deleteMode;
        this.emptyTrash = emptyTrash;
        this.afterTimeValue = afterTimeValue;
        this.afterTimeUnit = afterTimeUnit;
        this.defaultGridSize = defaultGridSize;
        this.refreshModuleAfterTimeout = refreshModuleAfterTimeout;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getEmptyTrash() {
        return emptyTrash;
    }

    public void setEmptyTrash(boolean emptyTrash) {
        this.emptyTrash = emptyTrash;
    }

    public DeleteMode getDeleteMode() {
        return deleteMode;
    }

    public void setDeleteMode(DeleteMode deleteMode) {
        this.deleteMode = deleteMode;
    }

    public int getAfterTimeValue() {
        return afterTimeValue;
    }

    public void setAfterTimeValue(int afterTimeValue) {
        this.afterTimeValue = afterTimeValue;
    }

    public TimeUnit getAfterTimeUnit() {
        return afterTimeUnit;
    }

    public void setAfterTimeUnit(TimeUnit afterTimeUnit) {
        this.afterTimeUnit = afterTimeUnit;
    }

    public int getDefaultGridSize() {
        return defaultGridSize;
    }

    public void setDefaultGridSize(int defaultGridSize) {
        this.defaultGridSize = defaultGridSize;
    }

    public boolean isRefreshModuleAfterTimeout() {
        return refreshModuleAfterTimeout;
    }

    public void setRefreshModuleAfterTimeout(boolean refreshModuleAfterTimeout) {
        this.refreshModuleAfterTimeout = refreshModuleAfterTimeout;
    }

}
