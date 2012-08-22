package org.motechproject.openmrs.rest.util;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.motechproject.dao.MotechJsonReader;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public final class JsonUtils {

    private JsonUtils() { }

    private static final Logger LOGGER = Logger.getLogger(JsonUtils.class);
    private static final MotechJsonReader READER = new MotechJsonReader();
    private static Map<Type, Object> providedAdapters = new HashMap<Type, Object>();

    static {
        providedAdapters.put(Date.class, new OpenMrsDateAdapter());
    }

    public static Object readJson(String json, Type type) {
        return READER.readFromString(json, type, providedAdapters);
    }

    public static Object readJsonWithAdapters(String json, Type type, Map<Type, Object> adapters) {
        adapters.putAll(providedAdapters);
        return READER.readFromString(json, type, adapters);
    }

    private static class OpenMrsDateAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            Date date = null;
            try {
                date = DateUtil.parseOpenMrsDate(json.getAsString());
            } catch (ParseException e) {
                LOGGER.error("Failed to parse date: " + e.getMessage());
            }
            return date;
        }

        @Override
        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(DateUtil.formatToOpenMrsDate(src));
        }
    }
}
