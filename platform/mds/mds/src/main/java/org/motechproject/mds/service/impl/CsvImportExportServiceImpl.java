package org.motechproject.mds.service.impl;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.domain.ComboboxHolder;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.RelationshipHolder;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.ex.entity.EntityNotFoundException;
import org.motechproject.mds.ex.csv.CsvExportException;
import org.motechproject.mds.ex.csv.CsvImportException;
import org.motechproject.mds.repository.AllEntities;
import org.motechproject.mds.service.CsvImportExportService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.helper.FieldHelper;
import org.motechproject.mds.helper.DataServiceHelper;
import org.motechproject.mds.util.PropertyUtil;
import org.motechproject.mds.util.TypeHelper;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.CsvMapWriter;
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

/**
 * Implementation of the @{link CsvImportExportServiceImpl}.
 * Uses the SuperCSV library for handling CSV files.
 */
@Service("csvImportExportServiceImpl")
public class CsvImportExportServiceImpl implements CsvImportExportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvImportExportServiceImpl.class);

    private static final char LIST_JOIN_CHAR = ',';

    @Autowired
    private AllEntities allEntities;

    @Autowired
    private BundleContext bundleContext;

    @Override
    @Transactional
    public long exportCsv(long entityId, Writer writer) {
        final Entity entity = getEntity(entityId);
        return exportCsv(entity, writer);
    }

    @Override
    @Transactional
    public long exportCsv(String entityClassName, Writer writer) {
        final Entity entity = getEntity(entityClassName);
        return exportCsv(entity, writer);
    }

    private long exportCsv(Entity entity, Writer writer) {
        final MotechDataService dataService = DataServiceHelper.getDataService(bundleContext, entity);

        final String[] headers = fieldsToHeaders(entity.getFields());
        final Map<String, Field> fieldMap = FieldHelper.fieldMapByName(entity.getFields());

        try (CsvMapWriter csvMapWriter = new CsvMapWriter(writer, CsvPreference.STANDARD_PREFERENCE)) {
            csvMapWriter.writeHeader(headers);

            long rowsExported = 0;
            Map<String, String> row = new HashMap<>();

            for (Object instance : dataService.retrieveAll()) {
                buildCsvRow(row, fieldMap, instance, headers);
                csvMapWriter.write(row, headers);
                rowsExported++;
            }

            return rowsExported;
        } catch (IOException e) {
            throw new CsvExportException("IO Error when writing CSV", e);
        }
    }

    @Override
    @Transactional
    public long importCsv(long entityId, Reader reader) {
        final Entity entity = getEntity(entityId);
        return importCsv(entity, reader);
    }

    @Override
    @Transactional
    public long importCsv(String entityClassName, Reader reader) {
        final  Entity entity = getEntity(entityClassName);
        return importCsv(entity, reader);
    }

    private long importCsv(Entity entity, Reader reader) {
        final MotechDataService dataService = DataServiceHelper.getDataService(bundleContext, entity);

        final Map<String, Field> fieldMap = FieldHelper.fieldMapByName(entity.getFields());

        try (CsvMapReader csvMapReader = new CsvMapReader(reader, CsvPreference.STANDARD_PREFERENCE)) {

            long rowsImported = 0;
            Map<String, String> row;

            final String headers[] = csvMapReader.getHeader(true);

            while ((row = csvMapReader.read(headers)) != null) {
                importInstanceFromRow(row, headers, fieldMap, dataService);
                rowsImported++;
            }

            return rowsImported;
        } catch (IOException e) {
            throw new CsvImportException("IO Error when importing CSV", e);
        }
    }

    private Entity getEntity(long entityId) {
        Entity entity = allEntities.retrieveById(entityId);
        assertEntityExists(entity);
        return entity;
    }

    private Entity getEntity(String entityClassName) {
        Entity entity = allEntities.retrieveByClassName(entityClassName);
        assertEntityExists(entity);
        return entity;
    }

    private void assertEntityExists(Entity entity) {
        if (entity == null) {
            throw new EntityNotFoundException();
        }
    }

    private String[] fieldsToHeaders(List<Field> fields) {
        List<String> fieldNames = new ArrayList<>();
        for (Field field : fields) {
            fieldNames.add(field.getName());
        }
        return fieldNames.toArray(new String[fieldNames.size()]);
    }

    private void buildCsvRow(Map<String, String> row, Map<String, Field> fieldMap, Object instance, String[] headers) {
        row.clear();
        for (String fieldName : headers) {
            Field field = fieldMap.get(fieldName);

            Object value = PropertyUtil.safeGetProperty(instance, fieldName);
            String csvValue;

            if (field.getType().isRelationship()) {
                csvValue = formatRelationship(value);
            } else {
                csvValue = TypeHelper.format(value, LIST_JOIN_CHAR);
            }
            row.put(fieldName, csvValue);
        }
    }

    private void importInstanceFromRow(Map<String, String> row, String[] headers, Map<String, Field> fieldMap,
                                       MotechDataService dataService) {
        Class entityClass = dataService.getClassType();

        boolean newInstance = true;
        Object instance = null;
        try {
            String id = row.get(Constants.Util.ID_FIELD_NAME);

            if (StringUtils.isNotBlank(id)) {
                instance = dataService.findById(Long.valueOf(id));
                if (instance != null) {
                    newInstance = false;
                    LOGGER.debug("Updating {} with {}", entityClass.getName(), id);
                }
            }

            if (instance == null) {
                LOGGER.debug("Creating new {}", entityClass.getName());
                instance = entityClass.newInstance();
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new CsvImportException("Unable to create instance of " + entityClass.getName(), e);
        }

        for (String fieldName : headers) {
            Field field = fieldMap.get(fieldName);

            if (field == null) {
                LOGGER.warn("No field with name {} in entity {}, however such row exists in CSV. Ignoring.",
                        fieldName, entityClass.getName());
                continue;
            }

            if (row.containsKey(fieldName)) {
                String csvValue = row.get(field.getName());

                Object parsedValue = parseValue(csvValue, field, entityClass.getClassLoader());

                try {
                    PropertyUtil.setProperty(instance, fieldName, parsedValue);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    String msg = String.format("Error when processing field: %s, value in CSV file is %s",
                            field.getName(), csvValue);
                    throw new CsvImportException(msg, e);
                }
            }
        }

        if (newInstance) {
            dataService.create(instance);
        } else {
            dataService.update(instance);
        }
    }

    private Object parseValue(String csvValue, Field field, ClassLoader entityCl) {
        final Type type = field.getType();

        Object value;
        if (type.isCombobox()) {
            value = parseComboboxValue(csvValue, field, entityCl);
        } else if (type.isRelationship()) {
            value = parseRelationshipValue(csvValue, field);
        } else {
            value = TypeHelper.parse(csvValue, type.getTypeClass());
        }

        // for strings, return a blank if the columns is in the file
        if (value == null && String.class.equals(type.getTypeClass())) {
            value = "";
        }

        return value;
    }

    private Object parseComboboxValue(String csvValue, Field field, ClassLoader classLoader) {
        ComboboxHolder comboboxHolder = new ComboboxHolder(field);
        if (comboboxHolder.isList()) {
            return TypeHelper.parse(csvValue, comboboxHolder.getTypeClassName(),
                    comboboxHolder.getUnderlyingType(), classLoader);
        } else {
            return TypeHelper.parse(csvValue, comboboxHolder.getUnderlyingType(), classLoader);
        }
    }

    private Object parseRelationshipValue(String csvValue, Field field) {
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
        MotechDataService dataService = DataServiceHelper.getDataService(bundleContext, entityClass);
        Object obj = dataService.findById(id);

        if (obj == null) {
            LOGGER.warn("Unable to find {} instance with id {}. Ignoring, you will have to create this relationship manually",
                    entityClass, id);
        }

        return obj;
    }

    private String formatRelationship(Object object) {
        if (object instanceof Collection) {
            int i = 0;
            StringBuilder sb = new StringBuilder();
            for (Object item : (Collection) object) {
                if (i++ != 0) {
                    sb.append(',');
                }
                sb.append(PropertyUtil.safeGetProperty(item, Constants.Util.ID_FIELD_NAME));
            }
            return sb.toString();
        } else if (object != null) {
            return String.valueOf(PropertyUtil.safeGetProperty(object, Constants.Util.ID_FIELD_NAME));
        } else {
            return "";
        }
    }
}
