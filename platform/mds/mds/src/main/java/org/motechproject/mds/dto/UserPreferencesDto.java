package org.motechproject.mds.dto;

import java.util.List;

/**
 * The <code>UserPreferencesDto</code> contains information about user preferences of an entity.
 */
public class UserPreferencesDto {

    private String className;

    private String username;

    private Integer gridRowsNumber;

    private List<String> visibleFields;

    public UserPreferencesDto(String className, String username, Integer gridRowsNumber, List<String> visibleFields) {
        this.className = className;
        this.username = username;
        this.gridRowsNumber = gridRowsNumber;
        this.visibleFields = visibleFields;
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
}
