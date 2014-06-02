package org.motechproject.mds.javassist;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;
import org.apache.commons.lang.LocaleUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.util.TypeHelper;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang.StringUtils.capitalize;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.uncapitalize;

/**
 * Builder class for javassist related tasks. Helps with building appropriate elements of class e.g.
 * fields, getters, field initializer
 */
public final class JavassistBuilder {

    private JavassistBuilder() {
    }

    public static CtField createField(CtClass declaring, CtClass type, String name, String genericSignature) throws CannotCompileException {
        String fieldName = uncapitalize(name);
        CtField field = new CtField(type, fieldName, declaring);
        field.setModifiers(Modifier.PRIVATE);

        if (isNotBlank(genericSignature)) {
            field.setGenericSignature(genericSignature);
        }

        return field;
    }

    public static CtMethod createGetter(String fieldName, CtClass declaring, CtField field) throws CannotCompileException {
        String capitalized = capitalize(fieldName);

        String standardGetter = "get" + capitalized;
        String booleanGetter = "is" + capitalized;

        // we have to check what kind of getter is defined in the given class definition
        // and create the new one with the same name
        String methodName = JavassistHelper.containsDeclaredMethod(declaring, booleanGetter) ? booleanGetter : standardGetter;

        return CtNewMethod.getter(methodName, field);
    }

    public static CtMethod createSetter(String fieldName, CtField field) throws CannotCompileException {
        return CtNewMethod.setter("set" + capitalize(fieldName), field);
    }

    public static CtField.Initializer createInitializer(String typeClass, String defaultValueAsString) {
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
            case "org.joda.time.LocalDate":
                LocalDate localDate = (LocalDate) defaultValue;
                String initStr = String.format("%d, %d, %d",
                        localDate.getYear(), localDate.getMonthOfYear(), localDate.getDayOfMonth());
                return createSimpleInitializer(typeClass, initStr);
            case "java.util.Locale":
                return createLocaleInitializer(defaultValueAsString);
            default:
                return null;
        }
    }

    public static CtField.Initializer createListInitializer(String genericType, Object defaultValue) {
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

    public static CtField.Initializer createSimpleInitializer(String type, Object defaultValue) {
        return createSimpleInitializer(type, defaultValue.toString());
    }

    public static CtField.Initializer createSimpleInitializer(String type, String defaultValue) {
        return CtField.Initializer.byExpr(String.format("new %s(%s)", type, defaultValue));
    }

    public static CtField.Initializer createEnumInitializer(String enumType, String defaultValue) {
        return CtField.Initializer.byExpr(String.format("%s.%s", enumType, defaultValue));
    }

    public static CtField.Initializer createLocaleInitializer(String defaultValue) {
        return CtField.Initializer.byExpr(String.format("%s.toLocale(\"%s\")", LocaleUtils.class.getName(), defaultValue));
    }
}
