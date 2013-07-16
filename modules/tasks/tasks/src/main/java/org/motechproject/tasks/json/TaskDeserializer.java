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
import org.motechproject.tasks.domain.DataSource;
import org.motechproject.tasks.domain.EventParameter;
import org.motechproject.tasks.domain.Filter;
import org.motechproject.tasks.domain.FilterSet;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActionInformation;
import org.motechproject.tasks.domain.TaskAdditionalData;
import org.motechproject.tasks.domain.TaskConfig;
import org.motechproject.tasks.domain.TaskError;
import org.motechproject.tasks.domain.TaskEventInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.motechproject.tasks.domain.KeyInformation.TRIGGER_PREFIX;

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

        setProperty("id", "_id", stringType);
        setProperty("revision", "_rev", stringType);

        setProperty("description", stringType);
        setProperty("name", stringType);
        setProperty("enabled", stringType);
        setProperty("taskConfig", typeFactory.constructType(TaskConfig.class));
        setProperty("trigger", typeFactory.constructType(TaskEventInformation.class));

        setProperty(
                "validationErrors",
                typeFactory.constructCollectionType(Set.class, TaskError.class)
        );

        if (jsonNode.has("filters")) {
            /* backward compatibility */
            JsonNode jsonFilters = jsonNode.get("filters");
            Iterator<JsonNode> iterator = jsonFilters.getElements();
            FilterSet filterSet = new FilterSet();

            while (iterator.hasNext()) {
                JsonNode jsonFilter = iterator.next();
                Filter filter = mapper.readValue(jsonFilter, Filter.class);

                if (jsonFilter.has("eventParameter")) {
                    EventParameter eventParameter = mapper.readValue(
                            jsonFilter.get("eventParameter"), EventParameter.class
                    );

                    filter.setDisplayName(format("Trigger.%s", eventParameter.getDisplayName()));
                    filter.setKey(format("%s.%s", TRIGGER_PREFIX, eventParameter.getEventKey()));
                    filter.setType(eventParameter.getType());
                }

                filterSet.addFilter(filter);
            }

            task.getTaskConfig().removeFilterSets().add(filterSet);
        }

        if (jsonNode.has("additionalData")) {
            /* backward compatibility */
            Map<String, List<TaskAdditionalData>> additionalData = mapper.readValue(
                    jsonNode.get("additionalData"), typeFactory.constructMapType(
                            Map.class, stringType, typeFactory.constructCollectionType(
                                    List.class, TaskAdditionalData.class
                            )
                    )
            );

            task.getTaskConfig().removeDataSources();

            for (Map.Entry<String, List<TaskAdditionalData>> entry : additionalData.entrySet()) {
                for (TaskAdditionalData data : entry.getValue()) {
                    DataSource.Lookup lookup = new DataSource.Lookup(
                            data.getLookupField(), data.getLookupValue()
                    );

                    task.getTaskConfig().add(new DataSource(entry.getKey(), data.getId(),
                            data.getType(), lookup.getField(), asList(lookup), data.isFailIfDataNotFound()
                    ));
                }
            }
        }

        if (jsonNode.has("action")) {
            /* backward compatibility */
            TaskActionInformation action = mapper.readValue(
                    jsonNode.get("action"), TaskActionInformation.class
            );

            if (jsonNode.has("actionInputFields")) {
                Map<String, String> values = mapper.readValue(
                        jsonNode.get("actionInputFields"),
                        typeFactory.constructMapType(Map.class, stringType, stringType)
                );

                action.setValues(values);
            }

            task.addAction(action);
        } else {
            setProperty(
                    "actions",
                    typeFactory.constructCollectionType(List.class, TaskActionInformation.class)
            );
        }

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
