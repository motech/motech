package org.motechproject.mds.web;

import java.util.Map;

/**
 * The <code>DraftData</code> contains information that are used later for creating temporary
 * changes in field.
 */
public class DraftData {
    public static final String PATH = "path";
    public static final String FIELD_ID = "fieldId";
    public static final String VALUE = "value";
    public static final String TYPE_CLASS = "typeClass";
    public static final String DISPLAY_NAME = "displayName";
    public static final String NAME = "name";
    public static final String ADVANCED = "advanced";
    public static final String SECURITY = "security";
    public static final String FIELD = "field";

    public static final String ADD_NEW_INDEX = "$addNewIndex";
    public static final String REMOVE_INDEX = "$removeIndex";

    private boolean create;
    private boolean edit;
    private boolean remove;
    private Map<String, Object> values;

    public boolean isCreate() {
        return create;
    }

    public void setCreate(boolean create) {
        this.create = create;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public boolean isRemove() {
        return remove;
    }

    public void setRemove(boolean remove) {
        this.remove = remove;
    }

    public Object getValue(String key) {
        return values.get(key);
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public void setValues(Map<String, Object> values) {
        this.values = values;
    }

    public boolean isForField() {
        return getValue(FIELD_ID) != null;
    }

    public boolean isForAdvanced() {
        return getValue(ADVANCED) != null;
    }

    public boolean isForSecurity() {
        return getValue(SECURITY) != null;
    }

    public String getPath() {
        Object path = getValue(PATH);
        return (path == null) ? null : path.toString();
    }
}
