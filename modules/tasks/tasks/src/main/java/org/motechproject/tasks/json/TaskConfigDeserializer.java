package org.motechproject.tasks.json;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.tasks.domain.mds.task.TaskConfig;
import org.motechproject.tasks.domain.mds.task.TaskConfigStep;
import org.motechproject.tasks.dto.DataSourceDto;
import org.motechproject.tasks.dto.FilterSetDto;
import org.motechproject.tasks.dto.TaskConfigStepDto;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * {@code JsonDeserializer} for {@code TaskConfig} class.
 */
public class TaskConfigDeserializer extends JsonDeserializer<TaskConfig> {

    @Override
    public TaskConfig deserialize(JsonParser parser,
                                  DeserializationContext context) throws IOException {
        JsonNode jsonNode = parser.readValueAsTree();
        TaskConfig config = new TaskConfig();
        ObjectMapper mapper;

        ObjectCodec codec = parser.getCodec();

        if (codec instanceof ObjectMapper) {
            mapper = (ObjectMapper) codec;
        } else {
            mapper = new ObjectMapper();
        }

        if (jsonNode.has("steps")) {
            Iterator<JsonNode> steps = jsonNode.get("steps").getElements();

            while (steps.hasNext()) {
                JsonNode next = steps.next();
                if (isDtoType(next)) {
                    config.add(mapper.readValue(next, TaskConfigStepDto.class));
                } else {
                    config.add(mapper.readValue(next, TaskConfigStep.class));
                }
            }
        }

        return config;
    }

    private boolean isDtoType(JsonNode node) {
        String value = node.get("@type").asText();
        List<String> subtypeNames = Arrays.asList(DataSourceDto.class.getSimpleName(), FilterSetDto.class.getSimpleName());

        return subtypeNames.contains(value);
    }
}
