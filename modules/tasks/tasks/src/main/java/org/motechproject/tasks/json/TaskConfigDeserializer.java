package org.motechproject.tasks.json;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.tasks.domain.TaskConfig;
import org.motechproject.tasks.domain.TaskConfigStep;

import java.io.IOException;
import java.util.Iterator;

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
                config.add(mapper.readValue(steps.next(), TaskConfigStep.class));
            }
        }

        return config;
    }
}
