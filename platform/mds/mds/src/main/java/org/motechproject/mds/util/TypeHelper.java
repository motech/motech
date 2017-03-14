package org.motechproject.mds.util;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.commons.collections.bidimap.UnmodifiableBidiMap;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ClassUtils;
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
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import static org.apache.commons.lang.StringUtils.replaceEach;
import static org.apache.commons.lang.StringUtils.split;
import static org.motechproject.mds.util.Constants.MetadataKeys.MAP_KEY_TYPE;
import static org.motechproject.mds.util.Constants.MetadataKeys.MAP_VALUE_TYPE;

/**
 * A helper class for parsing and formatting MDS supported types.
 */
public final class TypeHelper {

    private static final DateTimeFormatter DTF;
    private static final BidiMap PRIMITIVE_TYPE_MAP;
    private static final Map<String, Class<?>> PRIMITIVE_WRAPPER_NAME_MAP;
    private static final Map<String, Set<String>> MAP_SUPPORTED_TYPES;
    private static final Map<String, Class<? extends Collection>> COLLECTION_IMPLEMENTATIONS;

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

        MAP_SUPPORTED_TYPES = new HashMap<>();

        Set<String> keyClasses = new HashSet<>();
        keyClasses.add(String.class.getName());
        keyClasses.add(Long.class.getName());
        keyClasses.add(Integer.class.getName());

        Set<String> valueClasses = new HashSet<>();
        valueClasses.add(String.class.getName());
        valueClasses.add(Long.class.getName());
        valueClasses.add(Integer.class.getName());

        MAP_SUPPORTED_TYPES.put(MAP_KEY_TYPE, keyClasses);
        MAP_SUPPORTED_TYPES.put(MAP_VALUE_TYPE, valueClasses);
    }

    /**
     * Attempts to parse given value to an instance of a given class.
     * Throws {@link java.lang.IllegalArgumentException} if this method is unable to parse the value.
     *
     * @param val value to parse
     * @param toClass a class to turn value into
     * @return parsed value, and instance of the given class
     */
    public static Object parse(Object val, Class<?> toClass) {
        return parse(val, toClass.getName());
    }

    /**
     * Attempts to parse given value to an instance of a given class.
     * Throws {@link java.lang.IllegalArgumentException} if this method is unable to parse the value.
     *
     * @param val value to parse
     * @param toClass fully qualified class name
     * @return parsed value, and instance of the given class
     */
    public static Object parse(Object val, String toClass) {
        return parse(val, toClass, null, null);
    }

    /**
     * Attempts to parse given value to an instance of a given class. The class may be loaded using
     * custom class loader, in case of a failure to load it via default class loader.
     * Throws {@link java.lang.IllegalArgumentException} if this method is unable to parse the value.
     *
     * @param val value to parse
     * @param toClass fully qualified class name
     * @param classLoader class loader to use, in case of a failure to find class of name {@code toClass}
     * @return parsed value, and instance of the given class
     */
    public static Object parse(Object val, String toClass, ClassLoader classLoader) {
        return parse(val, toClass, null, classLoader);
    }

    /**
     * Attempts to parse given value to an instance of a given class. The class may have a generic type.
     * Throws {@link java.lang.IllegalArgumentException} if this method is unable to parse the value.
     *
     * @param val value to parse
     * @param toClass fully qualified class name
     * @param genericType fully qualified class name of a generic type
     * @return parsed value, and instance of the given class
     */
    public static Object parse(Object val, String toClass, String genericType) {
        return parse(val, toClass, genericType, null);
    }

    /**
     * Attempts to parse given value to an instance of a given class. The class may have a generic type
     * and can be loaded using custom class loader, in case of a failure to load it via default class loader.
     * Throws {@link java.lang.IllegalArgumentException} if this method is unable to parse the value.
     *
     * @param val value to parse
     * @param toClass fully qualified class name
     * @param genericType fully qualified class name of a generic type
     * @param classLoader class loader to use, in case of a failure to find class of name {@code toClass}
     * @return parsed value, and instance of the given class
     */
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

    /**
     * Attempts to parse given String to an instance of a given class. It will throw
     * {@link java.lang.IllegalStateException} in case this method was not able to parse the value.
     *
     * @param str String to parse
     * @param toClass a class to turn value into
     * @return parsed value, an instance of the given class
     */
    public static Object parseString(String str, Class<?> toClass) {
        return parseString(str, toClass, null);
    }

    /**
     * Attempts to parse given String to an instance of a given class. It will throw
     * {@link java.lang.IllegalStateException} in case this method was not able to parse the value.
     *
     * @param str String to parse
     * @param toClass fully qualified class name
     * @return parsed value, an instance of the given class
     */
    public static Object parseString(String str, String toClass) {
        return parseString(str, getClassDefinition(toClass), null);
    }

    /**
     * Attempts to parse given String to an instance of a given class. The class may also
     * have a generic type. It will throw {@link java.lang.IllegalStateException} in case this
     * method was not able to parse the value.
     *
     * @param str String to parse
     * @param toClass a class to turn value into
     * @param generic generic class
     * @return parsed value, an instance of the given class
     */
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

            if (Collection.class.isAssignableFrom(toClass)) {
                return parseStringToCollection(str, toClass, generic);
            } else if (Map.class.isAssignableFrom(toClass)) {
                return parseStringToMap(str);
            } else if (Locale.class.isAssignableFrom(toClass)) {
                return LocaleUtils.toLocale(str);
            } else if (Byte[].class.isAssignableFrom(toClass)) {
                return ArrayUtils.toObject(str.getBytes());
            } else {
                return MethodUtils.invokeStaticMethod(toClass, "valueOf", str);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Unable to parse value " + str + " to " + toClass, e);
        }
    }

    private static Object parseAssignableType(Object val, Class toClassDefinition, Class genericType) {
        if (Collection.class.isAssignableFrom(toClassDefinition)) {
            return parseCollection((Collection) val, toClassDefinition, genericType);
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

    private static Object parseStringToCollection(String str, Class<?> toClass, Class<?> generic) {
        Collection collection;
        if (Collection.class.isAssignableFrom(toClass)) {
            collection = suggestAndCreateCollectionImplementation((Class<Collection>) toClass);
        } else {
            return str;
        }

        if (null != generic && generic.isEnum()) {
            String[] stringArray = breakString(str);
            Class<? extends Enum> enumClass = (Class<? extends Enum>) generic;

            for (String string : stringArray) {
                collection.add(Enum.valueOf(enumClass, string));
            }
        } else if (null != generic) {
            String[] stringArray = breakStringForCollection(str);
            for (String strItem : stringArray) {
                collection.add(parse(strItem, generic));
            }
        } else {
            String[] stringArray = breakStringForCollection(str);
            for (String element : stringArray) {
                collection.add(element.trim().replaceAll("%20", " "));
            }
        }

        return collection;
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

    public static String[] breakStringForCollection(String str) {
        return breakString(
                str,
                new String[]{"[", "]", "{", "}", "\""},
                new String[]{"=", "\n", "\r\n", ", "},
                new String[]{":", ",", ",", ","},
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

    /**
     * Parses given {@link java.lang.String} to {@link java.util.Map}. Each new entry should be preceeded
     * by a comma mark (,). The key and value should be split with a colon mark (:). By default parsed
     * values will be String type.
     *
     * @param str String to parse
     * @return Map, parsed from the given String
     */
    public static Map parseStringToMap(String str) {
        return parseStringToMap(String.class.getName(), String.class.getName(), str);
    }

    /**
     * Parses given {@link java.lang.String} to {@link java.util.Map}. Each new entry should be preceeded
     * by a comma mark (,). The key and value should be split with a colon mark (:). Types of the parsed values
     * depend on the given keyClass and valueClass.
     *
     * @param keyClass the type of key
     * @param valueClass the type of value
     * @param str String String to parse
     * @return Map, parsed from the given String
     */
    public static <K, V> Map<K, V> parseStringToMap(Class<K> keyClass, Class<V> valueClass, String str) {
        return parseStringToMap(keyClass.getName(), valueClass.getName(), str);
    }

    /**
     * Parses given {@link java.lang.String} to {@link java.util.Map}. Each new entry should be preceeded
     * by a comma mark (,). The key and value should be split with a colon mark (:). Types of the parsed values
     * depend on the given keyClass and valueClass.
     *
     * @param keyClass the type of key
     * @param valueClass the type of value
     * @param str String String to parse
     * @return Map, parsed from the given String
     */
    public static Map parseStringToMap(String keyClass, String valueClass, String str) {
        String[] entries = breakStringForCollection(str);
        Map map = new LinkedHashMap<>();

        if (entries != null) {
            for (String entry : entries) {
                if (!entry.isEmpty()) {
                    String[] values = split(entry, ":", 2);
                    String val = (values.length > 1) ? values[1].trim() : "";
                    map.put(parseMapValue(values[0].trim(), keyClass, true), parseMapValue(val, valueClass, false));
                }
            }
        }

        return map;
    }

    public static Object parseStringToUUID(String val) {
        return UUID.fromString(val);
    }
    
    public static Object parseMapValue(Object valueToParse, String type, boolean isKey) {
        Object parsedValue = null;

        if (isTypeSupportedInMap(type, isKey)) {
            if (type.equals(String.class.getName())) {
                parsedValue = valueToParse.toString();
            } else if (type.equals(Integer.class.getName())) {
                parsedValue = Integer.valueOf(valueToParse.toString());
            } else if (type.equals(Long.class.getName())) {
                parsedValue = Long.valueOf(valueToParse.toString());
            }
        }

        return parsedValue == null ? valueToParse : parsedValue;
    }

    /**
     * Returns true if the given type is supported in map, otherwise false.
     *
     * @param type the type to be checked
     * @param isKey true if it is key of the map, otherwise false
     * @return true if the given type is supported in map, otherwise false
     */
    public static boolean isTypeSupportedInMap(String type, boolean isKey) {
        if (isKey) {
            return MAP_SUPPORTED_TYPES.get(MAP_KEY_TYPE) != null && MAP_SUPPORTED_TYPES.get(MAP_KEY_TYPE).contains(type);
        } else {
            return MAP_SUPPORTED_TYPES.get(MAP_VALUE_TYPE) != null && MAP_SUPPORTED_TYPES.get(MAP_VALUE_TYPE).contains(type);
        }
    }

    /**
     * Allows parsing of the various date types. Parsing from the {@link org.joda.time.LocalDate} and
     * {@link org.motechproject.commons.date.model.Time} is not supported at the moment.
     *
     * @param val value to parse
     * @param toClass destination class
     * @return date, parsed to the specified type
     */
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
                || LocalDate.class.isAssignableFrom(toClass)
                || isJavaTimeDate(toClass);
    }

    private static boolean isJavaTimeDate(Class<?> toClass) {
        return java.time.LocalDate.class.isAssignableFrom(toClass)
                || java.time.LocalDateTime.class.isAssignableFrom(toClass);
    }

    private static Object parseDateTime(DateTime val, String toClass) {
        switch (toClass) {
            case "org.joda.time.DateTime":
                return val;
            case "java.time.LocalDateTime":
                return java.time.LocalDateTime.of(val.getYear(), val.getMonthOfYear(), val.getDayOfMonth(), val.getHourOfDay(), val.getMinuteOfHour());
            case "java.util.Date":
                return val.toDate();
            case "org.joda.time.LocalDate":
                return val.toLocalDate();
            case "java.time.LocalDate":
                return java.time.LocalDate.of(val.getYear(), val.getMonthOfYear(), val.getDayOfMonth());
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
        } else if (java.time.LocalDate.class.isAssignableFrom(toClass)) {
            return java.time.LocalDate.parse(str);
        } else if (java.time.LocalDateTime.class.isAssignableFrom(toClass)) {
            return str.contains("T") ? parseJavaLocalDateTimeBackend(str) : parseJavaLocalDateTimeUI(str);
        } else {
            return null;
        }
    }

    private static LocalDateTime parseJavaLocalDateTimeUI(String str) {
        //First parse to Joda DateTime for timezone calculation
        org.joda.time.LocalDateTime dateTime = DTF.parseLocalDateTime(str);
        //Then create LocalDateTime from Joda DateTime and return it
        return LocalDateTime.of(
                dateTime.getYear(),
                dateTime.getMonthOfYear(),
                dateTime.getDayOfMonth(),
                dateTime.getHourOfDay(),
                dateTime.getMinuteOfHour(),
                dateTime.getSecondOfMinute());
    }

    private static LocalDateTime parseJavaLocalDateTimeBackend(String str) {
        String pattern = str.contains(".") ? "yyyy-MM-dd-HH:mm:ss.SSS" : "yyyy-MM-dd-HH:mm";
        return LocalDateTime.parse(str.replace("T", "-"),
                java.time.format.DateTimeFormatter.ofPattern(pattern));
    }

    public static Collection parseCollection(Collection val, Class<?> toClassDefinition, Class<?> generic) {
        Collection collection;
        if (List.class.isAssignableFrom(toClassDefinition)) {
            collection = new ArrayList();
        } else if (Set.class.isAssignableFrom(toClassDefinition)) {
            collection = new HashSet();
        } else if (Collection.class.isAssignableFrom(toClassDefinition)) {
            collection = new ArrayList();
        } else {
            return val;
        }

        if (null != generic) {
            if (generic.isEnum()) {
                Class<? extends Enum> enumClass = (Class<? extends Enum>) generic;

                for (Object item : val) {
                    collection.add(Enum.valueOf(enumClass, item.toString()));
                }
            }
        } else {
            collection.addAll(val);
        }

        return collection;
    }

    /**
     * Turns given {@link java.lang.Integer} into {@link boolean}.
     *
     * @param val value to parse
     * @return true if value is not null and greater than 0; false otherwise
     */
    public static boolean parseIntToBool(Integer val) {
        return val != null && val > 0;
    }

    /**
     * Creates a {@link java.lang.String} representation of the given value. If given object
     * is a {@link java.util.List}, each new element is placed in a new line.
     *
     * @param obj value to retrieve {@link java.lang.String} representation for
     * @return {@link java.lang.String} representation of an object
     */
    public static String format(Object obj) {
        return format(obj, '\n');
    }

    /**
     * Creates a {@link java.lang.String} representation of the given value. If given object
     * is a {@link java.util.List} a character put between next values can be specified.
     *
     * @param obj value to retrieve {@link java.lang.String} representation for
     * @param collJoinChar character to put between next elements of a collection; applicable if given object is a collection
     * @return {@link java.lang.String} representation of an object
     */
    public static String format(Object obj, char collJoinChar) {
        if (obj instanceof Collection) {
            return StringUtils.join((Collection) obj, collJoinChar);
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

    /**
     * Parses given value into {@link java.lang.Number} or one of the standard Java
     * types, extending Number.
     *
     * @param val value to parse
     * @param toClass fully qualified class name
     * @return parsed value
     */
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

    /**
     * Verifies whether given class has got a primitive equivalent.
     *
     * @param clazz class to verify
     * @return true if given wrapper class has got a primitive equivalent; false otherwise
     */
    public static boolean hasPrimitive(Class<?> clazz) {
        return PRIMITIVE_TYPE_MAP.containsKey(clazz);
    }

    /**
     * Verifies whether given class is primitive.
     *
     * @param clazz class to verify
     * @return true if given class is primitive; false otherwise
     */
    public static boolean isPrimitive(Class<?> clazz) {
        return PRIMITIVE_TYPE_MAP.containsValue(clazz);
    }

    /**
     * Verifies whether given class is primitive.
     *
     * @param className fully qualified class name to verify
     * @return true if given class is primitive; false otherwise
     */
    public static boolean isPrimitive(String className) {
        for (Object clazz : PRIMITIVE_TYPE_MAP.values()) {
            if (((Class) clazz).getName().equals(className)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Retrieves equivalent primitive class for the given wrapper class.
     *
     * @param clazz wrapper class
     * @return equivalent primitive class
     */
    public static Class<?> getPrimitive(Class<?> clazz) {
        return (Class<?>) PRIMITIVE_TYPE_MAP.get(clazz);
    }

    /**
     * Retrieves equivalent wrapper class for the given primitive type.
     *
     * @param clazz primitive type
     * @return equivalent wrapper class
     */
    public static Class<?> getWrapperForPrimitive(Class<?> clazz) {
        return (Class<?>) PRIMITIVE_TYPE_MAP.getKey(clazz);
    }

    public static String getClassNameForMdsType(Class<?> clazz) {
        Class<?> chosenClass = clazz;

        // box primitives
        if (clazz.isPrimitive() || byte[].class.equals(clazz)) {
            chosenClass = TypeHelper.getWrapperForPrimitive(clazz);
        }

        return chosenClass.getName();
    }

    /**
     * Parses given value to {@link org.motechproject.commons.api.Range}. If passed value is assignable
     * neither to range nor to map, it throws {@link java.lang.IllegalArgumentException}. If value is a map,
     * it should contain keys "min" and "max".
     *
     * @param object value to parse
     * @param typeClass fully qualified class name of the range values
     * @return {@link org.motechproject.commons.api.Range} value
     */
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

    /**
     * Parses given {@link java.util.Collection} class into {@link java.util.Set}. If given value is
     * not a subtype of {@link java.util.Collection} it throws {@link java.lang.IllegalArgumentException}.
     *
     * @param object value to parse
     * @param typeClass type of the values that should be placed in {@link java.util.Set}
     * @param classLoader optional class loader to use, loading type class
     * @return Set, parsed from the given value
     */
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

    /**
     * Returns concrete class, for the given collection interface or abstract class. If given class
     * is already concrete, it will return that class. Throws {@link java.lang.IllegalArgumentException}
     * if class of given name cannot be loaded.
     *
     * @param collectionClass fully qualified class name to find implementation for
     * @return concrete class
     */
    public static Class suggestCollectionImplementation(String collectionClass) {
        if (StringUtils.isBlank(collectionClass)) {
            return null;
        }

        try {
            return suggestCollectionImplementation((Class<? extends Collection>)
                    TypeHelper.class.getClassLoader().loadClass(collectionClass));
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Unable to load collection class", e);
        }
    }

    /**
     * Returns concrete class, for the given collection interface or abstract class. If given class
     * is already concrete, it will return that class.
     *
     * @param collectionClass collection class to find implementation for
     * @return concrete class
     */
    public static Class<? extends Collection> suggestCollectionImplementation(Class<? extends Collection> collectionClass) {
        Class<? extends Collection> collClass = null;

        if (collectionClass != null && (collectionClass.isInterface() || Modifier.isAbstract(collectionClass.getModifiers()))) {
            // for interface such as List (or abstract classes), go to the implementation map
            collClass = COLLECTION_IMPLEMENTATIONS.get(collectionClass.getName());
        } else if (collectionClass != null &&
                ConstructorUtils.getAccessibleConstructor(collectionClass, new Class[0]) == null) {
            // this is an implementation class, but no default constructor, datanucleus collection for example
            // go through interfaces and try to find an interface we have in the collection map
            for (Object intClassObj : ClassUtils.getAllInterfaces(collectionClass)) {
                Class intClass = (Class) intClassObj;
                collClass = COLLECTION_IMPLEMENTATIONS.get(intClass.getName());
                if (collClass != null) {
                    break;
                }
            }
        } else {
            // this is an implementation that can be instantiated, return it
            collClass = collectionClass;
        }
        return collClass == null ? ArrayList.class : collClass; // NOPMD - bug in PMD, objects to ArrayList.class here
    }

    public static Collection suggestAndCreateCollectionImplementation(Class<? extends Collection> collectionClass) {
        Class<? extends Collection> suggestedClass = suggestCollectionImplementation(collectionClass);
        try {
            return suggestedClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Unable to instantiate collection fo class " + suggestedClass.getName(), e);
        }
    }

    /**
     * Returns the provided value as a collection. If the value is a collection, it is simply returned.
     * If the value is a non-collection and non null object, it is returned as an arrayList containing one object.
     * IF the object is null, then an empty arraylist is returned.
     *
     * @param value the value to parse
     * @param <T> the type of the collection
     * @return the value as a collection
     */
    public static <T> Collection<T> asCollection(Object value) {
        if (value instanceof Collection) {
            return (Collection<T>) value;
        } else if (value != null) {
            return new ArrayList<>(Collections.singletonList((T) value));
        } else {
            return new ArrayList<>();
        }
    }

    // class names as strings below in order to avoid package cycles

    public static boolean isBaseEntity(String entitySuperClass) {
        return Object.class.getName().equalsIgnoreCase(entitySuperClass) ||
                "org.motechproject.mds.domain.MdsEntity".equalsIgnoreCase(entitySuperClass) ||
                "org.motechproject.mds.domain.MdsVersionedEntity".equalsIgnoreCase(entitySuperClass);
    }

    public static boolean isSubclassOfMdsEntity(String entitySuperClass) {
        return "org.motechproject.mds.domain.MdsEntity".equalsIgnoreCase(entitySuperClass)
                || "org.motechproject.mds.domain.MdsVersionedEntity".equalsIgnoreCase(entitySuperClass);
    }

    public static boolean isSubclassOfMdsVersionedEntity(String entitySuperClass) {
        return "org.motechproject.mds.domain.MdsVersionedEntity".equalsIgnoreCase(entitySuperClass);
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
