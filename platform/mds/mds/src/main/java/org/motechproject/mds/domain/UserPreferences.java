package org.motechproject.mds.domain;

import org.motechproject.mds.dto.UserPreferencesDto;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.ForeignKeyAction;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.util.HashSet;
import java.util.Set;

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
    private Set<Field> selectedFields;

    @Persistent(defaultFetchGroup = "true")
    @Join
    @Element(column = "unselectedField",  deleteAction = ForeignKeyAction.CASCADE)
    private Set<Field> unselectedFields;

    public UserPreferences() {
        this(null, null, null, null, null);
    }

    public UserPreferences(String username, String className, Integer gridRowsNumber, Set<Field> selectedFields, Set<Field> unselectedFields) {
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

    public Set<Field> getSelectedFields() {
        if (selectedFields == null) {
            return new HashSet<>();
        }
        return selectedFields;
    }

    public void setSelectedFields(Set<Field> selectedFields) {
        this.selectedFields = selectedFields;
    }

    public Set<Field> getUnselectedFields() {
        if (unselectedFields == null) {
            return new HashSet<>();
        }
        return unselectedFields;
    }

    public void setUnselectedFields(Set<Field> unselectedFields) {
        this.unselectedFields = unselectedFields;
    }

    public UserPreferencesDto toDto(Set<String> displayableFields) {
        Set<String> mergedFields = new HashSet<>(displayableFields);
        Set<String> selected = new HashSet<>();
        Set<String> unselected = new HashSet<>();

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
            selectedFields = new HashSet<>();
        }
        selectedFields.add(field);

        // if field is selected then we must remove it from unselected
        if (unselectedFields != null && unselectedFields.contains(field)) {
            unselectedFields.remove(field);
        }
    }

    public void unselectField(Field field) {
        if (unselectedFields == null) {
            unselectedFields = new HashSet<>();
        }
        unselectedFields.add(field);

        // if field is unselected then we must remove it from selected
        if (selectedFields != null && selectedFields.contains(field)) {
            selectedFields.remove(field);
        }
    }
}
