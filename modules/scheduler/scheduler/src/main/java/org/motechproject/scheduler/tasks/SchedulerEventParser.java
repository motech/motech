package org.motechproject.scheduler.tasks;

import org.motechproject.commons.api.TasksEventParser;
import org.motechproject.scheduler.constants.SchedulerConstants;
import org.motechproject.scheduler.service.MotechSchedulerService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Responsible for parsing subject of the Tasks triggers originating from the Scheduler module.
 */
@Service
public class SchedulerEventParser implements TasksEventParser {

    @Override
    public Map<String, Object> parseEventParameters(String eventSubject, Map<String, Object> eventParameters) {
        return eventParameters;
    }

    @Override
    public String parseEventSubject(String eventSubject, Map<String, Object> eventParameters) {
        return (String) eventParameters.get(MotechSchedulerService.JOB_ID_KEY);
    }

    @Override
    public String getName() {
        return SchedulerConstants.PARSER_NAME;
    }
}
