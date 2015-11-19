package org.motechproject.mds.domain;

import org.motechproject.mds.dto.UserPreferencesDto;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.ForeignKeyAction;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.util.ArrayList;
import java.util.List;

/**
 * The <code>UserPreferences</code> class contains information about an entity user preferences. For example grid size
 * on the UI.
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE, detachable = "true")
public class UserPreferences {

    @PrimaryKey
    @Persistent
    private String username;

    @PrimaryKey
    @Persistent
    private String className;

    @Persistent
    private Integer gridRowsNumber;

    @Persistent(defaultFetchGroup = "true")
    @Join
    @Element(column = "selectedField",  deleteAction = ForeignKeyAction.CASCADE)
    private List<Field> selectedFields;

    @Persistent(defaultFetchGroup = "true")
    @Join
    @Element(column = "unselectedField",  deleteAction = ForeignKeyAction.CASCADE)
    private List<Field> unselectedFields;

    public UserPreferences() {
    }

    public UserPreferences(String username, String className, Integer gridRowsNumber, List<Field> selectedFields, List<Field> unselectedFields) {
        this.username = username;
        this.className = className;
        this.gridRowsNumber = gridRowsNumber;
        this.selectedFields = selectedFields;
        this.unselectedFields = unselectedFields;
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

    public Integer getGridRowsNumber() {
        return gridRowsNumber;
    }

    public void setGridRowsNumber(Integer gridRowsNumber) {
        this.gridRowsNumber = gridRowsNumber;
    }

    public List<Field> getSelectedFields() {
        if (selectedFields == null) {
            return new ArrayList<>();
        }
        return selectedFields;
    }

    public void setSelectedFields(List<Field> selectedFields) {
        this.selectedFields = selectedFields;
    }


    public List<Field> getUnselectedFields() {
        if (unselectedFields == null) {
            return new ArrayList<>();
        }
        return unselectedFields;
    }

    public void setUnselectedFields(List<Field> unselectedFields) {
        this.unselectedFields = unselectedFields;
    }

    public UserPreferencesDto toDto(List<String> displayableFields) {
        List<String> mergedFields = new ArrayList<>(displayableFields);
        List<String> selected = new ArrayList<>();
        List<String> unselected = new ArrayList<>();

        for (Field field : getUnselectedFields()) {
            if (mergedFields.contains(field.getName())) {
                mergedFields.remove(field.getName());
            }
            unselected.add(field.getName());
        }

        for (Field field : getSelectedFields()) {
            if (!mergedFields.contains(field.getName())) {
                mergedFields.add(field.getName());
            }
            selected.add(field.getName());
        }

        return new UserPreferencesDto(className, username, gridRowsNumber, mergedFields, selected, unselected);
    }

    public void selectField(Field field) {
        if (selectedFields == null) {
            selectedFields = new ArrayList<>();
        }
        selectedFields.add(field);

        // if field is selected then we must remove it from unselected list
        if (unselectedFields != null && unselectedFields.contains(field)) {
            unselectedFields.remove(field);
        }
    }

    public void unselectField(Field field) {
        if (unselectedFields == null) {
            unselectedFields = new ArrayList<>();
        }
        unselectedFields.add(field);

        // if field is unselected then we must remove it from selected list
        if (selectedFields != null && selectedFields.contains(field)) {
            selectedFields.remove(field);
        }
    }
}
