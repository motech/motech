package org.motechproject.openmrs.rest.model;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class IdentifierType {
    private String uuid;
    private String name;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static class IdentifierTypeSerializer implements JsonSerializer<IdentifierType> {
        @Override
        public JsonElement serialize(IdentifierType src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getUuid());
        }
    }
}
