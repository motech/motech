package org.motechproject.mds.dto;

import java.util.Set;

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
     * The set of the fields which will be displayed on the UI. It is constructed from selected fields, unselected fields
     * and advance settings of the entity.
     */
    private Set<String> visibleFields;

    /**
     * The set of the selected fields.
     */
    private Set<String> selectedFields;

    /**
     * The set of the unselected fields.
     */
    private Set<String> unselectedFields;

    public UserPreferencesDto(String className, String username, Integer gridRowsNumber, Set<String> visibleFields, Set<String> selectedFields, Set<String> unselectedFields) {
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

    public Set<String> getVisibleFields() {
        return visibleFields;
    }

    public void setVisibleFields(Set<String> visibleFields) {
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

    public Set<String> getSelectedFields() {
        return selectedFields;
    }

    public void setSelectedFields(Set<String> selectedFields) {
        this.selectedFields = selectedFields;
    }

    public Set<String> getUnselectedFields() {
        return unselectedFields;
    }

    public void setUnselectedFields(Set<String> unselectedFields) {
        this.unselectedFields = unselectedFields;
    }
}
