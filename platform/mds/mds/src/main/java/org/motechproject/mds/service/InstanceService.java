package org.motechproject.mds.service;

import org.motechproject.mds.dto.FieldInstanceDto;
import org.motechproject.mds.filter.Filter;
import org.motechproject.mds.util.QueryParams;
import org.motechproject.mds.web.domain.EntityRecord;
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

    List<EntityRecord> getEntityRecords(Long entityId, QueryParams queryParams);

    List<EntityRecord> getEntityRecords(Long entityId);

    List<EntityRecord> getEntityRecordsFromLookup(Long entityId, String lookupName, Map<String, Object> lookupMap,
                                                  QueryParams queryParams);

    List<EntityRecord> getEntityRecordsWithFilter(Long entityId, Filter filter, QueryParams queryParams);

    List<FieldInstanceDto> getInstanceFields(Long entityId, Long instanceId);

    List<HistoryRecord> getInstanceHistory(Long entityId, Long instanceId);

    HistoryRecord getHistoryRecord(Long entityId, Long instanceId, Long historyId);

    EntityRecord newInstance(Long entityId);

    EntityRecord getEntityInstance(Long entityId, Long instanceId);

    long countRecordsByLookup(Long entityId, String lookupName, Map<String, Object> lookupMap);

    long countRecordsWithFilter(Long entityId, Filter filter);

    void deleteInstance(Long entityId, Long instanceId);

    void revertInstanceFromTrash(Long entityId, Long instanceId);

    void revertPreviousVersion(Long entityId, Long instanceId, Long historyId);

    List<EntityRecord> getTrashRecords(Long entityId);
}
