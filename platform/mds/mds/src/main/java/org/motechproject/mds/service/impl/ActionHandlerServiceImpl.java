package org.motechproject.mds.service.impl;

import org.motechproject.mds.domain.ComboboxHolder;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.RelationshipHolder;
import org.motechproject.mds.exception.action.ActionHandlerException;
import org.motechproject.mds.exception.entity.ServiceNotFoundException;
import org.motechproject.mds.helper.DataServiceHelper;
import org.motechproject.mds.repository.internal.AllEntities;
import org.motechproject.mds.service.ActionHandlerService;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.PropertyUtil;
import org.motechproject.mds.util.TypeHelper;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * Default implementation of ActionHandlerService interface
 *
 * @see org.motechproject.mds.service.ActionHandlerService
 */
@Service
public class ActionHandlerServiceImpl implements ActionHandlerService {
    private static final String ENTITY_KEY = "@ENTITY";
    private static final String ENTITY_ID_KEY = Constants.Util.ID_FIELD_NAME;
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionHandlerServiceImpl.class);

    private BundleContext bundleContext;
    private AllEntities allEntities;

    @Override
    public void create(Map<String, Object> parameters) throws ActionHandlerException {
        LOGGER.debug("Action CREATE: params: {}", parameters);

        String entityClassName = getEntityClassName(parameters);
        MotechDataService dataService = getEntityDataService(entityClassName);
        Entity entity = getEntity(entityClassName);

        Object instance = createEntityInstance(dataService);
        setInstanceProperties(instance, entity.getFields(), parameters);

        dataService.create(instance);
    }

    @Override
    @Transactional
    public void update(Map<String, Object> parameters) throws ActionHandlerException {
        LOGGER.debug("Action UPDATE: params: {}", parameters);

        String entityClassName = getEntityClassName(parameters);
        MotechDataService dataService = getEntityDataService(entityClassName);
        Entity entity = getEntity(entityClassName);

        Long instanceId = getInstanceId(parameters, true);
        Object instance = retrieveEntityInstance(dataService, instanceId);
        setInstanceProperties(instance, entity.getFields(), parameters);

        dataService.update(instance);
    }

    @Override
    @Transactional
    public void createOrUpdate(Map<String, Object> parameters) throws ActionHandlerException {
        LOGGER.debug("Action CREATE OR UPDATE: params {}", parameters);

        String entityClassName = getEntityClassName(parameters);
        MotechDataService dataService = getEntityDataService(entityClassName);
        Entity entity = getEntity(entityClassName);
        Long instanceId = getInstanceId(parameters, false);

        Object instance = createEntityInstance(dataService);
        PropertyUtil.safeSetProperty(instance, Constants.Util.ID_FIELD_NAME, instanceId);
        setInstanceProperties(instance, entity.getFields(), parameters);

        dataService.createOrUpdate(instance);
    }

    @Override
    @Transactional
    public void delete(Map<String, Object> parameters) throws ActionHandlerException {
        LOGGER.debug("Action DELETE: params: {}", parameters);

        String entityClassName = getEntityClassName(parameters);
        MotechDataService dataService = getEntityDataService(entityClassName);
        Long instanceId = getInstanceId(parameters, true);
        Object instance = retrieveEntityInstance(dataService, instanceId);

        dataService.delete(instance);
    }

    private void setInstanceProperties(Object instance, List<Field> fields, Map<String, Object> properties) throws ActionHandlerException {
        for (Field field : fields) {
            Object value = properties.get(field.getName());
            if (null != value) {
                setInstanceProperty(instance, field, value);
            }
        }
    }

    private void setInstanceProperty(Object instance, Field field, Object value) throws ActionHandlerException {
        if (field.getType().isRelationship()) {
            setRelationshipInstanceProperty(instance, field, value);
        } else if (field.getType().isCombobox()) {
            setComboboxInstanceProperty(instance, field, value);
        } else if (field.getType().isMap()) {
            setMapInstanceProperty(instance, field, value);
        } else {
            setPlainInstanceProperty(instance, field, value);
        }
    }

    private void setRelationshipInstanceProperty(Object instance, Field field, Object value)
            throws ActionHandlerException {
        RelationshipHolder relationshipHolder = new RelationshipHolder(field);
        try {
            if (null != value) {
                String relatedClassName = relationshipHolder.getRelatedClass();
                MotechDataService relatedClassDataService = getEntityDataService(relatedClassName);

                if (relationshipHolder.isManyToOne() || relationshipHolder.isOneToOne()) {
                    Object relatedInstance = getRelatedInstance(relatedClassDataService, value);
                    PropertyUtil.safeSetProperty(instance, field.getName(), relatedInstance);
                } else if (relationshipHolder.isManyToMany() || relationshipHolder.isOneToMany()) {
                    List<Object> relatedInstances = getRelatedInstances(relatedClassDataService, value);
                    PropertyUtil.safeSetCollectionProperty(instance, field.getName(), relatedInstances);
                }
            } else {
                PropertyUtil.safeSetProperty(instance, field.getName(), null);
            }
        } catch (RuntimeException e) {
            throw new ActionHandlerException("Cannot set instance property " + field.getName() + " with value " + value, e);
        }
    }

    private List<Object> getRelatedInstances(MotechDataService relatedClassDataService, Object value) {
        if (value instanceof List) {
            List list = (List) value;
            List<Object> relatedInstances = new ArrayList<>(list.size());
            for (Object entry : list) {
                Object relatedInstance = getRelatedInstance(relatedClassDataService, entry);
                relatedInstances.add(relatedInstance);
            }
            return relatedInstances;
        } else {
            throw new IllegalArgumentException("Passed argument cannot be converted to List: " + value);
        }
    }

    private Object getRelatedInstance(MotechDataService relatedClassDataService, Object value) {
        if (value.getClass().isAssignableFrom(relatedClassDataService.getClassType())) {
            return value;
        } else {
            Long relatedInstanceId = getRelatedInstanceId(value);
            return relatedClassDataService.findById(relatedInstanceId);
        }
    }

    private Long getRelatedInstanceId(Object value) {
        if (value instanceof String) {
            return Long.parseLong((String) value);
        } else if (value instanceof Long || value instanceof Integer) {
            return ((Number) value).longValue();
        } else {
            throw new IllegalArgumentException("Passed argument cannot be converted to Long: " + value);
        }
    }

    private void setComboboxInstanceProperty(Object instance, Field field, Object value) {
        ComboboxHolder comboboxHolder = new ComboboxHolder(field);
        String underlyingType = comboboxHolder.getUnderlyingType();
        if (comboboxHolder.isAllowMultipleSelections()) {
            setMultiValueComboboxInstanceProperty(instance, field, value, underlyingType);
        } else {
            setSingleValueComboboxInstanceProperty(instance, field, value, underlyingType);
        }
    }

    private void setMultiValueComboboxInstanceProperty(Object instance, Field field, Object value, String underlyingType) {
        if (value instanceof List) {
            List list = (List) value;
            List<Object> parsedValues = new ArrayList<>();
            for (Object entry : list) {
                Object parsedValue = TypeHelper.parse(entry, underlyingType, instance.getClass().getClassLoader());
                parsedValues.add(parsedValue);
            }
            PropertyUtil.safeSetCollectionProperty(instance, field.getName(), parsedValues);
        } else {
            throw new IllegalArgumentException("Passed argument is not a List: " + value);
        }
    }

    private void setSingleValueComboboxInstanceProperty(Object instance, Field field, Object value, String underlyingType) {
        Object parsedValue = TypeHelper.parse(value, underlyingType, instance.getClass().getClassLoader());
        PropertyUtil.safeSetProperty(instance, field.getName(), parsedValue);
    }

    private void setMapInstanceProperty(Object instance, Field field, Object value) {
        if (value instanceof Map) {
            Map map = (Map) value;
            Map<String, String> convertedMap = convertToStringStringMap(map);
            PropertyUtil.safeSetProperty(instance, field.getName(), convertedMap);
        } else {
            throw new IllegalArgumentException("Passed argument is not a Map: " + value);
        }
    }

    private Map<String, String> convertToStringStringMap(Map map) {
        Map<String, String> convertedMap = new HashMap<>();
        for (Object entryObject : map.entrySet()) {
            Map.Entry entry = (Map.Entry) entryObject;
            convertedMap.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
        }
        return convertedMap;
    }

    private void setPlainInstanceProperty(Object instance, Field field, Object value) throws ActionHandlerException {
        try {
            Class<?> propertyType = PropertyUtil.getPropertyType(instance, field.getName());
            Object parsedValue = TypeHelper.parse(value, propertyType);
            PropertyUtil.setProperty(instance, field.getName(), parsedValue);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new ActionHandlerException("Cannot set instance property " + field.getName() + " with value " + value, e);
        }
    }

    private Entity getEntity(String entityClassName) {
        return allEntities.retrieveByClassName(entityClassName);
    }

    private String getEntityClassName(Map<String, Object> parameters) throws ActionHandlerException {
        String entityClassName = (String) parameters.get(ENTITY_KEY);
        if (isBlank(entityClassName)) {
            throw new ActionHandlerException("Missing entity name");
        }
        return entityClassName;
    }

    private Object createEntityInstance(MotechDataService dataService) throws ActionHandlerException {
        Class<?> entityClass = dataService.getClassType();
        try {
            return entityClass.newInstance();
        } catch (InstantiationException e) {
            throw new ActionHandlerException("Cannot instantiate entity: " + entityClass.getName(), e);
        } catch (IllegalAccessException e) {
            throw new ActionHandlerException("No access to entity constructor: " + entityClass.getName(), e);
        }
    }

    private Long getInstanceId(Map<String, Object> parameters, boolean required) throws ActionHandlerException {
        try {
            Long instanceId = (Long) parameters.get(ENTITY_ID_KEY);
            if (required && null == instanceId) {
                throw new ActionHandlerException("Missing instance id");
            }
            return instanceId;
        } catch (ClassCastException e) {
            throw new ActionHandlerException("Invalid instance id format", e);
        }
    }

    private Object retrieveEntityInstance(MotechDataService dataService, Long instanceId) throws ActionHandlerException {
        Object instance = dataService.findById(instanceId);
        if (null == instance) {
            throw new ActionHandlerException("Instance does not exists");
        }
        return instance;
    }

    private MotechDataService getEntityDataService(String entityClassName) throws ActionHandlerException {
        try {
            return DataServiceHelper.getDataService(bundleContext, entityClassName);
        } catch (ServiceNotFoundException e) {
            throw new ActionHandlerException("Not found data service for entity: " + entityClassName, e);
        }
    }

    @Autowired
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Autowired
    public void setAllEntities(AllEntities allEntities) {
        this.allEntities = allEntities;
    }
}
