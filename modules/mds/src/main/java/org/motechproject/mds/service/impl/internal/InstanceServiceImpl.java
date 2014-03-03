package org.motechproject.mds.service.impl.internal;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.commons.lang.reflect.MethodUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.builder.MDSClassLoader;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.FieldInstanceDto;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.ex.EntityNotFoundException;
import org.motechproject.mds.ex.FieldNotFoundException;
import org.motechproject.mds.ex.LookupExecutionException;
import org.motechproject.mds.ex.LookupNotFoundException;
import org.motechproject.mds.ex.ObjectNotFoundException;
import org.motechproject.mds.ex.ObjectReadException;
import org.motechproject.mds.ex.ObjectUpdateException;
import org.motechproject.mds.ex.ServiceNotFoundException;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.service.BaseMdsService;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.InstanceService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.LookupName;
import org.motechproject.mds.util.QueryParams;
import org.motechproject.mds.util.TypeHelper;
import org.motechproject.mds.web.ExampleData;
import org.motechproject.mds.web.domain.EntityRecord;
import org.motechproject.mds.web.domain.FieldRecord;
import org.motechproject.mds.web.domain.HistoryRecord;
import org.motechproject.mds.web.domain.PreviousRecord;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of the {@link org.motechproject.mds.service.InstanceService} interface.
 */
@Service
public class InstanceServiceImpl extends BaseMdsService implements InstanceService {

    private static final Logger LOG = LoggerFactory.getLogger(InstanceServiceImpl.class);

    private static final DateTimeFormatter DTF = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm Z");

    private ExampleData exampleData = new ExampleData();

    private EntityService entityService;
    private BundleContext bundleContext;

    @Override
    @Transactional
    public Object saveInstance(EntityRecord entityRecord) {
        EntityDto entity = getEntity(entityRecord.getEntitySchemaId());
        String className = entity.getClassName();

        try {
            MotechDataService service = getServiceForEntity(entity);
            Class<?> entityClass = MDSClassLoader.getInstance().loadClass(className);

            boolean newObject = entityRecord.getId() == null;

            Object instance;
            if (newObject) {
                instance = entityClass.newInstance();
            } else {
                instance = service.retrieve("id", entityRecord.getId());
                if (instance == null) {
                    throw new ObjectNotFoundException();
                }
            }

            updateFields(instance, entityRecord.getFields());

            if (newObject) {
                return service.create(instance);
            } else {
                return service.update(instance);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
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
        List<FieldDto> fields = entityService.getFields(entityId);

        MotechDataService service = getServiceForEntity(entity);

        List instances = service.retrieveAll(queryParams);

        return instancesToRecords(instances, entity, fields);
    }

    @Override
    @Transactional
    public List<EntityRecord> getEntityRecordsFromLookup(Long entityId, String lookupName, Map<String, String> lookupMap,
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
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            LOG.error("Error while executing lookup " + lookupName, e);
            throw new LookupExecutionException(e);
        }
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
    public long countRecordsByLookup(Long entityId, String lookupName, Map<String, String> lookupMap) {
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
    public List<HistoryRecord> getInstanceHistory(Long instanceId) {
        return exampleData.getInstanceHistoryRecordsById(instanceId);
    }

    @Override
    @Transactional
    public List<PreviousRecord> getPreviousRecords(Long instanceId) {
        return exampleData.getPreviousRecordsById(instanceId);
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

        Object instance = service.retrieve("id", instanceId);

        if (instance == null) {
            throw new ObjectNotFoundException();
        }

        List<FieldDto> fields = entityService.getEntityFields(entityId);

        return instanceToRecord(instance, entity, fields);
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

    private List<Object> getLookupArgs(LookupDto lookup, List<FieldDto> fields, Map<String, String> lookupMap) {
        List<Object> args = new ArrayList<>();
        for (Long lookupFieldId : lookup.getFieldList()) {
            FieldDto field = getFieldById(fields, lookupFieldId);

            String val = lookupMap.get(field.getBasic().getName());

            Object arg = TypeHelper.parse(val, field.getType().getTypeClass());
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
        ServiceReference ref = bundleContext.getServiceReference(MotechClassPool.getInterfaceName(entity.getClassName()));
        if (ref == null) {
            throw new ServiceNotFoundException();
        }
        return (MotechDataService) bundleContext.getService(ref);
    }

    private void updateFields(Object instance, List<FieldRecord> fieldRecords) {
        try {
            for (FieldRecord fieldRecord : fieldRecords) {
                setProperty(instance, fieldRecord);
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

                // turn dates to string format
                if (value instanceof DateTime) {
                    value = DTF.print((DateTime) value);
                } else if (value instanceof Date) {
                    value = DTF.print(((Date) value).getTime());
                } else if (value instanceof Time) {
                    value = ((Time) value).timeStr();
                    // TODO: temporary solution for single value select combobox
                } else if (value instanceof List) {
                    List list = (List) value;
                    if (!list.isEmpty()) {
                        value = list.get(0);
                    }
                }

                FieldRecord fieldRecord = new FieldRecord(field);
                fieldRecord.setValue(value);

                fieldRecords.add(fieldRecord);
            }

            Field idField = FieldUtils.getDeclaredField(instance.getClass(), "id", true);
            Number id = (Number) idField.get(instance);

            return new EntityRecord(id.longValue(), entityDto.getId(), fieldRecords);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
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

    private void setProperty(Object instance, FieldRecord fieldRecord)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        Object value = fieldRecord.getValue();

        Object parsedValue = TypeHelper.parse(value, fieldRecord.getType().getTypeClass());
        String methodName = "set" + StringUtils.capitalize(fieldRecord.getName());
        Class<?> propertyClass = MDSClassLoader.getInstance().loadClass(fieldRecord.getType().getTypeClass());

        Method method = MethodUtils.getAccessibleMethod(instance.getClass(), methodName, propertyClass);
        if (method == null) {
            throw new NoSuchMethodException(String.format("No setter %s for field %s", methodName, fieldRecord.getName()));
        }

        method.invoke(instance, parsedValue);
    }

    private Object getProperty(Object instance, FieldDto field)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String fieldName = field.getBasic().getName();
        String methodName = "get" + StringUtils.capitalize(fieldName);

        Method method = MethodUtils.getAccessibleMethod(instance.getClass(), methodName, new Class[]{});
        // for booleans try the 'is' getter
        if (method == null && field.getType().getTypeClass().equals(Boolean.class.getName())) {
            methodName = "is" + StringUtils.capitalize(fieldName);
            method = MethodUtils.getAccessibleMethod(instance.getClass(), methodName, new Class[]{});
        }

        if (method == null) {
            throw new NoSuchMethodException(String.format("No getter %s for field %s", methodName, fieldName));
        }

        return method.invoke(instance);
    }

    @Autowired
    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }

    @Autowired
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
}
