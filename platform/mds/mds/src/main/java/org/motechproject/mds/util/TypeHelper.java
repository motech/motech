package org.motechproject.mds.util;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.commons.collections.bidimap.UnmodifiableBidiMap;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.MethodUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.motechproject.commons.api.Range;
import org.motechproject.commons.date.model.Time;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.apache.commons.lang.StringUtils.replaceEach;
import static org.apache.commons.lang.StringUtils.split;

/**
 * A helper class for parsing and formatting mds supported types.
 */
public final class TypeHelper {

    private static final DateTimeFormatter DTF;
    private static final BidiMap PRIMITIVE_TYPE_MAP;
    private static final Map<String, Class<?>> PRIMITIVE_WRAPPER_NAME_MAP;
    private static final Map<String, Class> COLLECTION_IMPLEMENTATIONS;

    static {
        DateTimeParser[] parsers = {
                DateTimeFormat.forPattern("yyyy-MM-dd").getParser(),
                DateTimeFormat.forPattern("yyyy-MM-dd HH:mm Z").getParser(),
                DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ").getParser(),
                DateTimeFormat.fullDateTime().getParser(),
                DateTimeFormat.fullDate().getParser(),
                DateTimeFormat.shortDateTime().getParser(),
                DateTimeFormat.shortDate().getParser()
        };
        DTF = new DateTimeFormatterBuilder().append(null, parsers).toFormatter();

        BidiMap primitiveTypeMap = new DualHashBidiMap();
        primitiveTypeMap.put(Integer.class, int.class);
        primitiveTypeMap.put(Long.class, long.class);
        primitiveTypeMap.put(Short.class, short.class);
        primitiveTypeMap.put(Byte.class, byte.class);
        primitiveTypeMap.put(Byte[].class, byte[].class);
        primitiveTypeMap.put(Double.class, double.class);
        primitiveTypeMap.put(Float.class, float.class);
        primitiveTypeMap.put(Character.class, char.class);
        primitiveTypeMap.put(Boolean.class, boolean.class);

        PRIMITIVE_TYPE_MAP = UnmodifiableBidiMap.decorate(primitiveTypeMap);

        Map<String, Class<?>> primitiveWrapperNameMap = new HashMap<>();

        primitiveWrapperNameMap.put(Integer.class.getName(), Integer.class);
        primitiveWrapperNameMap.put(Long.class.getName(), Long.class);
        primitiveWrapperNameMap.put(Short.class.getName(), Short.class);
        primitiveWrapperNameMap.put(Byte.class.getName(), Byte.class);
        primitiveWrapperNameMap.put(Byte[].class.getName(), Byte[].class);
        primitiveWrapperNameMap.put(Double.class.getName(), Double.class);
        primitiveWrapperNameMap.put(Float.class.getName(), Float.class);
        primitiveWrapperNameMap.put(Character.class.getName(), Character.class);
        primitiveWrapperNameMap.put(Boolean.class.getName(), Boolean.class);

        primitiveWrapperNameMap.put(int.class.getName(), Integer.class);
        primitiveWrapperNameMap.put(long.class.getName(), Long.class);
        primitiveWrapperNameMap.put(short.class.getName(), Short.class);
        primitiveWrapperNameMap.put(byte.class.getName(), Byte.class);
        primitiveWrapperNameMap.put(byte[].class.getName(), Byte[].class);
        primitiveWrapperNameMap.put(double.class.getName(), Double.class);
        primitiveWrapperNameMap.put(float.class.getName(), Float.class);
        primitiveWrapperNameMap.put(char.class.getName(), Character.class);
        primitiveWrapperNameMap.put(boolean.class.getName(), Boolean.class);

        PRIMITIVE_WRAPPER_NAME_MAP = primitiveWrapperNameMap;

        COLLECTION_IMPLEMENTATIONS = new HashMap<>();

        // PMD sees this as declaring ArrayList type fields for some reason
        COLLECTION_IMPLEMENTATIONS.put(List.class.getName(), ArrayList.class); // NOPMD - bug in PMD, objects to ArrayList.class here
        COLLECTION_IMPLEMENTATIONS.put(Set.class.getName(), HashSet.class); // NOPMD - bug in PMD, objects to HashSet.class here
        COLLECTION_IMPLEMENTATIONS.put(SortedSet.class.getName(), TreeSet.class); // NOPMD - bug in PMD, objects to TreeSet.class here
        COLLECTION_IMPLEMENTATIONS.put(Queue.class.getName(), ArrayDeque.class);
    }

    public static Object parse(Object val, Class<?> toClass) {
        return parse(val, toClass.getName());
    }

    public static Object parse(Object val, String toClass) {
        return parse(val, toClass, null, null);
    }

    public static Object parse(Object val, String toClass, ClassLoader classLoader) {
        return parse(val, toClass, null, classLoader);
    }

    public static Object parse(Object val, String toClass, String genericType) {
        return parse(val, toClass, genericType, null);
    }

    public static Object parse(Object val, String toClass, String genericType, ClassLoader classLoader) {
        Class<?> generic = null != genericType ? getClassDefinition(genericType, classLoader) : null;
        Class<?> toClassDefinition = getClassDefinition(toClass, classLoader);

        if (null == val) {
            return null;
        } else if (toClassDefinition.isAssignableFrom(val.getClass())) {
            return parseAssignableType(val, toClassDefinition, generic);
        } else if (val instanceof String) {
            return parseString((String) val, toClassDefinition, generic);
        } else if (val instanceof Integer && Boolean.class.getName().equals(toClass)) {
            return parseIntToBool((Integer) val);
        } else if (bothNumbers(val, toClass)) {
            return parseNumber(val, toClass);
        } else if (bothDateOrTime(val, toClass)) {
            return parseDateToDate(val, toClass);
        } else if (PRIMITIVE_WRAPPER_NAME_MAP.containsKey(toClassDefinition)) {
            return parsePrimitive(val, toClassDefinition);
        } else {
            throw unableToParseException(val, toClass);
        }
    }

    public static Object parseString(String str, Class<?> toClass) {
        return parseString(str, toClass, null);
    }

    public static Object parseString(String str, String toClass) {
        return parseString(str, getClassDefinition(toClass), null);
    }

    public static Object parseString(String str, Class<?> toClass, Class<?> generic) {
        if (isBlank(str, toClass)) {
            return (String.class.isAssignableFrom(toClass)) ? "" : null;
        }

        if (isDateOrPeriod(toClass)) {
            return parseDateOrPeriod(toClass, str);
        }

        try {
            if (toClass.isEnum()) {
                Class<? extends Enum> enumClass = (Class<? extends Enum>) toClass;
                return Enum.valueOf(enumClass, str);
            }

            if (toClass.isAssignableFrom(List.class)) {
                return parseStringToList(str, generic);
            } else if (toClass.isAssignableFrom(Map.class)) {
                return parseStringToMap(str);
            } else if (toClass.isAssignableFrom(Locale.class)) {
                return LocaleUtils.toLocale(str);
            } else if (toClass.isAssignableFrom(Byte[].class)) {
                return ArrayUtils.toObject(str.getBytes());
            } else {
                return MethodUtils.invokeStaticMethod(toClass, "valueOf", str);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Unable to parse value " + str + " to " + toClass, e);
        }
    }

    private static Object parseAssignableType(Object val, Class toClassDefinition, Class genericType) {
        if (List.class.isAssignableFrom(toClassDefinition)) {
            return parseList((List) val, genericType);
        }

        return val;
    }

    private static Object parsePrimitive(Object val, Class toClass) {
        try {
            return MethodUtils.invokeStaticMethod(toClass, "valueOf", val);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw unableToParseException(val, toClass, e);
        }
    }

    private static boolean isBlank(String str, Class<?> toClass) {
        return StringUtils.isBlank(str)
                && !Collection.class.isAssignableFrom(toClass)
                && !Map.class.isAssignableFrom(toClass);
    }

    private static Object parseStringToList(String str, Class<?> generic) {
        List list = new ArrayList();

        if (null != generic && generic.isEnum()) {
            String[] stringArray = breakString(str);
            Class<? extends Enum> enumClass = (Class<? extends Enum>) generic;

            for (String string : stringArray) {
                list.add(Enum.valueOf(enumClass, string));
            }
        } else if (null != generic) {
            String[] stringArray = breakString(str);
            for (String strItem : stringArray) {
                list.add(parse(strItem, generic));
            }
        } else {
            String[] stringArray = breakStringForList(str);
            for (String element : stringArray) {
                list.add(element.trim().replaceAll("%20", " "));
            }
        }

        return list;
    }

    public static String buildStringFromList(List<String> items) {
        return StringUtils.join(items, '\n');
    }

    public static String[] breakString(String str) {
        return breakString(
                str,
                new String[]{"[", "]", "{", "}", "\"", " "},
                new String[]{"=", "\n", "\r\n"},
                new String[]{":", ",", ","},
                ","
        );
    }

    public static String[] breakStringForList(String str) {
        return breakString(
                str,
                new String[]{"[", "]", "{", "}", "\""},
                new String[]{"=", "\n", "\r\n"},
                new String[]{":", ",", ","},
                ","
        );
    }

    public static String[] breakString(String str, String[] removes, String[] search,
                                       String[] replacement, String separator) {
        String[] empty = new String[removes.length];
        Arrays.fill(empty, "");

        String removed = replaceEach(str, removes, empty);
        String replaced = replaceEach(removed, search, replacement);
        return split(replaced, separator);
    }

    public static Map parseStringToMap(String str) {
        String[] entries = breakString(str);
        Map map = new HashMap<>();

        for (String entry : entries) {
            if (!entry.isEmpty()) {
                String[] values = split(entry, ":", 2);
                String val = (values.length > 1) ? values[1].trim() : "";
                map.put(values[0].trim(), val);
            }
        }

        return map;
    }

    public static Object parseDateToDate(Object val, String toClass) {
        if (val instanceof DateTime) {
            return parseDateTime((DateTime) val, toClass);
        } else if (val instanceof Date) {
            return parseDate((Date) val, toClass);
        } else if (val instanceof LocalDate) {
            throw new UnsupportedOperationException("Date parsing from LocalDate is not supported");
        } else if (val instanceof Time) {
            throw new UnsupportedOperationException("Date parsing from Time is not supported");
        } else {
            return null;
        }
    }

    private static boolean isDateOrPeriod(Class<?> toClass) {
        return isDate(toClass) || Period.class.isAssignableFrom(toClass);
    }

    private static boolean isDateOrTime(Class<?> toClass) {
        return isDate(toClass) || Time.class.isAssignableFrom(toClass);
    }

    private static boolean isDate(Class<?> toClass) {
        return DateTime.class.isAssignableFrom(toClass)
                || Date.class.isAssignableFrom(toClass)
                || LocalDate.class.isAssignableFrom(toClass);
    }

    private static Object parseDateTime(DateTime val, String toClass) {
        switch (toClass) {
            case "org.joda.time.DateTime":
                return val;
            case "java.util.Date":
                return val.toDate();
            case "org.joda.time.LocalDate":
                return val.toLocalDate();
            case "org.motechproject.commons.date.model.Time":
                return new Time(val.getHourOfDay(), val.getMinuteOfHour());
            default:
                return null;
        }
    }

    private static Object parseDate(Date val, String toClass) {
        switch (toClass) {
            case "org.joda.time.DateTime":
                return new DateTime(val);
            case "java.util.Date":
                return val;
            case "org.joda.time.LocalDate":
                return LocalDate.fromDateFields(val);
            case "org.motechproject.commons.date.model.Time":
                DateTime dateTime = new DateTime(val);
                return new Time(dateTime.getHourOfDay(), dateTime.getMinuteOfHour());
            default:
                return null;
        }
    }

    private static Object parseDateOrPeriod(Class<?> toClass, String str) {
        if (DateTime.class.isAssignableFrom(toClass)) {
            return DTF.parseDateTime(str);
        } else if (Date.class.isAssignableFrom(toClass)) {
            return DTF.parseDateTime(str).toDate();
        } else if (Period.class.isAssignableFrom(toClass)) {
            return Period.parse(str);
        } else if (LocalDate.class.isAssignableFrom(toClass)) {
            String val = str;
            int tIndex = str.indexOf('T');
            if (tIndex >= 0) {
                val = str.substring(0, tIndex);
            }
            return LocalDate.parse(val);
        } else {
            return null;
        }
    }

    public static List parseList(List val, Class<?> generic) {
        List list = new ArrayList();

        if (null != generic) {
            if (generic.isEnum()) {
                Class<? extends Enum> enumClass = (Class<? extends Enum>) generic;

                for (Object item : val) {
                    list.add(Enum.valueOf(enumClass, item.toString()));
                }
            }
        } else {
            list.addAll(val);
        }

        return list;
    }

    public static boolean parseIntToBool(Integer val) {
        return val != null && val > 0;
    }

    public static String format(Object obj) {
        return format(obj, '\n');
    }

    public static String format(Object obj, char listJoinChar) {
        if (obj instanceof List) {
            return StringUtils.join((List) obj, listJoinChar);
        } else if (obj instanceof Map) {
            StringBuilder result = new StringBuilder();

            for (Object entry : ((Map) obj).entrySet()) {
                result = result
                        .append(((Map.Entry) entry).getKey().toString())
                        .append(": ")
                        .append(((Map.Entry) entry).getValue().toString())
                        .append("\n");
            }

            return result.toString();
        } else if (obj instanceof Time) {
            return ((Time) obj).timeStr();
        } else if (obj instanceof Date) {
            return new DateTime(((Date) obj).getTime()).toString();
        } else {
            return (obj == null) ? "" : obj.toString();
        }
    }

    public static Number parseNumber(Object val, String toClass) {
        Number number = (Number) val;
        switch (toClass) {
            case "java.lang.Integer":
                return number.intValue();
            case "java.lang.Long":
                return number.longValue();
            case "java.lang.Short":
                return number.shortValue();
            case "java.lang.Double":
                return number.doubleValue();
            case "java.lang.Float":
                return number.floatValue();
            case "java.lang.Byte":
                return number.byteValue();
            default:
                return number;
        }
    }

    public static boolean hasPrimitive(Class<?> clazz) {
        return PRIMITIVE_TYPE_MAP.containsKey(clazz);
    }

    public static boolean isPrimitive(Class<?> clazz) {
        return PRIMITIVE_TYPE_MAP.containsValue(clazz);
    }

    public static boolean isPrimitive(String className) {
        for (Object clazz : PRIMITIVE_TYPE_MAP.values()) {
            if (((Class) clazz).getName().equals(className)) {
                return true;
            }
        }

        return false;
    }

    public static Class<?> getPrimitive(Class<?> clazz) {
        return (Class<?>) PRIMITIVE_TYPE_MAP.get(clazz);
    }

    public static Class<?> getWrapperForPrimitive(Class<?> clazz) {
        return (Class<?>) PRIMITIVE_TYPE_MAP.getKey(clazz);
    }

    public static Range toRange(Object object, String typeClass) {
        if (object instanceof Range) {
            return (Range) object;
        } else if (object instanceof Map) {
            Map map = (Map) object;

            Object min = parse(map.get("min"), typeClass);
            Object max = parse(map.get("max"), typeClass);

            return new Range(min, max);
        } else if (object instanceof String) {
            // we parse Strings in the format of 'X..Y' to ranges
            String objectAsStr = (String) object;

            String minStr = null;
            String maxStr = null;

            if (objectAsStr.endsWith("..")) {
                minStr = objectAsStr.substring(0, objectAsStr.length() - 2);
            } else if (objectAsStr.startsWith("..")) {
                maxStr = objectAsStr.substring(2, objectAsStr.length());
            } else {
                String[] split = objectAsStr.split("\\.\\.");
                if (split.length != 2) {
                    throw unableToParseException(object, Range.class);
                }
                minStr = split[0];
                maxStr = split[1];
            }

            Object min = parse(minStr, typeClass);
            Object max = parse(maxStr, typeClass);

            return new Range(min, max);
        } else {
            throw unableToParseException(object, Range.class);
        }
    }

    public static Set toSet(Object object, String typeClass, ClassLoader classLoader) {
        if (object instanceof Set) {
            return (Set) object;
        } else if (object instanceof Collection) {
            Set set = new HashSet();

            Collection collection = (Collection) object;

            for (Object collMember : collection) {
                Object value = null;

                if (collMember instanceof Map) {
                    // we receive maps such as {"val": value} from the UI
                    Map map = (Map) collMember;
                    if (map.containsKey("val")) {
                        value = map.get("val");
                    }
                } else {
                    value = collMember;
                }

                if (value != null) {
                    set.add(parse(value, typeClass, classLoader));
                }
            }

            return set;
        } else if (object instanceof String) {
            // split by ',' and then parse that as a collection
            String[] values = StringUtils.split((String) object, ',');
            return toSet(Arrays.asList(values), typeClass, classLoader);
        } else {
            throw unableToParseException(object, Set.class);
        }
    }

    public static Class suggestCollectionImplementation(String collectionClass) {
        if (StringUtils.isBlank(collectionClass)) {
            return null;
        }

        try {
            return suggestCollectionImplementation(TypeHelper.class.getClassLoader().loadClass(collectionClass));
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Unable to load collection class", e);
        }
    }

    public static Class suggestCollectionImplementation(Class collectionClass) {
        if (collectionClass != null && (collectionClass.isInterface() || Modifier.isAbstract(collectionClass.getModifiers()))) {
            return COLLECTION_IMPLEMENTATIONS.get(collectionClass.getName());
        } else {
            return collectionClass;
        }
    }

    private static boolean bothNumbers(Object val, String toClass) {
        return val instanceof Number && Number.class.isAssignableFrom(getClassDefinition(toClass));
    }

    private static boolean bothDateOrTime(Object val, String toClass) {
        return isDateOrTime(val.getClass()) && isDateOrTime(getClassDefinition(toClass));
    }

    private static Class getClassDefinition(String clazz) {
        return getClassDefinition(clazz, null);
    }

    @SuppressWarnings("PMD.PreserveStackTrace")
    private static Class getClassDefinition(String clazz, ClassLoader classLoader) {
        Class<?> definition;

        try {
            if (PRIMITIVE_WRAPPER_NAME_MAP.containsKey(clazz)) {
                definition = PRIMITIVE_WRAPPER_NAME_MAP.get(clazz);
            } else {
                definition = TypeHelper.class.getClassLoader().loadClass(clazz);
            }
        } catch (ClassNotFoundException e1) {
            ClassLoader safeClassLoader = null == classLoader
                    ? MDSClassLoader.getInstance()
                    : classLoader;

            try {
                definition = safeClassLoader.loadClass(clazz);
            } catch (ClassNotFoundException e2) {
                throw new IllegalArgumentException("Unable to load class " + clazz, e2);
            }
        }

        return definition;
    }

    private static IllegalArgumentException unableToParseException(Object val, Class toClass) {
        return unableToParseException(val, toClass.getName());
    }

    private static IllegalArgumentException unableToParseException(Object val, String toClass) {
        return unableToParseException(val, toClass, null);
    }

    private static IllegalArgumentException unableToParseException(Object val, Class toClass, Throwable cause) {
        return unableToParseException(val, toClass.getName(), cause);
    }

    private static IllegalArgumentException unableToParseException(Object val, String toClass, Throwable cause) {
        return new IllegalArgumentException("Unable to parse " + val + " to " + toClass, cause);
    }

    private TypeHelper() {
    }
}
