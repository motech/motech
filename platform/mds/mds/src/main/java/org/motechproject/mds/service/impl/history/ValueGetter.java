package org.motechproject.mds.service.impl.history;

import org.motechproject.mds.domain.ComboboxHolder;
import org.motechproject.mds.domain.EntityType;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.FieldMetadata;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.ex.ServiceNotFoundException;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.ObjectReference;
import org.motechproject.mds.util.PropertyUtil;
import org.motechproject.mds.util.TypeHelper;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.motechproject.mds.util.Constants.MetadataKeys.RELATED_CLASS;
import static org.motechproject.mds.util.Constants.MetadataKeys.RELATED_FIELD;

public class ValueGetter {

    private BasePersistenceService persistenceService;
    private BundleContext bundleContext;

    public ValueGetter(BasePersistenceService persistenceService, BundleContext bundleContext) {
        this.persistenceService = persistenceService;
        this.bundleContext = bundleContext;
    }

    public Object getValue(Field field, Object src, Object target, EntityType type, ObjectReference objectReference) {
        Type fieldType = field.getType();
        ComboboxHolder holder = fieldType.isCombobox() ? new ComboboxHolder(field) : null;

        Object value = fieldType.isBlob()
                ? findService(src.getClass()).getDetachedField(src, field.getName())
                : PropertyUtil.safeGetProperty(src, field.getName());

        if (null == value) {
            return null;
        } else if (fieldType.isRelationship()) {
            if (objectReference != null && field.getName().equals(objectReference.getFieldName())) {
                value = getValueForReference(objectReference);
            } else {
                value = parseRelationshipValue(field, type, value, target);
            }
        } else if (!TypeHelper.isPrimitive(value.getClass()) && !fieldType.isBlob()) {
            value = parseValue(target, fieldType, holder, value);
        }

        return value;
    }

    private Object parseRelationshipValue(Field field, EntityType type, Object value, Object reference) {
        FieldMetadata relatedClassMetadata = field.getMetadata(RELATED_CLASS);
        FieldMetadata relatedFieldMetadata = field.getMetadata(RELATED_FIELD);

        String className = relatedClassMetadata.getValue();
        String fieldName = relatedFieldMetadata == null ? null : relatedFieldMetadata.getValue();
        Object obj = value;

        Class<?> clazz = HistoryTrashClassHelper.getClass(className, type, bundleContext);

        if (obj instanceof Collection) {
            Collection collection = (Collection) obj;
            List tmp = new ArrayList();

            for (Object element : collection) {
                Object item;

                if (fieldName != null) {
                    item = persistenceService.create(clazz, element, type, this,
                            new ObjectReference(fieldName, reference, field.getName()));
                } else {
                    item = persistenceService.create(clazz, element, type, this);
                }

                tmp.add(item);
            }

            obj = tmp;
        } else {
            if (fieldName != null) {
                obj = persistenceService.create(clazz, obj, type, this,
                        new ObjectReference(fieldName, reference, field.getName()));
            } else {
                obj = persistenceService.create(clazz, obj, type, this);
            }
        }

        return obj;
    }

    // TODO: abstract? check trash
    protected void updateRelationshipField(Object newHistoryField, Object realCurrentField) {
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

        if (null != holder && holder.isEnumList()) {
            String genericType = holder.getEnumName();
            obj = TypeHelper.parse(valueAsString, List.class.getName(), genericType, classLoader);
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
            throw new ServiceNotFoundException();
        }

        return (MotechDataService) bundleContext.getService(ref);
    }

    protected Object getValueForReference(ObjectReference reference) {
        return reference.getReference();
    }
}
