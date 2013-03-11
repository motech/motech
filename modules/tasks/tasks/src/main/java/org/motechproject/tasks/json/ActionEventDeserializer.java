package org.motechproject.tasks.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.ActionParameter;

import java.lang.reflect.Type;

import static org.apache.commons.lang.StringUtils.isBlank;

public class ActionEventDeserializer implements JsonDeserializer<ActionEvent> {
    public static final String DESCRIPTION_FIELD = "description";
    public static final String DISPLAY_NAME_FIELD = "displayName";
    public static final String SUBJECT_FIELD = "subject";
    public static final String SERVICE_INTERFACE_FIELD = "serviceInterface";
    public static final String SERVICE_METHOD_FIELD = "serviceMethod";
    public static final String ACTION_PARAMETERS_FIELD = "actionParameters";

    @Override
    public ActionEvent deserialize(JsonElement element, Type type, JsonDeserializationContext context) {
        ActionEvent actionEvent = null;

        if (element.isJsonObject()) {
            JsonObject jsonObject = element.getAsJsonObject();

            actionEvent = new ActionEvent();
            actionEvent.setSubject(getValue(jsonObject, SUBJECT_FIELD));
            actionEvent.setServiceInterface(getValue(jsonObject, SERVICE_INTERFACE_FIELD));
            actionEvent.setServiceMethod(getValue(jsonObject, SERVICE_METHOD_FIELD));

            if (!actionEvent.hasSubject() && !actionEvent.hasService()) {
                throw new JsonParseException("Channel action must contains subject and/or serviceInterface and serviceMethod");
            }

            actionEvent.setDescription(getValue(jsonObject, DESCRIPTION_FIELD));
            actionEvent.setDisplayName(getValue(jsonObject, DISPLAY_NAME_FIELD));

            if (jsonObject.has(ACTION_PARAMETERS_FIELD)) {
                JsonArray jsonArray = jsonObject.getAsJsonArray(ACTION_PARAMETERS_FIELD);

                for (int i = 0; i < jsonArray.size(); ++i) {
                    ActionParameter parameter = context.deserialize(jsonArray.get(i), ActionParameter.class);
                    boolean changeOrder = parameter.getOrder() == null;

                    actionEvent.addParameter(parameter, changeOrder);
                }
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
