package org.motechproject.mds.service.impl.internal;

import org.motechproject.mds.domain.AvailableFieldTypeMapping;
import org.motechproject.mds.domain.EntityMapping;
import org.motechproject.mds.domain.FieldMapping;
import org.motechproject.mds.domain.LookupMapping;
import org.motechproject.mds.domain.TypeValidationMapping;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.FieldInstanceDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.SecuritySettingsDto;
import org.motechproject.mds.dto.ValidationCriterionDto;
import org.motechproject.mds.ex.EntityAlreadyExistException;
import org.motechproject.mds.ex.EntityNotFoundException;
import org.motechproject.mds.ex.EntityReadOnlyException;
import org.motechproject.mds.repository.AllEntityMappings;
import org.motechproject.mds.repository.AllFieldTypes;
import org.motechproject.mds.repository.AllTypeValidationMappings;
import org.motechproject.mds.service.BaseMdsService;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.MDSConstructor;
import org.motechproject.mds.web.DraftData;
import org.motechproject.mds.web.ExampleData;
import org.motechproject.mds.web.domain.EntityRecord;
import org.motechproject.mds.web.domain.HistoryRecord;
import org.motechproject.mds.web.domain.PreviousRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.motechproject.mds.constants.Constants.Packages;

/**
 * Default implementation of {@link org.motechproject.mds.service.EntityService} interface.
 */
@Service
public class EntityServiceImpl extends BaseMdsService implements EntityService {
    private AllEntityMappings allEntityMappings;
    private MDSConstructor constructor;
    private AllFieldTypes allFieldTypes;
    private AllTypeValidationMappings allTypeValidationMappings;

    // TODO remove this once everything is in db
    private ExampleData exampleData = new ExampleData();

    @Override
    @Transactional
    public EntityDto createEntity(EntityDto entity) throws IOException {
        if (entity.isReadOnly()) {
            throw new EntityReadOnlyException();
        }

        if (allEntityMappings.containsEntity(entity.getName())) {
            throw new EntityAlreadyExistException();
        }

        String className = String.format("%s.%s", Packages.ENTITY, entity.getName());
        EntityMapping entityMapping = allEntityMappings.save(className);
        constructor.constructEntity(entityMapping);

        return entityMapping.toDto();
    }

    @Override
    @Transactional
    public boolean saveDraftEntityChanges(Long entityId, DraftData draftData) {
        exampleData.draft(entityId, draftData);
        return exampleData.isAnyChangeInFields(entityId);
    }

    @Override
    @Transactional
    public void abandonChanges(Long entityId) {
        exampleData.abandonChanges(entityId);
    }

    @Override
    @Transactional
    public void commitChanges(Long entityId) {
        EntityMapping entity = getSecureEntity(entityId);

        for (FieldDto field : getFields(entityId)) {
            if (field.getId() == null || entity.getField(field.getId()) == null) {
                AvailableFieldTypeMapping type = allFieldTypes.getByName(field.getType().getDisplayName());
                TypeValidationMapping validationMapping = allTypeValidationMappings.getEmptyValidationForType(type);
                if (validationMapping != null) {
                    for (ValidationCriterionDto criterionDto : field.getValidation().getCriteria()) {
                        validationMapping.getCriterionByName(criterionDto.getDisplayName()).setEnabled(criterionDto.isEnabled());
                        validationMapping.getCriterionByName(criterionDto.getDisplayName()).setValue(criterionDto.getValue().toString());
                    }
                }
                entity.getFields().add(new FieldMapping(field, entity, type, validationMapping));
            } else {
                entity.getField(field.getId()).update(field);
            }
        }

        // TODO: get data from draft, not example data
        saveLookups(entity,
                exampleData.getAdvanced(entityId).getIndexes(),
                exampleData.getAdvanced(entityId).getRestOptions().getLookupIds());

        exampleData.commitChanges(entityId);
    }

    @Override
    @Transactional
    public List<EntityRecord> getEntityRecords(Long entityId) {
        return exampleData.getEntityRecordsById(entityId);
    }

    @Override
    @Transactional
    public AdvancedSettingsDto getAdvancedSettings(Long entityId) {
        EntityMapping entity = getSecureEntity(entityId);

        AdvancedSettingsDto advancedSettings = new AdvancedSettingsDto();

        List<LookupDto> lookups = new ArrayList<>();
        List<String> restExposedLookups  = new ArrayList<>();
        for (LookupMapping lookup : entity.getLookups()) {
            lookups.add(lookup.toDto());
            if (lookup.isExposedViaRest()) {
                restExposedLookups.add(lookup.getLookupName());
            }
        }

        advancedSettings.setIndexes(lookups);
        advancedSettings.getRestOptions().setLookupIds(restExposedLookups);

        return advancedSettings;
    }

    @Override
    @Transactional
    public SecuritySettingsDto getSecuritySettings(Long entityId) {
        return exampleData.getSecurity(entityId);
    }

    @Override
    @Transactional
    public List<FieldInstanceDto> getInstanceFields(Long instanceId) {
        return exampleData.getInstanceFields(instanceId);
    }

    @Override
    @Transactional
    public List<HistoryRecord> getInstanceHistory(Long instanceId) {
        return exampleData.getInstanceHistoryRecordsById(instanceId);
    }

    @Override
    @Transactional
    public List<PreviousRecord> getPreviousRecords(Long instanceId) {
        return exampleData.getPreviousRecordsById(instanceId);
    }

    @Override
    @Transactional
    public void deleteEntity(EntityDto entity) {
        allEntityMappings.delete(entity.getId());
    }

    @Override
    @Transactional
    public List<EntityDto> listEntities() {
        List<EntityDto> entityDtos = new ArrayList<>();

        for (EntityMapping entity : allEntityMappings.getAllEntities()) {
            entityDtos.add(entity.toDto());
        }

        return entityDtos;
    }

    @Override
    @Transactional
    public EntityDto getEntity(Long entityId) {
        EntityMapping entity = allEntityMappings.getEntityById(entityId);
        return (entity == null) ? null : entity.toDto();
    }

    @Override
    public List<FieldDto> getFields(Long entityId) {
        return exampleData.getFields(entityId);
    }

    @Override
    public FieldDto findFieldByName(Long entityId, String name) {
        return exampleData.findFieldByName(entityId, name);
    }

    @Autowired
    public void setAllEntityMappings(AllEntityMappings allEntityMappings) {
        this.allEntityMappings = allEntityMappings;
    }

    @Autowired
    public void setConstructor(MDSConstructor constructor) {
        this.constructor = constructor;
    }

    private EntityMapping getSecureEntity(Long entityId) {
        EntityMapping entity = allEntityMappings.getEntityById(entityId);

        if (entity == null) {
            throw new EntityNotFoundException();
        }

        if (entity.isReadOnly()) {
            throw new EntityReadOnlyException();
        }

        return entity;
    }

    private void saveLookups(EntityMapping entity, List<LookupDto> lookups, List<String> restExposedLookupNames) {
        List<LookupMapping> updatedLookups = new LinkedList<>();

        for (LookupDto lookup : lookups) {
            if (lookup.getId() == null || entity.getLookup(lookup.getId()) == null) {
                updatedLookups.add(new LookupMapping(
                        lookup.getLookupName(),
                        lookup.isSingleObjectReturn(),
                        restExposedLookupNames.contains(lookup.getLookupName()),
                        entity));
            } else {
                LookupMapping mapping = entity.getLookup(lookup.getId());
                mapping.setLookupName(lookup.getLookupName());
                mapping.setSingleObjectReturn(lookup.isSingleObjectReturn());
                mapping.setExposedViaRest(lookups.contains(lookup.getLookupName()));
            }
        }

        entity.setLookups(updatedLookups);
    }

    @Autowired
    public void setAllFieldTypes(AllFieldTypes allFieldTypes) {
        this.allFieldTypes = allFieldTypes;
    }

    @Autowired
    public void setAllTypeValidationMappings(AllTypeValidationMappings allTypeValidationMappings) {
        this.allTypeValidationMappings = allTypeValidationMappings;
    }
}
