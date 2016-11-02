package org.motechproject.tasks.dto;

import org.motechproject.tasks.domain.enums.ParameterType;

public abstract class ParameterDto {

    private String displayName;
    private ParameterType type;

    protected ParameterDto(String displayName, ParameterType type) {
        this.displayName = displayName;
        this.type = type;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public ParameterType getType() {
        return type;
    }

    public void setType(ParameterType type) {
        this.type = type;
    }
}
