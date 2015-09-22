package org.motechproject.mds.helper;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;
import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.util.JavassistUtil;
import org.motechproject.mds.util.MemberUtil;
import org.motechproject.mds.util.TypeHelper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.uncapitalize;

/**
 * Builder class for javassist related tasks. Helps with building appropriate elements of class e.g.
 * fields, getters, field initializer
 */
public final class JavassistBuilder {

    private JavassistBuilder() {
    }

    /**
     * Creates class field with the given name for the given class declaration and type.
     *
     * @param declaring the class to which the field will be added
     * @param type the field type
     * @param name the field name
     * @param genericSignature the generic signature
     * @return An instance of {@link javassist.CtField} represents a field
     * @throws CannotCompileException when bytecode transformation has failed
     */
    public static CtField createField(CtClass declaring, CtClass type, String name, String genericSignature) throws CannotCompileException {
        String fieldName = uncapitalize(name);
        CtField field = new CtField(type, fieldName, declaring);
        field.setModifiers(Modifier.PRIVATE);

        if (isNotBlank(genericSignature)) {
            field.setGenericSignature(genericSignature);
        }

        return field;
    }

    /**
     * Creates a public getter method with the given field name for the given class declaration and type.
     *
     * @param fieldName the field name
     * @param declaring the class to which the getter will be added
     * @param field the field declaration
     * @return An instance of {@link javassist.CtMethod} represents a getter method
     * @throws CannotCompileException when bytecode transformation has failed
     */
    public static CtMethod createGetter(String fieldName, CtClass declaring, CtField field) throws CannotCompileException {
        String methodName = MemberUtil.getGetterName(fieldName, declaring);

        CtMethod getter = CtNewMethod.getter(methodName, field);

        String genericFieldSignature = field.getGenericSignature();
        if (StringUtils.isNotBlank(genericFieldSignature)) {
            String getterSignature = JavassistUtil.genericGetterSignature(genericFieldSignature);
            getter.setGenericSignature(getterSignature);
        }

        return getter;
    }

    /**
     * Creates a public setter method with the given field name for the given class declaration and type.
     *
     * @param fieldName the field name
     * @param field the field declaration
     * @return An instance of {@link javassist.CtMethod} represents a setter method
     * @throws CannotCompileException when bytecode transformation has failed
     */
    public static CtMethod createSetter(String fieldName, CtField field) throws CannotCompileException {
        String methodName = MemberUtil.getSetterName(fieldName);

        CtMethod setter = CtNewMethod.setter(methodName, field);

        String genericFieldSignature = field.getGenericSignature();
        if (StringUtils.isNotBlank(genericFieldSignature)) {
            String setterSignature = JavassistUtil.genericSetterSignature(genericFieldSignature);
            setter.setGenericSignature(setterSignature);
        }

        return setter;
    }

    /**
     * Creates a field initializer for the given type and default value.
     *
     * @param typeClass the field type
     * @param defaultValueAsString the default value for field as string
     * @return field initializer
     */
    public static CtField.Initializer createInitializer(String typeClass, String defaultValueAsString) {
        Object defaultValue = TypeHelper.parse(defaultValueAsString, typeClass);

        //The distinction made in order to avoid the cyclomatic complexity error
        if(typeClass.startsWith("java")) {
            return createInitializerForJavaPlatformPackages(typeClass, defaultValue, defaultValueAsString);
        } else {
            return createInitializerForThirdPartyPackages(typeClass, defaultValue);
        }
    }

    private static CtField.Initializer createInitializerForJavaPlatformPackages(String typeClass,
                                                                                Object defaultValue,
                                                                                String defaultValueAsString) {
        switch (typeClass) {
            case "java.lang.Integer":
            case "java.lang.Double":
            case "java.lang.Boolean":
                return createSimpleInitializer(typeClass, defaultValue);
            case "java.lang.String":
                return CtField.Initializer.constant((String) defaultValue);
            case "java.time.LocalDateTime":
                LocalDateTime localDateTime = (LocalDateTime) defaultValue;
                return createJavaTimeInitializer(typeClass, localDateTime.toString());
            case "java.util.Date":
                Date date = (Date) defaultValue;
                return createSimpleInitializer(typeClass, date.getTime() + "l"); // explicit long
            case "java.time.LocalDate":
                java.time.LocalDate javaLocalDate = (java.time.LocalDate) defaultValue;
                return createJavaTimeInitializer(typeClass, javaLocalDate.toString());
            case "java.util.Locale":
                return createLocaleInitializer(defaultValueAsString);
            default:
                return null;
        }
    }

    private static CtField.Initializer createInitializerForThirdPartyPackages(String typeClass,
                                                                              Object defaultValue) {
        switch (typeClass) {
            case "org.motechproject.commons.date.model.Time":
                Time time = (Time) defaultValue;
                return createSimpleInitializer(typeClass, '"' + time.timeStr() + '"');
            case "org.joda.time.DateTime":
                DateTime dateTime = (DateTime) defaultValue;
                return createSimpleInitializer(typeClass, dateTime.getMillis() + "l"); // explicit long
            case "org.joda.time.LocalDate":
                LocalDate localDate = (LocalDate) defaultValue;
                String initStr = String.format("%d, %d, %d",
                        localDate.getYear(), localDate.getMonthOfYear(), localDate.getDayOfMonth());
                return createSimpleInitializer(typeClass, initStr);
            default:
                return null;
        }
    }

    /**
     * Creates a collection initializer for the given generic type and default value.
     *
     * @param genericType the generic type
     * @param defaultValue the default value
     * @return initializer for collections
     */
    public static CtField.Initializer createCollectionInitializer(String genericType, Object defaultValue) {
        if (List.class.isAssignableFrom(defaultValue.getClass())) {
            return createListInitializer(genericType, defaultValue);
        } else if (Set.class.isAssignableFrom(defaultValue.getClass())) {
            return createSetInitializer(genericType, defaultValue);
        } else {
            return null;
        }
    }

    /**
     * Creates a list initializer for the given generic type and default value.
     *
     * @param genericType the generic type
     * @param defaultValue the default value
     * @return initializer for lists
     */
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

    /**
     * Creates a set initializer for the given generic type and default value.
     *
     * @param genericType the generic type
     * @param defaultValue the default value
     * @return initializer for sets
     */
    public static CtField.Initializer createSetInitializer(String genericType, Object defaultValue) {
        StringBuilder sb = new StringBuilder();

        sb.append("new java.util.HashSet(");
        sb.append(Arrays.class.getName());
        sb.append(".asList(new Object[]{");

        Set defValSet = (Set) defaultValue;

        for (Object obj : defValSet) {
            if (String.class.getName().equalsIgnoreCase(genericType)) {
                // set of strings
                sb.append('\"');
                sb.append(obj);
                sb.append('\"');
            } else {
                // set of enums
                sb.append(genericType);
                sb.append('.');
                sb.append(obj);
            }

            sb.append(',');

        }

        sb.deleteCharAt(sb.lastIndexOf(","));
        sb.append("}))");

        return CtField.Initializer.byExpr(sb.toString());
    }

    /**
     * Makes a simple initializer for the given type and default value.
     *
     * @param type the field type
     * @param defaultValue the default value
     * @return simple initializer
     */
    public static CtField.Initializer createSimpleInitializer(String type, Object defaultValue) {
        return createSimpleInitializer(type, defaultValue.toString());
    }

    /**
     * Makes a simple initializer for the given type and default value.
     *
     * @param type the field type
     * @param defaultValue the default value as string
     * @return simple initializer
     */
    public static CtField.Initializer createSimpleInitializer(String type, String defaultValue) {
        return CtField.Initializer.byExpr(String.format("new %s(%s)", type, defaultValue));
    }

    /**
     * Makes a initializer for {@link java.time.LocalDate} or {@link java.time.LocalDateTime} class
     *
     * @param type the field type
     * @param defaultValue the default value as string
     * @return {@link java.time.LocalDate} or {@link java.time.LocalDateTime} initializer based on a type parameter
     */
    public static CtField.Initializer createJavaTimeInitializer(String type, String defaultValue) {
        return CtField.Initializer.byExpr(String.format("%s.parse(\"%s\")", type, defaultValue));
    }

    /**
     * Makes an initializer for enums.
     *
     * @param enumType the enum type
     * @param defaultValue the default value
     * @return enum initializer
     */
    public static CtField.Initializer createEnumInitializer(String enumType, String defaultValue) {
        return CtField.Initializer.byExpr(String.format("%s.%s", enumType, defaultValue));
    }

    /**
     * Makes an initializer for {@link java.util.Locale} class.
     *
     * @param defaultValue the default value
     * @return {@link java.util.Locale} initializer
     */
    public static CtField.Initializer createLocaleInitializer(String defaultValue) {
        return CtField.Initializer.byExpr(String.format("%s.toLocale(\"%s\")", LocaleUtils.class.getName(), defaultValue));
    }
}
