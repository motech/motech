package org.motechproject.mds.service.impl.csv;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.dto.BrowsingSettingsDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.entityinfo.EntityInfo;
import org.motechproject.mds.entityinfo.EntityInfoReader;
import org.motechproject.mds.exception.csv.DataExportException;
import org.motechproject.mds.exception.entity.EntityNotFoundException;
import org.motechproject.mds.helper.DataServiceHelper;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.CsvExportCustomizer;
import org.motechproject.mds.service.DefaultCsvExportCustomizer;
import org.motechproject.mds.service.MDSLookupService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.service.impl.csv.writer.TableWriter;
import org.motechproject.mds.util.PropertyUtil;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Base class used by classes responsible for exporting MDS Data in a tabular CSV-like form.
 * Using the {@link TableWriter} class, implementing classes can provide their own ouput format.
 */
public abstract class AbstractMdsExporter {

    @Autowired
    private BundleContext bundleContext;

    @Autowired
    private MDSLookupService mdsLookupService;

    @Autowired
    private EntityInfoReader entityInfoReader;

    protected long exportData(EntityInfo entityInfo, TableWriter writer) {
        return exportData(entityInfo, writer, new DefaultCsvExportCustomizer());
    }

    protected long exportData(EntityInfo entityInfo, TableWriter writer, CsvExportCustomizer exportCustomizer) {
        return exportData(entityInfo, writer, "", null, null, null, exportCustomizer);
    }

    protected long exportData(EntityInfo entityInfo, TableWriter writer, String lookupName, QueryParams params, List<String> headers,
                           Map<String, Object> lookupFields, CsvExportCustomizer exportCustomizer) {
        final MotechDataService dataService = DataServiceHelper.getDataService(bundleContext, entityInfo.getClassName());

        final Map<String, FieldDto> fieldMap = new HashMap<>();
        for (FieldDto field : entityInfo.getFieldDtos()) {
            fieldMap.put(exportCustomizer.exportDisplayName(field), field);
        }

        // we must respect field ordering
        String[] orderedHeaders = orderHeaders(entityInfo.getAdvancedSettings().getBrowsing(), headers == null ? fieldsToHeaders(entityInfo.getFieldDtos(), exportCustomizer) : headers.toArray(new String[headers.size()]),
                entityInfo.getFieldDtos(), exportCustomizer);

        try {
            writer.writeHeader(orderedHeaders);

            long rowsExported = 0;
            Map<String, String> row = new HashMap<>();

            List<Object> instances = StringUtils.isBlank(lookupName) ? dataService.retrieveAll(params) :
                    mdsLookupService.findMany(entityInfo.getClassName(), lookupName, lookupFields, params);

            for (Object instance : instances) {
                buildCsvRow(row, fieldMap, instance, orderedHeaders, exportCustomizer);
                writer.writeRow(row, orderedHeaders);
                rowsExported++;
            }

            return rowsExported;
        } catch (IOException e) {
            throw new DataExportException("IO Error when writing data", e);
        }
    }

    protected EntityInfo getEntity(long entityId) {
        EntityInfo entityInfo = entityInfoReader.getEntityInfo(entityId);
        if (entityInfo == null) {
            throw new EntityNotFoundException(entityId);
        }
        return entityInfo;
    }

    protected EntityInfo getEntity(String entityClassName) {
        EntityInfo entityInfo = entityInfoReader.getEntityInfo(entityClassName);
        if (entityInfo == null) {
            throw new EntityNotFoundException(entityClassName);
        }
        return entityInfo;
    }

    protected String[] orderHeaders(BrowsingSettingsDto browsingSettingsDtos, String[]selectedHeaders, List<FieldDto> entityFields, CsvExportCustomizer customizer) {
        Set<String> selectedHeadersSet = new HashSet<>(Arrays.asList(selectedHeaders));
        TreeSet<FieldDto> orderedFields = new TreeSet<>(customizer.columnOrderComparator(browsingSettingsDtos));

        for (FieldDto field : entityFields) {
            if (selectedHeadersSet.contains(customizer.exportDisplayName(field))) {
                orderedFields.add(field);
            }
        }

        // after ordering, we are only interested in field names
        List<String> headers = new ArrayList<>();
        for (FieldDto field : orderedFields) {
            headers.add(customizer.exportDisplayName(field));
        }

        return headers.toArray(new String[headers.size()]);
    }

    private String[] fieldsToHeaders(List<FieldDto> fields, CsvExportCustomizer customizer) {
        List<String> fieldNames = new ArrayList<>();
        for (FieldDto field : fields) {
            fieldNames.add(customizer.exportDisplayName(field));
        }

        return fieldNames.toArray(new String[fieldNames.size()]);
    }

    private void buildCsvRow(Map<String, String> row, Map<String, FieldDto> fieldMap, Object instance, String[] headers,
                             CsvExportCustomizer exportCustomizer) {
        row.clear();
        for (String fieldName : headers) {
            FieldDto field = fieldMap.get(fieldName);

            Object value = PropertyUtil.safeGetProperty(instance, field.getBasic().getName());
            String csvValue = exportCustomizer.formatField(field, value);

            row.put(fieldName, csvValue);
        }
    }

    protected BundleContext getBundleContext() {
        return bundleContext;
    }

    protected MDSLookupService getMdsLookupService() {
        return mdsLookupService;
    }

    protected EntityInfoReader getEntityInfoReader() {
        return entityInfoReader;
    }
}
