package org.motechproject.mds.service.impl.csv;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.ex.csv.DataExportException;
import org.motechproject.mds.ex.entity.EntityNotFoundException;
import org.motechproject.mds.helper.DataServiceHelper;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.service.CsvExportCustomizer;
import org.motechproject.mds.service.DefaultCsvExportCustomizer;
import org.motechproject.mds.service.MDSLookupService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.service.impl.csv.writer.TableWriter;
import org.motechproject.mds.util.PropertyUtil;
import org.motechproject.mds.util.TypeHelper;
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

    private static final char LIST_JOIN_CHAR = ',';

    @Autowired
    private BundleContext bundleContext;

    @Autowired
    private AllEntities allEntities;

    @Autowired
    private MDSLookupService mdsLookupService;

    protected long exportData(Entity entity, TableWriter writer) {
        return exportData(entity, writer, new DefaultCsvExportCustomizer());
    }

    protected long exportData(Entity entity, TableWriter writer, CsvExportCustomizer exportCustomizer) {
        return exportData(entity, writer, "", null, null, null, exportCustomizer);
    }

    protected long exportData(Entity entity, TableWriter writer, String lookupName, QueryParams params, List<String> headers,
                           Map<String, Object> lookupFields, CsvExportCustomizer exportCustomizer) {
        final MotechDataService dataService = DataServiceHelper.getDataService(bundleContext, entity);

        final Map<String, Field> fieldMap = new HashMap<>();
        for (Field field : entity.getFields()) {
            fieldMap.put(exportCustomizer.exportDisplayName(field), field);
        }

        // we must respect field ordering
        String[] orderedHeaders = orderHeaders(headers == null ? fieldsToHeaders(entity.getFields(), exportCustomizer) : headers.toArray(new String[headers.size()]),
                entity.getFields(), exportCustomizer);

        try {
            writer.writeHeader(orderedHeaders);

            long rowsExported = 0;
            Map<String, String> row = new HashMap<>();

            List<Object> instances = StringUtils.isBlank(lookupName) ? dataService.retrieveAll(params) :
                    mdsLookupService.findMany(entity.getClassName(), lookupName, lookupFields, params);

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

    protected Entity getEntity(long entityId) {
        Entity entity = allEntities.retrieveById(entityId);
        assertEntityExists(entity);
        return entity;
    }

    protected Entity getEntity(String entityClassName) {
        Entity entity = allEntities.retrieveByClassName(entityClassName);
        assertEntityExists(entity);
        return entity;
    }

    private void assertEntityExists(Entity entity) {
        if (entity == null) {
            throw new EntityNotFoundException();
        }
    }

    protected String[] orderHeaders(String[] selectedHeaders, List<Field> entityFields, CsvExportCustomizer customizer) {
        Set<String> selectedHeadersSet = new HashSet<>(Arrays.asList(selectedHeaders));
        TreeSet<Field> orderedFields = new TreeSet<>(customizer.columnOrderComparator());

        for (Field field : entityFields) {
            if (selectedHeadersSet.contains(customizer.exportDisplayName(field))) {
                orderedFields.add(field);
            }
        }

        // after ordering, we are only interested in field names
        List<String> headers = new ArrayList<>();
        for (Field field : orderedFields) {
            headers.add(customizer.exportDisplayName(field));
        }

        return headers.toArray(new String[headers.size()]);
    }

    private String[] fieldsToHeaders(List<Field> fields, CsvExportCustomizer customizer) {
        List<String> fieldNames = new ArrayList<>();
        for (Field field : fields) {
            fieldNames.add(customizer.exportDisplayName(field));
        }

        return fieldNames.toArray(new String[fieldNames.size()]);
    }

    private void buildCsvRow(Map<String, String> row, Map<String, Field> fieldMap, Object instance, String[] headers,
                             CsvExportCustomizer exportCustomizer) {
        row.clear();
        for (String fieldName : headers) {
            Field field = fieldMap.get(fieldName);

            Object value = PropertyUtil.safeGetProperty(instance, field.getName());
            String csvValue;

            if (field.getType().isRelationship()) {
                csvValue = exportCustomizer.formatRelationship(value);
            } else {
                csvValue = TypeHelper.format(value, LIST_JOIN_CHAR);
            }
            row.put(fieldName, csvValue);
        }
    }

    protected BundleContext getBundleContext() {
        return bundleContext;
    }

    protected AllEntities getAllEntities() {
        return allEntities;
    }

    protected MDSLookupService getMdsLookupService() {
        return mdsLookupService;
    }
}
