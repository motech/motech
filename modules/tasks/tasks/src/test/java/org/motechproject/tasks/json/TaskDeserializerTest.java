package org.motechproject.tasks.json;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.motechproject.tasks.domain.DataSource;
import org.motechproject.tasks.domain.EventParameter;
import org.motechproject.tasks.domain.Filter;
import org.motechproject.tasks.domain.FilterSet;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActionInformation;
import org.motechproject.tasks.domain.TaskError;
import org.motechproject.tasks.domain.TaskEventInformation;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TaskDeserializerTest {
    private static final Task EXPECTED_TASK;
    private JsonParser jsonParser;

    static {
        List<Filter> filters = new ArrayList<>();
        filters.add(new Filter(new EventParameter("mrs.observation.field.observationConceptName", "ObservationConceptName"), true, "equals", "pregnancy_urine_test"));
        filters.add(new Filter(new EventParameter("mrs.observation.field.value", "ObservationValue"), true, "equals", "positive"));

        String name = "Pregnancy SMS";
        boolean enabled = false;

        Map<String, String> actionValues = new HashMap<>();
        actionValues.put("delivery_time", "12:00");
        actionValues.put("message", "Congratulations,    {{ad.6899548ec91d9ad04e3aad9cf2aa19f9.Person#1.firstName}}, your pregnancy test was positive. Please reply to schedule a clinic visit with your midwife.");
        actionValues.put("message", "{{trigger.PatientId}}");

        TaskActionInformation actionInformation = new TaskActionInformation(null, "sms.api", "motech-sms-api-bundle", "0.19.0.SNAPSHOT", "SendSMS", actionValues);
        TaskEventInformation triggerInformation = new TaskEventInformation(null, "mrs.name", "motech-mrs-api", "0.19.0.SNAPSHOT", "org.motechproject.mrs.api.Observaion.Created");

        Set<TaskError> validationErrors = new HashSet<>();
        validationErrors.add(new TaskError("validation.error.providerObjectLookupNotExist", "mrs.person.lookupField.id", "Person", "mrs.name"));

        String id = "6899548ec91d9ad04e3aad9cf2ad4e05";
        String revision = "0c023b58f6fa64f0a7896e9d5083f210";

        EXPECTED_TASK = new Task();
        EXPECTED_TASK.getTaskConfig().add(new FilterSet(filters));
        EXPECTED_TASK.getTaskConfig().add(new DataSource("6899548ec91d9ad04e3aad9cf2aa19f9", 1L, "Person", "id", asList(new DataSource.Lookup("mrs.person.lookupField.id", "trigger.PatientId")), false));
        EXPECTED_TASK.setName(name);
        EXPECTED_TASK.setEnabled(enabled);
        EXPECTED_TASK.setActions(asList(actionInformation));
        EXPECTED_TASK.setTrigger(triggerInformation);
        EXPECTED_TASK.setValidationErrors(validationErrors);

        EXPECTED_TASK.setId(id);
        EXPECTED_TASK.setRevision(revision);
    }

    public TaskDeserializerTest(String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory jsonFactory = new JsonFactory(mapper);

        StringWriter writer = new StringWriter();
        IOUtils.copy(this.getClass().getResourceAsStream(path), writer);

        jsonParser = jsonFactory.createJsonParser(writer.toString());
    }

    @Parameters
    public static Collection<Object[]> testParameters() {
        return asList(new Object[][]{
                {"/old-task-version.json"},
                {"/new-task-version.json"}
        });
    }

    @Test
    public void shouldDeserializeJson() throws IOException {
        Assert.assertEquals(EXPECTED_TASK, new TaskDeserializer().deserialize(jsonParser, null));
    }
}
