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
    @Element(column = "visibleField",  deleteAction = ForeignKeyAction.DEFAULT)
    private List<Field> visibleFields;

    public UserPreferences() {
    }

    public UserPreferences(String username, String className, List<Field> visibleFields) {
        this.username = username;
        this.className = className;
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

    public Integer getGridRowsNumber() {
        return gridRowsNumber;
    }

    public void setGridRowsNumber(Integer gridRowsNumber) {
        this.gridRowsNumber = gridRowsNumber;
    }

    public List<Field> getVisibleFields() {
        if (visibleFields == null) {
            return new ArrayList<>();
        }
        return visibleFields;
    }

    public void setVisibleFields(List<Field> visibleFields) {
        this.visibleFields = visibleFields;
    }

    public UserPreferencesDto toDto() {
        List<String> fields = new ArrayList<>();
        for (Field field : getVisibleFields()) {
            fields.add(field.getName());
        }
        return new UserPreferencesDto(className, username, gridRowsNumber, fields);
    }

    public void addField(Field field) {
        if (visibleFields == null) {
            visibleFields = new ArrayList<>();
        }
        visibleFields.add(field);
    }

    public void removeField(Field field) {
        if (visibleFields != null && visibleFields.contains(field)) {
            visibleFields.remove(field);
        }
    }
}
