package org.motechproject.mds.builder.impl;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.NotFoundException;
import org.motechproject.mds.builder.EntityBuilder;
import org.motechproject.mds.domain.ClassData;
import org.motechproject.mds.domain.ComboboxHolder;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.EntityType;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Relationship;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.ex.entity.EntityCreationException;
import org.motechproject.mds.ex.object.PropertyCreationException;
import org.motechproject.mds.helper.EnumHelper;
import org.motechproject.mds.helper.JavassistBuilder;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.JavassistUtil;
import org.motechproject.mds.util.MemberUtil;
import org.motechproject.mds.util.TypeHelper;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Collection;

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
    public ClassData build(Entity entity) {
        LOGGER.debug("Building EUDE: " + entity.getName());
        return build(entity, EntityType.STANDARD, null);
    }

    @Override
    public ClassData buildDDE(Entity entity, Bundle bundle) {
        LOGGER.debug("Building DDE: " + entity.getClassName());
        return build(entity, EntityType.STANDARD, bundle);
    }

    @Override
    public void prepareHistoryClass(Entity entity) {
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
    public void prepareTrashClass(Entity entity) {
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
    public ClassData buildHistory(Entity entity) {
        LOGGER.debug("Building history class for: {}", entity.getClassName());
        return build(entity, EntityType.HISTORY, null);
    }

    @Override
    public ClassData buildTrash(Entity entity) {
        LOGGER.debug("Building trash class for: {}", entity.getClassName());
        return build(entity, EntityType.TRASH, null);
    }

    private ClassData build(Entity entity, EntityType type, Bundle bundle) {
        try {
            CtClass declaring = makeClass(entity, type, bundle);

            switch (type) {
                case HISTORY:
                    String className = type.getName(entity.getClassName());
                    String simpleName = ClassName.getSimpleName(className);
                    Type idType = entity.getField("id").getType();

                    // add 4 extra fields to history class definition

                    // this field is related with id field in entity
                    addProperty(
                            declaring, idType.getTypeClassName(), simpleName + "CurrentVersion",
                            null
                    );

                    // this field is a flag that inform whether the instance with id (field above)
                    // is in trash or not.
                    addProperty(
                            declaring, Boolean.class.getName(), simpleName + "FromTrash", "false"
                    );

                    // this field contains information about the schema version of an entity
                    addProperty(
                            declaring, Long.class.getName(), simpleName + "SchemaVersion", null
                    );
                    break;
                case TRASH:
                    // this field contains information about the schema version of an entity
                    addProperty(declaring, Long.class.getName(), "schemaVersion", null);
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

    private CtClass makeClass(Entity entity, EntityType type, Bundle bundle)
            throws NotFoundException, CannotCompileException, ReflectiveOperationException {
        // try to get declaring class
        CtClass declaring = getDeclaringClass(entity, type, bundle);

        // check and add default constructor if necessary
        injectDefaultConstructor(declaring);

        // create properties (add fields, getters and setters)
        for (Field field : entity.getFields()) {
            try {

                // We skip version fields for trash and history
                if (field.isVersionField() && type != EntityType.STANDARD) {
                    continue;
                }

                String fieldName = field.getName();
                CtField ctField;

                if (!shouldLeaveExistingField(field, declaring)) {
                    JavassistUtil.removeFieldIfExists(declaring, fieldName);
                    ctField = createField(declaring, entity, field, type);

                    if (isBlank(field.getDefaultValue())) {
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
                throw new EntityCreationException("Error while processing field " + field.getName(), e);
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

    private CtClass getDeclaringClass(Entity entity, EntityType type, Bundle bundle)
            throws NotFoundException {
        String className = type.getName(entity.getClassName());
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
                             String defaultValue)
            throws PropertyCreationException {
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

    private CtField createField(CtClass declaring, Entity entity, Field field,
                                EntityType entityType)
            throws IllegalAccessException, InstantiationException, CannotCompileException {
        Type fieldType = field.getType();
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
            Relationship relationshipType = (Relationship) fieldType.getTypeClass().newInstance();

            genericSignature = relationshipType.getGenericSignature(field, entityType);
            type = classPool.getOrNull(relationshipType.getFieldType(field, entityType));
        } else {
            type = classPool.getOrNull(fieldType.getTypeClassName());
        }

        return JavassistBuilder.createField(declaring, type, field.getName(), genericSignature);
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

    private CtField.Initializer createInitializer(Entity entity, Field field) {
        Type type = field.getType();
        CtField.Initializer initializer = null;

        if (type.isCombobox()) {
            ComboboxHolder holder = new ComboboxHolder(entity, field);

            if (holder.isStringCollection()) {
                Object defaultValue = TypeHelper.parse(field.getDefaultValue(), holder.getTypeClassName());
                initializer = JavassistBuilder.createCollectionInitializer(
                        holder.getUnderlyingType(), defaultValue
                );
            } else if (holder.isEnumCollection()) {
                Object defaultValue = TypeHelper.parse(field.getDefaultValue(), holder.getTypeClassName());
                initializer = JavassistBuilder.createCollectionInitializer(
                        holder.getEnumName(), EnumHelper.prefixEnumValues((Collection) defaultValue)
                );
            } else if (holder.isString()) {
                initializer = JavassistBuilder.createInitializer(
                        holder.getUnderlyingType(), field.getDefaultValue()
                );
            } else if (holder.isEnum()) {
                initializer = JavassistBuilder.createEnumInitializer(
                        holder.getEnumName(), EnumHelper.prefixEnumValue(field.getDefaultValue())
                );
            }
        } else if (!type.isRelationship()) {
            initializer = JavassistBuilder.createInitializer(
                    type.getTypeClassName(), field.getDefaultValue()
            );
        }

        return initializer;
    }

    private boolean shouldLeaveExistingField(Field field, CtClass declaring) {
        return field.isReadOnly()
                && (JavassistUtil.containsField(declaring, field.getName()) ||
                    JavassistUtil.containsDeclaredField(declaring, field.getName()));
    }

    private boolean shouldLeaveExistingMethod(Field field, String methodName, CtClass declaring) {
        return field.isReadOnly()
                && (JavassistUtil.containsMethod(declaring, methodName) ||
                    JavassistUtil.containsDeclaredMethod(declaring, methodName));
    }
}
