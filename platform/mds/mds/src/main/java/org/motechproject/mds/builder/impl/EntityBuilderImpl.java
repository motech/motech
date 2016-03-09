package org.motechproject.mds.builder.impl;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.NotFoundException;
import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.builder.EntityBuilder;
import org.motechproject.mds.domain.ClassData;
import org.motechproject.mds.domain.ComboboxHolder;
import org.motechproject.mds.domain.EntityType;
import org.motechproject.mds.domain.Relationship;
import org.motechproject.mds.dto.EntityDto;
import org.motechproject.mds.dto.FieldDto;
import org.motechproject.mds.dto.TypeDto;
import org.motechproject.mds.exception.entity.EntityCreationException;
import org.motechproject.mds.exception.object.PropertyCreationException;
import org.motechproject.mds.helper.EnumHelper;
import org.motechproject.mds.helper.JavassistBuilder;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.Constants;
import org.motechproject.mds.util.JavassistUtil;
import org.motechproject.mds.util.MemberUtil;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.uncapitalize;

/**
 * The <code>EntityBuilderImpl</code> is used to build classes for a given entity.
 * This implementation relies on Javassist to build the class definition.
 */
@Component
public class EntityBuilderImpl implements EntityBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityBuilderImpl.class);

    private final ClassPool classPool = MotechClassPool.getDefault();

    @Override
    public ClassData build(EntityDto entity, List<FieldDto> fields) {
        LOGGER.debug("Building EUDE: " + entity.getName());
        return build(entity, fields, EntityType.STANDARD, null);
    }

    @Override
    public ClassData buildDDE(EntityDto entity, List<FieldDto> fields, Bundle bundle) {
        LOGGER.debug("Building DDE: " + entity.getClassName());
        return build(entity, fields, EntityType.STANDARD, bundle);
    }

    @Override
    public void prepareHistoryClass(EntityDto entity) {
        String className = entity.getClassName();
        LOGGER.debug("Building empty history class for: {}", className);

        String historyClassName = ClassName.getHistoryClassName(className);
        CtClass historyClass = classPool.getOrNull(historyClassName);

        // we can edit classes
        if (historyClass != null) {
            historyClass.defrost();
        }

        //build empty history class
        classPool.makeClass(historyClassName);
    }

    @Override
    public void prepareTrashClass(EntityDto entity) {
        String className = entity.getClassName();
        LOGGER.debug("Building empty trash class for: {}", className);

        String trashClassName = ClassName.getTrashClassName(className);
        CtClass trashClass = classPool.getOrNull(trashClassName);

        // we can edit classes
        if (trashClass != null) {
            trashClass.defrost();
        }

        classPool.makeClass(trashClassName);
    }

    @Override
    public ClassData buildHistory(EntityDto entity, List<FieldDto> fields) {
        LOGGER.debug("Building history class for: {}", entity.getClassName());
        return build(entity, fields, EntityType.HISTORY, null);
    }

    @Override
    public ClassData buildTrash(EntityDto entity, List<FieldDto> fields) {
        LOGGER.debug("Building trash class for: {}", entity.getClassName());
        return build(entity, fields, EntityType.TRASH, null);
    }

    private ClassData build(EntityDto entity, List<FieldDto> fields, EntityType type, Bundle bundle) {
        try {
            CtClass declaring = makeClass(entity, fields, type, bundle);

            switch (type) {
                case HISTORY:
                    String className = type.getClassName(entity.getClassName());
                    String simpleName = ClassName.getSimpleName(className);
                    TypeDto idType = TypeDto.LONG;

                    // add 4 extra fields to history class definition

                    // this field is related with id field in entity
                    addProperty(
                            declaring, idType.getTypeClass(), simpleName + Constants.Util.CURRENT_VERSION,
                            null
                    );

                    // this field contains information about the schema version of an entity
                    addProperty(
                            declaring, Long.class.getName(), simpleName + StringUtils.capitalize(Constants.Util.SCHEMA_VERSION_FIELD_NAME), null
                    );
                    break;
                case TRASH:
                    // this field contains information about the schema version of an entity
                    addProperty(declaring, Long.class.getName(), Constants.Util.SCHEMA_VERSION_FIELD_NAME, null);
                    break;
                default:
            }

            return new ClassData(
                    declaring.getName(), entity.getModule(), entity.getNamespace(),
                    declaring.toBytecode(), type
            );
        } catch (ReflectiveOperationException | CannotCompileException | IOException | NotFoundException e) {
            throw new EntityCreationException("Unable to create entity " + entity.getName(), e);
        }
    }

    private CtClass makeClass(EntityDto entity, List<FieldDto> fields, EntityType type, Bundle bundle)
            throws NotFoundException, CannotCompileException, ReflectiveOperationException {
        // try to get declaring class
        CtClass declaring = getDeclaringClass(entity, type, bundle);

        // check and add default constructor if necessary
        injectDefaultConstructor(declaring);

        // create properties (add fields, getters and setters)
        for (FieldDto field : fields) {
            String fieldName = field.getBasic().getName();
            try {

                // We skip version fields for trash and history
                if (field.isVersionField() && type != EntityType.STANDARD) {
                    continue;
                }

                CtField ctField;

                if (!shouldLeaveExistingField(field, declaring)) {
                    JavassistUtil.removeFieldIfExists(declaring, fieldName);
                    ctField = createField(declaring, entity, field, type);

                    if (isObjectNullOrBlankString(field.getBasic().getDefaultValue())) {
                        declaring.addField(ctField);
                    } else {
                        declaring.addField(ctField, createInitializer(entity, field));
                    }
                } else {
                    ctField = JavassistUtil.findField(declaring, fieldName);
                }

                String getter = MemberUtil.getGetterName(fieldName, declaring);
                String setter = MemberUtil.getSetterName(fieldName);

                if (!shouldLeaveExistingMethod(field, getter, declaring)) {
                    createGetter(declaring, fieldName, ctField);
                }

                if (!shouldLeaveExistingMethod(field, setter, declaring)) {
                    createSetter(declaring, fieldName, ctField);
                }
            } catch (RuntimeException e) {
                throw new EntityCreationException("Error while processing field " + fieldName, e);
            }
        }

        return declaring;
    }

    private void injectDefaultConstructor(CtClass ctClass) {
        CtConstructor[] constructors = ctClass.getDeclaredConstructors();

        // No constructors? Nothing to do here - Java will inject default one
        if (constructors.length == 0) {
            return;
        }

        try {
            for (CtConstructor constructor : constructors) {
                int parameters = constructor.getParameterTypes().length;
                int modifiers = constructor.getModifiers();

                if (parameters == 0 && Modifier.isPublic(modifiers)) {
                    // Default constructor present? Nothing to do here.
                    return;
                } else if (parameters == 0 && !Modifier.isPublic(modifiers)) {
                    // If there's a default private or protected constructor, we remove it to create a public one
                    ctClass.removeConstructor(constructor);
                    break;
                }
            }
        } catch (NotFoundException e) {
            LOGGER.error("Could not read constructor parameters for class {}.", ctClass.getName());
        }

        // We create and inject default constructor
        try {
            CtConstructor defaultConstructor = CtNewConstructor.make(new CtClass[]{}, new CtClass[]{}, ctClass);
            ctClass.addConstructor(defaultConstructor);
        } catch (CannotCompileException e) {
            LOGGER.error("Could not create and insert default constructor for class {}.", ctClass.getName());
        }
    }

    private CtClass getDeclaringClass(EntityDto entity, EntityType type, Bundle bundle)
            throws NotFoundException {
        String className = type.getClassName(entity.getClassName());
        boolean isDDE = null != bundle;

        CtClass declaring = classPool.getOrNull(className);

        if (null != declaring) {
            // we can edit classes
            declaring.defrost();
        } else if (isDDE) {
            try {
                declaring = JavassistUtil.loadClass(bundle, entity.getClassName(), classPool);
            } catch (IOException e) {
                throw new NotFoundException(e.getMessage(), e);
            }
        }

        return isDDE ? declaring : classPool.makeClass(className);
    }

    private void addProperty(CtClass declaring, String typeClassName, String propertyName,
                             String defaultValue) {
        try {
            String name = uncapitalize(propertyName);
            JavassistUtil.removeFieldIfExists(declaring, propertyName);

            CtClass type = classPool.getOrNull(typeClassName);
            CtField field = JavassistBuilder.createField(declaring, type, propertyName, null);

            if (isBlank(defaultValue)) {
                declaring.addField(field);
            } else {
                CtField.Initializer initializer = JavassistBuilder.createInitializer(
                        typeClassName, defaultValue
                );
                declaring.addField(field, initializer);
            }

            createGetter(declaring, name, field);
            createSetter(declaring, name, field);
        } catch (CannotCompileException e) {
            throw new PropertyCreationException("Error while creating property " + propertyName, e);
        }
    }

    private CtField createField(CtClass declaring, EntityDto entity, FieldDto field,
                                EntityType entityType)
            throws IllegalAccessException, InstantiationException, CannotCompileException, ClassNotFoundException {
        TypeDto fieldType = field.getType();
        String genericSignature = null;
        CtClass type = null;

        if (fieldType.isCombobox()) {
            ComboboxHolder holder = new ComboboxHolder(entity, field);

            if (holder.isEnum() || holder.isEnumCollection()) {
                type = classPool.getOrNull(holder.getEnumName());

                if (holder.isEnumCollection()) {
                    genericSignature = JavassistUtil.genericSignature(
                            holder.getTypeClassName(), holder.getEnumName()
                    );
                    type = classPool.getOrNull(holder.getTypeClassName());
                }
            } else if (holder.isStringCollection()) {
                genericSignature = JavassistUtil.genericSignature(holder.getTypeClassName(), holder.getUnderlyingType());
                type = classPool.getOrNull(holder.getTypeClassName());
            } else if (holder.isString()) {
                type = classPool.getOrNull(holder.getUnderlyingType());
            }
        } else if (fieldType.isRelationship()) {
            Class fieldClass = getClass().getClassLoader().loadClass(fieldType.getTypeClass());
            Relationship relationshipType = (Relationship) fieldClass.newInstance();

            genericSignature = relationshipType.getGenericSignature(field, entityType);
            type = classPool.getOrNull(relationshipType.getFieldType(field, entityType));
        } else {
            type = classPool.getOrNull(fieldType.getTypeClass());
        }

        return JavassistBuilder.createField(declaring, type, field.getBasic().getName(), genericSignature);
    }

    private void createGetter(CtClass declaring, String fieldName, CtField ctField)
            throws CannotCompileException {
        CtMethod getter = JavassistBuilder.createGetter(fieldName, declaring, ctField);
        JavassistUtil.removeMethodIfExists(declaring, getter.getName());
        declaring.addMethod(getter);
    }

    private void createSetter(CtClass declaring, String fieldName, CtField field)
            throws CannotCompileException {
        CtMethod setter = JavassistBuilder.createSetter(fieldName, field);
        JavassistUtil.removeMethodIfExists(declaring, setter.getName());
        declaring.addMethod(setter);
    }

    private CtField.Initializer createInitializer(EntityDto entity, FieldDto field) {
        TypeDto type = field.getType();
        CtField.Initializer initializer = null;

        if (type.isCombobox()) {
            ComboboxHolder holder = new ComboboxHolder(entity, field);

            if (holder.isStringCollection()) {
                initializer = JavassistBuilder.createCollectionInitializer(
                        holder.getUnderlyingType(), field.getBasic().getDefaultValue()
                );
            } else if (holder.isEnumCollection()) {
                Object defaultValue = field.getBasic().getDefaultValue();
                initializer = JavassistBuilder.createCollectionInitializer(
                        holder.getEnumName(), EnumHelper.prefixEnumValues((Collection) defaultValue)
                );
            } else if (holder.isString()) {
                initializer = JavassistBuilder.createInitializer(
                        holder.getUnderlyingType(), field.getBasic().getDefaultValue().toString()
                );
            } else if (holder.isEnum()) {
                initializer = JavassistBuilder.createEnumInitializer(
                        holder.getEnumName(), EnumHelper.prefixEnumValue(field.getBasic().getDefaultValue().toString())
                );
            }
        } else if (!type.isRelationship()) {
            initializer = JavassistBuilder.createInitializer(
                    type.getTypeClass(), field.getBasic().getDefaultValue()
            );
        }

        return initializer;
    }

    private boolean shouldLeaveExistingField(FieldDto field, CtClass declaring) {
        return field.isReadOnly()
                && (JavassistUtil.containsField(declaring, field.getBasic().getName()) ||
                    JavassistUtil.containsDeclaredField(declaring, field.getBasic().getName()));
    }

    private boolean shouldLeaveExistingMethod(FieldDto field, String methodName, CtClass declaring) {
        return field.isReadOnly()
                && (JavassistUtil.containsMethod(declaring, methodName) ||
                    JavassistUtil.containsDeclaredMethod(declaring, methodName));
    }

    private boolean isObjectNullOrBlankString(Object obj) {
        if (obj instanceof String) {
            String str = (String) obj;
            return StringUtils.isBlank(str);
        } else {
            return obj == null;
        }
    }
}
