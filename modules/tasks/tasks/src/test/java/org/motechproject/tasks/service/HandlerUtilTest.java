package org.motechproject.tasks.service;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.commons.date.util.DateTimeSourceUtil;
import org.motechproject.event.MotechEvent;
import org.motechproject.tasks.domain.EventParameter;
import org.motechproject.tasks.domain.Filter;
import org.motechproject.tasks.domain.KeyInformation;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskAdditionalData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.motechproject.tasks.domain.KeyInformation.ADDITIONAL_DATA_PREFIX;
import static org.motechproject.tasks.domain.KeyInformation.TRIGGER_PREFIX;
import static org.motechproject.tasks.domain.OperatorType.CONTAINS;
import static org.motechproject.tasks.domain.OperatorType.ENDSWITH;
import static org.motechproject.tasks.domain.OperatorType.EQUALS;
import static org.motechproject.tasks.domain.OperatorType.EXIST;
import static org.motechproject.tasks.domain.OperatorType.GT;
import static org.motechproject.tasks.domain.OperatorType.LT;
import static org.motechproject.tasks.domain.OperatorType.STARTSWITH;
import static org.motechproject.tasks.domain.ParameterType.BOOLEAN;
import static org.motechproject.tasks.domain.ParameterType.DATE;
import static org.motechproject.tasks.domain.ParameterType.DOUBLE;
import static org.motechproject.tasks.domain.ParameterType.INTEGER;
import static org.motechproject.tasks.domain.ParameterType.LIST;
import static org.motechproject.tasks.domain.ParameterType.LONG;
import static org.motechproject.tasks.domain.ParameterType.MAP;
import static org.motechproject.tasks.domain.ParameterType.TEXTAREA;
import static org.motechproject.tasks.domain.ParameterType.TIME;
import static org.motechproject.tasks.domain.ParameterType.UNICODE;
import static org.motechproject.tasks.service.HandlerUtil.checkFilters;
import static org.motechproject.tasks.service.HandlerUtil.convertTo;
import static org.motechproject.tasks.service.HandlerUtil.findAdditionalData;
import static org.motechproject.tasks.service.HandlerUtil.getFieldValue;
import static org.motechproject.tasks.service.HandlerUtil.getTriggerKey;

public class HandlerUtilTest {
    private static final String EVENT_KEY = "event.key";
    private static final String OBJECT_TYPE = "Test";
    private static final Long OBJECT_ID = 1L;
    private static final String DATA_PROVIDER_ID = "123456789";
    private static final String EVENT_KEY_VALUE = "trigger.event.key.value";
    private static final String KEY_VALUE = "key";

    private class HandlerUtilObjectTest {
        private int id;

        private HandlerUtilObjectTest() {
            this.id = OBJECT_ID.intValue();
        }

        public int getId() {
            return id;
        }
    }

    @Test
    public void testConvertTo() {
        DateTime now = DateTimeSourceUtil.now().withSecondOfMinute(0).withMillis(0);

        assertEquals("text", convertTo(UNICODE, "text"));
        assertEquals("text\nline2", convertTo(TEXTAREA, "text\nline2"));
        assertEquals(123, convertTo(INTEGER, "123"));
        assertEquals(100000000000L, convertTo(LONG, "100000000000"));
        assertEquals(123.45, convertTo(DOUBLE, "123.45"));
        assertEquals(now, convertTo(DATE, now.toString("yyyy-MM-dd HH:mm Z")));
        assertEquals(true, convertTo(BOOLEAN, "true"));
        assertEquals("key:value\nkey2:value2", convertTo(MAP, "key:value\nkey2:value2"));
        assertEquals("value\nvalue2", convertTo(LIST, "value\nvalue2"));

        assertTime(now, (DateTime) convertTo(TIME, now.toString("HH:mm Z")));
    }

    @Test
    public void testGetFieldValue() throws Exception {
        HandlerUtilObjectTest test = new HandlerUtilObjectTest();
        int value = (int) getFieldValue(test, "id");

        assertEquals(OBJECT_ID.intValue(), value);
    }

    @Test
    public void testFindAdditionalData() {
        Task task = mock(Task.class);
        KeyInformation key = KeyInformation.parse(String.format("%s.%s.%s#%d.%s", ADDITIONAL_DATA_PREFIX, DATA_PROVIDER_ID, OBJECT_TYPE, OBJECT_ID, KEY_VALUE));

        when(task.containsAdditionalData(DATA_PROVIDER_ID)).thenReturn(false);

        assertNull(findAdditionalData(task, key));

        TaskAdditionalData taskAdditionalData = new TaskAdditionalData();
        taskAdditionalData.setId(2L);
        taskAdditionalData.setType("Test2");

        when(task.containsAdditionalData(DATA_PROVIDER_ID)).thenReturn(true);
        when(task.getAdditionalData(DATA_PROVIDER_ID)).thenReturn(asList(taskAdditionalData));

        assertNull(findAdditionalData(task, key));

        taskAdditionalData.setId(OBJECT_ID);
        taskAdditionalData.setType(OBJECT_TYPE);

        assertEquals(taskAdditionalData, findAdditionalData(task, key));
    }

    @Test(expected = IllegalStateException.class)
    public void testGetTriggerKey() throws Exception {
        String empty = "";
        Map<String, Object> parameters = new HashMap<>();

        MotechEvent event = mock(MotechEvent.class);
        KeyInformation key = KeyInformation.parse(String.format("%s.%s", TRIGGER_PREFIX, EVENT_KEY));

        when(event.getParameters()).thenReturn(null);

        assertEquals(empty, getTriggerKey(event, key));

        when(event.getParameters()).thenReturn(parameters);

        Map<String, String> child = new HashMap<>();
        child.put("key", EVENT_KEY_VALUE);

        parameters.put("event", child);

        assertEquals(EVENT_KEY_VALUE, getTriggerKey(event, key));

        parameters.clear();

        getTriggerKey(event, key);
    }

    @Test
    public void testCheckFilters() {
        assertTrue(checkFilters(null, null));
        assertTrue(checkFilters(new ArrayList<Filter>(), null));

        List<Filter> filters = new ArrayList<>();
        filters.add(new Filter(new EventParameter("EventName", "eventName"), true, CONTAINS.getValue(), "ven"));
        filters.add(new Filter(new EventParameter("EventName", "eventName", TEXTAREA), true, EXIST.getValue(), ""));
        filters.add(new Filter(new EventParameter("EventName", "eventName"), true, EQUALS.getValue(), "event name"));
        filters.add(new Filter(new EventParameter("EventName", "eventName"), true, STARTSWITH.getValue(), "ev"));
        filters.add(new Filter(new EventParameter("EventName", "eventName"), true, ENDSWITH.getValue(), "me"));

        filters.add(new Filter(new EventParameter("ExternalID", "externalId", INTEGER), true, GT.getValue(), "19"));
        filters.add(new Filter(new EventParameter("ExternalID", "externalId", INTEGER), false, GT.getValue(), "1234567891"));
        filters.add(new Filter(new EventParameter("ExternalID", "externalId", INTEGER), true, LT.getValue(), "1234567891"));
        filters.add(new Filter(new EventParameter("ExternalID", "externalId", INTEGER), false, LT.getValue(), "123"));
        filters.add(new Filter(new EventParameter("ExternalID", "externalId", INTEGER), true, EQUALS.getValue(), "123456789"));
        filters.add(new Filter(new EventParameter("ExternalID", "externalId", INTEGER), false, EQUALS.getValue(), "789"));
        filters.add(new Filter(new EventParameter("ExternalID", "externalId", INTEGER), true, EXIST.getValue(), ""));

        assertFalse(checkFilters(filters, new HashMap<String, Object>()));

        Map<String, Object> triggerParameters = new HashMap<>();
        triggerParameters.put("eventName", "etName");
        triggerParameters.put("externalId", "12345");

        assertFalse(checkFilters(filters, triggerParameters));

        triggerParameters.put("eventName", "event name");
        triggerParameters.put("externalId", "123456789");

        assertTrue(checkFilters(filters, triggerParameters));

        Filter filter = new Filter(new EventParameter("EventName", "eventName"), true, "abc", "");
        filters.add(filter);

        assertFalse(checkFilters(filters, triggerParameters));

        filters.remove(filter);
        filters.add(new Filter(new EventParameter("ExternalID", "externalId", INTEGER), true, "abc", ""));

        assertFalse(checkFilters(filters, triggerParameters));
    }

    private void assertTime(DateTime expected, DateTime actual) {
        assertEquals(expected.getHourOfDay(), actual.getHourOfDay());
        assertEquals(expected.getMinuteOfHour(), actual.getMinuteOfHour());
        assertEquals(expected.getZone(), actual.getZone());
    }
}
