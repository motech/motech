package org.motechproject.mds.docs.swagger.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.docs.swagger.model.ParameterType;

import java.lang.reflect.Type;

/**
 * Json serializer, deserializer fo the {@link org.motechproject.mds.docs.swagger.model.ParameterType} enum.
 */
public class ParameterTypeAdapter implements JsonDeserializer<ParameterType>, JsonSerializer<ParameterType> {

    @Override
    public ParameterType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return ParameterType.valueOf(StringUtils.upperCase(json.getAsString()));
    }

    @Override
    public JsonElement serialize(ParameterType src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString().toLowerCase());
    }
}
