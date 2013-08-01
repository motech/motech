package org.motechproject.tasks.json;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.motechproject.tasks.domain.DataSource;
import org.motechproject.tasks.domain.EventParameter;
import org.motechproject.tasks.domain.Filter;
import org.motechproject.tasks.domain.FilterSet;
import org.motechproject.tasks.domain.TaskConfig;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class TaskConfigDeserializerTest {

    @Test
    public void shouldDeserializeJsonWithFailWhenObjectNotFound() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory jsonFactory = new JsonFactory(mapper);

        StringWriter writer = new StringWriter();
        IOUtils.copy(this.getClass().getResourceAsStream("/task-config.json"), writer);

        JsonParser jsonParser = jsonFactory.createJsonParser(writer.toString());

        List<Filter> filters = new ArrayList<>();
        filters.add(new Filter(new EventParameter("mrs.observation.field.observationConceptName", "ObservationConceptName"), true, "equals", "pregnancy_urine_test"));
        filters.add(new Filter(new EventParameter("mrs.observation.field.value", "ObservationValue"), true, "equals", "positive"));

        TaskConfig expected = new TaskConfig()
                .add(new FilterSet(filters))
                .add(new DataSource("6899548ec91d9ad04e3aad9cf2aa19f9", 1L, "Person", "id", asList(new DataSource.Lookup("mrs.person.lookupField.id", "trigger.PatientId")), true));

        TaskConfig actual = new TaskConfigDeserializer().deserialize(jsonParser, null);

        Assert.assertEquals(expected.getDataSources().first().isFailIfDataNotFound(), actual.getDataSources().first().isFailIfDataNotFound());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldDeserializeJsonWithoutFailWhenObjectNotFound() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory jsonFactory = new JsonFactory(mapper);

        StringWriter writer = new StringWriter();
        IOUtils.copy(this.getClass().getResourceAsStream("/task-config-data-not-found.json"), writer);

        JsonParser jsonParser = jsonFactory.createJsonParser(writer.toString());

        List<Filter> filters = new ArrayList<>();
        filters.add(new Filter(new EventParameter("mrs.observation.field.observationConceptName", "ObservationConceptName"), true, "equals", "pregnancy_urine_test"));
        filters.add(new Filter(new EventParameter("mrs.observation.field.value", "ObservationValue"), true, "equals", "positive"));

        TaskConfig expected = new TaskConfig()
                .add(new FilterSet(filters))
                .add(new DataSource("6899548ec91d9ad04e3aad9cf2aa19f9", 1L, "Person", "id", asList(new DataSource.Lookup("mrs.person.lookupField.id", "trigger.PatientId")), false));

        TaskConfig actual = new TaskConfigDeserializer().deserialize(jsonParser, null);


        Assert.assertEquals(expected.getDataSources().first().isFailIfDataNotFound(), actual.getDataSources().first().isFailIfDataNotFound());
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldReturnEmptyConfig() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory jsonFactory = new JsonFactory(mapper);
        JsonParser jsonParser = jsonFactory.createJsonParser("{}");

        Assert.assertEquals(new TaskConfig(), new TaskConfigDeserializer().deserialize(jsonParser, null));
    }

}
