package org.motechproject.mds.helper;

import org.apache.commons.lang.reflect.MethodUtils;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.FieldMetadata;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.util.MemberUtil;
import org.motechproject.mds.util.PropertyUtil;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.motechproject.mds.util.Constants.MetadataKeys.ENUM_CLASS_NAME;
import static org.motechproject.mds.util.Constants.MetadataKeys.OWNING_SIDE;
import static org.motechproject.mds.util.Constants.MetadataKeys.RELATED_CLASS;
import static org.motechproject.mds.util.Constants.MetadataKeys.RELATED_FIELD;
import static org.motechproject.mds.util.Constants.MetadataKeys.RELATIONSHIP_COLLECTION_TYPE;

/**
 * Utility class handling dynamic setting of field values
 */
public final class FieldHelper {

    public static void setField(Object current, String path, List value) {
        String[] splitPath = path.split("\\.");

        Object target = findTargetForField(current, splitPath);
        setFieldOnTarget(target, splitPath[splitPath.length - 1], value);
    }

    public static Map<String, Field> fieldMapByName(Collection<Field> fields) {
        Map<String, Field> map = new HashMap<>(fields.size());
        for (Field field : fields) {
            map.put(field.getName(), field);
        }
        return map;
    }

    public static void addOrUpdateMetadataForCombobox(Field field) {
        if (field.getType().isCombobox()) {
            FieldMetadata metadata = field.getMetadata(ENUM_CLASS_NAME);
            if (metadata == null) {
                metadata = new FieldMetadata(field, ENUM_CLASS_NAME);
                metadata.setValue(MemberUtil.getDefaultEnumName(field.getEntity().getClassName(), field.getName()));
                field.addMetadata(metadata);
            } else {
                field.setMetadataValue(ENUM_CLASS_NAME, MemberUtil.getDefaultEnumName(field.getEntity().getClassName(), field.getName()));
            }
        }
    }

    public static void addMetadataForRelationship(String typeClass, Field field) {
        if (TypeDto.ONE_TO_ONE_RELATIONSHIP.getTypeClass().equals(typeClass) ||
                TypeDto.MANY_TO_ONE_RELATIONSHIP.getTypeClass().equals(typeClass)) {
            setMetadataForOneToOneRelationship(field);
        } else if (TypeDto.ONE_TO_MANY_RELATIONSHIP.getTypeClass().equals(typeClass)) {
            setMetadataForOneToManyRelationship(field);
        } else if (TypeDto.MANY_TO_MANY_RELATIONSHIP.getTypeClass().equals(typeClass)) {
            setMetadataForManyToManyRelationship(field, true);
        }
    }

    public static void createMetadataForManyToManyRelationship(Field field, String relatedClass, String collectionType,
                                                               String relatedField, boolean isOwningSide) {
        setMetadataForManyToManyRelationship(field, isOwningSide);
        field.setMetadataValue(RELATED_CLASS, relatedClass);
        field.setMetadataValue(RELATIONSHIP_COLLECTION_TYPE, collectionType);
        field.setMetadataValue(RELATED_FIELD, relatedField);
    }

    private static void setMetadataForOneToOneRelationship(Field field) {
        if (field != null) {
            FieldMetadata metadata = new FieldMetadata(field, RELATED_CLASS);
            field.addMetadata(metadata);
        }
    }

    private static void setMetadataForOneToManyRelationship(Field field) {
        setMetadataForOneToOneRelationship(field);
        if (field != null) {
            FieldMetadata metadata = new FieldMetadata(field, RELATIONSHIP_COLLECTION_TYPE);
            field.addMetadata(metadata);
        }
    }

    public static void setMetadataForManyToManyRelationship(Field field, boolean isOwningSide) {
        setMetadataForOneToManyRelationship(field);
        if (field != null) {
            FieldMetadata metadata = new FieldMetadata(field, RELATED_FIELD);
            field.addMetadata(metadata);
            if (isOwningSide) {
                metadata = new FieldMetadata(field, OWNING_SIDE);
                metadata.setValue("true");
                field.addMetadata(metadata);
            }
        }
    }

    private static void setFieldOnTarget(Object target, String property, List value) {
        try {
            if (property.startsWith("$")) {
                String methodName = property.substring(1);
                Class[] parameterTypes = new Class[null == value ? 0 : value.size()];
                Object[] args = null != value
                        ? value.toArray(new Object[value.size()])
                        : new Object[0];

                for (int i = 0; i < args.length; ++i) {
                    Object item = args[i];
                    parameterTypes[i] = item instanceof List ? List.class : item.getClass();
                }

                MethodUtils.invokeMethod(target, methodName, args, parameterTypes);
            } else {
                PropertyDescriptor descriptor = PropertyUtil.getPropertyDescriptor(target, property);

                if (descriptor == null) {
                    throw new IllegalStateException("Property [" + property + "] not available on class: "
                            + target.getClass().getName());
                } else {
                    PropertyUtil.safeSetProperty(target, property, value.get(0));
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static Object findTargetForField(Object start, String[] path) {
        Object current = start;

        for (int i = 0; i < path.length - 1; ++i) {
            String property = path[i];

            if (current == null) {
                throw new IllegalArgumentException("Field on path is null");
            } else if (current instanceof List) {
                int idx = Integer.parseInt(property);
                current = ((List) current).get(idx);
            } else if (current instanceof Map) {
                current = ((Map) current).get(property);
            } else {
                current = PropertyUtil.safeGetProperty(current, property);
            }
        }

        return current;
    }

    private FieldHelper() {
    }
}
