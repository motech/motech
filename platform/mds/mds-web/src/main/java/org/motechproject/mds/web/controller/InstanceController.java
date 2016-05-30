package org.motechproject.mds.web.controller;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.motechproject.mds.dto.CsvImportResults;
import org.motechproject.mds.dto.FieldInstanceDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.ex.csv.CsvImportException;
import org.motechproject.mds.ex.entity.EntityNotFoundException;
import org.motechproject.mds.filter.Filter;
import org.motechproject.mds.filter.Filters;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.CsvImportExportService;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.web.domain.BasicEntityRecord;
import org.motechproject.mds.web.domain.BasicHistoryRecord;
import org.motechproject.mds.web.domain.EntityRecord;
import org.motechproject.mds.web.domain.FieldRecord;
import org.motechproject.mds.web.domain.GridSettings;
import org.motechproject.mds.web.domain.HistoryRecord;
import org.motechproject.mds.web.domain.Records;
import org.motechproject.mds.web.service.InstanceService;
import org.motechproject.mds.web.util.query.QueryParamsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.CharEncoding.UTF_8;

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
    private InstanceService instanceService;

    @Autowired
    private CsvImportExportService csvImportExportService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping(value = "/instances", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void saveInstance(@RequestBody EntityRecord record) {
        instanceService.saveInstance(decodeBlobFiles(record));
    }

    @RequestMapping(value = "/instances/{instanceId}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void updateInstance(@RequestBody EntityRecord record) {
        instanceService.saveInstance(decodeBlobFiles(record));
    }

    @RequestMapping(value = "/instances/deleteBlob/{entityId}/{instanceId}/{fieldId}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void deleteBlobContent(@PathVariable Long entityId, @PathVariable Long instanceId, @PathVariable Long fieldId) {
        EntityRecord record = instanceService.getEntityInstance(entityId, instanceId);
        instanceService.saveInstance(record, fieldId);
    }

    @RequestMapping(value = "/instances/{entityId}/new")
    @ResponseBody
    public EntityRecord newInstance(@PathVariable Long entityId) {
        return instanceService.newInstance(entityId);
    }

    @RequestMapping(value = "/instances/{entityId}/{instanceId}/fields", method = RequestMethod.GET)
    @ResponseBody
    public List<FieldInstanceDto> getInstanceFields(@PathVariable Long entityId, @PathVariable Long instanceId) {
        return instanceService.getInstanceFields(entityId, instanceId);
    }

    @RequestMapping(value = "/instances/{entityId}/{instanceId}/{fieldName}", method = RequestMethod.GET)
    @ResponseBody
    public void getBlobField(@PathVariable Long entityId, @PathVariable Long instanceId, @PathVariable String fieldName, HttpServletResponse response) throws IOException {
        byte[] content;
        Object value = instanceService.getInstanceField(entityId, instanceId, fieldName);
        if (value instanceof  byte[]) {
            content = (byte[]) value;
        } else {
            content = ArrayUtils.toPrimitive((Byte[]) value);
        }

        try (OutputStream outputStream = response.getOutputStream()) {
            response.setHeader("Accept-Ranges", "bytes");

            if (content.length == 0) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
            }

            outputStream.write(content);
        }
    }

    @RequestMapping(value = "/instances/{entityId}/delete/{instanceId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteInstance(@PathVariable Long entityId, @PathVariable Long instanceId) {
        instanceService.deleteInstance(entityId, instanceId);
    }

    @RequestMapping(value = "/instances/{entityId}/revertFromTrash/{instanceId}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public void revertInstanceFromTrash(@PathVariable Long entityId, @PathVariable Long instanceId) {
        instanceService.revertInstanceFromTrash(entityId, instanceId);
    }

    @RequestMapping(value = "/instances/{entityId}/{instanceId}/history", method = RequestMethod.GET)
    @ResponseBody
    public Records<BasicHistoryRecord> getHistory(@PathVariable Long entityId, @PathVariable Long instanceId, GridSettings settings) {
        QueryParams queryParams = QueryParamsBuilder.buildQueryParams(settings);
        List<BasicHistoryRecord> historyRecordsList = instanceService.getInstanceHistory(entityId, instanceId, queryParams);

        long recordCount = instanceService.countHistoryRecords(entityId, instanceId);
        int rowCount = (int) Math.ceil(recordCount / (double) queryParams.getPageSize());

        return new Records<>(queryParams.getPage(), rowCount, (int) recordCount, historyRecordsList);
    }

    @RequestMapping(value = "/instances/{entityId}/{instanceId}/previousVersion/{historyId}", method = RequestMethod.GET)
    @ResponseBody
    public HistoryRecord getPreviousInstance(@PathVariable Long entityId, @PathVariable Long instanceId, @PathVariable Long historyId) {
        HistoryRecord historyRecord = instanceService.getHistoryRecord(entityId, instanceId, historyId);
        if (historyRecord == null) {
            throw new EntityNotFoundException(entityId);
        }
        return historyRecord;
    }

    @RequestMapping(value = "/instances/{entityId}/{instanceId}/revert/{historyId}", method = RequestMethod.GET)
    @ResponseBody
    public void revertPreviousVersion(@PathVariable Long entityId, @PathVariable Long instanceId, @PathVariable Long historyId) {
        instanceService.revertPreviousVersion(entityId, instanceId, historyId);
    }

    @RequestMapping(value = "/instances/{entityId}/instance/{instanceId}", method = RequestMethod.GET)
    @ResponseBody
    public EntityRecord getInstance(@PathVariable Long entityId, @PathVariable Long instanceId) {
        return instanceService.getEntityInstance(entityId, instanceId);
    }

    /**
     * Retrieves instance and builds field value from it. Used when user adds related instances
     * in Data Browser UI.
     *
     *
     * @param entityId the id of entity which has related field
     * @param fieldId the id of related field
     * @param instanceId the id of instance which will be related to given field
     * @return instance value as related field
     */
    @RequestMapping(value = "/instances/{entityId}/field/{fieldId}/instance/{instanceId}", method = RequestMethod.GET)
    @ResponseBody
    public FieldRecord getInstanceValueAsRelatedField(@PathVariable Long entityId, @PathVariable Long fieldId, @PathVariable Long instanceId) {
        return instanceService.getInstanceValueAsRelatedField(entityId, fieldId, instanceId);
    }

    @RequestMapping(value = "/entities/{entityId}/trash", method = RequestMethod.GET)
    @ResponseBody
    public Records<BasicEntityRecord> getTrash(@PathVariable Long entityId, GridSettings settings) {
        QueryParams queryParams = QueryParamsBuilder.buildQueryParams(settings);
        List<BasicEntityRecord> trashRecordsList = instanceService.getTrashRecords(entityId, queryParams);

        long recordCount = instanceService.countTrashRecords(entityId);
        int rowCount = (int) Math.ceil(recordCount / (double) queryParams.getPageSize());

        return new Records<>(queryParams.getPage(), rowCount, (int) recordCount, trashRecordsList);

    }

    @RequestMapping(value = "/entities/{entityId}/trash/{instanceId}", method = RequestMethod.GET)
    @ResponseBody
    public EntityRecord getSingleTrashInstance(@PathVariable Long entityId, @PathVariable Long instanceId) {
        return instanceService.getSingleTrashRecord(entityId, instanceId);
    }

    @RequestMapping(value = "/entities/{entityId}/exportInstances", method = RequestMethod.GET)
    public void exportEntityInstances(@PathVariable Long entityId, GridSettings settings,
                                      @RequestParam String exportRecords,
                                      @RequestParam String outputFormat,
                                      HttpServletResponse response) throws IOException {
        if (!Constants.ExportFormat.isValidFormat(outputFormat)) {
            throw new IllegalArgumentException("Invalid export format: " + outputFormat);
        }

        instanceService.verifyEntityAccess(entityId);

        final String fileName = "Entity_" + entityId + "_instances";

        response.setContentType("text/csv");
        response.setCharacterEncoding(UTF_8);
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=" + fileName + "." + outputFormat.toLowerCase());

        final Integer pageSize = StringUtils.equalsIgnoreCase(exportRecords, "all") ? null : Integer.valueOf(exportRecords);
        final Map<String, Object> fieldMap = getFields(settings);

        QueryParams queryParams = new QueryParams(1, pageSize, QueryParamsBuilder.buildOrderList(settings, fieldMap));

        if (Constants.ExportFormat.PDF.equals(outputFormat)) {
            csvImportExportService.exportPdf(entityId, response.getOutputStream(), settings.getLookup(), queryParams,
                    settings.getSelectedFields(), fieldMap);
        } else {
            csvImportExportService.exportCsv(entityId, response.getWriter(), settings.getLookup(), queryParams,
                    settings.getSelectedFields(), fieldMap);
        }
    }

    @RequestMapping(value = "/entities/{entityId}/instances", method = RequestMethod.POST)
    @ResponseBody
    public Records<BasicEntityRecord> getInstances(@PathVariable Long entityId, GridSettings settings) throws IOException {
        String lookup = settings.getLookup();
        String filterStr = settings.getFilter();
        Map<String, Object> fieldMap = getFields(settings);

        QueryParams queryParams = QueryParamsBuilder.buildQueryParams(settings, fieldMap);

        List<BasicEntityRecord> entityRecords;
        long recordCount;

        if (StringUtils.isNotBlank(lookup)) {
            entityRecords = instanceService.getEntityRecordsFromLookup(entityId, lookup, fieldMap, queryParams);
            recordCount = instanceService.countRecordsByLookup(entityId, lookup, fieldMap);
        } else if (filterSet(filterStr)) {
            Filters filters = new Filters(objectMapper.readValue(filterStr, Filter[].class));
            filters.setMultiselect(instanceService.getEntityFields(entityId));

            entityRecords = instanceService.getEntityRecordsWithFilter(entityId, filters, queryParams);
            recordCount = instanceService.countRecordsWithFilters(entityId, filters);
        } else {
            entityRecords = instanceService.getEntityRecords(entityId, queryParams);
            recordCount = instanceService.countRecords(entityId);
        }

        int rowCount = (int) Math.ceil(recordCount / (double) queryParams.getPageSize());

        return new Records<>(queryParams.getPage(), rowCount, (int) recordCount, entityRecords);
    }

    @RequestMapping(value = "/instances/{entityId}/csvimport", method = RequestMethod.POST)
    @ResponseBody
    public long importCsv(@PathVariable long entityId, @RequestParam(required = true)  MultipartFile csvFile) {
        instanceService.verifyEntityAccess(entityId);
        instanceService.validateNonEditableProperty(entityId);
        try {
            try (InputStream in = csvFile.getInputStream()) {
                Reader reader = new InputStreamReader(in);
                CsvImportResults results = csvImportExportService.importCsv(entityId, reader, csvFile.getOriginalFilename());
                return results.totalNumberOfImportedInstances();
            }
        } catch (IOException e) {
            throw new CsvImportException("Unable to open uploaded file", e);
        }
    }

    private Map<String, Object> getFields(GridSettings gridSettings) throws IOException {
        if (gridSettings.getFields() == null) {
            return null;
        } else {
            return objectMapper.readValue(gridSettings.getFields(), new TypeReference<LinkedHashMap>() {});
        }
    }

    private boolean filterSet(String filterStr) {
        return StringUtils.isNotBlank(filterStr) && !"[]".equals(filterStr);
    }

    private EntityRecord decodeBlobFiles(EntityRecord record) {
        for (FieldRecord field : record.getFields()) {
            if (TypeDto.BLOB.getTypeClass().equals(field.getType().getTypeClass())) {
                byte[] content = field.getValue() != null ?
                        field.getValue().toString().getBytes() :
                        ArrayUtils.EMPTY_BYTE_ARRAY;

                field.setValue(decodeBase64(content));
            }
        }
        return record;
    }

    private Byte[] decodeBase64(byte[] content) {
        if (content == null || content.length == 0) {
            return null;
        }

        Base64 decoder = new Base64();
        //We must remove "data:(content type);base64," prefix and then decode content
        int index = ArrayUtils.indexOf(content, (byte) ',') + 1;

        return ArrayUtils.toObject(decoder.decode(ArrayUtils.subarray(content, index, content.length)));
    }
}
