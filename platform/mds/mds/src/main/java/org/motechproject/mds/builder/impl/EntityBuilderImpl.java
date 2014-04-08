package org.motechproject.mds.builder.impl;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import org.joda.time.DateTime;
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.builder.EntityBuilder;
import org.motechproject.mds.domain.ClassData;
import org.motechproject.mds.domain.ComboboxHolder;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.domain.Field;
import org.motechproject.mds.domain.Type;
import org.motechproject.mds.ex.EntityCreationException;
import org.motechproject.mds.javassist.JavassistHelper;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.util.ClassName;
import org.motechproject.mds.util.TypeHelper;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang.StringUtils.capitalize;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.uncapitalize;

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
        try {
            String className = entity.getClassName();

            CtClass ctClass = classPool.getOrNull(className);

            // we can edit classes
            if (ctClass != null) {
                ctClass.defrost();
            }

            ctClass = classPool.makeClass(className);
            addFields(ctClass, entity);

            return new ClassData(entity, ctClass.toBytecode());
        } catch (Exception e) {
            throw new EntityCreationException(e);
        }
    }

    @Override
    public ClassData buildDDE(Entity entity, Bundle bundle) {
        String className = entity.getClassName();
        LOG.info("Building DDE: " + className);

        try {
            CtClass ddeClass = classPool.getOrNull(className);
            if (ddeClass != null) {
                // already defined, defrost
                ddeClass.defrost();
            } else {
                // load from the bundle
                ddeClass = JavassistHelper.loadClass(bundle, className, classPool);
            }

            addFields(ddeClass, entity);

            return new ClassData(entity, ddeClass.toBytecode());
        } catch (Exception e) {
            throw new EntityCreationException(e);
        }
    }

    @Override
    public ClassData buildHistory(Entity entity) {
        try {
            String className = entity.getClassName();
            LOG.info("Building history class for: {}", className);

            String historyClassName = ClassName.getHistoryClassName(className);
            CtClass historyClass = classPool.getOrNull(historyClassName);

            // we can edit classes
            if (historyClass != null) {
                historyClass.defrost();
            }

            historyClass = classPool.makeClass(historyClassName);
            String simpleName = historyClass.getSimpleName();
            Type idType = entity.getField("id").getType();

            // add 4 extra fields to history class definition

            // this field is related with id field in entity
            addProperty(historyClass, idType.getTypeClassName(), simpleName + "CurrentVersion");

            // this field is a flag that inform whether the instance with id (field above) is in
            // trash or not.
            addProperty(historyClass, Boolean.class.getName(), simpleName + "FromTrash", "false");

            // this field has information about previous historical data. It can be assumed that
            // if this field is empty, the history instance will represent the first changes.
            addProperty(historyClass, historyClassName, simpleName + "Previous");

            // this field has information about next (new) historical data. It can be assumed
            // that if this field is empty, the history instance will represent the newest changes.
            addProperty(historyClass, historyClassName, simpleName + "Next");

            // creates the same fields like in entity definition
            addFields(historyClass, entity);

            return new ClassData(
                    historyClassName, entity.getModule(), entity.getNamespace(),
                    historyClass.toBytecode()
            );
        } catch (Exception e) {
            throw new EntityCreationException(e);
        }
    }

    @Override
    public ClassData buildTrash(Entity entity) {
        try {
            String className = entity.getClassName();
            LOG.info("Building trash class for: {}", className);

            String trashClassName = ClassName.getTrashClassName(className);
            CtClass trashClass = classPool.getOrNull(trashClassName);

            // we can edit classes
            if (trashClass != null) {
                trashClass.defrost();
            }

            trashClass = classPool.makeClass(trashClassName);

            // creates the same fields like in entity definition
            addFields(trashClass, entity);

            addProperty(trashClass, Long.class.getName(), "schemaVersion");

            return new ClassData(
                    trashClassName, entity.getModule(), entity.getNamespace(),
                    trashClass.toBytecode()
            );
        } catch (Exception e) {
            throw new EntityCreationException(e);
        }
    }

    private void addFields(CtClass ctClass, Entity entity) throws CannotCompileException, NotFoundException, IOException {
        LOG.debug("Adding fields to class: " + ctClass.getName());

        for (Field field : entity.getFields()) {
            addProperty(ctClass, entity, field);
        }
    }

    private void addProperty(CtClass declaring, String typeClassName, String propertyName)
            throws CannotCompileException, NotFoundException {
        addProperty(declaring, typeClassName, propertyName, null);
    }

    private void addProperty(CtClass declaring, String typeClassName, String propertyName,
                             String defaultValue) throws CannotCompileException, NotFoundException {
        JavassistHelper.removeDeclaredFieldIfExists(declaring, propertyName);

        CtClass type = classPool.getOrNull(typeClassName);
        CtField field = createField(declaring, type, propertyName, null);
        CtMethod getter = createGetter(propertyName, field);
        CtMethod setter = createSetter(propertyName, field);

        if (isBlank(defaultValue)) {
            declaring.addField(field);
        } else {
            declaring.addField(field, createInitializer(typeClassName, defaultValue));
        }

        JavassistHelper.removeDeclaredMethodIfExists(declaring, getter.getName());
        JavassistHelper.removeDeclaredMethodIfExists(declaring, setter.getName());

        declaring.addMethod(getter);
        declaring.addMethod(setter);
    }

    private void addProperty(CtClass declaring, Entity entity, Field field) throws CannotCompileException, NotFoundException, IOException {
        JavassistHelper.removeDeclaredFieldIfExists(declaring, field.getName());

        CtField ctField = createField(declaring, entity, field);
        CtMethod getter = createGetter(field.getName(), ctField);
        CtMethod setter = createSetter(field.getName(), ctField);

        if (isBlank(field.getDefaultValue())) {
            declaring.addField(ctField);
        } else {
            declaring.addField(ctField, createInitializer(entity, field));
        }

        JavassistHelper.removeDeclaredMethodIfExists(declaring, getter.getName());
        JavassistHelper.removeDeclaredMethodIfExists(declaring, setter.getName());

        declaring.addMethod(getter);
        declaring.addMethod(setter);
    }

    private CtField createField(CtClass declaring, Entity entity, Field field) throws CannotCompileException, IOException {
        Type fieldType = field.getType();
        String genericSignature = null;
        CtClass type = null;

        if (fieldType.isCombobox()) {
            ComboboxHolder holder = new ComboboxHolder(entity, field);

            if (holder.isEnum() || holder.isEnumList()) {
                type = classPool.getOrNull(holder.getEnumFullName());

                if (holder.isEnumList()) {
                    genericSignature = JavassistHelper.genericSignature(List.class, holder.getEnumFullName());
                    type = classPool.getOrNull(List.class.getName());
                }
            } else if (holder.isStringList()) {
                genericSignature = JavassistHelper.genericSignature(List.class, String.class);
                type = classPool.getOrNull(List.class.getName());
            } else if (holder.isString()) {
                type = classPool.getOrNull(String.class.getName());
            }
        } else {
            type = classPool.getOrNull(fieldType.getTypeClassName());
        }

        return createField(declaring, type, field.getName(), genericSignature);
    }

    private CtField createField(CtClass declaring, CtClass type, String name, String genericSignature) throws CannotCompileException {
        String fieldName = uncapitalize(name);
        CtField field = new CtField(type, fieldName, declaring);
        field.setModifiers(Modifier.PRIVATE);

        if (isNotBlank(genericSignature)) {
            field.setGenericSignature(genericSignature);
        }

        return field;
    }

    private CtMethod createGetter(String fieldName, CtField field) throws CannotCompileException {
        return CtNewMethod.getter("get" + capitalize(fieldName), field);
    }

    private CtMethod createSetter(String fieldName, CtField field) throws CannotCompileException {
        return CtNewMethod.setter("set" + capitalize(fieldName), field);
    }

    private CtField.Initializer createInitializer(Entity entity, Field field) {
        Type type = field.getType();
        CtField.Initializer initializer = null;

        if (type.isCombobox()) {
            ComboboxHolder holder = new ComboboxHolder(entity, field);

            if (holder.isStringList()) {
                Object defaultValue = TypeHelper.parse(field.getDefaultValue(), List.class);
                initializer = createListInitializer(String.class.getName(), defaultValue);
            } else if (holder.isEnumList()) {
                Object defaultValue = TypeHelper.parse(field.getDefaultValue(), List.class);
                initializer = createListInitializer(holder.getEnumSimpleName(), defaultValue);
            } else if (holder.isString()) {
                initializer = createInitializer(String.class.getName(), field.getDefaultValue());
            } else if (holder.isEnum()) {
                initializer = createEnumInitializer(holder.getEnumSimpleName(), field.getDefaultValue());
            }
        } else {
            initializer = createInitializer(type.getTypeClassName(), field.getDefaultValue());
        }

        return initializer;
    }

    private CtField.Initializer createInitializer(String typeClass, String defaultValueAsString) {
        Object defaultValue = TypeHelper.parse(defaultValueAsString, typeClass);

        switch (typeClass) {
            case "java.lang.Integer":
            case "java.lang.Double":
            case "java.lang.Boolean":
                return createSimpleInitializer(typeClass, defaultValue);
            case "java.lang.String":
                return CtField.Initializer.constant((String) defaultValue);
            case "org.motechproject.commons.date.model.Time":
                Time time = (Time) defaultValue;
                return createSimpleInitializer(typeClass, '"' + time.timeStr() + '"');
            case "org.joda.time.DateTime":
                DateTime dateTime = (DateTime) defaultValue;
                return createSimpleInitializer(typeClass, dateTime.getMillis() + "l"); // explicit long
            case "java.util.Date":
                Date date = (Date) defaultValue;
                return createSimpleInitializer(typeClass, date.getTime() + "l"); // explicit long
            default:
                return null;
        }
    }

    private CtField.Initializer createListInitializer(String genericType, Object defaultValue) {
        StringBuilder sb = new StringBuilder();

        sb.append("new java.util.ArrayList(");
        sb.append(Arrays.class.getName());
        sb.append(".asList(new Object[]{");

        List defValList = (List) defaultValue;

        for (int i = 0; i < defValList.size(); i++) {
            Object obj = defValList.get(i);

            if (String.class.getName().equalsIgnoreCase(genericType)) {
                // list of strings
                sb.append('\"');
                sb.append(obj);
                sb.append('\"');
            } else {
                // list of enums
                sb.append(genericType);
                sb.append('.');
                sb.append(obj);
            }

            if (i < defValList.size() - 1) {
                sb.append(',');
            }
        }

        sb.append("}))");

        return CtField.Initializer.byExpr(sb.toString());
    }

    private CtField.Initializer createSimpleInitializer(String type, Object defaultValue) {
        return CtField.Initializer.byExpr("new " + type + '(' + defaultValue.toString() + ')');
    }

    private CtField.Initializer createEnumInitializer(String enumType, String defaultValue) {
        return CtField.Initializer.byExpr(enumType + "." + defaultValue);
    }

}
