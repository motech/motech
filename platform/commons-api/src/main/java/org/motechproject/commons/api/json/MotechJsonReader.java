package org.motechproject.commons.api.json;

import com.google.gson.FieldNamingStrategy;
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

/**
 * Class responsible for creating objects from json. It can use {@code InputStream}, {@code String} or file classpath.
 */
public class MotechJsonReader {
    private static final Logger LOG = LoggerFactory.getLogger(MotechJsonReader.class);

    private static Map<Type, Object> standardTypeAdapters = new HashMap<Type, Object>();

    private FieldNamingStrategy fieldNamingStrategy;

    /**
     * Static initialisation block.
     */
    static {
        standardTypeAdapters.put(Date.class, new DateDeserializer());
        standardTypeAdapters.put(LocalDate.class, new LocalDateDeserializer());
        standardTypeAdapters.put(MotechProperties.class, new MotechPropertiesDeserializer());
    }

    /**
     * Constructor.
     */
    public MotechJsonReader() {
    }

    /**
     * Constructor.
     *
     * @param fieldNamingStrategy  the field naming strategy to be used when deserializing object
     */
    public MotechJsonReader(FieldNamingStrategy fieldNamingStrategy) {
        this.fieldNamingStrategy = fieldNamingStrategy;
    }

    /**
     * Creates object of type {@code ofType} from input stream.
     *
     * @param stream  the stream to deserialize
     * @param ofType  the type of created object
     * @return object of type {@code ofType}
     */
    public Object readFromStream(InputStream stream, Type ofType) {
        try {
            String jsonText = IOUtils.toString(stream);
            return from(jsonText, ofType, standardTypeAdapters, false);
        } catch (IOException e) {
            throw new JsonIOException(e);
        }
    }

    /**
     * Creates object of type {@code ofType} from input stream. Will only deserialize fields with {@code Expose} annotation.
     *
     * @param stream  the stream to deserialize
     * @param ofType  the type of created object
     * @return object of type {@code ofType}
     */
    public Object readFromStreamOnlyExposeAnnotations(InputStream stream, Type ofType) {
        try {
            String jsonText = IOUtils.toString(stream);
            return from(jsonText, ofType, standardTypeAdapters, true);
        } catch (IOException e) {
            throw new JsonIOException(e);
        }
    }

    /**
     * Creates object of type {@code ofType} from file under given classpath.
     *
     * @param classpathFile  the file to deserialize
     * @param ofType  the type of created object
     * @return object of type {@code ofType}
     */
    public Object readFromFile(String classpathFile, Type ofType) {
        InputStream inputStream = getClass().getResourceAsStream(classpathFile);
        if (inputStream == null) {
            throw new MotechException("File not found in classpath: " + classpathFile);
        }
        try {
            String jsonText = IOUtils.toString(inputStream);
            return from(jsonText, ofType, standardTypeAdapters, false);
        } catch (IOException e) {
            throw new JsonIOException(e);
        }
    }

    /**
     * Creates object of type {@code ofType} from given {@code String}.
     *
     * @param text  the {@code String} to deserialize
     * @param ofType  the type of created object
     * @return object of type {@code ofType}
     */
    public Object readFromString(String text, Type ofType) {
        return from(text, ofType, standardTypeAdapters, false);
    }

    /**
     * Creates object of type {@code ofType} from given {@code String}. Will only deserialize fields with {@code Expose} annotation.
     *
     * @param text  the {@code String} to deserialize
     * @param ofType  the type of created object
     * @return object of type {@code ofType}
     */
    public Object readFromStringOnlyExposeAnnotations(String text, Type ofType) {
        return from(text, ofType, standardTypeAdapters, true);
    }

    /**
     * Creates object of type {@code ofType} from given {@code String} using user-specified adapters.
     *
     * @param text  the {@code String} to deserialize
     * @param ofType  the type of created object
     * @param typeAdapters  custom adapters to use for deserialization
     * @return object of type {@code ofType}
     */
    public Object readFromString(String text, Type ofType, Map<Type, Object> typeAdapters) {
        return from(text, ofType, typeAdapters, false);
    }

    private Object from(String text, Type ofType, Map<Type, Object> typeAdapters, boolean onlyExposeAnnotations) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        for (Map.Entry<Type, Object> entry : typeAdapters.entrySet()) {
            gsonBuilder.registerTypeAdapter(entry.getKey(), entry.getValue());
        }

        if (fieldNamingStrategy != null) {
            gsonBuilder.setFieldNamingStrategy(fieldNamingStrategy);
        }

        if (onlyExposeAnnotations) {
            gsonBuilder.excludeFieldsWithoutExposeAnnotation();
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
