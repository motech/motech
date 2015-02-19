package org.motechproject.mds.service;

import org.motechproject.mds.domain.EntityDraft;
import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.DraftData;
import org.motechproject.mds.dto.DraftResult;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.RestOptionsDto;
import org.motechproject.mds.dto.TrackingDto;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * This interface provides methods related with executing actions on an entity.
 */
public interface EntityService {

    EntityDto createEntity(EntityDto entityDto);

    List<EntityDto> listEntities();

    List<EntityDto> listEntities(boolean withSecurityCheck);

    EntityDto getEntity(Long entityId);

    void deleteEntity(Long entityId);

    List<EntityDto> listWorkInProgress();

    EntityDto getEntityForEdit(Long entityId);

    EntityDto getEntityByClassName(String className);

    void addLookups(EntityDto entityDto, LookupDto... lookups);

    void addLookups(EntityDto entityDto, Collection<LookupDto> lookups);

    void addLookups(Long entityId, LookupDto... lookups);

    void addLookups(Long entityId, Collection<LookupDto> lookups);

    List<LookupDto> getEntityLookups(Long entityId);

    List<FieldDto> getFields(Long entityId);

    List<FieldDto> getEntityFields(Long entityId);

    FieldDto findFieldByName(Long entityId, String name);

    FieldDto findEntityFieldByName(Long entityId, String name);

    FieldDto getEntityFieldById(Long entityId, Long fieldId);

    DraftResult saveDraftEntityChanges(Long entityId, DraftData draftData, String username);

    DraftResult saveDraftEntityChanges(Long entityId, DraftData draftData);

    void abandonChanges(Long entityId);

    void commitChanges(Long entityId);

    void commitChanges(Long entityId, String changesOwner);

    AdvancedSettingsDto getAdvancedSettings(Long entityId);

    AdvancedSettingsDto getAdvancedSettings(Long entityId, boolean committed);

    void updateRestOptions(Long entityId, RestOptionsDto restOptionsDto);

    void updateTracking(Long entityId, TrackingDto trackingDto);

    EntityDraft getEntityDraft(Long entityId);

    EntityDraft getEntityDraft(Long entityId, String username);

    void addFields(EntityDto entity, FieldDto... fields);

    void addFields(EntityDto entity, Collection<FieldDto> fields);

    void addFields(Long entityId, FieldDto... fields);

    void addFields(Long entityId, Collection<FieldDto> fields);

    void addFilterableFields(EntityDto entityDto, Collection<String> fieldNames);

    void addDisplayedFields(EntityDto entityDto, Map<String, Long> positions);

    EntityDto updateDraft(Long entityId);

    LookupDto getLookupByName(Long entityId, String lookupName);

    List<FieldDto> getDisplayFields(Long entityId);

    List<EntityDto> getEntitiesWithLookups();

    Long getCurrentSchemaVersion(String entityClassName);

    void updateComboboxValues(Long entityId, Map<String, Collection> fieldValuesToUpdate);
}
