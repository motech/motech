package org.motechproject.tasks.dto;

import java.util.List;

public class TaskDataProviderObjectDto {

    private String displayName;
    private String type;
    private List<LookupFieldsParameterDto> lookupFields;
    private List<FieldParameterDto> fields;

    public TaskDataProviderObjectDto(String displayName, String type, List<LookupFieldsParameterDto> lookupFields, List<FieldParameterDto> fields) {
        this.displayName = displayName;
        this.type = type;
        this.lookupFields = lookupFields;
        this.fields = fields;
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

    public List<LookupFieldsParameterDto> getLookupFields() {
        return lookupFields;
    }

    public void setLookupFields(List<LookupFieldsParameterDto> lookupFields) {
        this.lookupFields = lookupFields;
    }

    public List<FieldParameterDto> getFields() {
        return fields;
    }

    public void setFields(List<FieldParameterDto> fields) {
        this.fields = fields;
    }
}
