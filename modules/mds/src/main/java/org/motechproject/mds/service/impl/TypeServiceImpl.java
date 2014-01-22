package org.motechproject.mds.service.impl;

import org.motechproject.mds.domain.AvailableFieldTypeMapping;
import org.motechproject.mds.domain.TypeSettingsMapping;
import org.motechproject.mds.dto.AvailableTypeDto;
import org.motechproject.mds.dto.FieldValidationDto;
import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.ex.TypeAlreadyExistsException;
import org.motechproject.mds.ex.TypeNotFoundException;
import org.motechproject.mds.repository.AllFieldTypes;
import org.motechproject.mds.repository.AllTypeSettingsMappings;
import org.motechproject.mds.service.BaseMdsService;
import org.motechproject.mds.service.TypeService;
import org.motechproject.mds.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default implementation of TypeService interface
 */
@Service
public class TypeServiceImpl extends BaseMdsService implements TypeService {

    private AllFieldTypes allFieldTypes;
    private AllTypeSettingsMappings allTypeSettingsMappings;

    private ValidationService validationService;

    @Override
    @Transactional
    public void createFieldType(AvailableTypeDto type, FieldValidationDto validation, SettingDto... settings) {
        if (allFieldTypes.typeExists(type)) {
            throw new TypeAlreadyExistsException();
        } else {
            AvailableFieldTypeMapping typeMapping = allFieldTypes.save(type);

            if (validation != null) {
                validationService.saveValidationForType(typeMapping, validation);
            }

            if (settings != null) {
                for (SettingDto settingDto : settings) {
                    allTypeSettingsMappings.save(settingDto, allFieldTypes.getByName(type.getType().getDisplayName()), typeMapping);
                }
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
        AvailableFieldTypeMapping toDelete = allFieldTypes.getByName(name);

        if (toDelete == null) {
            throw new TypeNotFoundException();
        }

        List<TypeSettingsMapping> settingsMappings = allTypeSettingsMappings.getSettingsForType(toDelete);
        if (settingsMappings != null) {
            for (TypeSettingsMapping settingsMapping : settingsMappings) {
                allTypeSettingsMappings.delete(settingsMapping.getId());
            }
        }

        validationService.deleteValidationForType(toDelete);

        allFieldTypes.delete(toDelete.getId());
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
    public void setEntityService(ValidationService validationService) {
        this.validationService = validationService;
    }
}
