package org.motechproject.mds.service.impl;

import org.motechproject.mds.domain.AvailableFieldTypeMapping;
import org.motechproject.mds.domain.TypeValidationMapping;
import org.motechproject.mds.dto.FieldValidationDto;
import org.motechproject.mds.dto.ValidationCriterionDto;
import org.motechproject.mds.ex.TypeValidationAlreadyExistsException;
import org.motechproject.mds.repository.AllFieldTypes;
import org.motechproject.mds.repository.AllTypeValidationMappings;
import org.motechproject.mds.repository.AllValidationCriterionMappings;
import org.motechproject.mds.service.BaseMdsService;
import org.motechproject.mds.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of {@link org.motechproject.mds.service.ValidationService} interface
 */
@Service
public class ValidationServiceImpl extends BaseMdsService implements ValidationService {

    private AllTypeValidationMappings allTypeValidationMappings;
    private AllValidationCriterionMappings allValidationCriterionMappings;
    private AllFieldTypes allFieldTypes;

    @Override
    @Transactional
    public void saveValidationForType(AvailableFieldTypeMapping type, FieldValidationDto validation) {
        if (allTypeValidationMappings.getValidationForType(type) != null) {
            throw new TypeValidationAlreadyExistsException();
        }

        TypeValidationMapping validationMapping = allTypeValidationMappings.save(type);
        for (ValidationCriterionDto criterionDto : validation.getCriteria()) {
            allValidationCriterionMappings.save(criterionDto, validationMapping, allFieldTypes.getByName(criterionDto.getType().getDisplayName()));
        }
    }

    @Override
    @Transactional
    public void deleteValidationForType(AvailableFieldTypeMapping type) {
        TypeValidationMapping validationMapping = allTypeValidationMappings.getValidationForType(type);
        if (validationMapping != null) {
            allTypeValidationMappings.delete(validationMapping.getId());
        }
    }

    @Autowired
    public void setAllFieldTypes(AllFieldTypes allFieldTypes) {
        this.allFieldTypes = allFieldTypes;
    }

    @Autowired
    public void setAllTypeValidationMappings(AllTypeValidationMappings allTypeValidationMappings) {
        this.allTypeValidationMappings = allTypeValidationMappings;
    }

    @Autowired
    public void setAllValidationCriterionMappings(AllValidationCriterionMappings allValidationCriterionMappings) {
        this.allValidationCriterionMappings = allValidationCriterionMappings;
    }
}
