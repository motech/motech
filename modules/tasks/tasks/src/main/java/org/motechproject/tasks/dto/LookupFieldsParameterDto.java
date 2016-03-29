package org.motechproject.tasks.dto;

import java.util.List;

public class LookupFieldsParameterDto {

    private String displayName;
    private List<String> fields;

    public LookupFieldsParameterDto(String displayName, List<String> fields) {
        this.displayName = displayName;
        this.fields = fields;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }
}
