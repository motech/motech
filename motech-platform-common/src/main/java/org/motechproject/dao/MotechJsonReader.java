package org.motechproject.dao;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

@Component
public class MotechJsonReader {
    public Object readFromFile(String classpathFile, Type type) {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(classpathFile);
        try {
            String jsonText = IOUtils.toString(inputStream);;
            return new Gson().fromJson(jsonText, type);
        } catch (IOException e) {
            throw new JsonIOException(e);
        }
    }
}
