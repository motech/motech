package org.motechproject.mds.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.MethodUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.motechproject.commons.date.model.Time;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * A helper class for parsing and formatting mds supported types.
 */
public final class TypeHelper {

    private static final  DateTimeFormatter DTF;

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
    }

    public static Object parse(Object val, Class<?> toClass) {
        return parse(val, toClass.getName());
    }

    public static Object parse(Object val, String toClass) {
        if (val == null || val.getClass().getName().equals(toClass)) {
            return val;
        } else if (val instanceof String) {
            return parseString((String) val, toClass);
        } else if (val instanceof Integer && Boolean.class.getName().equals(toClass)) {
            return parseIntToBool((Integer) val);
        } else {
            throw new IllegalArgumentException("Unable to parse " + val + " to " + toClass);
        }
    }

    public static Object parseString(String str, Class<?> toClass) {
        return parseString(str, toClass.getName());
    }

    public static Object parseString(String str, String toClass) {
        if (StringUtils.isBlank(str)) {
            return (String.class.getName().equals(toClass)) ? "" : null;
        }

        if (DateTime.class.getName().equals(toClass)) {
            return DTF.parseDateTime(str);
        } else if (Date.class.getName().equals(toClass)) {
            return DTF.parseDateTime(str).toDate();
        }

        try {
            Class<?> clazz = TypeHelper.class.getClassLoader().loadClass(toClass);

            if (clazz.isAssignableFrom(List.class)) {
                List list = new ArrayList();

                list.addAll(Arrays.asList(StringUtils.split(str, '\n')));

                return list;
            } else {
                return MethodUtils.invokeStaticMethod(clazz, "valueOf", str);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Unable to parse value", e);
        }
    }


    public static boolean parseIntToBool(Integer val) {
        return val != null && val > 0;
    }

    public static String format(Object obj) {
        if (obj instanceof List) {
            return StringUtils.join((List) obj, '\n');
        } else if (obj instanceof Time) {
            return ((Time) obj).timeStr();
        } else if (obj instanceof Date) {
            return new DateTime(((Date) obj).getTime()).toString();
        } else {
            return (obj == null) ? "" : obj.toString();
        }
    }

    private TypeHelper() {
    }
}
