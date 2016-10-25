package org.motechproject.tasks.json;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.motechproject.tasks.domain.mds.task.DataSource;
import org.motechproject.tasks.domain.mds.task.Filter;
import org.motechproject.tasks.domain.mds.task.FilterSet;
import org.motechproject.tasks.domain.mds.task.Lookup;
import org.motechproject.tasks.domain.mds.task.Task;
import org.motechproject.tasks.domain.mds.task.TaskActionInformation;
import org.motechproject.tasks.domain.mds.task.TaskError;
import org.motechproject.tasks.domain.mds.task.TaskTriggerInformation;

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
import static org.motechproject.tasks.domain.enums.ParameterType.UNICODE;

@RunWith(Parameterized.class)
public class TaskDeserializerTest {
    private static final Task EXPECTED_TASK;
    private JsonParser jsonParser;

    static {
        List<Filter> filters = new ArrayList<>();
        filters.add(new Filter("Concept name (Trigger)", "trigger.ObservationConceptName", UNICODE, true, "equals", "pregnancy_urine_test"));
        filters.add(new Filter("Observation value (Trigger)", "trigger.ObservationValue", UNICODE, true, "equals", "positive"));

        String name = "Pregnancy SMS";

        Map<String, String> actionValues = new HashMap<>();
        actionValues.put("delivery_time", "12:00");
        actionValues.put("message", "Congratulations,    {{ad.ProviderName.Person#1.firstName}}, your pregnancy test was positive. Please reply to schedule a clinic visit with your midwife.");
        actionValues.put("message", "{{trigger.PatientId}}");

        TaskActionInformation actionInformation = new TaskActionInformation(null, "sms.api", "motech-sms-api-bundle", "0.19.0.SNAPSHOT", "SendSMS", actionValues);
        TaskTriggerInformation triggerInformation = new TaskTriggerInformation(null, "mrs.name", "motech-mrs-api", "0.19.0.SNAPSHOT", "org.motechproject.mrs.api.Observaion.Created", null);

        Set<TaskError> validationErrors = new HashSet<>();
        validationErrors.add(new TaskError("task.validation.error.providerObjectLookupNotExist", "mrs.person.lookupField.id", "Person", "mrs.name"));

        EXPECTED_TASK = new Task();
        EXPECTED_TASK.getTaskConfig().add(new FilterSet(filters));
        EXPECTED_TASK.getTaskConfig().add(new DataSource("ProviderName", 6899548L, 1L, "Person", "id", "specifiedName", asList(new Lookup("mrs.person.lookupField.id", "trigger.PatientId")), false));
        EXPECTED_TASK.setName(name);
        EXPECTED_TASK.setEnabled(false);
        EXPECTED_TASK.setHasRegisteredChannel(true);
        EXPECTED_TASK.setActions(asList(actionInformation));
        EXPECTED_TASK.setTrigger(triggerInformation);
        EXPECTED_TASK.setValidationErrors(validationErrors);
        EXPECTED_TASK.setNumberOfRetries(3);
        EXPECTED_TASK.setRetryIntervalInMilliseconds(1000);
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
                {"/new-task-version.json"}
        });
    }

    @Test
    public void shouldDeserializeJson() throws IOException {
        Assert.assertEquals(EXPECTED_TASK, new TaskDeserializer().deserialize(jsonParser, null));
    }
}
