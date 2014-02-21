package org.motechproject.mds.service.impl.internal;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.builder.MDSClassLoader;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.FieldInstanceDto;
import org.motechproject.mds.ex.EntityNotFoundException;
import org.motechproject.mds.ex.ObjectNotFoundException;
import org.motechproject.mds.ex.ObjectReadException;
import org.motechproject.mds.ex.ObjectUpdateException;
import org.motechproject.mds.ex.ServiceNotFoundException;
import org.motechproject.mds.service.BaseMdsService;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.InstanceService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.Order;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException  e) {
            LOG.error("Unable to save object instance", e);
            throw new ObjectUpdateException(e);
        }
    }

    @Override
    @Transactional
    public List<EntityRecord> getEntityRecordsPaged(Long entityId, int page, int rows, Order order) {
        return retrieveRecords(entityId, page, rows, order);
    }

    @Override
    @Transactional
    public List<EntityRecord> getEntityRecords(Long entityId) {
        return retrieveRecords(entityId, null, null, null);
    }

    private List<EntityRecord> retrieveRecords(Long entityId, Integer page, Integer rows, Order order) {
        EntityDto entity = entityService.getEntity(entityId);

        assertEntityExists(entity);

        List<FieldDto> fields = entityService.getFields(entityId);

        MotechDataService service = getServiceForEntity(entity);

        List list;

        if (page == null || rows == null) {
            list = service.retrieveAll();
        } else if (order == null) {
            list = service.retrieveAll(page, rows);
        } else {
            list = service.retrieveAll(page, rows, order);
        }

        List<EntityRecord> records = new ArrayList<>();
        for (Object instance : list) {
            EntityRecord record = instanceToRecord(instance, entity, fields);
            records.add(record);
        }

        return records;
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
    public EntityRecord newInstance(Long entityId) {
        List<FieldDto> fields = entityService.getEntityFields(entityId);
        List<FieldRecord> fieldRecords = new ArrayList<>();
        for (FieldDto field : fields) {
            FieldRecord fieldRecord = new FieldRecord(field);
            fieldRecords.add(fieldRecord);
        }

        return new EntityRecord(null, entityId, fieldRecords);
    }

    @Override
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

    private EntityDto getEntity(Long entityId) {
        EntityDto entityDto = entityService.getEntity(entityId);
        assertEntityExists(entityDto);
        return entityDto;
    }

    private MotechDataService getServiceForEntity(EntityDto entity) {
        ServiceReference ref = bundleContext.getServiceReference(ClassName.getInterfaceName(entity.getClassName()));
        if (ref == null) {
            throw new ServiceNotFoundException();
        }
        return (MotechDataService) bundleContext.getService(ref);
    }

    private void updateFields(Object instance, List<FieldRecord> fieldRecords) {
        try {
            for (FieldRecord fieldRecord : fieldRecords) {
                Object value = fieldRecord.getValue();

                Object parsedValue = TypeHelper.parse(value, fieldRecord.getType().getTypeClass());

                PropertyUtils.setProperty(instance, fieldRecord.getName(), parsedValue);
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOG.error("Error while updating fields", e);
            throw new ObjectUpdateException(e);
        }
    }

    private EntityRecord instanceToRecord(Object instance, EntityDto entityDto, List<FieldDto> fields) {
        try {
            List<FieldRecord> fieldRecords = new ArrayList<>();

            for (FieldDto field : fields) {
                Object value = PropertyUtils.getProperty(instance, field.getBasic().getName());

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
            Long id = (Long) idField.get(instance);

            return new EntityRecord(id, entityDto.getId(), fieldRecords);
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

    @Autowired
    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }

    @Autowired
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
}
