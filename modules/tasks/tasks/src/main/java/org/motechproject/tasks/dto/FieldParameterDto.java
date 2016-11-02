package org.motechproject.tasks.dto;

import org.motechproject.tasks.domain.enums.ParameterType;

public class FieldParameterDto extends ParameterDto {

    private String fieldKey;

    public FieldParameterDto(String displayName, ParameterType type, String fieldKey) {
        super(displayName, type);
        this.fieldKey = fieldKey;
    }

    public String getFieldKey() {
        return fieldKey;
    }

    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
    }
}
