package org.motechproject.mds.util;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.commons.collections.bidimap.UnmodifiableBidiMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.MethodUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.motechproject.commons.api.Range;
import org.motechproject.commons.date.model.Time;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A helper class for parsing and formatting mds supported types.
 */
public final class TypeHelper {

    private static final DateTimeFormatter DTF;
    private static final BidiMap PRIMITIVE_TYPE_MAP;

    static {
        DateTimeParser[] parsers = {
                DateTimeFormat.forPattern("yyyy-MM-dd HH:mm Z").getParser(),
                DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ").getParser(),
                DateTimeFormat.fullDateTime().getParser(),
                DateTimeFormat.fullDate().getParser(),
                DateTimeFormat.shortDateTime().getParser(),
                DateTimeFormat.shortDate().getParser()
        };
        DTF = new DateTimeFormatterBuilder().append(null, parsers).toFormatter();

        BidiMap bidiMap = new DualHashBidiMap();
        bidiMap.put(Integer.class, int.class);
        bidiMap.put(Long.class, long.class);
        bidiMap.put(Short.class, short.class);
        bidiMap.put(Byte.class, byte.class);
        bidiMap.put(Double.class, double.class);
        bidiMap.put(Float.class, float.class);
        bidiMap.put(Character.class, char.class);
        bidiMap.put(Boolean.class, boolean.class);

        PRIMITIVE_TYPE_MAP = UnmodifiableBidiMap.decorate(bidiMap);
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

    public static Object parse(Object val, String toClass, String genericType, ClassLoader classLoader) {
        Class<?> generic = null != genericType ? getClassDefinition(genericType, classLoader) : null;
        Class<?> toClassDefiniton = getClassDefinition(toClass, classLoader);

        if (val == null || toClassDefiniton.isAssignableFrom(val.getClass())) {
            if (List.class.isAssignableFrom(toClassDefiniton)) {
                return parseList((List) val, generic);
            }

            return val;
        } else if (val instanceof String) {
            return parseString((String) val, toClassDefiniton, generic);
        } else if (val instanceof Integer && Boolean.class.getName().equals(toClass)) {
            return parseIntToBool((Integer) val);
        } else if (bothNumbers(val, toClass)) {
            return parseNumber(val, toClass);
        } else {
            throw new IllegalArgumentException("Unable to parse " + val + " to " + toClass);
        }
    }

    public static Object parseString(String str, Class<?> toClass) {
        return parseString(str, toClass, null);
    }

    public static Object parseString(String str, String toClass) {
        return parseString(str, getClassDefinition(toClass), null);
    }

    public static Object parseString(String str, Class<?> toClass, Class<?> generic) {
        if (StringUtils.isBlank(str)) {
            return (String.class.isAssignableFrom(toClass)) ? "" : null;
        }

        if (DateTime.class.isAssignableFrom(toClass)) {
            return DTF.parseDateTime(str);
        } else if (Date.class.isAssignableFrom(toClass)) {
            return DTF.parseDateTime(str).toDate();
        }

        try {
            if (toClass.isEnum()) {
                Class<? extends Enum> enumClass = (Class<? extends Enum>) toClass;
                return Enum.valueOf(enumClass, str);
            }

            if (toClass.isAssignableFrom(List.class)) {
                return parserStringToList(str, generic);
            } else if (toClass.isAssignableFrom(Map.class)) {
                return parserStringToMap(str);
            } else {
                return MethodUtils.invokeStaticMethod(toClass, "valueOf", str);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Unable to parse value", e);
        }
    }

    private static Object parserStringToList(String str, Class<?> generic) {
        List<String> stringList = Arrays.asList(StringUtils.split(str, '\n'));
        List list = new ArrayList();

        if (null != generic && generic.isEnum()) {
            Class<? extends Enum> enumClass = (Class<? extends Enum>) generic;

            for (String string : stringList) {
                list.add(Enum.valueOf(enumClass, string));
            }
        } else {
            list.addAll(stringList);
        }

        return list;
    }

    private static Object parserStringToMap(String str) {
        Map map = new HashMap<>();

        String[] entries = StringUtils.split(str, '\n');
        for (String entry : entries) {
            if (!entry.isEmpty()) {
                String[] values = StringUtils.split(entry, ":", 2);
                map.put(values[0].trim(), values[1].trim());
            }
        }
        return map;
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
        if (obj instanceof List) {
            return StringUtils.join((List) obj, '\n');
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
        } else {
            throw new IllegalArgumentException("Unable to parse " + object + " to a Range");
        }
    }

    public static Set toSet(Object object, String typeClass) {
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
                    set.add(parse(value, typeClass));
                }
            }

            return set;
        } else {
            throw new IllegalArgumentException("Unable to parse " + object + " to a Set");
        }
    }

    private static boolean bothNumbers(Object val, String toClass) {
        return val instanceof Number && Number.class.isAssignableFrom(getClassDefinition(toClass));
    }

    private static Class getClassDefinition(String clazz) {
        return getClassDefinition(clazz, null);
    }

    @SuppressWarnings("PMD.PreserveStackTrace")
    private static Class getClassDefinition(String clazz, ClassLoader classLoader) {
        ClassLoader safeClassLoader = null == classLoader ? MDSClassLoader.getInstance() : classLoader;
        Class<?> definition;

        try {
            definition = TypeHelper.class.getClassLoader().loadClass(clazz);
        } catch (ClassNotFoundException e1) {
            try {
                definition = safeClassLoader.loadClass(clazz);
            } catch (ClassNotFoundException e2) {
                throw new IllegalArgumentException("Unable to load class " + clazz, e2);
            }
        }

        return definition;
    }

    private TypeHelper() {
    }
}
