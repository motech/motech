package org.motechproject.mds.service;

import org.motechproject.mds.dto.AdvancedSettingsDto;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.FieldInstanceDto;
import org.motechproject.mds.dto.SecuritySettingsDto;
import org.motechproject.mds.web.DraftData;
import org.motechproject.mds.web.domain.EntityRecord;
import org.motechproject.mds.web.domain.HistoryRecord;
import org.motechproject.mds.web.domain.PreviousRecord;

import java.io.IOException;
import java.util.List;

/**
 * This interface provides methods related with executing actions on an entity.
 */
public interface EntityService {

    EntityDto createEntity(EntityDto entity) throws IOException;
    void deleteEntity(EntityDto entity);

    List<LookupDto> saveEntityLookups(Long entityId, List<LookupDto> lookups);

    List<EntityDto> listEntities();
    EntityDto getEntity(Long entityId);

    // TODO: replace with entity.getFields
    List<FieldDto> getFields(Long entityId);
    // TODO: replace with entity.findField
    FieldDto findFieldByName(Long entityId, String name);

    boolean saveDraftEntityChanges(Long entityId, DraftData draftData);
    void abandonChanges(Long entityId);
    void commitChanges(Long entityId);

    List<EntityRecord> getEntityRecords(Long entityId);

    // TODO: replace with entity.getAdvancedSettings
    AdvancedSettingsDto getAdvancedSettings(Long entityId);
    // TODO: replace with entity.getSecuritySettings
    SecuritySettingsDto getSecuritySettings(Long entityId);

    // TODO: move to InstanceService/HistoryService after we get rid of example data
    List<FieldInstanceDto> getInstanceFields(Long instanceId);
    List<HistoryRecord> getInstanceHistory(Long instanceId);
    List<PreviousRecord> getPreviousRecords(Long instanceId);
}
