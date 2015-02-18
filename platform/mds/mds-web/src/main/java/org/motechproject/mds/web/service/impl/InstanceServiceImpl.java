package org.motechproject.mds.web.service.impl;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.MethodUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.dto.DtoHelper;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.FieldInstanceDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.MetadataDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.ex.entity.EntityNotFoundException;
import org.motechproject.mds.ex.entity.EntitySchemaMismatchException;
import org.motechproject.mds.ex.lookup.LookupExecutionException;
import org.motechproject.mds.ex.lookup.LookupNotFoundException;
import org.motechproject.mds.ex.object.ObjectNotFoundException;
import org.motechproject.mds.ex.object.ObjectReadException;
import org.motechproject.mds.ex.object.ObjectUpdateException;
import org.motechproject.mds.filter.Filters;
import org.motechproject.mds.lookup.LookupExecutor;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.HistoryService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.service.TrashService;
import org.motechproject.mds.service.TypeService;
import org.motechproject.mds.service.impl.history.HistoryTrashClassHelper;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.helper.DataServiceHelper;
import org.motechproject.mds.util.MDSClassLoader;
import org.motechproject.mds.util.PropertyUtil;
import org.motechproject.mds.util.TypeHelper;
import org.motechproject.mds.web.domain.ComboboxHolder;
import org.motechproject.mds.web.domain.EntityRecord;
import org.motechproject.mds.web.domain.FieldRecord;
import org.motechproject.mds.web.domain.HistoryRecord;
import org.motechproject.mds.web.service.InstanceService;
import org.motechproject.osgi.web.util.WebBundleUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of the {@link org.motechproject.mds.web.service.InstanceService} interface.
 */
@Service
public class InstanceServiceImpl implements InstanceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceServiceImpl.class);

    private static final DateTimeFormatter DTF = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm Z");

    private static final String ID = "id";

    private EntityService entityService;
    private BundleContext bundleContext;
    private HistoryService historyService;
    private TrashService trashService;
    private TypeService typeService;

    @Override
    @Transactional
    public Object saveInstance(EntityRecord entityRecord) {
        return saveInstance(entityRecord, null);
    }

    @Override
    @Transactional
    public Object saveInstance(EntityRecord entityRecord, Long deleteValueFieldId) {
        EntityDto entity = getEntity(entityRecord.getEntitySchemaId());

        try {
            MotechDataService service = getServiceForEntity(entity);
            Class<?> entityClass = getEntityClass(entity);

            boolean newObject = entityRecord.getId() == null;

            Object instance;
            if (newObject) {
                instance = entityClass.newInstance();
            } else {
                instance = service.retrieve(ID, entityRecord.getId());
                if (instance == null) {
                    throw new ObjectNotFoundException();
                }
            }

            updateFields(instance, entityRecord.getFields(), service, deleteValueFieldId, !newObject);

            if (newObject) {
                return service.create(instance);
            } else {
                return service.update(instance);
            }
        } catch (Exception e) {
            LOGGER.error("Unable to save object instance", e);
            throw new ObjectUpdateException(e);
        }
    }

    @Override
    @Transactional
    public List<EntityRecord> getEntityRecords(Long entityId) {
        return getEntityRecords(entityId, null);
    }

    @Override
    @Transactional
    public List<EntityRecord> getEntityRecords(Long entityId, QueryParams queryParams) {
        EntityDto entity = getEntity(entityId);
        List<FieldDto> fields = entityService.getEntityFields(entityId);

        MotechDataService service = getServiceForEntity(entity);

        List instances = service.retrieveAll(queryParams);

        return instancesToRecords(instances, entity, fields);
    }

    @Override
    @Transactional
    public List<FieldDto> getEntityFields(Long entityId) {
        return entityService.getEntityFields(entityId);
    }

    @Override
    @Transactional
    public List<EntityRecord> getTrashRecords(Long entityId, QueryParams queryParams) {
        EntityDto entity = getEntity(entityId);
        List<FieldDto> fields = entityService.getEntityFields(entityId);
        Collection collection = trashService.getInstancesFromTrash(entity.getClassName(), queryParams);

        return instancesToRecords(collection, entity, fields);
    }

    @Override
    @Transactional
    public long countTrashRecords(Long entityId) {
        EntityDto entity = getEntity(entityId);

        return trashService.countTrashRecords(entity.getClassName());
    }

    @Override
    @Transactional
    public EntityRecord getSingleTrashRecord(Long entityId, Long instanceId) {
        EntityDto entityDto = getEntity(entityId);

        List<FieldDto> fields = entityService.getEntityFields(entityId);
        Object instance = trashService.findTrashById(instanceId, entityId);

        return instanceToRecord(instance, entityDto, fields);
    }

    @Override
    public Object getInstanceField(Long entityId, Long instanceId, String fieldName) {
        MotechDataService motechDataService = getServiceForEntity(getEntity(entityId));
        Object instance = motechDataService.retrieve(ID, instanceId);

        return motechDataService.getDetachedField(instance, fieldName);
    }

    @Override
    @Transactional
    public List<EntityRecord> getEntityRecordsFromLookup(Long entityId, String lookupName, Map<String, Object> lookupMap,
                                                         QueryParams queryParams) {
        EntityDto entity = getEntity(entityId);
        LookupDto lookup = getLookupByName(entityId, lookupName);
        List<FieldDto> fields = entityService.getEntityFields(entityId);
        Map<String, FieldDto> fieldMap = DtoHelper.asFieldMapByName(fields);

        MotechDataService service = getServiceForEntity(entity);

        try {
            LookupExecutor lookupExecutor = new LookupExecutor(service, lookup, fieldMap);

            Object result = lookupExecutor.execute(lookupMap, queryParams);

            if (lookup.isSingleObjectReturn()) {
                EntityRecord record = instanceToRecord(result, entity, fields);
                return (record == null) ? new ArrayList<EntityRecord>() : Arrays.asList(record);
            } else {
                List instances = (List) result;
                return instancesToRecords(instances, entity, fields);
            }
        } catch (Exception e) {
            throw new LookupExecutionException(e);
        }
    }

    @Override
    @Transactional
    public List<EntityRecord> getEntityRecordsWithFilter(Long entityId, Filters filters, QueryParams queryParams) {
        EntityDto entity = getEntity(entityId);
        List<FieldDto> fields = entityService.getEntityFields(entityId);
        MotechDataService service = getServiceForEntity(entity);

        List instances = service.filter(filters, queryParams);

        return instancesToRecords(instances, entity, fields);
    }

    @Override
    @Transactional
    public long countRecordsWithFilters(Long entityId, Filters filters) {
        EntityDto entity = getEntity(entityId);

        MotechDataService service = getServiceForEntity(entity);

        return service.countForFilters(filters);
    }

    @Override
    @Transactional
    public long countRecords(Long entityId) {
        EntityDto entity = getEntity(entityId);

        MotechDataService service = getServiceForEntity(entity);

        return service.count();
    }


    @Override
    @Transactional
    public long countRecordsByLookup(Long entityId, String lookupName, Map<String, Object> lookupMap) {
        EntityDto entity = getEntity(entityId);
        LookupDto lookup = getLookupByName(entityId, lookupName);
        List<FieldDto> fields = entityService.getEntityFields(entityId);
        Map<String, FieldDto> fieldMap = DtoHelper.asFieldMapByName(fields);

        MotechDataService service = getServiceForEntity(entity);

        try {
            LookupExecutor lookupExecutor = new LookupExecutor(service, lookup, fieldMap);

            return lookupExecutor.executeCount(lookupMap);
        } catch (Exception e) {
            throw new LookupExecutionException(e);
        }
    }

    @Override
    @Transactional
    public void revertPreviousVersion(Long entityId, Long instanceId, Long historyId) {
        HistoryRecord historyRecord = getHistoryRecord(entityId, instanceId, historyId);
        if (!historyRecord.isRevertable()) {
            throw new EntitySchemaMismatchException();
        }
        saveInstance(new EntityRecord(instanceId, entityId, historyRecord.getFields()));
    }

    @Override
    @Transactional
    public List<FieldInstanceDto> getInstanceFields(Long entityId, Long instanceId) {
        EntityDto entity = entityService.getEntity(entityId);

        assertEntityExists(entity);

        List<FieldDto> fields = entityService.getEntityFields(entityId);

        List<FieldInstanceDto> result = new ArrayList<>();
        for (FieldDto field : fields) {
            FieldInstanceDto fieldInstanceDto = new FieldInstanceDto(field.getId(), instanceId, field.getBasic());
            result.add(fieldInstanceDto);
        }

        return result;
    }

    @Override
    @Transactional
    public List<HistoryRecord> getInstanceHistory(Long entityId, Long instanceId, QueryParams queryParams) {
        EntityDto entity = getEntity(entityId);

        MotechDataService service = getServiceForEntity(entity);

        Object instance = service.retrieve(ID, instanceId);

        List history = historyService.getHistoryForInstance(instance, queryParams);
        List<HistoryRecord> result = new ArrayList<>();
        for (Object o : history) {
            result.add(convertToHistoryRecord(o, entity, instanceId));
        }
        return result;
    }

    @Override
    @Transactional
    public long countHistoryRecords(Long entityId, Long instanceId) {
        EntityDto entity = getEntity(entityId);
        MotechDataService service = getServiceForEntity(entity);
        Object instance = service.retrieve(ID, instanceId);

        return historyService.countHistoryRecords(instance);
    }

    @Override
    @Transactional
    public HistoryRecord getHistoryRecord(Long entityId, Long instanceId, Long historyId) {
        EntityDto entity = getEntity(entityId);
        MotechDataService service = getServiceForEntity(entity);
        Object instance = service.retrieve(ID, instanceId);

        Object historyInstance = historyService.getSingleHistoryInstance(instance, historyId);

        return convertToHistoryRecord(historyInstance, entity, instanceId);
    }

    @Override
    @Transactional
    public EntityRecord newInstance(Long entityId) {
        List<FieldDto> fields = entityService.getEntityFields(entityId);
        List<FieldRecord> fieldRecords = new ArrayList<>();
        for (FieldDto field : fields) {

            FieldRecord fieldRecord = new FieldRecord(field);
            fieldRecords.add(fieldRecord);
        }
        populateDefaultFields(fieldRecords);


        return new EntityRecord(null, entityId, fieldRecords);
    }

    @Override
    @Transactional
    public EntityRecord getEntityInstance(Long entityId, Long instanceId) {
        EntityDto entity = getEntity(entityId);

        MotechDataService service = getServiceForEntity(entity);

        Object instance = service.retrieve(ID, instanceId);

        if (instance == null) {
            throw new ObjectNotFoundException();
        }

        List<FieldDto> fields = entityService.getEntityFields(entityId);

        return instanceToRecord(instance, entity, fields);
    }

    @Override
    @Transactional
    public void deleteInstance(Long entityId, Long instanceId) {
        EntityDto entity = getEntity(entityId);

        MotechDataService service = getServiceForEntity(entity);
        service.delete(ID, instanceId);
    }

    @Override
    @Transactional
    public void revertInstanceFromTrash(Long entityId, Long instanceId) {
        EntityDto entity = getEntity(entityId);
        MotechDataService service = getServiceForEntity(entity);
        Object trash = service.findTrashInstanceById(instanceId, entityId);
        List<FieldRecord> fieldRecords = new LinkedList<>();
        Class<?> entityClass;
        Object newInstance = null;
        try {
            for (FieldDto field : entityService.getEntityFields(entity.getId())) {
                if ("id".equalsIgnoreCase(field.getBasic().getDisplayName())) {
                    continue;
                }
                Field f = trash.getClass().getDeclaredField(field.getBasic().getName());
                f.setAccessible(true);
                FieldRecord record = new FieldRecord(field);
                record.setValue(f.get(trash));
                fieldRecords.add(record);
            }
            entityClass = getEntityClass(entity);
            newInstance = entityClass.newInstance();
            updateFields(newInstance, fieldRecords, service, null);
        } catch (Exception e) {
            LOGGER.error("Field for " + entity.getClassName() + " not found", e);
        }
        service.revertFromTrash(newInstance, trash);
    }

    private void populateDefaultFields(List<FieldRecord> fieldRecords) {
        for (FieldRecord record : fieldRecords) {
            if (Constants.Util.CREATOR_FIELD_NAME.equals(record.getName()) ||
                    Constants.Util.OWNER_FIELD_NAME.equals(record.getName())) {
                record.setValue(SecurityContextHolder.getContext().getAuthentication().getName());
            }
        }
    }

    private LookupDto getLookupByName(Long entityId, String lookupName) {
        LookupDto lookup = entityService.getLookupByName(entityId, lookupName);
        if (lookup == null) {
            throw new LookupNotFoundException();
        }
        return lookup;
    }

    private EntityDto getEntity(Long entityId) {
        EntityDto entityDto = entityService.getEntity(entityId);
        assertEntityExists(entityDto);
        return entityDto;
    }

    private MotechDataService getServiceForEntity(EntityDto entity) {
        String className = entity.getClassName();
        return DataServiceHelper.getDataService(bundleContext, className);
    }

    private void updateFields(Object instance, List<FieldRecord> fieldRecords, MotechDataService service, Long deleteValueFieldId) {
        updateFields(instance, fieldRecords, service, deleteValueFieldId, false);
    }

    private void updateFields(Object instance, List<FieldRecord> fieldRecords, MotechDataService service, Long deleteValueFieldId, boolean retainId) {
        try {
            for (FieldRecord fieldRecord : fieldRecords) {
                // TODO: we ignore setting any relationship fields for now in the data browser
                if (!(retainId && ID.equals(fieldRecord.getName())) && !fieldRecord.getType().isRelationship()) {
                    setProperty(instance, fieldRecord, service, deleteValueFieldId);
                }
            }
        } catch (Exception e) {
            throw new ObjectUpdateException(e);
        }
    }

    private List<EntityRecord> instancesToRecords(Collection instances, EntityDto entity, List<FieldDto> fields) {
        List<EntityRecord> records = new ArrayList<>();
        for (Object instance : instances) {
            EntityRecord record = instanceToRecord(instance, entity, fields);
            records.add(record);
        }
        return records;
    }

    private EntityRecord instanceToRecord(Object instance, EntityDto entityDto, List<FieldDto> fields) {
        if (instance == null) {
            return null;
        }

        try {
            List<FieldRecord> fieldRecords = new ArrayList<>();

            for (FieldDto field : fields) {
                Object value = getProperty(instance, field);

                value = parseValueForDisplay(value, field.getMetadata(Constants.MetadataKeys.RELATED_FIELD));

                FieldRecord fieldRecord = new FieldRecord(field);
                fieldRecord.setValue(value);

                fieldRecords.add(fieldRecord);
            }

            Number id = (Number) PropertyUtil.safeGetProperty(instance, ID);
            return new EntityRecord(id == null ? null : id.longValue(), entityDto.getId(), fieldRecords);
        } catch (Exception e) {
            throw new ObjectReadException(e);
        }
    }

    private HistoryRecord convertToHistoryRecord(Object object, EntityDto entity, Long instanceId) {
        Long entityId = entity.getId();

        EntityRecord entityRecord = instanceToRecord(object, entity, entityService.getEntityFields(entityId));
        Long historyInstanceSchemaVersion = (Long) PropertyUtil.safeGetProperty(object,
                HistoryTrashClassHelper.schemaVersion(object.getClass()));
        Long currentSchemaVersion = entityService.getCurrentSchemaVersion(entity.getClassName());

        return new HistoryRecord(entityRecord.getId(), instanceId,
                historyInstanceSchemaVersion.equals(currentSchemaVersion), entityRecord.getFields());
    }

    private void assertEntityExists(EntityDto entity) {
        if (entity == null) {
            throw new EntityNotFoundException();
        }
    }

    private void setProperty(Object instance, FieldRecord fieldRecord, MotechDataService service, Long deleteValueFieldId) throws NoSuchMethodException, ClassNotFoundException {
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
        } else if (Map.class.getName().equals(methodParameterType)) {
            parameterType = classLoader.loadClass(methodParameterType);

            parsedValue = fieldRecord.getValue();
        } else {
            parameterType = classLoader.loadClass(methodParameterType);

            Object value = fieldRecord.getValue();
            String valueAsString = null == value ? null : TypeHelper.format(value);
            parsedValue = parseValue(holder, methodParameterType, classLoader, valueAsString);
        }
        Method method = MethodUtils.getAccessibleMethod(instance.getClass(), methodName, parameterType);


        if (method == null && TypeHelper.hasPrimitive(parameterType)) {
            method = MethodUtils.getAccessibleMethod(instance.getClass(), methodName, TypeHelper.getPrimitive(parameterType));
            // if the setter is for a primitive, but we have a null, we leave the default
            if (method != null && parsedValue == null) {
                return;
            }
        }

        invokeMethod(method, instance, parsedValue, methodName, fieldName);
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
        } catch (Exception e) {
            throw new IllegalStateException(String.format("There was a problem with set value '%s' to field '%s'", parsedValue, fieldName), e);
        }
    }

    private Object parseValue(ComboboxHolder holder, String methodParameterType, ClassLoader classLoader, String valueAsString) {
        Object parsedValue;

        if (null != holder && holder.isEnumList()) {
            String genericType = holder.getEnumName();
            parsedValue = TypeHelper.parse(valueAsString, List.class.getName(), genericType, classLoader);
        } else {
            parsedValue = TypeHelper.parse(valueAsString, methodParameterType, classLoader);
        }

        return parsedValue;
    }

    private String getMethodParameterType(TypeDto type, ComboboxHolder holder) {
        String methodParameterType;

        if (type.isCombobox() && null != holder) {
            if (holder.isEnum()) {
                methodParameterType = holder.getEnumName();
            } else if (holder.isEnumList()) {
                methodParameterType = List.class.getName();
            } else if (holder.isStringList()) {
                methodParameterType = List.class.getName();
            } else {
                methodParameterType = String.class.getName();
            }
        } else {
            methodParameterType = type.getTypeClass();
        }

        return methodParameterType;
    }

    private Object getProperty(Object instance, FieldDto field)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String fieldName = field.getBasic().getName();

        PropertyDescriptor propertyDescriptor = PropertyUtil.getPropertyDescriptor(instance, fieldName);
        Method readMethod = propertyDescriptor.getReadMethod();

        if (readMethod == null) {
            throw new NoSuchMethodException(String.format("No getter for field %s", fieldName));
        }

        if (TypeDto.BLOB.getTypeClass().equals(field.getType().getTypeClass())) {
            return ArrayUtils.EMPTY_BYTE_OBJECT_ARRAY;
        }

        return readMethod.invoke(instance);
    }

    private Object parseValueForDisplay(Object value, MetadataDto relatedFieldMetadata) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Object parsedValue = value;

        if (parsedValue instanceof DateTime) {
            parsedValue = DTF.print((DateTime) parsedValue);
        } else if (parsedValue instanceof Date) {
            parsedValue = DTF.print(((Date) parsedValue).getTime());
        } else if (parsedValue instanceof Time) {
            parsedValue = ((Time) parsedValue).timeStr();
        } else if (parsedValue instanceof LocalDate) {
            parsedValue = parsedValue.toString();
        } else if (relatedFieldMetadata != null) {
            parsedValue = removeCircularRelations(parsedValue, relatedFieldMetadata.getValue());
        }

        return parsedValue;
    }

    private Object removeCircularRelations(Object object, String relatedField) {
        // we must also handle a field that is a collection
        // because of this we handle regular fields as single objects collection here
        Collection objectsCollection = (object instanceof Collection) ? (Collection) object : Arrays.asList(object);

        for (Object item : objectsCollection) {
            if (item != null) {
                PropertyUtil.safeSetProperty(item, relatedField, null);
            }
        }

        return object;
    }

    private Class<?> getEntityClass(EntityDto entity) throws ClassNotFoundException {
        // get the declaring bundle, for DDE the module bundle, for EUDE the generated entities bundle
        Bundle declaringBundle;
        if (entity.isDDE()) {
            declaringBundle = WebBundleUtil.findBundleByName(bundleContext, entity.getModule());
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
}
