package org.motechproject.mds.dto;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * The <code>FieldValidationDto</code> class contains information about validation criteria for field.
 */
public class FieldValidationDto {
    private List<ValidationCriterionDto> validationCriteria;

    public FieldValidationDto() {
        validationCriteria = new ArrayList<>();
    }

    public FieldValidationDto(List<ValidationCriterionDto> validationCriteria) {
        this.validationCriteria = validationCriteria;
    }

    public List<ValidationCriterionDto> getValidationCriteria() {
        return validationCriteria;
    }

    public void setValidationCriteria(List<ValidationCriterionDto> validationCriteria) {
        this.validationCriteria = validationCriteria;
    }

    public static final FieldValidationDto INTEGER = new FieldValidationDto(
        new ArrayList<ValidationCriterionDto>() {
            {
                add(new ValidationCriterionDto("mds.field.validation.minValue", TypeDto.INTEGER, "", false));
                add(new ValidationCriterionDto("mds.field.validation.maxValue", TypeDto.INTEGER, "", false));
                add(new ValidationCriterionDto("mds.field.validation.mustBeInSet", TypeDto.STRING, "", false));
                add(new ValidationCriterionDto("mds.field.validation.cannotBeInSet", TypeDto.STRING, "", false));
            }
        }
    );

    public static final FieldValidationDto DECIMAL = new FieldValidationDto(
        new ArrayList<ValidationCriterionDto>() {
            {
                add(new ValidationCriterionDto("mds.field.validation.minValue", TypeDto.DOUBLE, "", false));
                add(new ValidationCriterionDto("mds.field.validation.maxValue", TypeDto.DOUBLE, "", false));
                add(new ValidationCriterionDto("mds.field.validation.mustBeInSet", TypeDto.STRING, "", false));
                add(new ValidationCriterionDto("mds.field.validation.cannotBeInSet", TypeDto.STRING, "", false));
            }
        }
    );

    public static final FieldValidationDto STRING = new FieldValidationDto(
        new ArrayList<ValidationCriterionDto>() {
            {
                add(new ValidationCriterionDto("mds.field.validation.regex", TypeDto.STRING, "", false));
                add(new ValidationCriterionDto("mds.field.validation.minLength", TypeDto.INTEGER, "", false));
                add(new ValidationCriterionDto("mds.field.validation.maxLength", TypeDto.INTEGER, "", false));
            }
        }
    );

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
