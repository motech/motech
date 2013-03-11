package org.motechproject.tasks.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.ActionParameter;
import org.motechproject.tasks.domain.ParameterType;

import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.tasks.json.ActionEventDeserializer.ACTION_PARAMETERS_FIELD;
import static org.motechproject.tasks.json.ActionEventDeserializer.DESCRIPTION_FIELD;
import static org.motechproject.tasks.json.ActionEventDeserializer.DISPLAY_NAME_FIELD;
import static org.motechproject.tasks.json.ActionEventDeserializer.SERVICE_INTERFACE_FIELD;
import static org.motechproject.tasks.json.ActionEventDeserializer.SERVICE_METHOD_FIELD;
import static org.motechproject.tasks.json.ActionEventDeserializer.SUBJECT_FIELD;

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

    private ActionEventDeserializer deserializer = new ActionEventDeserializer();

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
        ActionEvent expected = getExpected(true, false);
        JsonObject object = createJsonObject(expected);

        ActionEvent actual = deserializer.deserialize(object, null, null);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldDeserializeJsonWithServiceInfo() {
        ActionEvent expected = getExpected(false, true);
        JsonObject object = createJsonObject(expected);

        ActionEvent actual = deserializer.deserialize(object, null, null);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldDeserializeJsonWithActionParameters() {
        ActionEvent expected = getExpected(true, true);
        expected.setActionParameters(getActionParameters());

        JsonObject object = createJsonObject(expected);

        ActionParameter extra = new ActionParameter("Witout order", "withoutOrder");
        object.getAsJsonArray(ACTION_PARAMETERS_FIELD).add(createParameter(extra));

        ActionEvent actual = deserializer.deserialize(object, null, context);
        expected.addParameter(extra, true);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldNotSetEmptyActionParameterSet() {
        ActionEvent expected = getExpected(false, true);
        JsonObject object = createJsonObject(expected);

        object.add(ACTION_PARAMETERS_FIELD, new JsonArray());

        ActionEvent actual = deserializer.deserialize(object, null, null);

        assertEquals(expected, actual);
    }

    private ActionEvent getExpected(boolean subject, boolean service) {
        ActionEvent actionEvent = new ActionEvent();
        actionEvent.setDescription(DESCRIPTION_FIELD);
        actionEvent.setDisplayName(DISPLAY_NAME_FIELD);

        if (subject) {
            actionEvent.setSubject(SUBJECT_FIELD);
        }

        if (service) {
            actionEvent.setServiceInterface(SERVICE_INTERFACE_FIELD);
            actionEvent.setServiceMethod(SERVICE_METHOD_FIELD);
        }

        return actionEvent;
    }

    private SortedSet<ActionParameter> getActionParameters() {
        SortedSet<ActionParameter> parameters = new TreeSet<>();
        parameters.add(new ActionParameter(EXTERNAL_DISPLAY_NAME, EXTERNAL_KEY, 0));
        parameters.add(new ActionParameter(MOTECH_DISPLAY_NAME, MOTECH_KEY, ParameterType.INTEGER, 1));

        return parameters;
    }

    private JsonObject createJsonObject(ActionEvent actionEvent) {
        JsonObject object = new JsonObject();

        object.addProperty(DESCRIPTION_FIELD, actionEvent.getDescription());
        object.addProperty(DISPLAY_NAME_FIELD, actionEvent.getDisplayName());

        if (actionEvent.hasSubject()) {
            object.addProperty(SUBJECT_FIELD, actionEvent.getSubject());
        }

        if (actionEvent.hasService()) {
            object.addProperty(SERVICE_INTERFACE_FIELD, actionEvent.getServiceInterface());
            object.addProperty(SERVICE_METHOD_FIELD, actionEvent.getServiceMethod());
        }

        SortedSet<ActionParameter> parameters = actionEvent.getActionParameters();

        if (parameters != null && !parameters.isEmpty()) {
            JsonArray array = new JsonArray();

            for (ActionParameter parameter : parameters) {
                array.add(createParameter(parameter));
            }

            object.add(ACTION_PARAMETERS_FIELD, array);
        }

        return object;
    }

    private JsonObject createParameter(ActionParameter parameter) {
        JsonObject param = new JsonObject();

        param.addProperty(DISPLAY_NAME_FIELD, parameter.getDisplayName());
        param.addProperty(PARAMETER_TYPE_KEY, parameter.getType().getValue());
        param.addProperty(PARAMETER_KEY_KEY, parameter.getKey());
        param.addProperty(PARAMETER_ORDER_KEY, parameter.getOrder());

        when(context.deserialize(param, ActionParameter.class)).thenReturn(parameter);

        return param;
    }
}
