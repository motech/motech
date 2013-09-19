package org.motechproject.tasks.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.motechproject.tasks.contract.ActionEventRequest;
import org.motechproject.tasks.contract.ActionParameterRequest;

import java.lang.reflect.Type;

import static org.apache.commons.lang.StringUtils.isBlank;

public class ActionEventRequestDeserializer implements JsonDeserializer<ActionEventRequest> {
    public static final String DESCRIPTION_FIELD = "description";
    public static final String DISPLAY_NAME_FIELD = "displayName";
    public static final String SUBJECT_FIELD = "subject";
    public static final String SERVICE_INTERFACE_FIELD = "serviceInterface";
    public static final String SERVICE_METHOD_FIELD = "serviceMethod";
    public static final String ACTION_PARAMETERS_FIELD = "actionParameters";

    @Override
    public ActionEventRequest deserialize(JsonElement element, Type type, JsonDeserializationContext context) {
        ActionEventRequest actionEvent = null;

        if (element.isJsonObject()) {
            JsonObject jsonObject = element.getAsJsonObject();

            actionEvent = new ActionEventRequest(getValue(jsonObject, DISPLAY_NAME_FIELD), getValue(jsonObject, SUBJECT_FIELD), getValue(jsonObject, DESCRIPTION_FIELD), getValue(jsonObject, SERVICE_INTERFACE_FIELD), getValue(jsonObject, SERVICE_METHOD_FIELD));

            if (jsonObject.has(ACTION_PARAMETERS_FIELD)) {
                JsonArray jsonArray = jsonObject.getAsJsonArray(ACTION_PARAMETERS_FIELD);

                for (int i = 0; i < jsonArray.size(); ++i) {
                    ActionParameterRequest parameter = context.deserialize(jsonArray.get(i), ActionParameterRequest.class);
                    boolean changeOrder = parameter.getOrder() == null;

                    actionEvent.addParameter(parameter, changeOrder);
                }
            }

            if (!actionEvent.isValid()) {
                throw new JsonParseException("Channel action must contains subject and/or serviceInterface and serviceMethod");
            }
        }

        return actionEvent;
    }

    private String getValue(JsonObject jsonObject, String key) {
        String value = null;

        if (jsonObject.has(key)) {
            value = jsonObject.get(key).getAsString();
        }

        if (isBlank(value)) {
            value = null;
        }

        return value;
    }

}
