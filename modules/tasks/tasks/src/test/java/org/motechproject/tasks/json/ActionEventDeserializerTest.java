package org.motechproject.tasks.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.tasks.domain.ParameterType;
import org.motechproject.tasks.service.ActionEventRequest;
import org.motechproject.tasks.service.ActionParameterRequest;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.tasks.json.ActionEventRequestDeserializer.ACTION_PARAMETERS_FIELD;
import static org.motechproject.tasks.json.ActionEventRequestDeserializer.DESCRIPTION_FIELD;
import static org.motechproject.tasks.json.ActionEventRequestDeserializer.DISPLAY_NAME_FIELD;
import static org.motechproject.tasks.json.ActionEventRequestDeserializer.SERVICE_INTERFACE_FIELD;
import static org.motechproject.tasks.json.ActionEventRequestDeserializer.SERVICE_METHOD_FIELD;
import static org.motechproject.tasks.json.ActionEventRequestDeserializer.SUBJECT_FIELD;

public class ActionEventDeserializerTest {
    private static final String PARAMETER_TYPE_KEY = "type";
    private static final String PARAMETER_KEY_KEY = "key";
    private static final String PARAMETER_ORDER_KEY = "order";

    private static final String EXTERNAL_KEY = "externalId";
    private static final String EXTERNAL_DISPLAY_NAME = "ExternalId";

    private static final String MOTECH_KEY = "motechId";
    private static final String MOTECH_DISPLAY_NAME = "MotechId";

    @Mock
    private JsonDeserializationContext context;

    private ActionEventRequestDeserializer deserializer = new ActionEventRequestDeserializer();

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldReturnNullIfJsonElementIsNotJsonObject() {
        assertNull(deserializer.deserialize(new JsonPrimitive(true), null, null));
    }

    @Test(expected = JsonParseException.class)
    public void shouldThrowExceptionWhenSubjectAndServiceInfoAreNotExist() {
        deserializer.deserialize(new JsonObject(), null, null);
    }

    @Test
    public void shouldDeserializeJsonWithSubject() {
        ActionEventRequest expected = getExpected(true, false);
        JsonObject object = createJsonObject(expected);

        ActionEventRequest actual = deserializer.deserialize(object, null, null);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldDeserializeJsonWithServiceInfo() {
        ActionEventRequest expected = getExpected(false, true);
        JsonObject object = createJsonObject(expected);

        ActionEventRequest actual = deserializer.deserialize(object, null, null);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldDeserializeJsonWithActionParameters() {
        ActionEventRequest expected = new ActionEventRequest(DISPLAY_NAME_FIELD, SUBJECT_FIELD, DESCRIPTION_FIELD, SERVICE_INTERFACE_FIELD, SERVICE_METHOD_FIELD, getActionParameters());

        JsonObject object = createJsonObject(expected);

        ActionParameterRequest actionParameterRequestWithoutOrder = new ActionParameterRequest("Witout order", "withoutOrder");
        expected.addParameter(actionParameterRequestWithoutOrder, true);

        object.getAsJsonArray(ACTION_PARAMETERS_FIELD).add(createParameter(actionParameterRequestWithoutOrder));

        ActionEventRequest actual = deserializer.deserialize(object, null, context);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldDeserializeActionParameters() throws IOException {
        String json = "{\"displayName\":\"externalId\",\"type\":\"UNICODE\",\"key\":\"ExternalId\"}";

        TypeToken<ActionParameterRequest> typeToken = new TypeToken<ActionParameterRequest>() {
        };

        ActionParameterRequest actionParameterRequest = (ActionParameterRequest) new MotechJsonReader().readFromString(json, typeToken.getType());
        assertNotNull(actionParameterRequest);
        assertThat(actionParameterRequest.getDisplayName(), is("externalId"));
        assertThat(actionParameterRequest.getOrder(), nullValue());
    }

    @Test
    public void shouldDeserializeActionEvent() throws IOException {
        String json = "{description=\"description\", displayName=\"displayName\", subject=\"subject\", serviceInterface=\"serviceInterface\", serviceMethod=\"serviceMethod\"," +
                "actionParameters=[{\"displayName\":\"externalId\",\"type\":\"UNICODE\",\"key\":\"ExternalId\",\"order\":3},{\"displayName\":\"motechId\",\"type\":\"INTEGER\",\"key\":\"MotechId\"}]}";

        TypeToken<ActionEventRequest> typeToken = new TypeToken<ActionEventRequest>() {
        };
        Map<Type, Object> typeAdapters = new HashMap<>();
        typeAdapters.put(ActionEventRequest.class, new ActionEventRequestDeserializer());

        ActionEventRequest actionEventRequest = (ActionEventRequest) new MotechJsonReader().readFromString(json, typeToken.getType(), typeAdapters);

        assertNotNull(actionEventRequest);
        assertThat(actionEventRequest.getDisplayName(), is("displayName"));
        assertThat(actionEventRequest.getActionParameters().size(), is(2));
        assertThat(actionEventRequest.getActionParameters().iterator().next().getOrder(), is(3));
    }

    @Test
    public void shouldNotSetEmptyActionParameterSet() {
        ActionEventRequest expected = getExpected(false, true);
        JsonObject object = createJsonObject(expected);

        object.add(ACTION_PARAMETERS_FIELD, new JsonArray());

        ActionEventRequest actual = deserializer.deserialize(object, null, null);

        assertEquals(expected, actual);
    }

    private ActionEventRequest getExpected(boolean subject, boolean service) {
        String subjectFieldValue = null;
        String serviceInterfaceFieldValue = null;
        String serviceMethodFieldValue = null;

        if (subject) {
            subjectFieldValue = SUBJECT_FIELD;
        }

        if (service) {
            serviceInterfaceFieldValue = SERVICE_INTERFACE_FIELD;
            serviceMethodFieldValue = SERVICE_METHOD_FIELD;
        }

        return new ActionEventRequest(DISPLAY_NAME_FIELD, subjectFieldValue, DESCRIPTION_FIELD, serviceInterfaceFieldValue, serviceMethodFieldValue, new TreeSet<ActionParameterRequest>());
    }

    private SortedSet<ActionParameterRequest> getActionParameters() {
        SortedSet<ActionParameterRequest> parameters = new TreeSet<>();
        parameters.add(new ActionParameterRequest(EXTERNAL_DISPLAY_NAME, EXTERNAL_KEY, 0));
        parameters.add(new ActionParameterRequest(MOTECH_DISPLAY_NAME, MOTECH_KEY, 1, ParameterType.INTEGER.getValue()));

        return parameters;
    }

    private JsonObject createJsonObject(ActionEventRequest actionEventRequest) {
        JsonObject object = new JsonObject();

        object.addProperty(DESCRIPTION_FIELD, actionEventRequest.getDescription());
        object.addProperty(DISPLAY_NAME_FIELD, actionEventRequest.getDisplayName());

        if (actionEventRequest.hasSubject()) {
            object.addProperty(SUBJECT_FIELD, actionEventRequest.getSubject());
        }

        if (actionEventRequest.hasService()) {
            object.addProperty(SERVICE_INTERFACE_FIELD, actionEventRequest.getServiceInterface());
            object.addProperty(SERVICE_METHOD_FIELD, actionEventRequest.getServiceMethod());
        }

        SortedSet<ActionParameterRequest> parameters = actionEventRequest.getActionParameters();

        if (parameters != null && !parameters.isEmpty()) {
            JsonArray array = new JsonArray();

            for (ActionParameterRequest parameter : parameters) {
                array.add(createParameter(parameter));
            }

            object.add(ACTION_PARAMETERS_FIELD, array);
        }

        return object;
    }

    private JsonObject createParameter(ActionParameterRequest parameter) {
        JsonObject param = new JsonObject();

        param.addProperty(DISPLAY_NAME_FIELD, parameter.getDisplayName());
        param.addProperty(PARAMETER_TYPE_KEY, parameter.getType());
        param.addProperty(PARAMETER_KEY_KEY, parameter.getKey());
        param.addProperty(PARAMETER_ORDER_KEY, parameter.getOrder());

        when(context.deserialize(param, ActionParameterRequest.class)).thenReturn(parameter);

        return param;
    }
}
