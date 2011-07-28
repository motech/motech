package org.motechproject.dao;

import com.google.gson.*;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class MotechJsonReader {
    public Object readFromFile(String classpathFile, Type type) {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(classpathFile);
        try {
            String jsonText = IOUtils.toString(inputStream);
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Date.class, new DateDeserializer());
            Gson gson = gsonBuilder.create();
            return gson.fromJson(jsonText, type);
        } catch (IOException e) {
            throw new JsonIOException(e);
        }
    }

    private class DateDeserializer implements JsonDeserializer<Date> {
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonPrimitive asJsonPrimitive = json.getAsJsonPrimitive();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = format.parse(asJsonPrimitive.getAsString());
            } catch (ParseException e) {
                // TODO
            }
            return date;
        }
    }
}
