package org.motechproject.mds.service.impl.history;

import org.motechproject.mds.domain.ComboboxHolder;
import org.motechproject.mds.domain.EntityType;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.FieldMetadata;
import org.motechproject.mds.domain.RelationshipHolder;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.ex.entity.ServiceNotFoundException;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.ObjectReferenceRepository;
import org.motechproject.mds.util.PropertyUtil;
import org.motechproject.mds.util.TypeHelper;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import static org.motechproject.mds.util.Constants.MetadataKeys.RELATED_CLASS;

/**
 * This class is required for retrieving values from entities.
 * Since the history implementation makes additional changes to records
 * involved in relationships and sets additional field, this
 * class gives it an easy way to override the default behaviour.
 */
public class ValueGetter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValueGetter.class);

    private BasePersistenceService persistenceService;
    private BundleContext bundleContext;


    public ValueGetter(BasePersistenceService persistenceService, BundleContext bundleContext) {
        this.persistenceService = persistenceService;
        this.bundleContext = bundleContext;
    }

    public Object getValue(Field field, Object instance, Object recordInstance, EntityType type,
                           ObjectReferenceRepository objectReferenceRepository) {
        Type fieldType = field.getType();

        Long id = (Long) PropertyUtil.safeGetProperty(instance, Constants.Util.ID_FIELD_NAME);
        Object value = fieldType.isBlob()
                ? findService(instance.getClass()).getDetachedField(id, field.getName())
                : PropertyUtil.safeGetProperty(instance, field.getName());

        if (null == value) {
            return null;
        } else if (fieldType.isRelationship()) {
            objectReferenceRepository.saveHistoricalObject(instance, recordInstance);
            value = parseRelationshipValue(field, type, value, objectReferenceRepository);
            value = adjustRelationshipValue(value, field);
        } else if (!TypeHelper.isPrimitive(value.getClass()) && !fieldType.isBlob() && !fieldType.isMap()) {
            ComboboxHolder holder = fieldType.isCombobox() ? new ComboboxHolder(field) : null;
            value = parseValue(recordInstance, fieldType, holder, value);
        }

        return value;
    }

    private Object adjustRelationshipValue(Object value, Field field) {
        RelationshipHolder holder = new RelationshipHolder(field);

        // if a single object returned for a collection type relationship
        if ((holder.isOneToMany() || holder.isManyToMany()) &&
                (value != null && !(value instanceof Collection))) {

            Collection collection = getCollectionInstanceFromRelationshipType(field);
            collection.add(value);
            return collection;
        } else {
            return value;
        }
    }

    private Object parseRelationshipValue(Field field, EntityType type, Object value, ObjectReferenceRepository objectReferenceRepository) {
        FieldMetadata relatedClassMetadata = field.getMetadata(RELATED_CLASS);

        String className = relatedClassMetadata.getValue();
        Object obj = value;

        Class<?> clazz = HistoryTrashClassHelper.getClass(className, type, bundleContext);

        if (obj instanceof Collection) {
            Collection collection = (Collection) obj;
            Collection tmp = getCollectionInstanceFromRelationshipType(field);

            for (Object element : collection) {
                Object item = objectReferenceRepository.getHistoricalObject(element);

                if (item == null) {
                    item = persistenceService.create(clazz, element, type, this, objectReferenceRepository);
                }

                tmp.add(item);
            }

            obj = tmp;
        } else {
            Object item = objectReferenceRepository.getHistoricalObject(obj);

            if (item == null) {
                obj = persistenceService.create(clazz, obj, type, this, objectReferenceRepository);
            } else {
                obj = item;
            }
        }

        return obj;
    }

    private Collection getCollectionInstanceFromRelationshipType(Field field) {
        Collection instance = null;
        FieldMetadata metadata = field.getMetadata(Constants.MetadataKeys.RELATIONSHIP_COLLECTION_TYPE);

        if (metadata != null) {
            String collectionType = metadata.getValue();
            Class<? extends Collection> clazz = TypeHelper.suggestCollectionImplementation(collectionType);
            try {
                instance = clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.error("Failed while attempting to create an instance of collection type: {}", metadata.getValue());
            }
        }

        return instance;
    }

    /**
     * Updates fields of a new record after it is created.
     * Called for newly created relationship records as well.
     * @param newHistoryRecord the newly created record
     * @param realCurrentObj the actual current object
     */
    protected void updateRecordFields(Object newHistoryRecord, Object realCurrentObj) {
    }

    private Object parseValue(Object target, Type fieldType, ComboboxHolder holder, Object value) {
        // the value should be from the same class loader as history object
        ClassLoader classLoader = target.getClass().getClassLoader();
        String valueAsString;

        if (value instanceof Date) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            valueAsString = df.format(value);
        } else {
            valueAsString = value.toString();
        }

        Object obj;

        if (null != holder && holder.isEnumCollection()) {
            String genericType = holder.getEnumName();
            obj = TypeHelper.parse(valueAsString, holder.getTypeClassName(), genericType, classLoader);
        } else {
            String methodParameterType = HistoryTrashClassHelper.getMethodParameterType(fieldType, holder);
            obj = TypeHelper.parse(valueAsString, methodParameterType, classLoader);
        }

        return obj;
    }


    protected MotechDataService findService(Class<?> clazz) {
        String interfaceName = MotechClassPool.getInterfaceName(clazz.getName());
        ServiceReference ref = bundleContext.getServiceReference(interfaceName);

        if (ref == null) {
            throw new ServiceNotFoundException(interfaceName);
        }

        return (MotechDataService) bundleContext.getService(ref);
    }
}
