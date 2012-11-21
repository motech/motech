package org.motechproject.commcare.parser;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.commons.api.json.MotechJsonReader;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class FormAdapter {

    private static final MotechJsonReader READER = new MotechJsonReader();
    private static Map<Type, Object> providedAdapters = new HashMap<Type, Object>();

    static {
        providedAdapters.put(FormValueElement.class, new JsonFormAdapter());
    }

    public static CommcareForm readJson(String json) {
        Type type = new TypeToken<CommcareForm>() { } .getType();
        return (CommcareForm) READER.readFromString(json, type, providedAdapters);
    }


    private static class JsonFormAdapter implements JsonDeserializer<FormValueElement> {
        public FormValueElement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {

            FormValueElement formElement = recursivelyParse("form", json);

            return formElement;
        }
    }

    public static FormValueElement recursivelyParse(String key, JsonElement jsonElement) {
        FormValueElement formElement = new FormValueElement();
        formElement.setElementName(key);

        if (jsonElement.isJsonPrimitive()) {
            JsonPrimitive primitive = jsonElement.getAsJsonPrimitive();

            FormValueElement subFormElement = new FormValueElement();
            subFormElement.setElementName(key);
            subFormElement.setValue(primitive.getAsString());
            return subFormElement;

        } else if (jsonElement.isJsonArray()) {
            JsonArray array = jsonElement.getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                FormValueElement element = recursivelyParse(key, array.get(i));
                formElement.addFormValueElement(key, element);
            }
            return formElement;
        } else if (jsonElement.isJsonObject()) {
            JsonObject object = jsonElement.getAsJsonObject();
            for (Entry<String, JsonElement> entry : object.entrySet()) {
                if (entry.getValue().isJsonPrimitive()) {
                    JsonPrimitive value = entry.getValue().getAsJsonPrimitive();
                    if (isAttribute(entry.getKey())) {
                        formElement.addAttribute(entry.getKey().substring(1), value.getAsString());
                    } else if (isValue(entry.getKey())) {
                        formElement.setValue(value.getAsString());
                    } else {
                        FormValueElement element = recursivelyParse(entry.getKey(), entry.getValue());
                        formElement.addFormValueElement(element.getElementName(), element);
                    }
                } else {
                    FormValueElement element = recursivelyParse(entry.getKey(), entry.getValue());
                    formElement.addFormValueElement(element.getElementName(), element);
                }

            }
        }
        return formElement;
    }

    public static boolean isAttribute(String key) {
        if (key.startsWith("@")) {
            return true;
        }
        return false;
    }

    public static boolean isValue(String key) {
        if (key.startsWith("#")) {
            return true;
        }
        return false;
    }

    private FormAdapter() { }
}
