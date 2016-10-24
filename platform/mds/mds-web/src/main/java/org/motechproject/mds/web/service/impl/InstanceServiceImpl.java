package org.motechproject.mds.web.service.impl;

import javassist.CannotCompileException;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.commons.lang.reflect.MethodUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.display.DisplayHelper;
import org.motechproject.mds.domain.EntityType;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.FieldInstanceDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.MetadataDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.exception.entity.EntityInstancesNonEditableException;
import org.motechproject.mds.exception.entity.EntityNotFoundException;
import org.motechproject.mds.exception.field.FieldNotFoundException;
import org.motechproject.mds.exception.field.FieldReadOnlyException;
import org.motechproject.mds.exception.lookup.LookupExecutionException;
import org.motechproject.mds.exception.lookup.LookupExecutorException;
import org.motechproject.mds.exception.lookup.LookupNotFoundException;
import org.motechproject.mds.exception.object.ObjectCreateException;
import org.motechproject.mds.exception.object.ObjectNotFoundException;
import org.motechproject.mds.exception.object.ObjectUpdateException;
import org.motechproject.mds.exception.object.ObjectReadException;
import org.motechproject.mds.exception.object.SecurityException;
import org.motechproject.mds.filter.Filters;
import org.motechproject.mds.helper.DataServiceHelper;
import org.motechproject.mds.helper.bundle.MdsBundleHelper;
import org.motechproject.mds.lookup.LookupExecutor;
import org.motechproject.mds.query.InMemoryQueryFilter;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.HistoryService;
import org.motechproject.mds.service.HistoryTrashClassHelper;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.service.TrashService;
import org.motechproject.mds.service.TypeService;
import org.motechproject.mds.service.UserPreferencesService;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.MDSClassLoader;
import org.motechproject.mds.util.MemberUtil;
import org.motechproject.mds.util.PropertyUtil;
import org.motechproject.mds.util.SecurityMode;
import org.motechproject.mds.util.StateManagerUtil;
import org.motechproject.mds.util.TypeHelper;
import org.motechproject.mds.web.domain.BasicEntityRecord;
import org.motechproject.mds.web.domain.BasicFieldRecord;
import org.motechproject.mds.web.domain.BasicHistoryRecord;
import org.motechproject.mds.web.domain.ComboboxHolder;
import org.motechproject.mds.web.domain.EntityRecord;
import org.motechproject.mds.web.domain.FieldRecord;
import org.motechproject.mds.web.domain.HistoryRecord;
import org.motechproject.mds.web.domain.Records;
import org.motechproject.mds.web.domain.RelationshipsUpdate;
import org.motechproject.mds.web.service.InstanceService;
import org.motechproject.mds.web.util.RelationshipDisplayUtil;
import org.motechproject.osgi.web.util.WebBundleUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.InstanceNotFoundException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import static org.motechproject.mds.util.Constants.MetadataKeys.MAP_KEY_TYPE;
import static org.motechproject.mds.util.Constants.MetadataKeys.MAP_VALUE_TYPE;
import static org.motechproject.mds.util.Constants.MetadataKeys.RELATED_CLASS;
import static org.motechproject.mds.util.Constants.Util.ID_FIELD_NAME;
import static org.motechproject.mds.util.SecurityUtil.getUsername;

/**
 * Default implementation of the {@link org.motechproject.mds.web.service.InstanceService} interface.
 */
@Service
public class InstanceServiceImpl implements InstanceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceServiceImpl.class);
    private static final int MAX_LENGTH = 80;
    private static final String LOOKUP_EXCEPTION_MESSAGE_KEY = "mds.error.lookupExecError";

    private EntityService entityService;
    private BundleContext bundleContext;
    private HistoryService historyService;
    private TrashService trashService;
    private TypeService typeService;
    private RelationshipDisplayUtil relationshipDisplayUtil;
    private UserPreferencesService userPreferencesService;

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    @Transactional
    public Object saveInstance(EntityRecord entityRecord) {
        return saveInstance(entityRecord, null);
    }

    @Override
    @Transactional
    public Object saveInstance(EntityRecord entityRecord, Long deleteValueFieldId) {
        EntityDto entity = getEntity(entityRecord.getEntitySchemaId());
        validateCredentials(entity);
        validateNonEditableProperty(entity);

        List<FieldDto> entityFields = getEntityFields(entityRecord.getEntitySchemaId());
 
        try {
            MotechDataService service = getServiceForEntity(entity);
            Class<?> entityClass = getEntityClass(entity);

            boolean newObject = entityRecord.getId() == null;

            Object instance;
            if (newObject) {
                instance = newInstanceFromEntityRecord(entityClass, entityFields, entityRecord.getFields(), service);
                return service.create(instance);
            } else {
                instance = service.retrieve(ID_FIELD_NAME, entityRecord.getId());
                if (instance == null) {
                    throw new ObjectNotFoundException(entity.getName(), entityRecord.getId());
                }
                updateFields(instance, entityRecord.getFields(), service, deleteValueFieldId, true);
                return service.update(instance);
            }
        } catch (Exception e) {
            if (entityRecord.getId() == null) {
                throw new ObjectCreateException(entity.getName(), e);
            } else {
                throw new ObjectUpdateException(entity.getName(), entityRecord.getId(), e);
            }
        }
    }

    @Override
    public List<BasicEntityRecord> getEntityRecords(Long entityId) {
        return getEntityRecords(entityId, null);
    }

    @Override
    public List<BasicEntityRecord> getEntityRecords(Long entityId, QueryParams queryParams) {
        EntityDto entity = getEntity(entityId);
        validateCredentialsForReading(entity);
        List<FieldDto> fields = entityService.getEntityFieldsForUI(entityId);

        MotechDataService service = getServiceForEntity(entity);
        List instances = service.retrieveAll(queryParams);
        updateGridSize(entityId, queryParams);

        return instancesToBasicRecords(instances, entity, fields, service, EntityType.STANDARD);
    }

    @Override
    public List<FieldDto> getEntityFields(Long entityId) {
        validateCredentialsForReading(getEntity(entityId));
        return entityService.getEntityFieldsForUI(entityId);
    }

    @Override
    public List<BasicEntityRecord> getTrashRecords(Long entityId, QueryParams queryParams) {
        EntityDto entity = getEntity(entityId);
        validateCredentialsForReading(entity);

        MotechDataService service = getServiceForEntity(entity);
        List<FieldDto> fields = entityService.getEntityFieldsForUI(entityId);
        Collection collection = trashService.getInstancesFromTrash(entity.getClassName(), queryParams);
        updateGridSize(entityId, queryParams);

        return instancesToBasicRecords(collection, entity, fields, service, EntityType.TRASH);
    }

    @Override
    public long countTrashRecords(Long entityId) {
        EntityDto entity = getEntity(entityId);
        validateCredentialsForReading(entity);

        return trashService.countTrashRecords(entity.getClassName());
    }

    @Override
    public EntityRecord getSingleTrashRecord(Long entityId, Long instanceId) {
        EntityDto entity = getEntity(entityId);
        validateCredentialsForReading(entity);

        MotechDataService service = getServiceForEntity(entity);
        List<FieldDto> fields = entityService.getEntityFieldsForUI(entityId);
        Object instance = trashService.findTrashById(instanceId, entity.getClassName());
        Map<String, List<FieldDto>> relatedEntitiesFields = getRelatedEntitiesFields(fields);

        return instanceToRecord(instance, entity, fields, service, EntityType.TRASH, relatedEntitiesFields);
    }

    @Override
    public Object getInstanceField(Long entityId, Long instanceId, String fieldName) throws InstanceNotFoundException {
        EntityDto entity = getEntity(entityId);
        MotechDataService service = getServiceForEntity(entity);
        validateCredentialsForReading(entity);

        Object instance = service.findById(instanceId);
        if (instance == null) {
            throw new InstanceNotFoundException(String.format("Cannot find instance with id: %d", instanceId));
        }
        return service.getDetachedField(instance, fieldName);
    }

    @Override
    public List<BasicEntityRecord> getEntityRecordsFromLookup(Long entityId, String lookupName, Map<String, Object> lookupMap,
                                                         QueryParams queryParams) {
        EntityDto entity = getEntity(entityId);
        validateCredentialsForReading(entity);

        LookupDto lookup = getLookupByName(entityId, lookupName);
        List<FieldDto> fields = entityService.getEntityFieldsForUI(entityId);
        Map<String, FieldDto> fieldMap = entityService.getLookupFieldsMapping(entityId, lookupName);

        MotechDataService service = getServiceForEntity(entity);

        try {
            LookupExecutor lookupExecutor = new LookupExecutor(service, lookup, fieldMap);

            Object result = lookupExecutor.execute(lookupMap, queryParams);

            if (lookup.isSingleObjectReturn()) {
                BasicEntityRecord record = instanceToBasicRecord(result, entity, fields, service, EntityType.STANDARD,
                        getRelatedEntitiesFields(fields));
                return (record == null) ? new ArrayList<BasicEntityRecord>() : Collections.singletonList(record);
            } else {
                List instances = (List) result;
                return instancesToBasicRecords(instances, entity, fields, service, EntityType.STANDARD);
            }
        } catch (LookupExecutorException e) {
            if (e.getMessageKey() != null) {
                throw new LookupExecutionException(e, e.getMessageKey());
            } else {
                throw new LookupExecutionException(e, LOOKUP_EXCEPTION_MESSAGE_KEY);
            }
        } catch (RuntimeException e) {
            throw new LookupExecutionException(e, LOOKUP_EXCEPTION_MESSAGE_KEY);
        }
    }

    @Override
    public List<BasicEntityRecord> getEntityRecordsWithFilter(Long entityId, Filters filters, QueryParams queryParams) {
        EntityDto entity = getEntity(entityId);
        validateCredentialsForReading(entity);

        List<FieldDto> fields = entityService.getEntityFieldsForUI(entityId);
        MotechDataService service = getServiceForEntity(entity);

        List instances = service.filter(filters, queryParams);

        return instancesToBasicRecords(instances, entity, fields, service, EntityType.STANDARD);
    }

    @Override
    public long countRecordsWithFilters(Long entityId, Filters filters) {
        EntityDto entity = getEntity(entityId);
        validateCredentialsForReading(entity);
        MotechDataService service = getServiceForEntity(entity);

        return service.countForFilters(filters);
    }

    @Override
    public long countRecords(Long entityId) {
        EntityDto entity = getEntity(entityId);
        validateCredentialsForReading(entity);
        MotechDataService service = getServiceForEntity(entity);

        return service.count();
    }

    @Override
    public long countRecordsByLookup(Long entityId, String lookupName, Map<String, Object> lookupMap) {
        EntityDto entity = getEntity(entityId);
        validateCredentialsForReading(entity);

        LookupDto lookup = getLookupByName(entityId, lookupName);
        Map<String, FieldDto> fieldMap = entityService.getLookupFieldsMapping(entityId, lookupName);

        MotechDataService service = getServiceForEntity(entity);

        try {
            LookupExecutor lookupExecutor = new LookupExecutor(service, lookup, fieldMap);
            return lookupExecutor.executeCount(lookupMap);
        } catch (RuntimeException e) {
            throw new LookupExecutionException(e, LOOKUP_EXCEPTION_MESSAGE_KEY);
        }
    }

    @Override
    public void revertPreviousVersion(Long entityId, Long instanceId, Long historyId) {
        validateNonEditableProperty(entityId);
        EntityDto entity = getEntity(entityId);
        MotechDataService service = getServiceForEntity(entity);
        service.revertToHistoricalRevision(instanceId, historyId);
    }

    @Override
    public List<FieldInstanceDto> getInstanceFields(Long entityId, Long instanceId) {
        EntityDto entity = entityService.getEntity(entityId);
        validateCredentialsForReading(entity);

        List<FieldDto> fields = entityService.getEntityFieldsForUI(entityId);

        List<FieldInstanceDto> result = new ArrayList<>();
        for (FieldDto field : fields) {
            FieldInstanceDto fieldInstanceDto = new FieldInstanceDto(field.getId(), instanceId, field.getBasic());
            result.add(fieldInstanceDto);
        }

        return result;
    }

    @Override
    public List<BasicHistoryRecord> getInstanceHistory(Long entityId, Long instanceId, QueryParams queryParams) {
        EntityDto entity = getEntity(entityId);
        validateCredentialsForReading(entity);
        MotechDataService service = getServiceForEntity(entity);

        Object instance = service.retrieve(ID_FIELD_NAME, instanceId);

        List history = historyService.getHistoryForInstance(instance, queryParams);
        updateGridSize(entityId, queryParams);

        List<BasicHistoryRecord> result = new ArrayList<>();
        for (Object o : history) {
            result.add(convertToBasicHistoryRecord(o, entity, instanceId, service));
        }
        return result;
    }

    @Override
    public long countHistoryRecords(Long entityId, Long instanceId) {
        EntityDto entity = getEntity(entityId);
        validateCredentialsForReading(entity);
        MotechDataService service = getServiceForEntity(entity);

        Object instance = service.retrieve(ID_FIELD_NAME, instanceId);

        return historyService.countHistoryRecords(instance);
    }

    @Override
    public HistoryRecord getHistoryRecord(Long entityId, Long instanceId, Long historyId) {
        EntityDto entity = getEntity(entityId);
        validateCredentialsForReading(entity);
        MotechDataService service = getServiceForEntity(entity);

        Object instance = service.retrieve(ID_FIELD_NAME, instanceId);

        Object historyInstance = historyService.getSingleHistoryInstance(instance, historyId);

        return convertToHistoryRecord(historyInstance, entity, instanceId, service);
    }

    @Override
    public EntityRecord newInstance(Long entityId) {
        validateCredentials(getEntity(entityId));
        List<FieldDto> fields = entityService.getEntityFieldsForUI(entityId);
        List<FieldRecord> fieldRecords = new ArrayList<>();

        for (FieldDto field : fields) {
            FieldRecord fieldRecord = new FieldRecord(field);
            fieldRecords.add(fieldRecord);
        }
        populateDefaultFields(fieldRecords);

        return new EntityRecord(null, entityId, fieldRecords);
    }

    @Override
    public EntityRecord getEntityInstance(Long entityId, Long instanceId) {
        EntityDto entity = getEntity(entityId);
        validateCredentialsForReading(entity);
        MotechDataService service = getServiceForEntity(entity);

        Object instance = service.retrieve(ID_FIELD_NAME, instanceId);
        if (instance == null) {
            throw new ObjectNotFoundException(entity.getName(), instanceId);
        }

        List<FieldDto> fields = entityService.getEntityFieldsForUI(entityId);
        Map<String, List<FieldDto>> relatedEntitiesFields = getRelatedEntitiesFields(fields);

        return instanceToRecord(instance, entity, fields, service, EntityType.STANDARD, relatedEntitiesFields);
    }

    @Override
    public FieldRecord getInstanceValueAsRelatedField(Long entityId, Long fieldId, Long instanceId) {
        validateCredentialsForReading(getEntity(entityId));
        try {
            FieldRecord fieldRecord;
            FieldDto field = entityService.getEntityFieldById(entityId, fieldId);
            MotechDataService service = DataServiceHelper.getDataService(bundleContext, field.getMetadata(RELATED_CLASS).getValue());

            Object instance = service.findById(instanceId);
            if (instance == null) {
                throw new ObjectNotFoundException(service.getClassType().getName(), instanceId);
            }

            List<FieldDto> relatedEntityFields = getEntityFieldsByClassName(field.getMetadata(RELATED_CLASS).getValue());

            fieldRecord = new FieldRecord(field);
            fieldRecord.setValue(parseValueForDisplay(instance, relatedEntityFields));
            fieldRecord.setDisplayValue(instance.toString());
            return fieldRecord;
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new ObjectReadException(entityId, e);
        }
    }

    @Override
    public void deleteInstance(Long entityId, Long instanceId) {
        EntityDto entity = getEntity(entityId);
        validateCredentials(entity);
        validateNonEditableProperty(entity);
        MotechDataService service = getServiceForEntity(entity);

        service.delete(ID_FIELD_NAME, instanceId);
    }

    @Override
    public void revertInstanceFromTrash(Long entityId, Long instanceId) {
        EntityDto entity = getEntity(entityId);
        validateCredentials(entity);
        validateNonEditableProperty(entity);

        MotechDataService service = getServiceForEntity(entity);

        service.revertFromTrash(instanceId);
    }

    @Override
    public void verifyEntityAccess(Long entityId) {
        EntityDto entity = getEntity(entityId);
        validateCredentialsForReading(entity);
    }

    @Override
    public void validateNonEditableProperty(Long entityId) {
        validateNonEditableProperty(getEntity(entityId));
    }

    private void updateGridSize(Long entityId, QueryParams queryParams) {
        String username = getUsername();
        if (queryParams != null && StringUtils.isNotBlank(username)) {
            userPreferencesService.updateGridSize(entityId, username, queryParams.getPageSize());
        }
    }

    private void validateNonEditableProperty(EntityDto entity) {
        if (entity.isNonEditable()) {
            throw new EntityInstancesNonEditableException();
        }
    }

    @Override
    public Records<BasicEntityRecord> getRelatedFieldValue(Long entityId, Long instanceId, String fieldName,
                                              RelationshipsUpdate filter, QueryParams queryParams) {
        try {
            // first get the entity
            EntityDto entity = getEntity(entityId);
            validateCredentials(entity);
            List<FieldDto> fields = getEntityFields(entityId);

            String entityName = entity.getName();
            MotechDataService service = getServiceForEntity(entity);

            // then the related entity
            FieldDto relatedField = findFieldByName(fields, fieldName);
            if (relatedField == null) {
                throw new FieldNotFoundException(entity.getClassName(), fieldName);
            }

            String relatedClass = relatedField.getMetadataValue(Constants.MetadataKeys.RELATED_CLASS);
            if (StringUtils.isBlank(relatedClass)) {
                throw new IllegalArgumentException("Field " + fieldName + " in entity " + entity.getClassName() +
                    " is not a related field");
            }

            // these will be used for building the records
            EntityDto relatedEntity = getEntity(relatedClass);
            List<FieldDto> relatedFields = getEntityFields(relatedEntity.getId());
            MotechDataService relatedDataService = getServiceForEntity(relatedEntity);
            Collection relatedAsColl = new ArrayList<>();

            // If the relationship already exists, fetch instances and use correct type
            if (instanceId != null) {
                // get the instance of the original entity
                Object instance = service.findById(instanceId);

                if (instance == null) {
                    throw new ObjectNotFoundException(entityName, instanceId);
                }

                // the value of the related field
                relatedAsColl = TypeHelper.asCollection(PropertyUtil.getProperty(instance, fieldName));
            }

            relatedAsColl.addAll(relatedDataService.findByIds(filter.getAddedIds()));

            List<Long> updatedInstancesIds = new ArrayList<>();
            for (EntityRecord record : filter.getAddedNewRecords()) {
                Integer id = (Integer) record.getFieldByName(Constants.Util.ID_FIELD_NAME).getValue();
                if (id != null && id > 0) {
                    updatedInstancesIds.add(id.longValue());
                }
            }

            relatedAsColl.removeIf(new Predicate() {
                @Override
                public boolean test(Object o) {
                    Long objectId = (Long) PropertyUtil.safeGetProperty(o, Constants.Util.ID_FIELD_NAME);
                    return filter.getRemovedIds().contains(objectId) || updatedInstancesIds.contains(objectId);
                }
            });

            for (EntityRecord record : filter.getAddedNewRecords()) {
                relatedAsColl.add(newInstanceFromEntityRecord(getEntityClass(relatedEntity), relatedFields, record.getFields(), relatedDataService));
            }

            // apply pagination ordering (currently in memory)
            List filtered = InMemoryQueryFilter.filter(relatedAsColl, queryParams);

            // convert the instance to a grid-friendly form
            List<BasicEntityRecord> entityRecords = instancesToBasicRecords(filtered, relatedEntity, relatedFields,
                    relatedDataService, EntityType.STANDARD);

            // counts for the grid
            int recordCount = relatedAsColl.size();
            int rowCount = (int) Math.ceil(recordCount / (double) queryParams.getPageSize());

            // package as records
            return new Records<>(queryParams.getPage(), rowCount, recordCount, entityRecords);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | IllegalArgumentException |
                ClassNotFoundException | CannotCompileException | InstantiationException | NoSuchFieldException e) {
           throw new ObjectReadException(entityId, e);
        }
    }

    private Object newInstanceFromEntityRecord(Class<?> entityClass, List<FieldDto> entityFields, List<FieldRecord> fields, MotechDataService service)
            throws IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, CannotCompileException, NoSuchFieldException {
        Object instance = entityClass.newInstance();

        for (FieldDto entityField : entityFields) {
            if (entityField.getType().isMap() && entityField.getBasic().getDefaultValue() != null) {
                setInstanceFieldMap(instance, entityField);
            }
        }

        updateFields(instance, fields, service, null, false);
        return instance;
    }

    private void setInstanceFieldMap(Object instance, FieldDto entityField) {

        String strMap = entityField.getBasic().getDefaultValue().toString();

        String keyMetadata;
        String valueMetadata;

        if (entityField.getMetadata(MAP_KEY_TYPE) == null || entityField.getMetadata(MAP_VALUE_TYPE) == null) {
            keyMetadata = String.class.getName();
            valueMetadata = String.class.getName();
        } else {
            keyMetadata = entityField.getMetadata(MAP_KEY_TYPE).getValue();
            valueMetadata = entityField.getMetadata(MAP_VALUE_TYPE).getValue();
        }

        PropertyUtil.safeSetProperty(instance, entityField.getBasic().getName(), TypeHelper.parseStringToMap(keyMetadata, valueMetadata, strMap));
    }

    private void populateDefaultFields(List<FieldRecord> fieldRecords) {
        for (FieldRecord record : fieldRecords) {
            // we don't want to pre-populate anything for editable fields
            // if we pre-populate the owner field in such a case for example, it will fail validation
            if (Constants.Util.CREATOR_FIELD_NAME.equals(record.getName()) ||
                    Constants.Util.OWNER_FIELD_NAME.equals(record.getName())) {
                if (record.isNonEditable()) {
                    // make sure this is null, we don't want empty strings for these fields
                    record.setValue(null);
                } else {
                    record.setValue(SecurityContextHolder.getContext().getAuthentication().getName());
                }
            }
        }
    }

    private LookupDto getLookupByName(Long entityId, String lookupName) {
        LookupDto lookup = entityService.getLookupByName(entityId, lookupName);
        if (lookup == null) {
            throw new LookupNotFoundException(entityId, lookupName);
        }
        return lookup;
    }

    private EntityDto getEntity(Long entityId) {
        EntityDto entityDto = entityService.getEntity(entityId);
        if (entityDto == null) {
            throw new EntityNotFoundException(entityId);
        }
        return entityDto;
    }

    private EntityDto getEntity(String entityClassName) {
        EntityDto entityDto = entityService.getEntityByClassName(entityClassName);
        if (entityDto == null) {
            throw new EntityNotFoundException(entityClassName);
        }
        return entityDto;
    }

    private MotechDataService getServiceForEntity(EntityDto entity) {
        String className = entity.getClassName();
        return DataServiceHelper.getDataService(bundleContext, className);
    }

    private void updateFields(Object instance, List<FieldRecord> fieldRecords, MotechDataService service,
                              Long deleteValueFieldId, boolean retainId)
            throws NoSuchMethodException, ClassNotFoundException, CannotCompileException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        for (FieldRecord fieldRecord : fieldRecords) {
            if (!(retainId && ID_FIELD_NAME.equals(fieldRecord.getName())) && !fieldRecord.getType().isRelationship()) {
                setProperty(instance, fieldRecord, service, deleteValueFieldId, retainId);
            } else if (fieldRecord.getType().isRelationship()) {
                setRelationProperty(instance, fieldRecord);
            }
        }
    }
    private List<BasicEntityRecord> instancesToBasicRecords(Collection instances, EntityDto entity, List<FieldDto> fields,
                                                  MotechDataService service, EntityType entityType) {
        List<BasicEntityRecord> records = new ArrayList<>();

        Map<String, List<FieldDto>> relatedEntitiesFields = getRelatedEntitiesFields(fields);

        for (Object instance : instances) {
            BasicEntityRecord record = instanceToBasicRecord(instance, entity, fields, service, entityType,
                    relatedEntitiesFields);
            records.add(record);
        }
        return records;
    }

    private BasicEntityRecord instanceToBasicRecord(Object instance, EntityDto entityDto, List<FieldDto> fields,
                                                    MotechDataService service, EntityType entityType,
                                                    Map<String, List<FieldDto>> relatedEntitiesFields) {
        return instanceToRecord(instance, entityDto, fields, service, entityType, BasicEntityRecord.class,
                relatedEntitiesFields);
    }

    private EntityRecord instanceToRecord(Object instance, EntityDto entityDto, List<FieldDto> fields,
                                          MotechDataService service, EntityType entityType,
                                          Map<String, List<FieldDto>> relatedEntitiesFields) {
        return instanceToRecord(instance, entityDto, fields, service, entityType, EntityRecord.class,
                relatedEntitiesFields);
    }

    private <T extends BasicEntityRecord> T instanceToRecord(Object instance, EntityDto entityDto,
                                                             List<FieldDto> fields, MotechDataService service,
                                                             EntityType entityType, Class<T> clazz,
                                                             Map<String, List<FieldDto>> relatedEntitiesFields) {
        if (instance == null) {
            return null;
        }
        try {
            List fieldRecords = new ArrayList<>();
            boolean basic = BasicEntityRecord.class.equals(clazz);

            for (FieldDto field : fields) {
                if (entityType != EntityType.STANDARD && field.isVersionField()) {
                    continue;
                }

                Object value = getProperty(instance, field, service);
                Object displayValue = DisplayHelper.getDisplayValueForField(field, value, MAX_LENGTH);

                value = parseValueForDisplay(value, relatedEntitiesFields.get(field.getMetadata(Constants.MetadataKeys.RELATED_CLASS)));

                BasicFieldRecord fieldRecord = basic ? new BasicFieldRecord(field) : new FieldRecord(field);
                fieldRecord.setValue(value);
                fieldRecord.setDisplayValue(displayValue);
                fieldRecords.add(fieldRecord);
            }

            Number id = (Number) PropertyUtil.safeGetProperty(instance, ID_FIELD_NAME);
            Long parsedId = id == null ? null : id.longValue();
            return (T) (basic ? new BasicEntityRecord(parsedId, fieldRecords) :
                    new EntityRecord(parsedId, entityDto.getId(), fieldRecords));
        } catch (Exception e) {
            throw new ObjectReadException(entityDto.getName(), e);
        }
    }

    private HistoryRecord convertToHistoryRecord(Object object, EntityDto entity, Long instanceId,
                                                 MotechDataService service) {
        Long entityId = entity.getId();
        List<FieldDto> fields = getEntityFields(entityId);
        Map<String, List<FieldDto>> relatedEntitiesFields = getRelatedEntitiesFields(fields);

        EntityRecord entityRecord = instanceToRecord(object, entity, entityService.getEntityFieldsForUI(entityId),
                service, EntityType.HISTORY, relatedEntitiesFields);
        Long historyInstanceSchemaVersion = (Long) PropertyUtil.safeGetProperty(object,
                HistoryTrashClassHelper.historySchemaVersion(object.getClass()));
        Long currentSchemaVersion = entityService.getCurrentSchemaVersion(entity.getClassName());

        return new HistoryRecord(entityRecord.getId(), instanceId,
                historyInstanceSchemaVersion.equals(currentSchemaVersion), entityRecord.getFields());
    }

    private BasicHistoryRecord convertToBasicHistoryRecord(Object object, EntityDto entity, Long instanceId,
                                                           MotechDataService service) {
        Long entityId = entity.getId();
        List<FieldDto> fields = getEntityFields(entityId);
        Map<String, List<FieldDto>> relatedEntitiesFields = getRelatedEntitiesFields(fields);

        BasicEntityRecord entityRecord = instanceToBasicRecord(object, entity, entityService.getEntityFields(entityId),
                service, EntityType.HISTORY, relatedEntitiesFields);
        Long historyInstanceSchemaVersion = (Long) PropertyUtil.safeGetProperty(object,
                HistoryTrashClassHelper.historySchemaVersion(object.getClass()));
        Long currentSchemaVersion = entityService.getCurrentSchemaVersion(entity.getClassName());

        return new BasicHistoryRecord(entityRecord.getId(), instanceId,
                historyInstanceSchemaVersion.equals(currentSchemaVersion), entityRecord.getFields());
    }

    private void setProperty(Object instance, FieldRecord fieldRecord, MotechDataService service, Long deleteValueFieldId, boolean retainId) throws NoSuchMethodException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        String fieldName = fieldRecord.getName();
        TypeDto type = getType(fieldRecord);

        String methodName = "set" + StringUtils.capitalize(fieldName);
        ComboboxHolder holder = type.isCombobox() ? new ComboboxHolder(instance, fieldRecord) : null;
        String methodParameterType = getMethodParameterType(type, holder);

        ClassLoader classLoader = instance.getClass().getClassLoader();

        Class<?> parameterType;
        Object parsedValue;
        if (Byte[].class.getName().equals(methodParameterType) || byte[].class.getName().equals(methodParameterType)) {
            parameterType = getCorrectByteArrayType(methodParameterType);

            parsedValue = parseBlobValue(fieldRecord, service, fieldName, deleteValueFieldId, instance);
        } else {
            parameterType = classLoader.loadClass(methodParameterType);

            parsedValue = parseValue(holder, methodParameterType, fieldRecord, classLoader);
        }

        MetadataDto versionMetadata = fieldRecord.getMetadata(Constants.MetadataKeys.VERSION_FIELD);
        validateNonEditableField(fieldRecord, instance, parsedValue, versionMetadata);

        Method method = MethodUtils.getAccessibleMethod(instance.getClass(), methodName, parameterType);

        if (method == null && TypeHelper.hasPrimitive(parameterType)) {
            method = MethodUtils.getAccessibleMethod(instance.getClass(), methodName, TypeHelper.getPrimitive(parameterType));
            // if the setter is for a primitive, but we have a null, we leave the default
            if (method != null && parsedValue == null) {
                return;
            }
        }

        invokeMethod(method, instance, parsedValue, methodName, fieldName);
        setTransactionVersion(instance, fieldRecord, retainId, versionMetadata);
    }

    private void setTransactionVersion(Object instance, FieldRecord fieldRecord, boolean retainId, MetadataDto versionMetadata) {
        if (versionMetadata != null && Constants.Util.TRUE.equals(versionMetadata.getValue()) && retainId) {
            StateManagerUtil.setTransactionVersion(instance, fieldRecord.getValue(), fieldRecord.getName());
        }
    }

    private void setRelationProperty(Object instance, FieldRecord fieldRecord)
            throws NoSuchMethodException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException, InstantiationException, CannotCompileException {
        String fieldName = fieldRecord.getName();
        String methodName =  MemberUtil.getSetterName(fieldName);
        Class<?> clazz = instance.getClass().getClassLoader().loadClass(instance.getClass().getName());
        Field field = FieldUtils.getField(clazz, fieldName, true);
        Class<?> parameterType = field.getType();
        Object value = null;
        MotechDataService serviceForRelatedClass = null;
        TypeDto type = getType(fieldRecord);

        if (StringUtils.isNotEmpty(ObjectUtils.toString(fieldRecord.getValue()))) {
            Class<?> argumentType = null;
            if (type.equals(TypeDto.ONE_TO_MANY_RELATIONSHIP) || type.equals(TypeDto.MANY_TO_MANY_RELATIONSHIP)) {
                argumentType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
            } else if (type.equals(TypeDto.MANY_TO_ONE_RELATIONSHIP) || type.equals(TypeDto.ONE_TO_ONE_RELATIONSHIP)) {
                argumentType = parameterType;
            }

            serviceForRelatedClass = DataServiceHelper.getDataService(bundleContext, argumentType.getName());
            Object related = PropertyUtil.safeGetProperty(instance, fieldName);

            value = buildRelatedInstances(serviceForRelatedClass, parameterType, argumentType, fieldRecord.getValue(), related);
        }

        Method method = MethodUtils.getAccessibleMethod(instance.getClass(), methodName, parameterType);
        invokeMethod(method, instance, value, methodName, fieldName);
    }

    private Object buildRelatedInstances(MotechDataService service, Class<?> parameterType, Class<?> argumentType, Object fieldValue, Object relatedObject)
            throws IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, CannotCompileException, NoSuchFieldException {
        Object parsedValue;
        RelationshipsUpdate relationshipsUpdate = mapper.convertValue(fieldValue, RelationshipsUpdate.class);

        EntityDto relatedEntity = getEntity(argumentType.getName());
        List<FieldDto> entityFields = getEntityFields(relatedEntity.getId());

       if (Collection.class.isAssignableFrom(parameterType)) {
           parsedValue = parseRelationshipCollection(service, (Class<? extends Collection>) parameterType, argumentType, (Collection) relatedObject, relationshipsUpdate, entityFields);
       } else {
           parsedValue = parseRelationshipValue(service, argumentType, relationshipsUpdate, entityFields);
       }

       return parsedValue;
    }

    private Object parseRelationshipValue(MotechDataService service, Class<?> argumentType, RelationshipsUpdate relationshipsUpdate, List<FieldDto> entityFields)
            throws IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, CannotCompileException, NoSuchFieldException {
        Object parsedValue = null;

        if (!relationshipsUpdate.getAddedIds().isEmpty()) {
            parsedValue = findRelatedObjectById(relationshipsUpdate.getAddedIds().get(0), service);
        } else if (!relationshipsUpdate.getAddedNewRecords().isEmpty()) {
            parsedValue = newInstanceFromEntityRecord(argumentType, entityFields, relationshipsUpdate.getAddedNewRecords().get(0).getFields(), service);
        }

        return parsedValue;
    }

    private Object parseRelationshipCollection(MotechDataService service, Class<? extends Collection> parameterType, Class<?> argumentType, Collection existingRelatedInstances, RelationshipsUpdate relationshipsUpdate, List<FieldDto> entityFields)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, CannotCompileException, NoSuchFieldException {
        Collection relatedInstances;

        Class<?> collectionImplementation = TypeHelper.suggestCollectionImplementation(parameterType);
        relatedInstances = (Collection) (collectionImplementation == null ? new ArrayList() : collectionImplementation.newInstance());

        // Add already related instances, provided any exist
        if (existingRelatedInstances != null) {
            relatedInstances.addAll(existingRelatedInstances);
        }

        // Add new, existing relations, based on the passed IDs
        relatedInstances.addAll(service.findByIds(relationshipsUpdate.getAddedIds()));

        // Remove existing relations if their ID is on the list
        relatedInstances.removeIf(new Predicate() {
            @Override
            public boolean test(Object o) {
                return relationshipsUpdate.getRemovedIds().contains(PropertyUtil.safeGetProperty(o, Constants.Util.ID_FIELD_NAME));
            }
        });

        // Add new relations, that do not exist in db yet
        for (EntityRecord record : relationshipsUpdate.getAddedNewRecords()) {
            relatedInstances.add(newInstanceFromEntityRecord(argumentType, entityFields, record.getFields(), service));
        }
        return relatedInstances;
    }

    private Object findRelatedObjectById(Object id, MotechDataService service) {
        //We need parse id value to the long type
        return service.findById(TypeHelper.parseNumber(id, Long.class.getName()).longValue());
    }

    private Class getCorrectByteArrayType(String type) {
        return Byte[].class.getName().equals(type) ? Byte[].class : byte[].class;
    }

    private TypeDto getType(FieldRecord fieldRecord) {
        TypeDto type = fieldRecord.getType();

        if (type.isTextArea()) {
            type = typeService.findType(String.class);
        }

        return type;
    }

    private Object parseBlobValue(FieldRecord fieldRecord, MotechDataService service, String fieldName,
                              Long deleteValueFieldId, Object instance) {
        Object parsedValue;
        if ((ArrayUtils.EMPTY_BYTE_OBJECT_ARRAY.equals(fieldRecord.getValue()) || ArrayUtils.EMPTY_BYTE_ARRAY.equals(fieldRecord.getValue()))
                && !fieldRecord.getId().equals(deleteValueFieldId)) {
            parsedValue = service.getDetachedField(instance, fieldName);
        } else {
            parsedValue = fieldRecord.getValue();
        }

        return verifyParsedValue(parsedValue);
    }

    private Object verifyParsedValue(Object parsedValue) {
        if (parsedValue == null) {
            return ArrayUtils.EMPTY_BYTE_OBJECT_ARRAY;
        }
        return parsedValue;
    }

    private void invokeMethod(Method method, Object instance, Object parsedValue, String methodName, String fieldName) throws NoSuchMethodException {
        if (method == null) {
            throw new NoSuchMethodException(String.format("No setter %s for field %s", methodName, fieldName));
        }

        try {
            if (method.getParameterTypes()[0].equals(byte[].class)) {
                method.invoke(instance, parsedValue instanceof byte[] ? parsedValue : ArrayUtils.toPrimitive((Byte[]) parsedValue));
            } else {
                method.invoke(instance, parsedValue);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new IllegalStateException(String.format("There was a problem with set value '%s' to field '%s'", parsedValue, fieldName), e);
        }
    }

    private Object parseValue(ComboboxHolder holder, String methodParameterType, FieldRecord fieldRecord, ClassLoader classLoader) {
        Object parsedValue = fieldRecord.getValue();
        String valueAsString = null == parsedValue ? null : TypeHelper.format(parsedValue);

        if (parsedValue instanceof Map) {
            if (fieldRecord.getMetadata(MAP_KEY_TYPE) != null && fieldRecord.getMetadata(MAP_VALUE_TYPE) != null) {
                Map<Object, Object> parsedValueAsMap = (Map<Object, Object>) parsedValue;
                Map<Object, Object> parsedMap = new LinkedHashMap<>();

                for (Iterator<Map.Entry<Object, Object>> it = parsedValueAsMap.entrySet().iterator(); it.hasNext();) {
                    Map.Entry<Object, Object> entry = it.next();
                    parsedMap.put(TypeHelper.parseMapValue(entry.getKey(), fieldRecord.getMetadata(MAP_KEY_TYPE).getValue(), true),
                        TypeHelper.parseMapValue(entry.getValue(), fieldRecord.getMetadata(MAP_VALUE_TYPE).getValue(), false));
                    it.remove();
                }

                parsedValueAsMap.putAll(parsedMap);
            }
        } else if (null != holder && holder.isEnumCollection()) {
            String genericType = holder.getEnumName();
            parsedValue = TypeHelper.parse(valueAsString, holder.getTypeClassName(), genericType, classLoader);
        } else if (parsedValue instanceof String && UUID.class.getName().equals(methodParameterType)) {
            parsedValue = TypeHelper.parseStringToUUID(valueAsString);
        } else {
            parsedValue = TypeHelper.parse(valueAsString, methodParameterType, classLoader);
        }

        return parsedValue;
    }

    private String getMethodParameterType(TypeDto type, ComboboxHolder holder) {
        String methodParameterType;

        if (type.isCombobox() && null != holder) {
            methodParameterType = holder.getTypeClassName();
        } else {
            methodParameterType = type.getTypeClass();
        }

        return methodParameterType;
    }

    private Object getProperty(Object instance, FieldDto field, MotechDataService service)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String fieldName = StringUtils.uncapitalize(field.getBasic().getName());

        PropertyDescriptor propertyDescriptor = PropertyUtil.getPropertyDescriptor(instance, fieldName);
        if (propertyDescriptor == null) {
            throw new IllegalStateException("No property with name " + fieldName + " in "
                    + instance.getClass().getName());
        }

        Method readMethod = propertyDescriptor.getReadMethod();

        if (readMethod == null) {
            throw new NoSuchMethodException(String.format("No getter for field %s", fieldName));
        }

        if (TypeDto.BLOB.getTypeClass().equals(field.getType().getTypeClass())) {
            return ArrayUtils.EMPTY_BYTE_OBJECT_ARRAY;
        }

        if (isSCOField(field.getType().getTypeClass())) {
            return service.getDetachedField(instance, fieldName);
        }

        try {
            return readMethod.invoke(instance);
        } catch (InvocationTargetException e) {
            LOGGER.debug("Invocation target exception thrown when retrieving field {}. This may indicate a non loaded field",
                    fieldName, e);
            // fallback to the service
            Long id = (Long) PropertyUtil.safeGetProperty(instance, ID_FIELD_NAME);
            return service.getDetachedField(id == null ? instance : service.findById(id), fieldName);
        }
    }

    private boolean isSCOField(String typeClass) {
        return TypeDto.COLLECTION.getTypeClass().equals(typeClass) || TypeDto.MAP.getTypeClass().equals(typeClass);
    }

    private Object parseValueForDisplay(Object value, List<FieldDto> relatedEntityFields)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Object parsedValue = value;

        if (parsedValue instanceof DateTime) {
            parsedValue = DisplayHelper.DTF.print((DateTime) parsedValue);
        } else if (parsedValue instanceof Date) {
            parsedValue = DisplayHelper.DTF.print(((Date) parsedValue).getTime());
        } else if (parsedValue instanceof Time) {
            parsedValue = ((Time) parsedValue).timeStr();
        } else if (parsedValue instanceof LocalDate) {
            parsedValue = parsedValue.toString();
        } else if (parsedValue instanceof java.time.LocalDate) {
            parsedValue = parsedValue.toString();
        } else if (parsedValue instanceof LocalDateTime) {
            //Using ZonedDateTime for retrieving timezone
            ZonedDateTime zonedDateTime = ZonedDateTime.of((LocalDateTime) parsedValue, ZoneOffset.systemDefault());
            parsedValue = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm xxxx").format(zonedDateTime);
        } else if (relatedEntityFields != null) {
            // We do not want to return the whole chain of relationships for UI display, but just the first level.
            // Fetching whole relationship tree may cause trouble when serializing
            parsedValue = relationshipDisplayUtil.breakDeepRelationChainForDisplay(parsedValue, relatedEntityFields);
        }

        return parsedValue;
    }

    private Map<String, List<FieldDto>> getRelatedEntitiesFields(List<FieldDto> fields) {
        Map<String, List<FieldDto>> relatedEntitiesFields = new HashMap<>();

        for (FieldDto field : fields) {
            MetadataDto entityMetadata = field.getMetadata(Constants.MetadataKeys.RELATED_CLASS);
            if (entityMetadata != null) {
                String className = entityMetadata.getValue();
                relatedEntitiesFields.put(className, getEntityFieldsByClassName(className));
            }
        }

        return relatedEntitiesFields;
    }

    private Class<?> getEntityClass(EntityDto entity) throws ClassNotFoundException {
        // get the declaring bundle, for DDE the module bundle, for EUDE the generated entities bundle
        Bundle declaringBundle;
        if (entity.isDDE()) {
            declaringBundle = MdsBundleHelper.searchForBundle(bundleContext, entity);
        } else {
            declaringBundle = WebBundleUtil.findBundleBySymbolicName(bundleContext,
                    Constants.BundleNames.MDS_ENTITIES_SYMBOLIC_NAME);
        }

        Class<?> clazz;

        // if no bundle found, fallback to the MDSClassLoader
        if (declaringBundle == null) {
            clazz = MDSClassLoader.getInstance().loadClass(entity.getClassName());
        } else {
            clazz = declaringBundle.loadClass(entity.getClassName());
        }

        return clazz;
    }

    private void validateCredentials(EntityDto entity) {
        boolean authorized;
        SecurityMode securityMode = entity.getSecurityMode();
        if (securityMode != null) {
            Set<String> securityMembers = entity.getSecurityMembers();
            authorized = entity.hasAccessToEntityFromSecurityMode(securityMode, securityMembers);
            if (!authorized && !securityMode.isInstanceRestriction()) {
                throw new SecurityException();
            }
        }
    }

    private void validateCredentialsForReading(EntityDto entity) {
        boolean authorized = false;
        SecurityMode securityMode = entity.getSecurityMode();
        SecurityMode readOnlySecurityMode = entity.getReadOnlySecurityMode();

        if (securityMode != null) {
            Set<String> securityMembers = entity.getSecurityMembers();
            authorized = entity.hasAccessToEntityFromSecurityMode(securityMode, securityMembers);
            if (!authorized) {
                if (readOnlySecurityMode != null) {
                    Set<String> readOnlySecurityMembers = entity.getReadOnlySecurityMembers();
                    authorized = entity.hasAccessToEntityFromSecurityMode(readOnlySecurityMode, readOnlySecurityMembers);
                    if (isAuthorizedByReadAccessOrIsInstanceRestriction(authorized, readOnlySecurityMode, securityMode)) {
                        throw new SecurityException();
                    }
                }
            }
        }
        if (!authorized && readOnlySecurityMode != null) {
            Set<String> readOnlySecurityMembers = entity.getReadOnlySecurityMembers();
            authorized = entity.hasAccessToEntityFromSecurityMode(readOnlySecurityMode, readOnlySecurityMembers);
            if (!authorized && !readOnlySecurityMode.isInstanceRestriction()) {
                throw new SecurityException();
            }
        }
    }

    private boolean isAuthorizedByReadAccessOrIsInstanceRestriction(boolean authorized, SecurityMode readOnlySecurityMode, SecurityMode securityMode) {
        return !authorized && !readOnlySecurityMode.isInstanceRestriction() && !securityMode.isInstanceRestriction();
    }

    private void validateNonEditableField(FieldRecord fieldRecord, Object instance, Object parsedValue, MetadataDto versionMetadata) throws IllegalAccessException {
        Object fieldOldValue = FieldUtils.readField(instance,
                        StringUtils.uncapitalize(fieldRecord.getName()),
                        true);

        // We need to check if read only field value isn't changed
        // in some unexpected way. If so then throw exception
        if (fieldRecord.isNonEditable()
                // There is need to use Objects.equals as values - one or both - can be null
                // which would cause NullPointerException when just .equals() on null value
                && !Objects.equals(fieldOldValue, parsedValue)) {
            // Skip for version field
            if (versionMetadata != null && Constants.Util.TRUE.equals(versionMetadata.getValue())) {
                return;
            }
            throw new FieldReadOnlyException(instance.getClass().getName(), fieldRecord.getName());
        }
    }

    private FieldDto findFieldByName(List<FieldDto> fields, String fieldName) {
        for (FieldDto field : fields) {
            if (StringUtils.equals(fieldName, field.getBasic().getName())) {
                return field;
            }
        }
        return null;
    }

    private List<FieldDto> getEntityFieldsByClassName(String entityClassName) {
        Long entityId = entityService.getEntityByClassName(entityClassName).getId();
        return getEntityFields(entityId);
    }

    @Autowired
    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }

    @Autowired
    public void setTrashService(TrashService trashService) {
        this.trashService = trashService;
    }

    @Autowired
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Autowired
    public void setHistoryService(HistoryService historyService) {
        this.historyService = historyService;
    }

    @Autowired
    public void setTypeService(TypeService typeService) {
        this.typeService = typeService;
    }

    @Autowired
    public void setRelationshipDisplayUtil(RelationshipDisplayUtil relationshipDisplayUtil) {
        this.relationshipDisplayUtil = relationshipDisplayUtil;
    }

    @Autowired
    public void setUserPreferencesService(UserPreferencesService userPreferencesService) {
        this.userPreferencesService = userPreferencesService;
    }
}
