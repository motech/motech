package org.motechproject.mds.dto;

import java.util.Objects;
import java.util.Properties;

/**
 * The <code>MdsSettingDto</code> contains module settings.
 */
public class MdsSettingsDto {
    public static final String MDS_PROPERTIES_FILE_NAME = "motech-mds.properties";
    public static final String MDS_DELETE_MODE_PROPERTY = "mds.deleteMode";
    public static final String MDS_EMPTY_TRASH_PROPERTY = "mds.emptyTrash";
    public static final String MDS_TIME_VALUE_PROPERTY = "mds.emptyTrash.afterTimeValue";
    public static final String MDS_TIME_UNIT_PROPERTY = "mds.emptyTrash.afterTimeUnit";

    private String deleteMode;
    private Boolean emptyTrash;
    private int timeValue;
    private String timeUnit;

    public MdsSettingsDto() {
        this(null, false, 1, null);
    }

    public MdsSettingsDto(Properties properties) {
        this(
                properties.getProperty(MDS_DELETE_MODE_PROPERTY, MDS_PROPERTIES_FILE_NAME),
                properties.getProperty(MDS_EMPTY_TRASH_PROPERTY, MDS_PROPERTIES_FILE_NAME),
                properties.getProperty(MDS_TIME_VALUE_PROPERTY, MDS_PROPERTIES_FILE_NAME),
                properties.getProperty(MDS_TIME_UNIT_PROPERTY, MDS_PROPERTIES_FILE_NAME)
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
        properties.put(MDS_DELETE_MODE_PROPERTY, deleteMode);
        properties.put(MDS_EMPTY_TRASH_PROPERTY, emptyTrash);
        properties.put(MDS_TIME_VALUE_PROPERTY, timeValue);
        properties.put(MDS_TIME_UNIT_PROPERTY, timeUnit);

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
