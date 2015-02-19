package org.motechproject.mds.web.service;

import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.FieldInstanceDto;
import org.motechproject.mds.filter.Filters;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.web.domain.EntityRecord;
import org.motechproject.mds.web.domain.FieldRecord;
import org.motechproject.mds.web.domain.HistoryRecord;

import java.util.List;
import java.util.Map;

/**
 * The <code>InstanceService</code> interface, defines methods responsible for executing actions on the
 * instances of the entity.
 */
public interface InstanceService {

    long countRecords(Long entityId);

    Object saveInstance(EntityRecord entityRecord);

    Object saveInstance(EntityRecord entityRecord, Long deleteValueFieldId);

    List<EntityRecord> getEntityRecords(Long entityId, QueryParams queryParams);

    List<EntityRecord> getEntityRecords(Long entityId);

    List<FieldDto> getEntityFields(Long entityId);

    List<EntityRecord> getEntityRecordsFromLookup(Long entityId, String lookupName, Map<String, Object> lookupMap,
                                                  QueryParams queryParams);

    List<EntityRecord> getEntityRecordsWithFilter(Long entityId, Filters filters, QueryParams queryParams);

    List<FieldInstanceDto> getInstanceFields(Long entityId, Long instanceId);

    List<HistoryRecord> getInstanceHistory(Long entityId, Long instanceId, QueryParams queryParams);

    HistoryRecord getHistoryRecord(Long entityId, Long instanceId, Long historyId);

    long countHistoryRecords(Long entityId, Long instanceId);

    EntityRecord newInstance(Long entityId);

    EntityRecord getEntityInstance(Long entityId, Long instanceId);

    FieldRecord getInstanceValueAsRelatedField(Long entityId, Long fieldId, Long instanceId);

    long countRecordsByLookup(Long entityId, String lookupName, Map<String, Object> lookupMap);

    long countRecordsWithFilters(Long entityId, Filters filters);

    void deleteInstance(Long entityId, Long instanceId);

    void revertInstanceFromTrash(Long entityId, Long instanceId);

    void revertPreviousVersion(Long entityId, Long instanceId, Long historyId);

    List<EntityRecord> getTrashRecords(Long entityId, QueryParams queryParams);

    long countTrashRecords(Long entityId);

    EntityRecord getSingleTrashRecord(Long entityId, Long instanceId);

    Object getInstanceField(Long entityId, Long instanceId, String fieldName);
}
