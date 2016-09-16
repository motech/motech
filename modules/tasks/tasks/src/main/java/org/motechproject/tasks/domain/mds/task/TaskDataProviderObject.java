package org.motechproject.tasks.domain.mds.task;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.annotations.Cascade;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.util.SecurityMode;
import org.motechproject.tasks.constants.TasksRoles;
import org.motechproject.tasks.dto.FieldParameterDto;
import org.motechproject.tasks.dto.LookupFieldsParameterDto;
import org.motechproject.tasks.dto.TaskDataProviderObjectDto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;

/**
 * Represents a single object of the task data provider. It describes fields and lookups of an entity that is used as a
 * data store in the task module.
 */
@Entity
@Access(value = SecurityMode.PERMISSIONS, members = {TasksRoles.MANAGE_TASKS})
public class TaskDataProviderObject implements Serializable {
    private static final long serialVersionUID = 1767818631190935233L;

    @Field
    private String displayName;
    @Field
    private String type;
    @Field
    @Cascade(delete = true)
    private List<LookupFieldsParameter> lookupFields;
    @Field
    @Cascade(delete = true)
    private List<FieldParameter> fields;

    public TaskDataProviderObject() {
        this(null, null, null, null);
    }

    public TaskDataProviderObject(String displayName, String type, List<LookupFieldsParameter> lookupFields, List<FieldParameter> fields) {
        this.displayName = displayName;
        this.type = type;
        this.lookupFields = lookupFields == null ? new ArrayList<LookupFieldsParameter>() : lookupFields;
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
        if (CollectionUtils.isNotEmpty(lookupFields)) {
            if (lookupFields.get(0) instanceof String) {
                LookupFieldsParameter param = new LookupFieldsParameter();
                param.setDisplayName("id");
                param.setFields(asList((String) lookupFields.get(0)));
                this.lookupFields.add(param);
            } else if (lookupFields.get(0) instanceof LookupFieldsParameter) {
                for (Object obj : lookupFields) {
                    if (obj instanceof  LookupFieldsParameter) {
                        this.lookupFields.add((LookupFieldsParameter) obj);
                    } else {
                        throw new IllegalArgumentException("Mixed collection provided. Not an instance of LookupFieldsParameter: " + obj);
                    }
                }
            } else {
                for (Object o : lookupFields) {
                    LinkedHashMap<String, Object> map = (LinkedHashMap) o;
                    LookupFieldsParameter param = new LookupFieldsParameter();
                    param.setDisplayName(map.get("displayName").toString());
                    param.setFields((ArrayList<String>) map.get("fields"));
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

    public TaskDataProviderObjectDto toDto() {
        List<FieldParameterDto> fieldDtos = new ArrayList<>();
        List<LookupFieldsParameterDto> lookupFieldsDtos = new ArrayList<>();

        if (fields != null) {
            for (FieldParameter field : fields) {
                fieldDtos.add(field.toDto());
            }
       }
        if (lookupFields != null) {
            for (LookupFieldsParameter lookupField : lookupFields) {
                lookupFieldsDtos.add(lookupField.toDto());
            }
        }

        return new TaskDataProviderObjectDto(displayName, type, lookupFieldsDtos, fieldDtos);
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
