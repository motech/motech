package org.motechproject.mds.dto;

import java.util.List;

/**
 * The <code>UserPreferencesDto</code> contains information about user preferences of an entity.
 */
public class UserPreferencesDto {

    /**
     * The entity class name.
     */
    private String className;

    /**
     * Username of the preferences owner.
     */
    private String username;

    /**
     * The size of the grid.
     */
    private Integer gridRowsNumber;

    /**
     * The list of the fields which will be displayed on the UI. It is constructed from selected fields, unselected fields
     * and advance settings of the entity.
     */
    private List<String> visibleFields;

    /**
     * The list of the selected fields.
     */
    private List<String> selectedFields;

    /**
     * The list of the unselected fields.
     */
    private List<String> unselectedFields;

    public UserPreferencesDto(String className, String username, Integer gridRowsNumber, List<String> visibleFields, List<String> selectedFields, List<String> unselectedFields) {
        this.className = className;
        this.username = username;
        this.gridRowsNumber = gridRowsNumber;
        this.visibleFields = visibleFields;
        this.selectedFields = selectedFields;
        this.unselectedFields = unselectedFields;
    }

    public Integer getGridRowsNumber() {
        return gridRowsNumber;
    }

    public void setGridRowsNumber(Integer gridRowsNumber) {
        this.gridRowsNumber = gridRowsNumber;
    }

    public List<String> getVisibleFields() {
        return visibleFields;
    }

    public void setVisibleFields(List<String> visibleFields) {
        this.visibleFields = visibleFields;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<String> getSelectedFields() {
        return selectedFields;
    }

    public void setSelectedFields(List<String> selectedFields) {
        this.selectedFields = selectedFields;
    }

    public List<String> getUnselectedFields() {
        return unselectedFields;
    }

    public void setUnselectedFields(List<String> unselectedFields) {
        this.unselectedFields = unselectedFields;
    }
}
