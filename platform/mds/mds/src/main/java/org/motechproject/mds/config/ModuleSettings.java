package org.motechproject.mds.config;

import java.util.Objects;

/**
 * The <code>ModuleSettings</code> contains the base module settings which are inside the
 * {@link org.motechproject.mds.util.Constants.Config#MODULE_FILE}. The getters and setters
 * inside this class always checks the given property and if it is incorrect then the default
 * value of the given property will be returned.
 */
public class ModuleSettings {
    public static final DeleteMode DEFAULT_DELETE_MODE = DeleteMode.TRASH;
    public static final Boolean DEFAULT_EMPTY_TRASH = false;
    public static final Integer DEFAULT_TIME_VALUE = 1;
    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.HOURS;
    public static final Integer DEFAULT_GRID_SIZE = 50;

    private DeleteMode deleteMode;
    private Boolean emptyTrash;
    private Integer timeValue;
    private TimeUnit timeUnit;
    private Integer gridSize;

    public ModuleSettings() {
    }

    public DeleteMode getDeleteMode() {
        if (deleteMode == null) {
            return DEFAULT_DELETE_MODE;
        }
        return deleteMode;
    }

    public void setDeleteMode(DeleteMode deleteMode) {
        this.deleteMode = deleteMode == null ? DEFAULT_DELETE_MODE : deleteMode;
    }

    public Boolean isEmptyTrash() {
        if (emptyTrash == null) {
            return DEFAULT_EMPTY_TRASH;
        }
        return emptyTrash;
    }

    public void setEmptyTrash(Boolean emptyTrash) {
        this.emptyTrash = emptyTrash == null ? DEFAULT_EMPTY_TRASH : emptyTrash;
    }

    public Integer getTimeValue() {
        if (timeValue == null) {
            return DEFAULT_TIME_VALUE;
        }
        return timeValue;
    }

    public void setTimeValue(Integer timeValue) {
        if (timeValue == null) {
            this.timeValue = DEFAULT_TIME_VALUE;
        } else {
            this.timeValue = timeValue < 1 ? DEFAULT_TIME_VALUE : timeValue;
        }
    }

    public TimeUnit getTimeUnit() {
        if (timeUnit == null) {
            return DEFAULT_TIME_UNIT;
        }
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit == null ? DEFAULT_TIME_UNIT : timeUnit;
    }

    public void setGridSize(Integer gridSize) {
        this.gridSize = gridSize == null ? DEFAULT_GRID_SIZE : gridSize;
    }

    public Integer getGridSize() {
        if (gridSize == null) {
            return DEFAULT_GRID_SIZE;
        }
        return gridSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDeleteMode(), isEmptyTrash(), getTimeValue(), getTimeUnit(), getGridSize());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final ModuleSettings other = (ModuleSettings) obj;

        return Objects.equals(this.getDeleteMode(), other.getDeleteMode())
                && Objects.equals(this.isEmptyTrash(), other.isEmptyTrash())
                && Objects.equals(this.getTimeValue(), other.getTimeValue())
                && Objects.equals(this.getTimeUnit(), other.getTimeUnit())
                && Objects.equals(this.getGridSize(), other.getGridSize());
    }
}
