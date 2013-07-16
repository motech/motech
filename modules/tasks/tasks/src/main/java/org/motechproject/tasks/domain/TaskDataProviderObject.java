package org.motechproject.tasks.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;

public class TaskDataProviderObject implements Serializable {
    private static final long serialVersionUID = 1767818631190935233L;

    private String displayName;
    private String type;
    private List<LookupFieldsParameter> lookupFields;
    private List<FieldParameter> fields;

    public TaskDataProviderObject() {
        this(null, null, null, null);
    }

    public TaskDataProviderObject(String displayName, String type, List<LookupFieldsParameter> lookupFields, List<FieldParameter> fields) {
        this.displayName = displayName;
        this.type = type;
        this.lookupFields = lookupFields == null ? new ArrayList<LookupFieldsParameter>(): lookupFields;
        this.fields = fields == null ? new ArrayList<FieldParameter>() : fields;
    }

    public boolean containsField(String fieldKey) {
        boolean found = false;

        for (FieldParameter parameter : getFields()) {
            if (equalsIgnoreCase(parameter.getFieldKey(), fieldKey)) {
                found = true;
                break;
            }
        }

        return found;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<LookupFieldsParameter> getLookupFields() {
        return lookupFields;
    }

    // Setter, with backwards compatibility
    public void setLookupFields(List<Object> lookupFields) {
        this.lookupFields.clear();
        if (lookupFields != null) {
            if (lookupFields.get(0) instanceof String) {
                LookupFieldsParameter param = new LookupFieldsParameter();
                param.setDisplayName("id");
                param.setFields(asList((String) lookupFields.get(0)));
                this.lookupFields.add(param);
            } else {
                for (Object o : lookupFields) {
                    LinkedHashMap<String, Object> map = (LinkedHashMap) o;
                    LookupFieldsParameter param = new LookupFieldsParameter();
                    param.setDisplayName(map.get("displayName").toString());
                    param.setFields((ArrayList<String>)map.get("fields"));
                    this.lookupFields.add(param);
                }
            }
        }
    }

    public List<FieldParameter> getFields() {
        return fields;
    }

    public void setFields(List<FieldParameter> fields) {
        this.fields.clear();

        if (fields != null) {
            this.fields.addAll(fields);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final TaskDataProviderObject other = (TaskDataProviderObject) obj;

        return Objects.equals(this.displayName, other.displayName) &&
                Objects.equals(this.type, other.type) &&
                Objects.equals(this.lookupFields, other.lookupFields) &&
                Objects.equals(this.fields, other.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayName, type, lookupFields, fields);
    }

    @Override
    public String toString() {
        return String.format("TaskDataProviderObject{displayName='%s', type='%s', lookupFields=%s, fields=%s}",
                displayName, type, lookupFields, fields);
    }
}