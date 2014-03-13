package org.motechproject.mds.web.controller;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.motechproject.commons.api.CsvConverter;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.FieldInstanceDto;
import org.motechproject.mds.ex.EntityNotFoundException;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.InstanceService;
import org.motechproject.mds.util.Order;
import org.motechproject.mds.util.QueryParams;
import org.motechproject.mds.web.comparator.HistoryRecordComparator;
import org.motechproject.mds.web.domain.EntityRecord;
import org.motechproject.mds.web.domain.FieldRecord;
import org.motechproject.mds.web.domain.GridSettings;
import org.motechproject.mds.web.domain.HistoryRecord;
import org.motechproject.mds.web.domain.PreviousRecord;
import org.motechproject.mds.web.domain.Records;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.CharEncoding.UTF_8;
import static org.motechproject.mds.util.Constants.Roles;

/**
 * The <code>InstanceController</code> is the Spring Framework Controller used by view layer for
 * managing entity instances.
 *
 * @see org.motechproject.mds.dto.FieldDto
 * @see org.motechproject.mds.dto.EntityDto
 */
@Controller
public class InstanceController extends MdsController {

    @Autowired
    private EntityService entityService;
    @Autowired
    private InstanceService instanceService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping(value = "/instances", method = RequestMethod.POST)
    @PreAuthorize(Roles.HAS_DATA_ACCESS)
    @ResponseStatus(HttpStatus.OK)
    public void saveInstance(@RequestBody EntityRecord record) {
        instanceService.saveInstance(record);
    }

    @RequestMapping(value = "/instances/{instanceId}", method = RequestMethod.POST)
    @PreAuthorize(Roles.HAS_DATA_ACCESS)
    @ResponseStatus(HttpStatus.OK)
    public void updateInstance(@RequestBody EntityRecord record) {
        instanceService.saveInstance(record);
    }

    @RequestMapping(value = "/instances/{entityId}/new")
    @PreAuthorize(Roles.HAS_DATA_ACCESS)
    @ResponseBody
    public EntityRecord newInstance(@PathVariable Long entityId) {
        return instanceService.newInstance(entityId);
    }

    @RequestMapping(value = "/instances/{entityId}/{instanceId}/fields", method = RequestMethod.GET)
    @PreAuthorize(Roles.HAS_DATA_ACCESS)
    @ResponseBody
    public List<FieldInstanceDto> getInstanceFields(@PathVariable Long entityId, @PathVariable Long instanceId) {
        return instanceService.getInstanceFields(entityId, instanceId);
    }

    @RequestMapping(value = "/instances/{instanceId}/history", method = RequestMethod.GET)
    @PreAuthorize(Roles.HAS_DATA_ACCESS)
    @ResponseBody
    public Records<HistoryRecord> getHistory(@PathVariable Long instanceId, GridSettings settings) {
        List<HistoryRecord> historyRecordsList = instanceService.getInstanceHistory(instanceId);

        boolean sortAscending = settings.getSortDirection() == null || "asc".equals(settings.getSortDirection());
        if (settings.getSortColumn() != null && !settings.getSortColumn().isEmpty() && !historyRecordsList.isEmpty()) {
            Collections.sort(
                    historyRecordsList, new HistoryRecordComparator(sortAscending, settings.getSortColumn())
            );
        }

        return new Records<>(0, 1, historyRecordsList);
    }

    @RequestMapping(value = "/instances/{instanceId}/previousVersion/{historyId}", method = RequestMethod.GET)
    @PreAuthorize(Roles.HAS_DATA_ACCESS)
    @ResponseBody
    public List<FieldRecord> getPreviousInstance(@PathVariable Long instanceId, @PathVariable Long historyId, GridSettings settings) {
        List<PreviousRecord> previousRecordsList = instanceService.getPreviousRecords(instanceId);
        for (PreviousRecord record : previousRecordsList) {
            if (record.getId().equals(historyId)) {
                return record.getFields();
            }
        }
        throw new EntityNotFoundException();
    }

    @RequestMapping(value = "/instances/{entityId}/instance/{instanceId}", method = RequestMethod.GET)
    @PreAuthorize(Roles.HAS_DATA_ACCESS)
    @ResponseBody
    public EntityRecord getInstance(@PathVariable Long entityId, @PathVariable Long instanceId) {
        return instanceService.getEntityInstance(entityId, instanceId);
    }

    @RequestMapping(value = "/entities/{entityId}/exportInstances", method = RequestMethod.GET)
    @PreAuthorize(Roles.HAS_DATA_ACCESS)
    public void exportEntityInstances(@PathVariable Long entityId, HttpServletResponse response) throws IOException {
        if (null == entityService.getEntity(entityId)) {
            throw new EntityNotFoundException();
        }

        String fileName = "Entity_" + entityId + "_instances";
        response.setContentType("text/csv");
        response.setCharacterEncoding(UTF_8);
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=" + fileName + ".csv");

        response.getWriter().write(CsvConverter.convertToCSV(prepareForCsvConversion(entityId,
                instanceService.getEntityRecords(entityId))));
    }

    private List<List<String>> prepareForCsvConversion(Long entityId, List<EntityRecord> entityList) {
        List<List<String>> list = new ArrayList<>();

        List<String> fieldNames = new ArrayList<>();
        for (FieldDto field : entityService.getFields(entityId)) {
            fieldNames.add(field.getBasic().getDisplayName());
        }
        list.add(fieldNames);

        for (EntityRecord entityRecord : entityList) {
            List<String> fieldValues = new ArrayList<>();
            for (FieldRecord fieldRecord : entityRecord.getFields()) {
                fieldValues.add(fieldRecord.getValue().toString());
            }
            list.add(fieldValues);
        }

        return list;
    }

    @RequestMapping(value = "/entities/{entityId}/instances", method = RequestMethod.POST)
    @PreAuthorize(Roles.HAS_DATA_ACCESS)
    @ResponseBody
    public Records<?> getInstances(@PathVariable Long entityId, @RequestBody final String url, GridSettings settings) throws IOException {
        Order order = null;
        if (!settings.getSortColumn().isEmpty()) {
            order = new Order(settings.getSortColumn(), settings.getSortDirection());
        }

        QueryParams queryParams = new QueryParams(settings.getPage(), settings.getRows(), order);

        String lookup = settings.getLookup();

        List<EntityRecord> entityRecords;
        long recordCount;

        if (StringUtils.isNotBlank(lookup)) {
            entityRecords = instanceService.getEntityRecordsFromLookup(entityId, lookup, getFields(settings), queryParams);
            recordCount = instanceService.countRecordsByLookup(entityId, lookup, getFields(settings));
        } else {
            entityRecords = instanceService.getEntityRecords(entityId, queryParams);
            recordCount = instanceService.countRecords(entityId);
        }

        int rowCount = (int) Math.ceil(recordCount / (double) settings.getRows());

        return new Records<>(settings.getPage(), rowCount, entityRecords);
    }

    private Map<String, String> getFields(GridSettings gridSettings) throws IOException {
        return objectMapper.readValue(gridSettings.getFields(), new TypeReference<HashMap<String, String>>() {});
    }

    @Autowired
    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }

    @Autowired
    public void setInstanceService(InstanceService instanceService) {
        this.instanceService = instanceService;
    }
}
