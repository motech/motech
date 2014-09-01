package org.motechproject.mds.annotations.internal;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.motechproject.mds.annotations.Cascade;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.InSet;
import org.motechproject.mds.annotations.NotInSet;
import org.motechproject.mds.domain.ManyToOneRelationship;
import org.motechproject.mds.domain.OneToManyRelationship;
import org.motechproject.mds.domain.OneToOneRelationship;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.domain.TypeValidation;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldBasicDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.FieldValidationDto;
import org.motechproject.mds.dto.MetadataDto;
import org.motechproject.mds.dto.SettingDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.dto.ValidationCriterionDto;
import org.motechproject.mds.reflections.ReflectionsUtil;
import org.motechproject.mds.service.EntityService;
import org.motechproject.mds.service.TypeService;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.MemberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Persistent;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.Boolean.parseBoolean;
import static org.motechproject.mds.reflections.ReflectionsUtil.getAnnotatedMembers;
import static org.motechproject.mds.reflections.ReflectionsUtil.getAnnotationClassLoaderSafe;
import static org.motechproject.mds.reflections.ReflectionsUtil.getAnnotationValue;
import static org.motechproject.mds.reflections.ReflectionsUtil.hasProperty;
import static org.motechproject.mds.util.Constants.AnnotationFields.DELETE;
import static org.motechproject.mds.util.Constants.AnnotationFields.DISPLAY_NAME;
import static org.motechproject.mds.util.Constants.AnnotationFields.MAX;
import static org.motechproject.mds.util.Constants.AnnotationFields.MIN;
import static org.motechproject.mds.util.Constants.AnnotationFields.NAME;
import static org.motechproject.mds.util.Constants.AnnotationFields.PERSIST;
import static org.motechproject.mds.util.Constants.AnnotationFields.REGEXP;
import static org.motechproject.mds.util.Constants.AnnotationFields.UPDATE;
import static org.motechproject.mds.util.Constants.AnnotationFields.VALUE;
import static org.motechproject.mds.util.Constants.MetadataKeys.ENUM_CLASS_NAME;
import static org.motechproject.mds.util.Constants.MetadataKeys.MAP_KEY_TYPE;
import static org.motechproject.mds.util.Constants.MetadataKeys.MAP_VALUE_TYPE;
import static org.motechproject.mds.util.Constants.MetadataKeys.RELATED_CLASS;
import static org.motechproject.mds.util.Constants.MetadataKeys.RELATED_FIELD;
import static org.motechproject.mds.util.Constants.Util.AUTO_GENERATED;
import static org.motechproject.mds.util.Constants.Util.AUTO_GENERATED_EDITABLE;
import static org.motechproject.mds.util.Constants.Util.GENERATED_FIELD_NAMES;
import static org.motechproject.mds.util.Constants.Util.OWNER_FIELD_NAME;
/**
 * The <code>FieldProcessor</code> provides a mechanism to finding fields or methods with the
 * {@link org.motechproject.mds.annotations.Field} annotation inside the class with the
 * {@link org.motechproject.mds.annotations.Entity} annotation.
 * <p/>
 * By default all public fields (the field is public if it has public modifier or single methods
 * called 'getter and 'setter') will be added in the MDS definition of the entity. The field type
 * will be mapped on the appropriate type in the MDS system. If the appropriate mapping does not
 * exist an {@link org.motechproject.mds.ex.TypeNotFoundException} exception will be raised.
 * <p/>
 * Fields or acceptable methods with the {@link org.motechproject.mds.annotations.Ignore}
 * annotation are ignored by the processor and they are not added into entity definition.
 *
 * @see org.motechproject.mds.annotations.Field
 * @see org.motechproject.mds.annotations.Entity
 * @see org.motechproject.mds.annotations.Ignore
 */
@Component
class FieldProcessor extends AbstractListProcessor<Field, FieldDto> {
    private static final Logger LOGGER = LoggerFactory.getLogger(FieldProcessor.class);

    private EntityService entityService;
    private TypeService typeService;

    private EntityDto entity;
    private Class clazz;

    @Override
    public Class<Field> getAnnotationType() {
        return Field.class;
    }

    @Override
    protected List<? extends AnnotatedElement> getElementsToProcess() {
        return getAnnotatedMembers(
                getAnnotationType(), clazz, new MethodPredicate(), new FieldPredicate(this)
        );
    }

    @Override
    protected void process(AnnotatedElement element) {
        AccessibleObject ac = (AccessibleObject) element;
        Class<?> classType = MemberUtil.getCorrectType(ac);
        Class<?> genericType = MemberUtil.getGenericType(element);
        Class<?> declaringClass = MemberUtil.getDeclaringClass(ac);
        Class<?> valueType = null;

        if (Map.class.isAssignableFrom(classType)) {
            valueType = MemberUtil.getGenericType(element, 1);
        }

        if (null != classType) {
            String fieldName = MemberUtil.getFieldName(ac);

            boolean isRelationship = ReflectionsUtil.hasAnnotationClassLoaderSafe(
                    genericType, genericType, Entity.class);

            String fieldMappedByThisElement = findFieldNameMappedByThisField(fieldName, genericType, declaringClass);
            String relatedFieldName = isRelationship ? findRelatedFieldName(element, genericType, declaringClass) : null;

            Field annotation = getAnnotationClassLoaderSafe(ac, classType, Field.class);

            TypeDto type = getCorrectType(classType, isRelationship, genericType, fieldMappedByThisElement);

            FieldBasicDto basic = new FieldBasicDto();
            basic.setDisplayName(getAnnotationValue(
                            annotation, DISPLAY_NAME, fieldName)
            );
            basic.setName(getAnnotationValue(
                            annotation, NAME, fieldName)
            );

            if (null != annotation) {
                basic.setRequired(annotation.required());
                basic.setDefaultValue(annotation.defaultValue());
                basic.setTooltip(annotation.tooltip());
            }

            FieldDto field = new FieldDto();
            field.setEntityId(entity.getId());
            field.setType(type);
            field.setBasic(basic);
            field.setValidation(createValidation(ac, type));
            field.setReadOnly(true);

            setFieldSettings(ac, classType, isRelationship, field);
            setFieldMetadata(classType, genericType, valueType, isRelationship, field, relatedFieldName);

            add(field);
        } else {
            LOGGER.warn("Field type is unknown in: {}", ac);
        }
    }

    private void setFieldMetadata(Class<?> classType, Class<?> genericType, Class<?> valueType, boolean isRelationship,
                                  FieldDto field, String relatedField) {
        if (classType.isEnum()) {
            field.addMetadata(new MetadataDto(ENUM_CLASS_NAME, classType.getName()));
        } else if (null != genericType && genericType.isEnum()) {
            field.addMetadata(new MetadataDto(ENUM_CLASS_NAME, genericType.getName()));
        } else if (null != genericType && isRelationship) {
            field.addMetadata(new MetadataDto(RELATED_CLASS, genericType.getName()));
            if (relatedField != null) {
                field.addMetadata(new MetadataDto(RELATED_FIELD, relatedField));
            }
        } else if (Map.class.isAssignableFrom(classType) && genericType != null) {
            field.addMetadata(new MetadataDto(MAP_KEY_TYPE, genericType.getName()));
            field.addMetadata(new MetadataDto(MAP_VALUE_TYPE, valueType.getName()));
        } else if (ArrayUtils.contains(GENERATED_FIELD_NAMES, field.getBasic().getName())) {
            setAutoGeneratedFieldMetadata(field);
        }
    }

    private void setAutoGeneratedFieldMetadata(FieldDto field) {
        if (!field.getBasic().getName().equals(OWNER_FIELD_NAME)) {
            field.addMetadata(new MetadataDto(AUTO_GENERATED, "true"));
        } else {
            field.addMetadata(new MetadataDto(AUTO_GENERATED_EDITABLE, "true"));
        }
    }

    private String findRelatedFieldName(AnnotatedElement element, Class<?> relatedFieldClass, Class<?> ownClass) {
        // first we check for mapped by annotation
        String mappedBy = getMappedBy(element);
        if (StringUtils.isNotBlank(mappedBy)) {
            return mappedBy;
        }

        // if this element is not mapped by anything, that check if anything maps to it
        return findFieldNameMappedByThisField(MemberUtil.getFieldName(element), relatedFieldClass, ownClass);
    }

    private String getMappedBy(AnnotatedElement element) {
        Persistent persistentAnnotation = getAnnotationClassLoaderSafe(element,
                MemberUtil.getCorrectType(element), Persistent.class);

        return  (persistentAnnotation != null) ? persistentAnnotation.mappedBy() : null;
    }

    private String findFieldNameMappedByThisField(String fieldName, Class<?> relatedFieldClass, Class<?> ownClass) {
        for (java.lang.reflect.Field field : relatedFieldClass.getDeclaredFields()) {
            // check if the element is mapped by this field
            String mappedBy = getMappedBy(field);
            if (fieldName.equals(mappedBy) && ownClass.isAssignableFrom(MemberUtil.getGenericType(field))) {
                return field.getName();
            }
        }

        return null;
    }

    private void setFieldSettings(AccessibleObject ac, Class<?> classType, boolean isRelationship, FieldDto field) {
        if (isRelationship) {
            field.setSettings(createRelationshipSettings(ac));
        } else if (List.class.isAssignableFrom(classType) || classType.isEnum()) {
            field.setSettings(createComboboxSettings(ac, classType));
        } else if (String.class.isAssignableFrom(classType)) {
            field.setSettings(createStringSettings(ac));
        }
    }

    private TypeDto getCorrectType(Class<?> classType, boolean isRelationship, Class<?> relatedClass,
                                   String fieldMappedByThisElement) {
        TypeDto type;

        if (isRelationship) {
            boolean isCollection = Collection.class.isAssignableFrom(classType);

            java.lang.reflect.Field mappedField = (fieldMappedByThisElement != null) ?
                    ReflectionUtils.findField(relatedClass, fieldMappedByThisElement) : null;

            if (isCollection) {
                // TODO: many-to-many
                type = typeService.findType(OneToManyRelationship.class);
            } else if (mappedField != null && Collection.class.isAssignableFrom(mappedField.getType())) {
                // a collection is mapped by this field
                type = typeService.findType(ManyToOneRelationship.class);
            } else {
                // its one to one
                type = typeService.findType(OneToOneRelationship.class);
            }
        } else {
            type = typeService.findType(classType.isEnum() ? List.class : classType);
        }

        return type;
    }

    @Override
    protected void afterExecution() {
        entityService.addFields(entity, getElements());
    }

    @Autowired
    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }

    @Autowired
    public void setTypeService(TypeService typeService) {
        this.typeService = typeService;
    }

    public void setEntity(EntityDto entity) {
        this.entity = entity;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    private List<SettingDto> createRelationshipSettings(AccessibleObject ac) {
        Cascade cascade = ReflectionsUtil.getAnnotationClassLoaderSafe(ac,
                MemberUtil.getCorrectType(ac), Cascade.class);

        boolean persist = parseBoolean(getAnnotationValue(cascade, PERSIST, TRUE.toString()));
        boolean update = parseBoolean(getAnnotationValue(cascade, UPDATE, TRUE.toString()));
        boolean delete = parseBoolean(getAnnotationValue(cascade, DELETE, FALSE.toString()));

        List<SettingDto> list = new ArrayList<>();
        list.add(new SettingDto("mds.form.label.cascadePersist", persist));
        list.add(new SettingDto("mds.form.label.cascadeUpdate", update));
        list.add(new SettingDto("mds.form.label.cascadeDelete", delete));

        return list;
    }

    private List<SettingDto> createComboboxSettings(AccessibleObject ac, Class<?> classType) {
        boolean allowMultipleSelections = List.class.isAssignableFrom(classType);
        boolean allowUserSupplied = false;
        List values = new LinkedList();

        if (List.class.isAssignableFrom(classType)) {
            Class<?> genericType = MemberUtil.getGenericType(ac);

            if (String.class.isAssignableFrom(genericType)) {
                allowUserSupplied = true;
            } else {
                Object[] enumConstants = genericType.getEnumConstants();

                if (ArrayUtils.isNotEmpty(enumConstants)) {
                    Collections.addAll(values, enumConstants);
                }
            }
        } else {
            Object[] enumConstants = classType.getEnumConstants();

            if (ArrayUtils.isNotEmpty(enumConstants)) {
                Collections.addAll(values, enumConstants);
            }
        }

        List<SettingDto> list = new ArrayList<>();
        list.add(new SettingDto(Constants.Settings.ALLOW_MULTIPLE_SELECTIONS, allowMultipleSelections));
        list.add(new SettingDto(Constants.Settings.ALLOW_USER_SUPPLIED, allowUserSupplied));
        list.add(new SettingDto(Constants.Settings.COMBOBOX_VALUES, values));

        return list;
    }

    private List<SettingDto> createStringSettings(AccessibleObject ac) {
        List<SettingDto> list = new ArrayList<>();
        // get length from jdo @Column annotation
        Column columnAnnotation = ReflectionsUtil.getAnnotation(ac, Column.class);

        // try getting the annotation from the private field if this is a getter/setter
        if (columnAnnotation == null && ac instanceof Method) {
            String fieldName = MemberUtil.getFieldName(ac);
            Method method = (Method) ac;
            Class<?> entityClass = method.getDeclaringClass();

            java.lang.reflect.Field referencedField = FieldUtils.getDeclaredField(entityClass, fieldName, true);
            if (referencedField != null) {
                columnAnnotation = ReflectionsUtil.getAnnotation(referencedField, Column.class);
            }
        }

        if (columnAnnotation != null) {
            int length = columnAnnotation.length();
            if (length >= 0) {
                list.add(new SettingDto(Constants.Settings.STRING_MAX_LENGTH, length));
            }
        }
        return list;
    }

    private FieldValidationDto createValidation(AccessibleObject ac, TypeDto type) {
        FieldValidationDto validationDto = null;

        for (Annotation annotation : ac.getAnnotations()) {
            List<TypeValidation> validations = typeService.findValidations(type, annotation.annotationType());

            for (TypeValidation validation : validations) {
                String displayName = validation.getDisplayName();
                Type valueType = typeService.getType(validation);

                if (null == valueType) {
                    throw new IllegalStateException("The valueType is not set in: " + validation);
                }

                String valueAsString = getValidationValue(displayName, annotation);

                if (InSet.class.isAssignableFrom(annotation.annotationType())
                        || NotInSet.class.isAssignableFrom(annotation.annotationType())) {
                    valueAsString = valueAsString.replaceAll("(\\{|\\})", "");
                }

                Object value = valueType.parse(valueAsString);

                ValidationCriterionDto dto = new ValidationCriterionDto();
                dto.setDisplayName(displayName);
                dto.setType(valueType.toDto());
                dto.setEnabled(true);
                dto.setValue(value);

                if (null == validationDto) {
                    validationDto = new FieldValidationDto();
                }

                validationDto.addCriterion(dto);
            }
        }

        return validationDto;
    }

    private String getValidationValue(String displayName, Annotation annotation) {
        String property;

        if (hasProperty(annotation, VALUE)) {
            property = VALUE;
        } else if (annotation instanceof Pattern) {
            property = REGEXP;
        } else if (annotation instanceof Size) {
            switch (displayName) {
                case "mds.field.validation.minLength":
                    property = MIN;
                    break;
                case "mds.field.validation.maxLength":
                    property = MAX;
                    break;
                default:
                    throw new IllegalArgumentException(
                            "The @Size annotation can be used only on fields with String type."
                    );
            }
        } else {
            throw new IllegalArgumentException("Not found correct property in annotation: " + annotation);
        }

        return getAnnotationValue(annotation, property);
    }

}
