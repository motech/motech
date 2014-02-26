package org.motechproject.mds.service;

import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.DraftResult;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.FieldInstanceDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.web.DraftData;
import org.motechproject.mds.web.domain.EntityRecord;
import org.motechproject.mds.web.domain.HistoryRecord;
import org.motechproject.mds.web.domain.PreviousRecord;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * This interface provides methods related with executing actions on an entity.
 */
public interface EntityService {

    EntityDto createEntity(EntityDto entityDto) throws IOException;

    List<EntityDto> listEntities();

    EntityDto getEntity(Long entityId);

    void deleteEntity(Long entityId);

    List<EntityDto> listWorkInProgress();

    EntityDto getEntityForEdit(Long entityId);

    EntityDto getEntityByClassName(String className);

    void addLookups(Long entityId, Collection<LookupDto> lookups);

    // TODO: replace with entity.getFields
    List<FieldDto> getFields(Long entityId);

    List<FieldDto> getEntityFields(Long entityId);

    // TODO: replace with entity.findField
    FieldDto findFieldByName(Long entityId, String name);

    DraftResult saveDraftEntityChanges(Long entityId, DraftData draftData);

    void abandonChanges(Long entityId);

    void commitChanges(Long entityId);

    // TODO: replace with entity.getAdvancedSettings
    AdvancedSettingsDto getAdvancedSettings(Long entityId);

    AdvancedSettingsDto getAdvancedSettings(Long entityId, boolean committed);

    List<EntityRecord> getEntityRecords(Long entityId);

    List<FieldInstanceDto> getInstanceFields(Long instanceId);

    List<HistoryRecord> getInstanceHistory(Long instanceId);

    List<PreviousRecord> getPreviousRecords(Long instanceId);

    void addFields(EntityDto entity, Collection<FieldDto> fields);

    void addFilterableFields(EntityDto entityDto, Collection<String> fieldNames);

    void addDisplayedFields(EntityDto entityDto, Map<String, Long> positions);

    void generateDDE(Long entityId);

    EntityDto updateDraft(Long entityId);

    LookupDto getLookupByName(Long entityId, String lookupName);
}
