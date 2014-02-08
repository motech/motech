package org.motechproject.mds.service.impl;

import org.motechproject.mds.domain.AvailableFieldType;
import org.motechproject.mds.domain.TypeSettings;
import org.motechproject.mds.domain.TypeValidation;
import org.motechproject.mds.domain.ValidationCriterion;
import org.motechproject.mds.dto.AvailableTypeDto;
import org.motechproject.mds.dto.FieldValidationDto;
import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.dto.ValidationCriterionDto;
import org.motechproject.mds.ex.TypeAlreadyExistsException;
import org.motechproject.mds.ex.TypeNotFoundException;
import org.motechproject.mds.repository.AllFieldTypes;
import org.motechproject.mds.repository.AllTypeSettingsMappings;
import org.motechproject.mds.repository.AllTypeValidationMappings;
import org.motechproject.mds.service.BaseMdsService;
import org.motechproject.mds.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of TypeService interface
 */
@Service
public class TypeServiceImpl extends BaseMdsService implements TypeService {

    private AllFieldTypes allFieldTypes;
    private AllTypeSettingsMappings allTypeSettingsMappings;
    private AllTypeValidationMappings allTypeValidationMappings;

    @Override
    @Transactional
    public void createFieldType(AvailableTypeDto type, FieldValidationDto validation, SettingDto... settings) {
        if (allFieldTypes.typeExists(type)) {
            throw new TypeAlreadyExistsException();
        } else {
            AvailableFieldType typeMapping = allFieldTypes.save(type);

            if (settings != null) {
                for (SettingDto settingDto : settings) {
                    allTypeSettingsMappings.save(settingDto, allFieldTypes.getByName(type.getType().getDisplayName()), typeMapping);
                }
            }

            if (validation != null) {

                List<ValidationCriterion> criterions = new ArrayList<>();
                for (ValidationCriterionDto criterionDto : validation.getCriteria()) {
                    criterions.add(new ValidationCriterion(criterionDto.getDisplayName(), null, typeMapping));
                }

                allTypeValidationMappings.save(new TypeValidation(typeMapping, criterions));
            }
        }
    }

    @Override
    @Transactional
    public List<AvailableTypeDto> getAllFieldTypes() {
        return allFieldTypes.getAll();
    }

    @Override
    @Transactional
    public void deleteFieldType(String name) {
        AvailableFieldType toDelete = allFieldTypes.getByName(name);

        if (toDelete == null) {
            throw new TypeNotFoundException();
        }

        List<TypeSettings> settingsMappings = allTypeSettingsMappings.getSettingsForType(toDelete);
        if (settingsMappings != null) {
            for (TypeSettings settingsMapping : settingsMappings) {
                allTypeSettingsMappings.delete(settingsMapping.getId());
            }
        }

        allFieldTypes.delete(toDelete.getId());
    }

    @Override
    @Transactional
    public TypeDto findType(Class<?> type) {
        AvailableFieldType mapping = allFieldTypes.getByClassName(type.getName());

        if (null != mapping) {
            return mapping.toDto().getType();
        } else {
            throw new TypeNotFoundException();
        }
    }

    @Autowired
    public void setAllFieldTypes(AllFieldTypes allFieldTypes) {
        this.allFieldTypes = allFieldTypes;
    }

    @Autowired
    public void setAllTypeSettingsMappings(AllTypeSettingsMappings allTypeSettingsMappings) {
        this.allTypeSettingsMappings = allTypeSettingsMappings;
    }

    @Autowired
    public void setAllTypeValidationMappings(AllTypeValidationMappings allTypeValidationMappings) {
        this.allTypeValidationMappings = allTypeValidationMappings;
    }
}
