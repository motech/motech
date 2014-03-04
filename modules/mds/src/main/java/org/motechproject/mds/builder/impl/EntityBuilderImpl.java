package org.motechproject.mds.builder.impl;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;
import org.joda.time.DateTime;
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.builder.ClassData;
import org.motechproject.mds.builder.EntityBuilder;
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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang.StringUtils.capitalize;
import static org.apache.commons.lang.StringUtils.isBlank;
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
            addFields(ctClass, entity.getFields());
            addHiddenFields(ctClass);

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

            addFields(ddeClass, entity.getFields());
            addHiddenFields(ddeClass);

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

            // creates the same fields like in entity definition
            addFields(historyClass, entity.getFields());

            Type idType = entity.getField("id").getType();

            // add 3 extra fields to history class definition

            // this field is related with id field in entity
            addProperty(historyClass, idType.getTypeClassName(), simpleName + "CurrentVersion");

            // this field has information about previous historical data. It can be assumed that
            // if this field is empty, the history instance will represent the first changes.
            addProperty(historyClass, historyClassName, simpleName + "Previous");

            // this field has information about next (new) historical data. It can be assumed
            // that if this field is empty, the history instance will represent the newest changes.
            addProperty(historyClass, historyClassName, simpleName + "Next");

            return new ClassData(
                    historyClassName, entity.getModule(), entity.getNamespace(),
                    historyClass.toBytecode()
            );
        } catch (Exception e) {
            throw new EntityCreationException(e);
        }
    }

    private void addFields(CtClass ctClass, List<Field> fields) throws CannotCompileException {
        LOG.debug("Adding fields to class: " + ctClass.getName());

        for (Field field : fields) {
            String fieldName = field.getName();

            if (!JavassistHelper.containsDeclaredField(ctClass, fieldName)) {
                String typeClassName = field.getType().getTypeClassName();
                String defaultValue = field.getDefaultValue();

                addProperty(ctClass, typeClassName, fieldName, defaultValue);
            }
        }
    }

    private void addHiddenFields(CtClass ctClass) throws CannotCompileException {
        // hidden field that informs MDS that the given instance is in the mds trash or not
        addProperty(ctClass, Boolean.class.getName(), "__IN_TRASH", "false");
    }

    private void addProperty(CtClass declaring, String typeClassName, String propertyName)
            throws CannotCompileException {
        addProperty(declaring, typeClassName, propertyName, null);
    }

    private void addProperty(CtClass declaring, String typeClassName, String propertyName,
                             String defaultValue) throws CannotCompileException {
        CtField field = createField(typeClassName, propertyName, declaring);
        CtMethod getter = createGetter(propertyName, field);
        CtMethod setter = createSetter(propertyName, field);

        if (isBlank(defaultValue)) {
            declaring.addField(field);
        } else {
            declaring.addField(field, createInitializer(typeClassName, defaultValue));
        }

        declaring.addMethod(getter);
        declaring.addMethod(setter);
    }

    private CtField createField(String typeClassName, String fieldName, CtClass declaring) throws CannotCompileException {

        CtClass type = classPool.getOrNull(typeClassName);
        String name = uncapitalize(fieldName);

        CtField field = new CtField(type, name, declaring);
        field.setModifiers(Modifier.PRIVATE);

        if (List.class.getName().equals(typeClassName)) {
            field.setGenericSignature(JavassistHelper.genericSignature(List.class, String.class));
        }

        return field;
    }

    private CtMethod createGetter(String fieldName, CtField field) throws CannotCompileException {
        return CtNewMethod.getter("get" + capitalize(fieldName), field);
    }

    private CtMethod createSetter(String fieldName, CtField field) throws CannotCompileException {
        return CtNewMethod.setter("set" + capitalize(fieldName), field);
    }

    private CtField.Initializer createInitializer(String typeClass, String defaultValueAsString) {
        Object defaultValue = TypeHelper.parse(defaultValueAsString, typeClass);

        switch (typeClass) {
            case "java.util.List":
                return createListInitializer(defaultValue);
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

    private CtField.Initializer createListInitializer(Object defaultValue) {
        StringBuilder sb = new StringBuilder();

        sb.append("new java.util.ArrayList(");
        sb.append(Arrays.class.getName()).append(".asList(new Object[]{");

        List defValList = (List) defaultValue;

        for (int i = 0; i < defValList.size(); i++) {
            Object obj = defValList.get(i);
            // list of strings
            sb.append('\"').append(obj).append('\"');

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
}
