package org.motechproject.scheduler.tasks;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.scheduler.constants.SchedulerConstants;
import org.motechproject.scheduler.service.MotechSchedulerService;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class SchedulerEventParserTest {

    private static final String SUBJECT = "some-subject";
    private static final String TRIGGER_LISTENER_SUBJECT = "trigger-listener-subject";

    private SchedulerEventParser eventParser;

    @Before
    public void setUp() {
        eventParser = new SchedulerEventParser();
    }

    @Test
    public void shouldParseEventParameters() throws Exception {

        Map<String, Object> parameters = prepareParameter(SUBJECT);

        String subject = eventParser.parseEventSubject(TRIGGER_LISTENER_SUBJECT, parameters);

        assertEquals(SUBJECT, subject);
    }

    @Test
    public void shouldParseEventSubject() throws Exception {

        Map<String, Object> expectedParameters = prepareParameter(SUBJECT);

        Map<String, Object> parameters = eventParser.parseEventParameters(TRIGGER_LISTENER_SUBJECT, expectedParameters);

        assertEquals(expectedParameters, parameters);
    }

    @Test
    public void shouldGetName() throws Exception {
        assertEquals(SchedulerConstants.PARSER_NAME, eventParser.getName());
    }

    private Map<String, Object> prepareParameter(String expectedSubject) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(MotechSchedulerService.JOB_ID_KEY, expectedSubject);
        return parameters;
    }
}