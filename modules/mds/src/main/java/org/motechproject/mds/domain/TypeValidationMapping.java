package org.motechproject.mds.domain;

import org.motechproject.mds.dto.FieldValidationDto;
import org.motechproject.mds.dto.ValidationCriterionDto;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.util.ArrayList;
import java.util.List;

/**
 * The <code>TypeValidationMapping</code> class contains information about type validation. This class is
 * related with table in database with the same name.
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE)
public class TypeValidationMapping {

    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    @PrimaryKey
    private Long id;

    @Persistent
    private String name;

    @Column(name = "type")
    private AvailableFieldTypeMapping type;

    @Persistent(mappedBy = "validation")
    @Element(dependent = "true")
    private List<ValidationCriterionMapping> criteria;

    public TypeValidationMapping() {
    }

    public TypeValidationMapping(AvailableFieldTypeMapping type) {
        this.name = (type == null) ? null : type.getDefaultName();
        this.type = type;
    }

    public TypeValidationMapping(AvailableFieldTypeMapping type, List<ValidationCriterionMapping> criteria) {
        this(type);
        this.criteria = criteria;
    }
    public FieldValidationDto toDto() {
        List<ValidationCriterionDto> validationCriteriaDto = new ArrayList<>();
        if (criteria != null) {
            for (ValidationCriterionMapping criterion : criteria) {
                validationCriteriaDto.add(criterion.toDto());
            }
        }

        return new FieldValidationDto(validationCriteriaDto.toArray(new ValidationCriterionDto[validationCriteriaDto.size()]));
    }

    public ValidationCriterionMapping getCriterionByName(String name) {
        if (criteria != null) {
            for (ValidationCriterionMapping criterionMapping : criteria) {
                if (criterionMapping.getDisplayName().equalsIgnoreCase(name)) {
                    return criterionMapping;
                }
            }
        }

        return null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AvailableFieldTypeMapping getType() {
        return type;
    }

    public void setType(AvailableFieldTypeMapping type) {
        this.type = type;
    }

    public List<ValidationCriterionMapping> getCriteria() {
        if (criteria == null) {
            criteria = new ArrayList<>();
        }
        return criteria;
    }

    public void setCriteria(List<ValidationCriterionMapping> criteria) {
        this.criteria = criteria;
    }

    public TypeValidationMapping copy() {
        List<ValidationCriterionMapping> criterionCopies = new ArrayList<>();

        for (ValidationCriterionMapping criterion : getCriteria()) {
            criterionCopies.add(criterion.copy());
        }

        return new TypeValidationMapping(type, criterionCopies);
    }
}
