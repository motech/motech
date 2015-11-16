package org.motechproject.mds.web.domain;

/**
 * The <code>UserPreferencesFields</code> contains data about visible fields update.
 */
public class UserPreferencesFields {

    private String field;

    private String action;

    public UserPreferencesFields() {}

    public UserPreferencesFields(String field, String action) {
        this.field = field;
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

}
