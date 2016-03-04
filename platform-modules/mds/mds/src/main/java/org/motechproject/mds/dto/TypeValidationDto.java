package org.motechproject.mds.dto;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Dto class representing a validation for a given type.
 */
public class TypeValidationDto {

    private Long id;
    private String displayName;
    private String valueType;
    private List<Class<? extends Annotation>> annotations;

    public TypeValidationDto() {
    }

    public TypeValidationDto(String displayName, String valueType) {
        this.displayName = displayName;
        this.valueType = valueType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public List<Class<? extends Annotation>> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<Class<? extends Annotation>> annotations) {
        this.annotations = annotations;
    }
}
