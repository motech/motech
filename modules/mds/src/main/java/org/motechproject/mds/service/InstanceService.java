package org.motechproject.mds.service;

import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldInstanceDto;
import org.motechproject.mds.web.domain.EntityRecord;
import org.motechproject.mds.web.domain.FieldRecord;
import org.motechproject.mds.web.domain.HistoryRecord;
import org.motechproject.mds.web.domain.PreviousRecord;

import java.util.List;

/**
 * The <code>InstanceService</code> interface, defines methods responsible for executing actions on the
 * instances of the entity.
 */
public interface InstanceService {

    Object createInstance(EntityDto entityDto, List<FieldRecord> fieldRecords);

    List<?> getEntityRecordsPaged(Long entityId, Integer page, Integer rows);

    List<EntityRecord> getEntityRecords(Long entityId);

    List<FieldInstanceDto> getInstanceFields(Long instanceId);

    List<HistoryRecord> getInstanceHistory(Long instanceId);

    List<PreviousRecord> getPreviousRecords(Long instanceId);
}
