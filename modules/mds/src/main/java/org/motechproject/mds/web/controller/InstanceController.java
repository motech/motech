package org.motechproject.mds.web.controller;

import org.motechproject.mds.dto.FieldInstanceDto;
import org.motechproject.mds.ex.EntityNotFoundException;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.web.comparator.HistoryRecordComparator;
import org.motechproject.mds.web.domain.FieldRecord;
import org.motechproject.mds.web.domain.GridSettings;
import org.motechproject.mds.web.domain.HistoryRecord;
import org.motechproject.mds.web.domain.PreviousRecord;
import org.motechproject.mds.web.domain.Records;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;

import static org.motechproject.mds.util.Constants.Roles;

/**
 * The <code>FieldController</code> is the Spring Framework Controller used by view layer for
 * executing certain actions on entity fields.
 *
 * @see org.motechproject.mds.dto.FieldDto
 * @see org.motechproject.mds.dto.EntityDto
 */
@Controller
public class InstanceController extends MdsController {

    @Autowired
    private EntityService entityService;

    @RequestMapping(value = "/instances/{instanceId}/fields", method = RequestMethod.GET)
    @PreAuthorize(Roles.HAS_DATA_ACCESS)
    @ResponseBody
    public List<FieldInstanceDto> getInstanceFields(@PathVariable Long instanceId) {
        if (null == entityService.getEntity(instanceId)) {
            throw new EntityNotFoundException();
        }

        return entityService.getInstanceFields(instanceId);
    }

    @RequestMapping(value = "/instances/{instanceId}/history", method = RequestMethod.GET)
    @PreAuthorize(Roles.HAS_DATA_ACCESS)
    @ResponseBody
    public Records<HistoryRecord> getHistory(@PathVariable Long instanceId, GridSettings settings) {
        List<HistoryRecord> historyRecordsList = entityService.getInstanceHistory(instanceId);

        boolean sortAscending = settings.getSortDirection() == null || "asc".equals(settings.getSortDirection());
        if (settings.getSortColumn() != null && !settings.getSortColumn().isEmpty() && !historyRecordsList.isEmpty()) {
            Collections.sort(
                    historyRecordsList, new HistoryRecordComparator(sortAscending, settings.getSortColumn())
            );
        }

        return new Records<>(settings.getPage(), settings.getRows(), historyRecordsList);
    }

    @RequestMapping(value = "/instances/{instanceId}/previousVersion/{historyId}", method = RequestMethod.GET)
    @PreAuthorize(Roles.HAS_DATA_ACCESS)
    @ResponseBody
    public List<FieldRecord> getPreviousInstance(@PathVariable Long instanceId, @PathVariable Long historyId, GridSettings settings) {
        List<PreviousRecord> previousRecordsList = entityService.getPreviousRecords(instanceId);
        for (PreviousRecord record : previousRecordsList) {
            if (record.getId().equals(historyId)) {
                return record.getFields();
            }
        }
        throw new EntityNotFoundException();
    }
}
