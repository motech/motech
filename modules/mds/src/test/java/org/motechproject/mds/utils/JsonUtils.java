package org.motechproject.mds.utils;


import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Ignore;

import java.io.IOException;
import java.io.StringWriter;

@Ignore
public class JsonUtils {
    public static String toJson(Object objectToSerialize) {
        ObjectMapper mapper = new ObjectMapper();
        StringWriter stringWriter = new StringWriter();
        try {
            mapper.writeValue(stringWriter, objectToSerialize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringWriter.toString();
    }

    public static <T> T fromJson(String jsonString, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        T serializedObject = null;
        try {
            serializedObject = mapper.readValue(jsonString, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serializedObject;
    }
}
