package org.motechproject.tasks.json;

import org.apache.commons.beanutils.BeanUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;
import org.joda.time.DateTime;
import org.motechproject.tasks.domain.mds.task.Task;
import org.motechproject.tasks.domain.mds.task.TaskActionInformation;
import org.motechproject.tasks.domain.mds.task.TaskConfig;
import org.motechproject.tasks.domain.mds.task.TaskError;
import org.motechproject.tasks.domain.mds.task.TaskTriggerInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

/**
 * {@code JsonDeserializer} for the {@code Task} class.
 */
public class TaskDeserializer extends JsonDeserializer<Task> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskDeserializer.class);

    private ObjectMapper mapper;
    private Task task;
    private JsonNode jsonNode;

    @Override
    public Task deserialize(JsonParser jsonParser,
                            DeserializationContext deserializationContext) throws IOException {
        jsonNode = jsonParser.readValueAsTree();
        task = new Task();

        ObjectCodec codec = jsonParser.getCodec();

        if (codec instanceof ObjectMapper) {
            mapper = (ObjectMapper) codec;
        } else {
            mapper = new ObjectMapper();
        }

        TypeFactory typeFactory = mapper.getTypeFactory();
        JavaType stringType = typeFactory.constructType(String.class);
        JavaType longType = typeFactory.constructType(Long.class);
        JavaType intType = typeFactory.constructType(Integer.class);
        JavaType dateTime = typeFactory.constructType(DateTime.class);

        setProperty("id", longType);
        setProperty("owner", stringType);
        setProperty("creator", stringType);
        setProperty("creationDate", dateTime);
        setProperty("modificationDate", dateTime);
        setProperty("modifiedBy", stringType);
        setProperty("description", stringType);
        setProperty("name", stringType);
        setProperty("enabled", stringType);
        setProperty("hasRegisteredChannel", stringType);
        setProperty("taskConfig", typeFactory.constructType(TaskConfig.class));
        setProperty("trigger", typeFactory.constructType(TaskTriggerInformation.class));
        setProperty("numberOfRetries", intType);
        setProperty("retryIntervalInMilliseconds", intType);
        setProperty("retryTaskOnFailure", stringType);

        setProperty(
                "validationErrors",
                typeFactory.constructCollectionType(Set.class, TaskError.class)
        );

        setProperty(
                "actions",
                typeFactory.constructCollectionType(List.class, TaskActionInformation.class)
        );

        return task;
    }

    private void setProperty(String propertyName, JavaType javaType) {
        setProperty(propertyName, propertyName, javaType);
    }

    private void setProperty(String propertyName, String jsonPropertyName, JavaType javaType) {
        if (jsonNode.has(jsonPropertyName)) {
            try {
                Object value = mapper.readValue(jsonNode.get(jsonPropertyName), javaType);
                BeanUtils.setProperty(task, propertyName, value);
            } catch (IllegalAccessException | InvocationTargetException | IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
