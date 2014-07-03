package org.motechproject.mds.builder.impl;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import org.apache.commons.lang.StringUtils;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.mds.builder.EntityBuilder;
import org.motechproject.mds.domain.ClassData;
import org.motechproject.mds.domain.ComboboxHolder;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.EntityType;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Relationship;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.ex.EntityCreationException;
import org.motechproject.mds.javassist.JavassistBuilder;
import org.motechproject.mds.javassist.JavassistHelper;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.SecurityUtil;
import org.motechproject.mds.util.TypeHelper;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import static org.apache.commons.lang.StringUtils.capitalize;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.uncapitalize;
import static org.motechproject.mds.util.Constants.Util.MODIFICATION_DATE_FIELD_NAME;
import static org.motechproject.mds.util.Constants.Util.MODIFIED_BY_FIELD_NAME;

/**
 * The <code>EntityBuilderImpl</code> is used build classes for a given entity.
 * This implementation relies on Javassist to build the class definition.
 */
@Component
public class EntityBuilderImpl implements EntityBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(EntityBuilderImpl.class);
    private final ClassPool classPool = MotechClassPool.getDefault();

    @Override
    public ClassData build(Entity entity) {
        LOG.info("Building EUDE: " + entity.getName());
        return build(entity, EntityType.STANDARD, null);
    }

    @Override
    public ClassData buildDDE(Entity entity, Bundle bundle) {
        LOG.info("Building DDE: " + entity.getClassName());
        return build(entity, EntityType.STANDARD, bundle);
    }

    @Override
    public void prepareHistoryClass(Entity entity) {
        String className = entity.getClassName();
        LOG.info("Building empty history class for: {}", className);

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
        LOG.info("Building empty trash class for: {}", className);

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
        LOG.info("Building history class for: {}", entity.getClassName());
        return build(entity, EntityType.HISTORY, null);
    }

    @Override
    public ClassData buildTrash(Entity entity) {
        LOG.info("Building trash class for: {}", entity.getClassName());
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
                            null, type
                    );

                    // this field is a flag that inform whether the instance with id (field above)
                    // is in trash or not.
                    addProperty(
                            declaring, Boolean.class.getName(), simpleName + "FromTrash", "false",
                            type
                    );

                    // this field is a flag informing whether this history record is a current
                    // revision of an instance
                    addProperty(
                            declaring, Boolean.class.getName(), simpleName + "IsLast", null, type
                    );

                    // this field contains information about the schema version of an entity
                    addProperty(
                            declaring, Long.class.getName(), simpleName + "SchemaVersion", null,
                            type
                    );
                    break;
                case TRASH:
                    // this field contains information about the schema version of an entity
                    addProperty(declaring, Long.class.getName(), "schemaVersion", null, type);
                    break;
                default:
            }

            return new ClassData(
                    declaring.getName(), entity.getModule(), entity.getNamespace(),
                    declaring.toBytecode(), type
            );
        } catch (Exception e) {
            throw new EntityCreationException(e);
        }
    }

    private CtClass makeClass(Entity entity, EntityType type, Bundle bundle)
            throws NotFoundException, CannotCompileException, ReflectiveOperationException {
        // try to get declaring class
        CtClass declaring = getDeclaringClass(entity, type, bundle);

        // add fields
        addFields(declaring, entity, type);

        // we pass EntityType.UNKNOWN arg as entity type to create standard setters for these
        // fields
        createProperty(declaring, entity, MODIFICATION_DATE_FIELD_NAME, EntityType.UNKNOWN);
        createProperty(declaring, entity, MODIFIED_BY_FIELD_NAME, EntityType.UNKNOWN);

        // this method should not be added into history and trash classes
        if (EntityType.STANDARD == type) {
            addUpdateModificationDataMethod(declaring);
        }

        // convert fields into properties (add getters and setters)
        createProperties(declaring, entity, type);

        return declaring;
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
                declaring = JavassistHelper.loadClass(bundle, entity.getClassName(), classPool);
            } catch (IOException e) {
                throw new NotFoundException(e.getMessage(), e);
            }
        }

        return isDDE ? declaring : classPool.makeClass(className);
    }

    private void addFields(CtClass declaring, Entity entity, EntityType type)
            throws CannotCompileException, ReflectiveOperationException {
        LOG.debug("Adding fields to class: " + declaring.getName());

        for (Field field : entity.getFields()) {
            if (!shouldLeaveExistingField(field, declaring)) {
                JavassistHelper.removeFieldIfExists(declaring, field.getName());
                CtField ctField = createField(declaring, entity, field, type);

                if (isBlank(field.getDefaultValue())) {
                    declaring.addField(ctField);
                } else {
                    declaring.addField(ctField, createInitializer(entity, field));
                }
            }
        }
    }

    private void createProperties(CtClass declaring, Entity entity, EntityType type)
            throws NotFoundException, CannotCompileException {
        LOG.debug("Adding fields to class: " + declaring.getName());

        for (Field field : entity.getFields()) {
            createProperty(declaring, field, type);
        }
    }

    private void createProperty(CtClass declaring, Entity entity, String field, EntityType type)
            throws CannotCompileException, NotFoundException {
        createProperty(declaring, entity.getField(field), type);
    }

    private void createProperty(CtClass declaring, Field field, EntityType type)
            throws CannotCompileException, NotFoundException {
        String fieldName = field.getName();
        CtField ctField = JavassistHelper.findField(declaring, fieldName);

        String getter = JavassistBuilder.getGetterName(fieldName, declaring, ctField);
        String setter = JavassistBuilder.getSetterName(fieldName);

        if (!shouldLeaveExistingMethod(field, getter, declaring)) {
            createGetter(declaring, fieldName, ctField);
        }

        if (!shouldLeaveExistingMethod(field, setter, declaring)) {
            createSetter(declaring, fieldName, ctField, type);
        }
    }

    private void addProperty(CtClass declaring, String typeClassName, String propertyName,
                             String defaultValue, EntityType entityType)
            throws CannotCompileException, NotFoundException {
        String name = uncapitalize(propertyName);
        JavassistHelper.removeFieldIfExists(declaring, propertyName);

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
        createSetter(declaring, name, field, entityType);
    }

    private CtField createField(CtClass declaring, Entity entity, Field field,
                                EntityType entityType)
            throws IllegalAccessException, InstantiationException, CannotCompileException {
        Type fieldType = field.getType();
        String genericSignature = null;
        CtClass type = null;

        if (fieldType.isCombobox()) {
            ComboboxHolder holder = new ComboboxHolder(entity, field);

            if (holder.isEnum() || holder.isEnumList()) {
                type = classPool.getOrNull(holder.getEnumName());

                if (holder.isEnumList()) {
                    genericSignature = JavassistHelper.genericSignature(
                            List.class, holder.getEnumName()
                    );
                    type = classPool.getOrNull(List.class.getName());
                }
            } else if (holder.isStringList()) {
                genericSignature = JavassistHelper.genericSignature(List.class, String.class);
                type = classPool.getOrNull(List.class.getName());
            } else if (holder.isString()) {
                type = classPool.getOrNull(String.class.getName());
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
        JavassistHelper.removeMethodIfExists(declaring, getter.getName());
        declaring.addMethod(getter);
    }

    private void createSetter(CtClass declaring, String fieldName, CtField field, EntityType type)
            throws CannotCompileException, NotFoundException {
        if (EntityType.STANDARD == type) {
            createCustomSetter(declaring, fieldName, field);
        } else {
            // history and trash classes should have standard setters for fields
            createStandardSetter(declaring, fieldName, field);
        }
    }

    private void createStandardSetter(CtClass declaring, String fieldName, CtField ctField)
            throws CannotCompileException {
        CtMethod setter = JavassistBuilder.createSetter(fieldName, ctField);
        JavassistHelper.removeMethodIfExists(declaring, setter.getName());
        declaring.addMethod(setter);
    }

    private void createCustomSetter(CtClass declaring, String fieldName, CtField ctField)
            throws CannotCompileException, NotFoundException {
        String src = String.format(
                "public void set%s(%s arg) { this.%s = arg; updateModificationData(); }",
                capitalize(fieldName), ctField.getType().getName(), fieldName
        );

        CtMethod setter = CtNewMethod.make(src, declaring);
        JavassistHelper.removeMethodIfExists(declaring, setter.getName());
        declaring.addMethod(setter);
    }

    private void addUpdateModificationDataMethod(CtClass declaring) throws CannotCompileException {
        String methodName = "updateModificationData";

        String modificationDate = String.format(
                "set%s(%s.now());",
                capitalize(MODIFICATION_DATE_FIELD_NAME), DateUtil.class.getName()
        );
        String modifiedBy = String.format(
                "set%s(%s.defaultIfBlank(%s.getUsername(), \"\"));",
                capitalize(MODIFIED_BY_FIELD_NAME),
                StringUtils.class.getName(), SecurityUtil.class.getName()
        );
        String src = String.format(
                "private void %s() { %s %s }", methodName, modificationDate, modifiedBy
        );

        CtMethod method = CtNewMethod.make(src, declaring);
        JavassistHelper.removeDeclaredMethodIfExists(declaring, methodName);
        declaring.addMethod(method);
    }

    private CtField.Initializer createInitializer(Entity entity, Field field) {
        Type type = field.getType();
        CtField.Initializer initializer = null;

        if (type.isCombobox()) {
            ComboboxHolder holder = new ComboboxHolder(entity, field);

            if (holder.isStringList()) {
                Object defaultValue = TypeHelper.parse(field.getDefaultValue(), List.class);
                initializer = JavassistBuilder.createListInitializer(
                        String.class.getName(), defaultValue
                );
            } else if (holder.isEnumList()) {
                Object defaultValue = TypeHelper.parse(field.getDefaultValue(), List.class);
                initializer = JavassistBuilder.createListInitializer(
                        holder.getEnumName(), defaultValue
                );
            } else if (holder.isString()) {
                initializer = JavassistBuilder.createInitializer(
                        String.class.getName(), field.getDefaultValue()
                );
            } else if (holder.isEnum()) {
                initializer = JavassistBuilder.createEnumInitializer(
                        holder.getEnumName(), field.getDefaultValue()
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
                && JavassistHelper.containsField(declaring, field.getName());
    }

    private boolean shouldLeaveExistingMethod(Field field, String methodName, CtClass declaring) {
        return field.isReadOnly()
                && JavassistHelper.containsMethod(declaring, methodName);
    }
}
