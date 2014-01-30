package org.motechproject.commons.api.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import org.apache.commons.io.IOUtils;
import org.joda.time.LocalDate;
import org.motechproject.commons.api.MotechException;
import org.motechproject.commons.api.model.MotechProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MotechJsonReader {
    private static final Logger LOG = LoggerFactory.getLogger(MotechJsonReader.class);

    private static Map<Type, Object> standardTypeAdapters = new HashMap<Type, Object>();

    static {
        standardTypeAdapters.put(Date.class, new DateDeserializer());
        standardTypeAdapters.put(LocalDate.class, new LocalDateDeserializer());
        standardTypeAdapters.put(MotechProperties.class, new MotechPropertiesDeserializer());
    }

    public Object readFromStream(InputStream stream, Type ofType) {
        try {
            String jsonText = IOUtils.toString(stream);
            return from(jsonText, ofType, standardTypeAdapters);
        } catch (IOException e) {
            throw new JsonIOException(e);
        }
    }

    public Object readFromFile(String classpathFile, Type ofType) {
        InputStream inputStream = getClass().getResourceAsStream(classpathFile);
        if (inputStream == null) {
            throw new MotechException("File not found in classpath: " + classpathFile);
        }
        try {
            String jsonText = IOUtils.toString(inputStream);
            return from(jsonText, ofType, standardTypeAdapters);
        } catch (IOException e) {
            throw new JsonIOException(e);
        }
    }

    public Object readFromString(String text, Type ofType) {
        return from(text, ofType, standardTypeAdapters);
    }

    public Object readFromString(String text, Type ofType, Map<Type, Object> typeAdapters) {
        return from(text, ofType, typeAdapters);
    }

    private Object from(String text, Type ofType, Map<Type, Object> typeAdapters) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        for (Map.Entry<Type, Object> entry : typeAdapters.entrySet()) {
            gsonBuilder.registerTypeAdapter(entry.getKey(), entry.getValue());
        }
        Gson gson = gsonBuilder.create();
        try {
            return gson.fromJson(text, ofType);
        } catch (JsonParseException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new JsonParseException(ex);
        }
    }

    private static class DateDeserializer implements JsonDeserializer<Date> {
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            JsonPrimitive asJsonPrimitive = json.getAsJsonPrimitive();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = format.parse(asJsonPrimitive.getAsString());
            } catch (ParseException e) {
                LOG.error(e.getMessage(), e);
            }
            return date;
        }
    }

    private static class LocalDateDeserializer implements JsonDeserializer<LocalDate> {
        @Override
        public LocalDate deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            return new LocalDate(jsonElement.getAsJsonPrimitive().getAsString());
        }
    }

    private static class MotechPropertiesDeserializer implements JsonDeserializer<MotechProperties> {
        @Override
        public MotechProperties deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            MotechProperties motechProperties = new MotechProperties();
            for (Map.Entry<String, JsonElement> property : jsonObject.entrySet()) {
                if (property.getValue().isJsonPrimitive()) {
                    motechProperties.put(property.getKey(), property.getValue().getAsString());
                } else if (property.getValue().isJsonNull()) {
                    motechProperties.put(property.getKey(), null);
                }
            }
            return motechProperties;
        }
    }
}
