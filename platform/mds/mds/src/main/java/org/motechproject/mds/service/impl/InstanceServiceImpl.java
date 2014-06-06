package org.motechproject.mds.service.impl;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.commons.lang.reflect.MethodUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.domain.ComboboxHolder;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.FieldInstanceDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.dto.LookupFieldDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.ex.EntityNotFoundException;
import org.motechproject.mds.ex.EntitySchemaMismatchException;
import org.motechproject.mds.ex.FieldNotFoundException;
import org.motechproject.mds.ex.LookupExecutionException;
import org.motechproject.mds.ex.LookupNotFoundException;
import org.motechproject.mds.ex.ObjectNotFoundException;
import org.motechproject.mds.ex.ObjectReadException;
import org.motechproject.mds.ex.ObjectUpdateException;
import org.motechproject.mds.ex.ServiceNotFoundException;
import org.motechproject.mds.filter.Filter;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.HistoryService;
import org.motechproject.mds.service.InstanceService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.service.TrashService;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.LookupName;
import org.motechproject.mds.util.MDSClassLoader;
import org.motechproject.mds.util.PropertyUtil;
import org.motechproject.mds.util.TypeHelper;
import org.motechproject.mds.web.domain.EntityRecord;
import org.motechproject.mds.web.domain.FieldRecord;
import org.motechproject.mds.web.domain.HistoryRecord;
import org.motechproject.osgi.web.util.WebBundleUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
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

import static org.motechproject.mds.util.HistoryFieldUtil.schemaVersion;

/**
 * Default implementation of the {@link org.motechproject.mds.service.InstanceService} interface.
 */
@Service
public class InstanceServiceImpl implements InstanceService {

    private static final Logger LOG = LoggerFactory.getLogger(InstanceServiceImpl.class);

    private static final DateTimeFormatter DTF = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm Z");

    private static final String ID = "id";

    private EntityService entityService;
    private BundleContext bundleContext;
    private HistoryService historyService;
    private TrashService trashService;

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
            LOG.error("Unable to save object instance", e);
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
    public List<EntityRecord> getTrashRecords(Long entityId,  QueryParams queryParams) {
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

        MotechDataService service = getServiceForEntity(entity);

        List<Object> args = getLookupArgs(lookup, fields, lookupMap);

        // we pass on the query params last
        args.add(queryParams);

        try {
            String methodName = lookup.getMethodName();

            Object result = MethodUtils.invokeMethod(service, methodName, args.toArray(new Object[args.size()]));

            if (lookup.isSingleObjectReturn()) {
                EntityRecord record = instanceToRecord(result, entity, fields);
                return (record == null) ? new ArrayList<EntityRecord>() : Arrays.asList(record);
            } else {
                List instances = (List) result;
                return instancesToRecords(instances, entity, fields);
            }
        } catch (Exception e) {
            LOG.error("Error while executing lookup " + lookupName, e);
            throw new LookupExecutionException(e);
        }
    }

    @Override
    @Transactional
    public List<EntityRecord> getEntityRecordsWithFilter(Long entityId, Filter filter, QueryParams queryParams) {
        EntityDto entity = getEntity(entityId);
        List<FieldDto> fields = entityService.getEntityFields(entityId);

        MotechDataService service = getServiceForEntity(entity);

        List instances = service.filter(filter, queryParams);

        return instancesToRecords(instances, entity, fields);
    }


    @Override
    @Transactional
    public long countRecordsWithFilter(Long entityId, Filter filter) {
        EntityDto entity = getEntity(entityId);

        MotechDataService service = getServiceForEntity(entity);

        return service.countForFilter(filter);
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

        String methodName = LookupName.lookupCountMethod(lookup.getMethodName());

        List<Object> args = getLookupArgs(lookup, fields, lookupMap);

        MotechDataService service = getServiceForEntity(entity);

        try {
            return (long) MethodUtils.invokeMethod(service, methodName, args.toArray());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            LOG.error("Unable to execute count lookup " + lookupName, e);
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
            EntityRecord entityRecord = instanceToRecord(o, entity, entityService.getEntityFields(entityId));
            Long historyInstanceSchemaVersion = (Long) PropertyUtil.safeGetProperty(o, schemaVersion(o.getClass()));
            Long currentSchemaVersion = entityService.getCurrentSchemaVersion(entity.getClassName());

            result.add(new HistoryRecord(entityRecord.getId(), instanceId,
                    historyInstanceSchemaVersion.equals(currentSchemaVersion), entityRecord.getFields()));
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
        for (HistoryRecord historyRecord : getInstanceHistory(entityId, instanceId, null)) {
            if (historyId.equals(historyRecord.getId())) {
                return historyRecord;
            }
        }
        return null;
    }

    @Override
    @Transactional
    public EntityRecord newInstance(Long entityId) {
        List<FieldDto> fields = entityService.getEntityFields(entityId);
        List<FieldRecord> fieldRecords = new ArrayList<>();
        for (FieldDto field : fields) {
            // TODO: remove this as part of MOTECH-1087
            prepareDefaultValue(field);

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
            LOG.error("Field for " + entity.getClassName() + " not found", e);
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

    private List<Object> getLookupArgs(LookupDto lookup, List<FieldDto> fields, Map<String, Object> lookupMap) {
        List<Object> args = new ArrayList<>();
        for (LookupFieldDto lookupField : lookup.getLookupFields()) {
            FieldDto field = getFieldById(fields, lookupField.getId());

            Object val = lookupMap.get(field.getBasic().getName());
            String typeClass = field.getType().getTypeClass();

            Object arg;
            if (lookupField.getType() == LookupFieldDto.Type.RANGE) {
                arg = TypeHelper.toRange(val, typeClass);
            } else if (lookupField.getType() == LookupFieldDto.Type.SET) {
                arg = TypeHelper.toSet(val, typeClass);
            } else {
                arg = TypeHelper.parse(val, typeClass);
            }

            args.add(arg);
        }
        return args;
    }

    private EntityDto getEntity(Long entityId) {
        EntityDto entityDto = entityService.getEntity(entityId);
        assertEntityExists(entityDto);
        return entityDto;
    }

    private MotechDataService getServiceForEntity(EntityDto entity) {
        String className = entity.getClassName();
        String interfaceName = MotechClassPool.getInterfaceName(className);
        ServiceReference ref = bundleContext.getServiceReference(interfaceName);

        if (ref == null) {
            throw new ServiceNotFoundException();
        }

        return (MotechDataService) bundleContext.getService(ref);
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
            LOG.error("Error while updating fields", e);
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

                value = parseValueForDisplay(value);

                FieldRecord fieldRecord = new FieldRecord(field);
                fieldRecord.setValue(value);

                fieldRecords.add(fieldRecord);
            }

            Field idField = FieldUtils.getDeclaredField(instance.getClass(), ID, true);
            Number id = (Number) idField.get(instance);

            return new EntityRecord(id == null ? null : id.longValue(), entityDto.getId(), fieldRecords);
        } catch (Exception e) {
            LOG.error("Unable to read object", e);
            throw new ObjectReadException(e);
        }
    }

    private void assertEntityExists(EntityDto entity) {
        if (entity == null) {
            throw new EntityNotFoundException();
        }
    }

    private FieldDto getFieldById(List<FieldDto> fields, Long id) {
        for (FieldDto field : fields) {
            if (field.getId().equals(id)) {
                return field;
            }
        }
        throw new FieldNotFoundException();
    }

    private void setProperty(Object instance, FieldRecord fieldRecord, MotechDataService service, Long deleteValueFieldId) throws NoSuchMethodException, ClassNotFoundException {
        String fieldName = fieldRecord.getName();
        TypeDto type = fieldRecord.getType();

        String methodName = "set" + StringUtils.capitalize(fieldName);
        ComboboxHolder holder = type.isCombobox() ? new ComboboxHolder(instance, fieldRecord) : null;
        String methodParameterType = getMethodParameterType(type, holder);

        ClassLoader classLoader = instance.getClass().getClassLoader();

        Class<?> parameterType;
        Object parsedValue;
        if (Byte[].class.getName().equals(methodParameterType)) {
            parameterType = Byte[].class;

            if (ArrayUtils.EMPTY_BYTE_OBJECT_ARRAY.equals(fieldRecord.getValue()) && !fieldRecord.getId().equals(deleteValueFieldId)) {
                parsedValue = service.getDetachedField(instance, fieldName);
            } else {
                parsedValue = fieldRecord.getValue();
            }

            parsedValue = verifyParsedValue(parsedValue);
        } else {
            parameterType = classLoader.loadClass(methodParameterType);

            Object value = fieldRecord.getValue();
            String valueAsString = null == value ? null : value.toString();
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
            method.invoke(instance, parsedValue);
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

    private Object parseValueForDisplay(Object value) {
        Object parsedValue = value;

        if (parsedValue instanceof DateTime) {
            parsedValue = DTF.print((DateTime) parsedValue);
        } else if (parsedValue instanceof Date) {
            parsedValue = DTF.print(((Date) parsedValue).getTime());
        } else if (parsedValue instanceof Time) {
            parsedValue = ((Time) parsedValue).timeStr();
        } else if (parsedValue instanceof Map) {
            parsedValue = parseMapForDisplay((Map) parsedValue);
        } else if (parsedValue instanceof LocalDate) {
            parsedValue = parsedValue.toString();
        }

        return parsedValue;
    }

    private String parseMapForDisplay(Map map) {
        StringBuilder displayValue = new StringBuilder();

        for (Object entry : map.entrySet()) {
            displayValue = displayValue
                    .append(((Map.Entry) entry).getKey().toString())
                    .append(": ")
                    .append(((Map.Entry) entry).getValue().toString())
                    .append("\n");
        }

        return displayValue.toString();
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

    private void prepareDefaultValue(FieldDto field) {
        if (LocalDate.class.getName().equals(field.getType().getTypeClass())) {
            Object val = TypeHelper.parse(field.getBasic().getDefaultValue(),
                                          LocalDate.class);

            if (val != null) {
                val = val.toString();
            }

            field.getBasic().setDefaultValue(val);
        }
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
}
