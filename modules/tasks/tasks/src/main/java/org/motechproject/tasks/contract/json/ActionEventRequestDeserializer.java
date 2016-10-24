package org.motechproject.tasks.contract.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.motechproject.tasks.contract.ActionEventRequest;
import org.motechproject.tasks.contract.builder.ActionEventRequestBuilder;
import org.motechproject.tasks.contract.ActionParameterRequest;

import java.lang.reflect.Type;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * {@code JsonDeserializer} for the {@code ActionEventRequest} class.
 */
public class ActionEventRequestDeserializer implements JsonDeserializer<ActionEventRequest> {

    public static final String DESCRIPTION_FIELD = "description";
    public static final String NAME_FIELD = "name";
    public static final String DISPLAY_NAME_FIELD = "displayName";
    public static final String SUBJECT_FIELD = "subject";
    public static final String SERVICE_INTERFACE_FIELD = "serviceInterface";
    public static final String SERVICE_METHOD_FIELD = "serviceMethod";
    public static final String SERVICE_METHOD_CALL_MANNER_FIELD = "serviceMethodCallManner";
    public static final String ACTION_PARAMETERS_FIELD = "actionParameters";
    public static final String POST_ACTION_PARAMETERS_FIELD = "postActionParameters";

    @Override
    public ActionEventRequest deserialize(JsonElement element, Type type, JsonDeserializationContext context) {
        ActionEventRequest actionEvent = null;

        if (element.isJsonObject()) {
            JsonObject jsonObject = element.getAsJsonObject();

            actionEvent = new ActionEventRequestBuilder().setDisplayName(getValue(jsonObject, DISPLAY_NAME_FIELD))
                    .setSubject(getValue(jsonObject, SUBJECT_FIELD)).setDescription(getValue(jsonObject, DESCRIPTION_FIELD))
                    .setServiceInterface(getValue(jsonObject, SERVICE_INTERFACE_FIELD))
                    .setServiceMethod(getValue(jsonObject, SERVICE_METHOD_FIELD))
                    .setServiceMethodCallManner(getValue(jsonObject, SERVICE_METHOD_CALL_MANNER_FIELD))
                    .setName(getValue(jsonObject, NAME_FIELD)).createActionEventRequest();

            if (jsonObject.has(ACTION_PARAMETERS_FIELD)) {
                JsonArray jsonArray = jsonObject.getAsJsonArray(ACTION_PARAMETERS_FIELD);

                for (int i = 0; i < jsonArray.size(); ++i) {
                    ActionParameterRequest parameter = context.deserialize(jsonArray.get(i), ActionParameterRequest.class);
                    boolean changeOrder = parameter.getOrder() == null;

                    actionEvent.addParameter(parameter, changeOrder);
                }
            }

            if (jsonObject.has(POST_ACTION_PARAMETERS_FIELD)) {
                JsonArray jsonArray = jsonObject.getAsJsonArray(POST_ACTION_PARAMETERS_FIELD);

                for (int i = 0; i < jsonArray.size(); ++i) {
                    ActionParameterRequest parameter = context.deserialize(jsonArray.get(i), ActionParameterRequest.class);
                    boolean changeOrder = parameter.getOrder() == null;

                    actionEvent.addPostActionParameter(parameter, changeOrder);
                }
            }

            if (!actionEvent.isValid()) {
                throw new JsonParseException("Channel action must contain subject and/or serviceInterface and serviceMethod");
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
