package org.motechproject.mds.dto;

import java.util.Objects;
import java.util.Properties;

import static org.motechproject.mds.constants.Constants.Config;

/**
 * The <code>MdsSettingDto</code> contains module settings.
 */
public class MdsSettingsDto {
    private String deleteMode;
    private Boolean emptyTrash;
    private int timeValue;
    private String timeUnit;

    public MdsSettingsDto() {
        this(null, false, 1, null);
    }

    public MdsSettingsDto(Properties properties) {
        this(
                properties.getProperty(Config.MDS_DELETE_MODE, Config.MODULE_FILE),
                properties.getProperty(Config.MDS_EMPTY_TRASH, Config.MODULE_FILE),
                properties.getProperty(Config.MDS_TIME_VALUE, Config.MODULE_FILE),
                properties.getProperty(Config.MDS_TIME_UNIT, Config.MODULE_FILE)
        );
    }

    public MdsSettingsDto(String deleteMode, String emptyTrash, String timeValue, String timeUnit) {
        this.deleteMode = deleteMode;
        this.emptyTrash = Boolean.parseBoolean(emptyTrash);
        this.timeValue = Integer.parseInt(timeValue);
        this.timeUnit = timeUnit;
    }

    public MdsSettingsDto(String deleteMode, Boolean emptyTrash, int timeValue, String timeUnit) {
        this.deleteMode = deleteMode;
        this.emptyTrash = emptyTrash;
        this.timeValue = timeValue;
        this.timeUnit = timeUnit;
    }

    public Properties toProperties() {
        Properties properties = new Properties();
        properties.put(Config.MDS_DELETE_MODE, deleteMode);
        properties.put(Config.MDS_EMPTY_TRASH, emptyTrash);
        properties.put(Config.MDS_TIME_VALUE, timeValue);
        properties.put(Config.MDS_TIME_UNIT, timeUnit);

        return properties;
    }

    public String getDeleteMode() {
        return deleteMode;
    }

    public void setDeleteMode(String deleteMode) {
        this.deleteMode = deleteMode;
    }

    public Boolean getEmptyTrash() {
        return emptyTrash;
    }

    public void setEmptyTrash(Boolean emptyTrash) {
        this.emptyTrash = emptyTrash;
    }

    public int getTimeValue() {
        return timeValue;
    }

    public void setTimeValue(int timeValue) {
        this.timeValue = timeValue;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(deleteMode, emptyTrash, timeValue, timeUnit);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final MdsSettingsDto other = (MdsSettingsDto) obj;

        return compareFields(other);
    }

    @Override
    public String toString() {
        return String.format(
                "MdsSettingsDto{deleteMode='%s', emptyTrash='%s', timeValue='%s', timeUnit='%s'}",
                deleteMode, emptyTrash, timeValue, timeUnit);
    }

    private Boolean compareFields(MdsSettingsDto other) {
        if (!Objects.equals(this.deleteMode, other.deleteMode)) {
            return false;
        } else if (!Objects.equals(this.emptyTrash, other.emptyTrash)) {
            return false;
        } else if (!Objects.equals(this.timeValue, other.timeValue)) {
            return false;
        } else if (!Objects.equals(this.timeUnit, other.timeUnit)) {
            return false;
        }

        return true;
    }
}
