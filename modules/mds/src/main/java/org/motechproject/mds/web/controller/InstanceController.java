package org.motechproject.mds.web.controller;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.motechproject.commons.api.CsvConverter;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.FieldInstanceDto;
import org.motechproject.mds.ex.EntityNotFoundException;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.InstanceService;
import org.motechproject.mds.web.comparator.EntityRecordComparator;
import org.motechproject.mds.web.comparator.HistoryRecordComparator;
import org.motechproject.mds.web.domain.EntityRecord;
import org.motechproject.mds.web.domain.FieldRecord;
import org.motechproject.mds.web.domain.GridSettings;
import org.motechproject.mds.web.domain.HistoryRecord;
import org.motechproject.mds.web.domain.PreviousRecord;
import org.motechproject.mds.web.domain.Records;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.CharEncoding.UTF_8;
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
    @Autowired
    private InstanceService instanceService;

    @RequestMapping(value = "/instances", method = RequestMethod.POST)
    @PreAuthorize(Roles.HAS_SCHEMA_ACCESS)
    public void saveInstance(@RequestBody EntityRecord record) {
        instanceService.createInstance(entityService.getEntity(record.getEntitySchemaId()), record.getFields());
    }

    @RequestMapping(value = "/instances/{instanceId}/fields", method = RequestMethod.GET)
    @PreAuthorize(Roles.HAS_DATA_ACCESS)
    @ResponseBody
    public List<FieldInstanceDto> getInstanceFields(@PathVariable Long instanceId) {
        if (null == entityService.getEntity(instanceId)) {
            throw new EntityNotFoundException();
        }

        return instanceService.getInstanceFields(instanceId);
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

        return new Records<>(settings.getPage(), settings.getRows(), historyRecordsList);
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

    @RequestMapping(value = "/entities/{entityId}/instance/{instanceId}", method = RequestMethod.GET)
    @PreAuthorize(Roles.HAS_DATA_ACCESS)
    @ResponseBody
    public List<FieldRecord> getInstance(@PathVariable Long entityId, @PathVariable String instanceId) {
        List<EntityRecord> entityList = instanceService.getEntityRecords(entityId);
        for (EntityRecord record : entityList) {
            if (record.getId().equals(instanceId)) {
                return record.getFields();
            }
        }
        throw new EntityNotFoundException();
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
    public Records<?> getInstances(@PathVariable Long entityId, @RequestBody final String url, GridSettings settings) {
        List<?> entityRecords = instanceService.getEntityRecordsPaged(entityId, settings.getPage(), settings.getRows());
        Map<String, Object> lookupMap = getLookupMap(url);

        if (!lookupMap.isEmpty()) {
            entityRecords = filterByLookup(entityRecords, lookupMap);
        }

        boolean sortAscending = settings.getSortDirection() == null || "asc".equals(settings.getSortDirection());

        //TODO: Sort records
        if (!settings.getSortColumn().isEmpty() && !entityRecords.isEmpty()) {
            Collections.sort(
                    new ArrayList<EntityRecord>(), new EntityRecordComparator(sortAscending, settings.getSortColumn())
            );
        }

        return new Records<>(entityRecords);
    }

    private List<?> filterByLookup(List<?> entityList, Map<String, Object> lookups) {
        for (Map.Entry<String, Object> entry : lookups.entrySet()) {
            Iterator<?> it = entityList.iterator();
            while (it.hasNext()) {
                Object record = it.next();
                for (Field field : record.getClass().getFields()) {
                    if (entry.getKey().equals(field.getName()) &&
                            !entry.getValue().toString().equalsIgnoreCase(field.toString())) {
                        it.remove();
                    }
                }
            }
        }

        return entityList;
    }

    private Map<String, Object> getLookupMap(String url) {
        final String fields = "fields=";

        int fieldsParam = url.indexOf(fields) + fields.length();
        String jsonFields = url.substring(fieldsParam, url.indexOf('&', fieldsParam));

        JsonFactory factory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper(factory);
        TypeReference<HashMap<String, Object>> typeRef
                = new TypeReference<
                HashMap<String, Object>
                >() {
        };
        try {
            jsonFields = URLDecoder.decode(jsonFields, "UTF-8");
            return mapper.readValue(jsonFields, typeRef);
        } catch (IOException e) {
            Logger.getLogger(EntityController.class).error("Failed to retrieve and/or parse lookup object from JSON" + e);
        }

        return new HashMap<>();
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
