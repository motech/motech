package org.motechproject.tasks.service;

import org.motechproject.commons.api.TasksEventParser;

import java.util.HashMap;
import java.util.Map;

public class SampleTasksEventParser implements TasksEventParser {

    public static final String PARSER_NAME = "SampleTestParser";

    /**
     * Modified map of event parameters that way, so each key and value in the map is
     * a substring containing three first characters of the original values. Also, the
     * value is converted to String if it already wasn't one.
     *
     * @param eventParameters Initial event parameters
     * @return Modified map of event parmaters
     */
    @Override
    public Map<String, Object> parseEventParameters(String eventSubject, Map<String, Object> eventParameters) {
        Map<String, Object> modifiedMap = new HashMap<>();

        for (Map.Entry<String, Object> entry : eventParameters.entrySet()) {
            modifiedMap.put(entry.getKey().substring(0, 3), entry.getValue().toString().substring(0, 3));
        }

        return modifiedMap;
    }

    @Override
    public String parseEventSubject(String eventSubject, Map<String, Object> eventParameters) {
        return eventSubject;
    }

    @Override
    public String getName() {
        return PARSER_NAME;
    }
}
