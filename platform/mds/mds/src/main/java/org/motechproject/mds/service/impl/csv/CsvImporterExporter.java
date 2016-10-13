package org.motechproject.mds.service.impl.csv;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.domain.ComboboxHolder;
import org.motechproject.mds.domain.RelationshipHolder;
import org.motechproject.mds.dto.CsvImportResults;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.MetadataDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.entityinfo.EntityInfo;
import org.motechproject.mds.exception.csv.CsvImportException;
import org.motechproject.mds.helper.DataServiceHelper;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.CsvExportCustomizer;
import org.motechproject.mds.service.CsvImportCustomizer;
import org.motechproject.mds.service.DefaultCsvExportCustomizer;
import org.motechproject.mds.service.DefaultCsvImportCustomizer;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.service.impl.csv.writer.CsvTableWriter;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.PropertyUtil;
import org.motechproject.mds.util.TypeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.motechproject.mds.util.Constants.MetadataKeys.MAP_KEY_TYPE;
import static org.motechproject.mds.util.Constants.MetadataKeys.MAP_VALUE_TYPE;

/**
 * Component used for importing CSV records to the database.
 * The reason for separating import logic is keeping the db transaction and sending the MOTECH event at completion separate.
 * This bean lives in the context of the generated MDS entities bundle.
 */
public class CsvImporterExporter extends AbstractMdsExporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvImporterExporter.class);

    /**
     * Imports instances of the given entity to the database.
     * @param entityId the ID of the entity for which instances will be imported
     * @param reader reader from which the csv file will be read
     * @param continueOnError if true, import will continue with next row if exception was encountered,
     *                        if false, import process will stop and rethrow the exception
     * @return IDs of instances updated/added during import
     */
    @Transactional
    public CsvImportResults importCsv(final long entityId, final Reader reader, boolean continueOnError, boolean clearData) {
        EntityInfo entityInfo = getEntity(entityId);
        return importCsv(entityInfo, reader, continueOnError, clearData);
    }

    /**
     * Imports instances of the given entity to the database.
     * @param entityId the ID of the entity for which instances will be imported
     * @param reader reader from which the csv file will be read
     * @param importCustomizer the customizer that will be used during instance import from rows
     * @param continueOnError if true, import will continue with next row if exception was encountered,
     *                        if false, import process will stop and rethrow the exception
     * @param clearData if true, import will clear instances from table
     * @return IDs of instances updated/added during import
     */
    @Transactional
    public CsvImportResults importCsv(final long entityId, final Reader reader, CsvImportCustomizer importCustomizer, boolean continueOnError, boolean clearData) {
        EntityInfo entityInfo = getEntity(entityId);
        return importCsv(entityInfo, reader, importCustomizer, continueOnError, clearData);
    }

    /**
     * Imports instances of the given entity to the database.
     * @param entityClassName the class name of the entity for which instances will be imported
     * @param reader reader from which the csv file will be read
     * @param continueOnError if true, import will continue with next row if exception was encountered,
     *                        if false, import process will stop and rethrow the exception
     * @return IDs of instances updated/added during import
     */
    @Transactional
    public CsvImportResults importCsv(final String entityClassName, final Reader reader, boolean continueOnError) {
        EntityInfo entityInfo = getEntity(entityClassName);
        return importCsv(entityInfo, reader, continueOnError, false);
    }

    /**
     * Exports entity instances to a CSV file.
     * @param entityId id of the entity for which the instances will be exported
     * @param writer the writer that will be used for output
     * @return number of exported instances
     */
    @Transactional
    public long exportCsv(final long entityId, final Writer writer) {
        EntityInfo entityInfo = getEntity(entityId);
        try (CsvTableWriter tableWriter = new CsvTableWriter(writer)) {
            return exportData(entityInfo, tableWriter);
        }
    }

    /**
     * Exports entity instances to a CSV file.
     * @param entityClassName the class name of the entity for which instances will be exported
     * @param writer the writer that will be used for output
     * @return number of exported instances
     */
    @Transactional
    public long exportCsv(final String entityClassName, final Writer writer) {
        EntityInfo entityInfo = getEntity(entityClassName);
        try (CsvTableWriter tableWriter = new CsvTableWriter(writer)) {
            return exportData(entityInfo, tableWriter);
        }
    }

    /**
     * Exports entity instances to a CSV file.
     * @param entityId id of the entity for which the instances will be exported
     * @param writer the writer that will be used for output
     * @param exportCustomizer the customizer that will be used during export
     * @return number of exported instances
     */
    @Transactional
    public long exportCsv(final long entityId, final Writer writer, final CsvExportCustomizer exportCustomizer) {
        EntityInfo entityInfo = getEntity(entityId);
        try (CsvTableWriter tableWriter = new CsvTableWriter(writer)) {
            return exportData(entityInfo, tableWriter, exportCustomizer);
        }
    }

    /**
     * Exports entity instances to a CSV file.
     * @param entityClassName the class name of the entity for which instances will be exported
     * @param writer the writer that will be used for output
     * @param exportCustomizer the customizer that will be used during export
     * @return number of exported instances
     */
    @Transactional
    public long exportCsv(final String entityClassName, final Writer writer, final CsvExportCustomizer exportCustomizer) {
        EntityInfo entityInfo = getEntity(entityClassName);
        try (CsvTableWriter tableWriter = new CsvTableWriter(writer)) {
            return exportData(entityInfo, tableWriter, exportCustomizer);
        }
    }

    /**
     * Exports entity instances to a CSV file.
     * @param entityId id of the entity for which the instances will be exported
     * @param writer the writer that will be used for output
     * @param lookupName the name of lookup
     * @param params query parameters to be used retrieving instances
     * @param headers the headers of exported file
     * @param lookupFields the lookupFields used in the lookup
     * @return number of exported instances
     */
    @Transactional
    public long exportCsv(long entityId, Writer writer, String lookupName, QueryParams params, List<String> headers,
                          Map<String, Object> lookupFields) {
        return exportCsv(entityId, writer, lookupName, params, headers, lookupFields,
                new DefaultCsvExportCustomizer());
    }

    /**
     * Exports entity instances to a CSV file.
     * @param entityClassName the class name of the entity for which instances will be exported
     * @param writer the writer that will be used for output
     * @param lookupName the name of lookup
     * @param params query parameters to be used retrieving instances
     * @param headers the headers of exported file
     * @param lookupFields the lookupFields used in the lookup
     * @return number of exported instances
     */
    @Transactional
    public long exportCsv(String entityClassName, Writer writer, String lookupName, QueryParams params, List<String> headers,
                          Map<String, Object> lookupFields) {
        return exportCsv(entityClassName, writer, lookupName, params, headers, lookupFields,
                new DefaultCsvExportCustomizer());
    }

    /**
     * Exports entity instances to a CSV file.
     * @param entityId id of the entity for which the instances will be exported
     * @param writer the writer that will be used for output
     * @param lookupName the name of lookup
     * @param params query parameters to be used retrieving instances
     * @param headers the headers of exported file
     * @param lookupFields the lookupFields used in the lookup
     * @param exportCustomizer the customizer that will be used during export
     * @return number of exported instances
     */
    @Transactional
    public long exportCsv(long entityId, Writer writer, String lookupName, QueryParams params, List<String> headers,
                          Map<String, Object> lookupFields, CsvExportCustomizer exportCustomizer) {
        EntityInfo entityInfo = getEntity(entityId);
        try (CsvTableWriter tableWriter = new CsvTableWriter(writer)){
            return exportData(entityInfo, tableWriter, lookupName, params, headers, lookupFields,
                    exportCustomizer);
        }
    }

    /**
     * Exports entity instances to a CSV file.
     * @param entityClassName the class name of the entity for which instances will be exported
     * @param writer the writer that will be used for output
     * @param lookupName the name of lookup
     * @param params query parameters to be used retrieving instances
     * @param headers the headers of exported file
     * @param lookupFields the lookupFields used in the lookup
     * @param exportCustomizer the customizer that will be used during export
     * @return number of exported instances
     */
    @Transactional
    public long exportCsv(String entityClassName, Writer writer, String lookupName, QueryParams params, List<String> headers,
                          Map<String, Object> lookupFields, CsvExportCustomizer exportCustomizer) {
        EntityInfo entityInfo = getEntity(entityClassName);
        try (CsvTableWriter tableWriter = new CsvTableWriter(writer)){
            return exportData(entityInfo, tableWriter, lookupName, params, headers, lookupFields,
                    exportCustomizer);
        }
    }

    private CsvImportResults importCsv(final EntityInfo entityInfo, final Reader reader, boolean continueOnError, boolean clearData) {
        return importCsv(entityInfo, reader, new DefaultCsvImportCustomizer(), continueOnError, clearData);
    }

    private CsvImportResults importCsv(final EntityInfo entityInfo, final Reader reader, CsvImportCustomizer importCustomizer,
                                       boolean continueOnError, boolean clearData) {
        final MotechDataService dataService = DataServiceHelper.getDataService(getBundleContext(), entityInfo.getClassName());

        Map<String, FieldDto> fieldCacheMap = new HashMap<>();

        if (clearData) {
            dataService.deleteAll();
        }

        try (CsvMapReader csvMapReader = new CsvMapReader(reader, CsvPreference.STANDARD_PREFERENCE)) {

            List<Long> newInstanceIDs = new ArrayList<>();
            List<Long> updatedInstanceIDs = new ArrayList<>();

            Map <Integer, String> exceptions = new HashMap<>();

            Map<String, String> row;
            int rowNum = 0;

            final String headers[] = csvMapReader.getHeader(true);

            while ((row = csvMapReader.read(headers)) != null) {
                rowNum++;
                try {
                    // import a row
                    RowImportResult rowImportResult = importInstanceFromRow(entityInfo.getEntity(), row, headers, fieldCacheMap, entityInfo.getFieldDtos(), dataService, importCustomizer);
                    Long id = rowImportResult.getId();

                    // put its ID in the correct list
                    if (rowImportResult.isNewInstance()) {
                        newInstanceIDs.add(id);
                    } else {
                        updatedInstanceIDs.add(id);
                    }
                } catch (RuntimeException e){
                    if (continueOnError) {
                        exceptions.put(rowNum, e.getMessage());
                    } else {
                        throw e;
                    }
                }
            }

            return new CsvImportResults(entityInfo.getEntity(), newInstanceIDs, updatedInstanceIDs, exceptions);
        } catch (IOException e) {
            throw new CsvImportException("IO Error when importing CSV", e);
        }
    }

    private RowImportResult importInstanceFromRow(EntityDto entityDto, Map<String, String> row, String[] headers, Map<String, FieldDto> fieldMap, List<FieldDto> fields,
                                                  MotechDataService dataService, CsvImportCustomizer importCustomizer) {
        Class entityClass = dataService.getClassType();

        boolean isNewInstance = true;
        Object instance;
        try {
            instance = importCustomizer.findExistingInstance(row, dataService);
            if (instance == null) {
                LOGGER.debug("Creating new {}", entityClass.getName());
                instance = entityClass.newInstance();
            } else {
                isNewInstance = false;
                LOGGER.debug("Updating {} with id {}", entityClass.getName(), row.get(Constants.Util.ID_FIELD_NAME));
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new CsvImportException("Unable to create instance of " + entityClass.getName(), e);
        }

        for (String fieldName : headers) {
            FieldDto field = findField(fieldName, fields, fieldMap, importCustomizer);

            if (field == null) {
                LOGGER.warn("No field with name {} in entity {}, however such row exists in CSV. Ignoring.",
                        fieldName, entityClass.getName());
                continue;
            }

            if (row.containsKey(fieldName)) {
                String csvValue = row.get(fieldName);

                Object parsedValue = parseValue(entityDto, csvValue, field, entityClass.getClassLoader());

                try {
                    PropertyUtil.setProperty(instance, StringUtils.uncapitalize(field.getBasic().getName()), parsedValue);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    String msg = String.format("Error when processing field: %s, value in CSV file is %s",
                            fieldName, csvValue);
                    throw new CsvImportException(msg, e);
                }
            }
        }

        Object importedInstance;
        if (isNewInstance) {
            importedInstance = importCustomizer.doCreate(instance, dataService);
        } else {
            importedInstance = importCustomizer.doUpdate(instance, dataService);
        }

        Long importedId = (Long) PropertyUtil.safeGetProperty(importedInstance, Constants.Util.ID_FIELD_NAME);

        return new RowImportResult(importedId, isNewInstance);
    }

    private FieldDto findField(String fieldName, List<FieldDto> fields, Map<String, FieldDto> fieldMap, CsvImportCustomizer importCustomizer) {
        if (!fieldMap.containsKey(fieldName)) {
            FieldDto field = importCustomizer.findField(fieldName, fields);
            fieldMap.put(fieldName, field);
        }
        return fieldMap.get(fieldName);
    }

    private Object parseValue(EntityDto entityDto, String csvValue, FieldDto field, ClassLoader entityCl) {
        final TypeDto type = field.getType();

        Object value;
        if (type.isCombobox()) {
            value = parseComboboxValue(entityDto, csvValue, field, entityCl);
        } else if (type.isRelationship()) {
            value = parseRelationshipValue(csvValue, field);
        } else if (type.isMap()) {
            MetadataDto keyMetadata = field.getMetadata(MAP_KEY_TYPE);
            MetadataDto valueMetadata = field.getMetadata(MAP_VALUE_TYPE);
            String mapKeyType = keyMetadata != null ? keyMetadata.getValue() : String.class.getName();
            String mapValueType = valueMetadata != null ? valueMetadata.getValue() : String.class.getName();

            value = TypeHelper.parseStringToMap(mapKeyType, mapValueType, csvValue);
        } else {
            value = TypeHelper.parse(csvValue, type.getTypeClass());
        }

        // for strings, return a blank if the columns is in the file
        if (value == null && String.class.equals(type.getTypeClass())) {
            value = "";
        }

        return value;
    }

    private Object parseComboboxValue(EntityDto entityDto, String csvValue, FieldDto field, ClassLoader classLoader) {
        ComboboxHolder comboboxHolder = new ComboboxHolder(entityDto, field);
        if (comboboxHolder.isCollection()) {
            return TypeHelper.parse(csvValue, comboboxHolder.getTypeClassName(),
                    comboboxHolder.getUnderlyingType(), classLoader);
        } else {
            return TypeHelper.parse(csvValue, comboboxHolder.getUnderlyingType(), classLoader);
        }
    }

    private Object parseRelationshipValue(String csvValue, FieldDto field) {
        RelationshipHolder relationshipHolder = new RelationshipHolder(field);
        if (relationshipHolder.isManyToMany() || relationshipHolder.isOneToMany()) {
            List<Long> ids = (List<Long>) TypeHelper.parse(csvValue, List.class.getName(), Long.class.getName());

            Collection<Object> relatedObjects = buildRelationshipCollection(relationshipHolder);
            if (ids != null) {
                for (Long id : ids) {
                    Object relatedObj = getRelatedObject(id, relationshipHolder.getRelatedClass());
                    if (relatedObj != null) {
                        relatedObjects.add(relatedObj);
                    }
                }
            }
            return relatedObjects;
        } else {
            Long id = (Long) TypeHelper.parse(csvValue, Long.class);
            return getRelatedObject(id, relationshipHolder.getRelatedClass());
        }
    }

    private Collection<Object> buildRelationshipCollection(RelationshipHolder relationshipHolder) {
        String collectionClassName = relationshipHolder.getCollectionClassName();
        Class collectionClass = TypeHelper.suggestCollectionImplementation(collectionClassName);
        if (collectionClass == null) {
            return new ArrayList<>();
        } else {
            try {
                return (Collection<Object>) collectionClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new CsvImportException("Unable to parse collection type " + relationshipHolder.getCollectionClassName(), e);
            }
        }
    }

    private Object getRelatedObject(Long id, String entityClass) {
        MotechDataService dataService = DataServiceHelper.getDataService(getBundleContext(), entityClass);
        Object obj = dataService.findById(id);

        if (obj == null) {
            LOGGER.warn("Unable to find {} instance with id {}. Ignoring, you will have to create this relationship manually",
                    entityClass, id);
        }

        return obj;
    }

    /**
     * This class represents a result of a single row import.
     * It contains the ID of the created instance, it also contains information about whether it is a
     * new instance.
     */
    private class RowImportResult {
        private final Long id;
        private final boolean newInstance;

        public RowImportResult(Long id, boolean newInstance) {
            this.id = id;
            this.newInstance = newInstance;
        }

        public Long getId() {
            return id;
        }

        public boolean isNewInstance() {
            return newInstance;
        }
    }
}
