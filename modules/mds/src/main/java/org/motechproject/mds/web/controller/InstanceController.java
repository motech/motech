package org.motechproject.mds.web.controller;

import org.motechproject.mds.constants.MdsRolesConstants;
import org.motechproject.mds.dto.FieldInstanceDto;
import org.motechproject.mds.ex.EntityNotFoundException;
import org.motechproject.mds.web.comparator.HistoryRecordComparator;
import org.motechproject.mds.web.domain.FieldRecord;
import org.motechproject.mds.web.domain.GridSettings;
import org.motechproject.mds.web.domain.HistoryRecord;
import org.motechproject.mds.web.domain.PreviousRecord;
import org.motechproject.mds.web.domain.Records;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;

/**
 * The <code>FieldController</code> is the Spring Framework Controller used by view layer for
 * executing certain actions on entity fields.
 *
 * @see org.motechproject.mds.dto.FieldDto
 * @see org.motechproject.mds.dto.EntityDto
 */
@Controller
public class InstanceController extends MdsController {

    @RequestMapping(value = "/instances/{instanceId}/fields", method = RequestMethod.GET)
    @PreAuthorize(MdsRolesConstants.HAS_DATA_ACCESS)
    @ResponseBody
    public List<FieldInstanceDto> getInstanceFields(@PathVariable String instanceId) {
        if (null == getExampleData().getEntity(instanceId)) {
            throw new EntityNotFoundException();
        }

        return getExampleData().getInstanceFields(instanceId);
    }

    @RequestMapping(value = "/instances/{instanceId}/history", method = RequestMethod.GET)
    @PreAuthorize(MdsRolesConstants.HAS_DATA_ACCESS)
    @ResponseBody
    public Records<HistoryRecord> getHistory(@PathVariable String instanceId, GridSettings settings) {
        List<HistoryRecord> historyRecordsList = getExampleData().getInstanceHistoryRecordsById(instanceId);

        boolean sortAscending = settings.getSortDirection() == null || "asc".equals(settings.getSortDirection());
        if (settings.getSortColumn() != null && !settings.getSortColumn().isEmpty() && !historyRecordsList.isEmpty()) {
            Collections.sort(
                    historyRecordsList, new HistoryRecordComparator(sortAscending, settings.getSortColumn())
            );
        }

        return new Records<>(settings.getPage(), settings.getRows(), historyRecordsList);
    }

    @RequestMapping(value = "/instances/{instanceId}/previousVersion/{historyId}", method = RequestMethod.GET)
    @PreAuthorize(MdsRolesConstants.HAS_DATA_ACCESS)
    @ResponseBody
    public List<FieldRecord> getPreviousInstance(@PathVariable String instanceId,@PathVariable String historyId, GridSettings settings) {
        List<PreviousRecord> previousRecordsList = getExampleData().getPreviousRecordsById(instanceId);
        for (PreviousRecord record : previousRecordsList) {
            if (record.getId().equals(historyId))  {
                return record.getFields();
            }
        }
        throw new EntityNotFoundException();
    }
}
