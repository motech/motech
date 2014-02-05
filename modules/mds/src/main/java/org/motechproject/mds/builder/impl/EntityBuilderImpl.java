package org.motechproject.mds.builder.impl;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.builder.ClassData;
import org.motechproject.mds.builder.EntityBuilder;
import org.motechproject.mds.domain.AvailableFieldTypeMapping;
import org.motechproject.mds.domain.EntityMapping;
import org.motechproject.mds.domain.FieldMapping;
import org.motechproject.mds.ex.EntityCreationException;
import org.motechproject.mds.javassist.JavassistHelper;
import org.motechproject.mds.javassist.MotechClassPool;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * The <code>EntityBuilderImpl</code> is used build classes for a given entity.
 * This implementation relies on Javassist to build the class definition.
 */
@Component
public class EntityBuilderImpl implements EntityBuilder {

    private final ClassPool classPool = MotechClassPool.getDefault();

    @Override
    public ClassData build(EntityMapping entityMapping) {
        try {
            String className = entityMapping.getClassName();

            CtClass ctClass = classPool.getOrNull(className);

            // we can edit classes
            if (ctClass != null) {
                ctClass.defrost();
            }

            ctClass = classPool.makeClass(className);

            addFields(ctClass, entityMapping.getFields());

            return new ClassData(className, ctClass.toBytecode());
        } catch (Exception e) {
            throw new EntityCreationException(e);
        }
    }

    private void addFields(CtClass ctClass, List<FieldMapping> fields) throws NotFoundException, CannotCompileException {
        for (FieldMapping field : fields) {
            String fieldName = field.getName();
            String typeClass = field.getType().getTypeClass();

            CtClass type = MotechClassPool.getDefault().get(typeClass);

            CtField ctField = new CtField(type, fieldName, ctClass);
            ctField.setModifiers(Modifier.PRIVATE);

            if (List.class.getName().equals(typeClass)) {
                ctField.setGenericSignature(JavassistHelper.genericFieldSignature(List.class, String.class));
            }

            if (StringUtils.isBlank(field.getDefaultValue())) {
                ctClass.addField(ctField);
            } else {
                ctClass.addField(ctField, initializerForField(field));
            }

            CtMethod getter = CtNewMethod.getter("get" + StringUtils.capitalize(fieldName), ctField);
            CtMethod setter = CtNewMethod.setter("set" + StringUtils.capitalize(fieldName), ctField);

            ctClass.addMethod(getter);
            ctClass.addMethod(setter);
        }
    }

    private CtField.Initializer initializerForField(FieldMapping field) throws NotFoundException {
        AvailableFieldTypeMapping fieldType = field.getType();
        String typeClass = fieldType.getTypeClass();

        Object defaultValue = fieldType.parse(field.getDefaultValue());

        switch (typeClass) {
            case "java.util.List":
                return listInitializer(defaultValue);
            case "java.lang.Integer":
            case "java.lang.Double":
            case "java.lang.Boolean":
                 return newInitializer(typeClass, defaultValue);
            case "java.lang.String":
                return CtField.Initializer.constant((String) defaultValue);
            case "org.motechproject.commons.date.model.Time":
                Time time  = (Time) defaultValue;
                return newInitializer(typeClass, '"' + time.timeStr() + '"');
            case "org.joda.time.DateTime":
                DateTime dateTime = (DateTime) defaultValue;
                return newInitializer(typeClass, dateTime.getMillis()  + "l"); // explicit long
            case "java.util.Date":
                Date date = (Date) defaultValue;
                return newInitializer(typeClass, date.getTime() + "l"); // explicit long
            default:
                return null;
        }
    }

    private CtField.Initializer listInitializer(Object defaultValue) {
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

    private CtField.Initializer newInitializer(String type, Object defaultValue) {
        return CtField.Initializer.byExpr("new " + type + '(' + defaultValue.toString() + ')');
    }
}
