package org.motechproject.tasks.service;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.event.MotechEvent;
import org.motechproject.tasks.domain.EventParameter;
import org.motechproject.tasks.domain.Filter;
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
import static org.motechproject.tasks.domain.OperatorType.CONTAINS;
import static org.motechproject.tasks.domain.OperatorType.ENDSWITH;
import static org.motechproject.tasks.domain.OperatorType.EQUALS;
import static org.motechproject.tasks.domain.OperatorType.EXIST;
import static org.motechproject.tasks.domain.OperatorType.GT;
import static org.motechproject.tasks.domain.OperatorType.LT;
import static org.motechproject.tasks.domain.OperatorType.STARTSWITH;
import static org.motechproject.tasks.domain.ParameterType.NUMBER;
import static org.motechproject.tasks.domain.ParameterType.TEXTAREA;
import static org.motechproject.tasks.service.HandlerUtil.checkFilters;
import static org.motechproject.tasks.service.HandlerUtil.convertToDate;
import static org.motechproject.tasks.service.HandlerUtil.convertToNumber;
import static org.motechproject.tasks.service.HandlerUtil.findAdditionalData;
import static org.motechproject.tasks.service.HandlerUtil.getFieldValue;
import static org.motechproject.tasks.service.HandlerUtil.getKeys;
import static org.motechproject.tasks.service.HandlerUtil.getTriggerKey;

public class HandlerUtilTest {
    private static final String EVENT_KEY = "eventKey";
    private static final String OBJECT_TYPE = "Test";
    private static final Long OBJECT_ID = 1L;
    private static final String DATA_PROVIDER_ID = "123456789";
    private static final String EVENT_KEY_VALUE = "event.key.value";

    private static final String KEY_1 = "trigger.DosageID";
    private static final String KEY_2 = "trigger.ExternalID";

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
    public void testGetFieldValue() throws Exception {
        HandlerUtilObjectTest test = new HandlerUtilObjectTest();
        String value = getFieldValue(test, "id");

        assertEquals(OBJECT_ID.toString(), value);
    }

    @Test
    public void testFindAdditionalData() {
        Task task = mock(Task.class);
        KeyInformation key = mock(KeyInformation.class);

        when(key.getDataProviderId()).thenReturn(DATA_PROVIDER_ID);
        when(task.containsAdditionalData(DATA_PROVIDER_ID)).thenReturn(false);

        assertNull(findAdditionalData(task, key));

        TaskAdditionalData taskAdditionalData = new TaskAdditionalData();
        taskAdditionalData.setId(2L);
        taskAdditionalData.setType("Test2");

        when(task.containsAdditionalData(DATA_PROVIDER_ID)).thenReturn(true);
        when(task.getAdditionalData(DATA_PROVIDER_ID)).thenReturn(asList(taskAdditionalData));

        when(key.getObjectId()).thenReturn(OBJECT_ID);
        when(key.getObjectType()).thenReturn(OBJECT_TYPE);

        assertNull(findAdditionalData(task, key));

        taskAdditionalData.setId(OBJECT_ID);
        taskAdditionalData.setType(OBJECT_TYPE);

        assertEquals(taskAdditionalData, findAdditionalData(task, key));
    }

    @Test
    public void testGetTriggerKey() {
        String empty = "";
        Map<String, Object> parameters = new HashMap<>();

        MotechEvent event = mock(MotechEvent.class);
        KeyInformation key = mock(KeyInformation.class);

        when(event.getParameters()).thenReturn(null);

        assertEquals(empty, getTriggerKey(event, key));

        when(event.getParameters()).thenReturn(parameters);
        when(key.getEventKey()).thenReturn(EVENT_KEY);

        assertEquals(empty, getTriggerKey(event, key));

        parameters.put(EVENT_KEY, null);

        assertEquals(empty, getTriggerKey(event, key));

        parameters.put(EVENT_KEY, EVENT_KEY_VALUE);

        assertEquals(EVENT_KEY_VALUE, getTriggerKey(event, key));
    }

    @Test
    public void testGetKeys() {
        List<KeyInformation> expected = new ArrayList<>();
        expected.add(new KeyInformation(KEY_1));
        expected.add(new KeyInformation(KEY_2));

        assertTrue(getKeys(null).isEmpty());
        assertTrue(getKeys("").isEmpty());

        String input = String.format("abc {{%s}} def {{%s}} ghi", KEY_1, KEY_2);

        List<KeyInformation> actual = getKeys(input);

        assertEquals(expected, actual);
    }

    @Test
    public void testConvertToNumber() {
        assertEquals(123, convertToNumber("123"));
        assertEquals(2.5, (Double) convertToNumber("2.5"), 0.0001);
        assertEquals(2000.75, (Double) convertToNumber("2000.75"), 0.0001);
    }

    @Test
    public void testConvertToDate() {
        DateTime now = new DateTime(2013, 2, 25, 10, 15);
        assertEquals(now, convertToDate(now.toString("yyyy-MM-dd HH:mm Z")));
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

        filters.add(new Filter(new EventParameter("ExternalID", "externalId", NUMBER), true, GT.getValue(), "19"));
        filters.add(new Filter(new EventParameter("ExternalID", "externalId", NUMBER), false, GT.getValue(), "1234567891"));
        filters.add(new Filter(new EventParameter("ExternalID", "externalId", NUMBER), true, LT.getValue(), "1234567891"));
        filters.add(new Filter(new EventParameter("ExternalID", "externalId", NUMBER), false, LT.getValue(), "123"));
        filters.add(new Filter(new EventParameter("ExternalID", "externalId", NUMBER), true, EQUALS.getValue(), "123456789"));
        filters.add(new Filter(new EventParameter("ExternalID", "externalId", NUMBER), false, EQUALS.getValue(), "789"));
        filters.add(new Filter(new EventParameter("ExternalID", "externalId", NUMBER), true, EXIST.getValue(), ""));

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
        filters.add(new Filter(new EventParameter("ExternalID", "externalId", NUMBER), true, "abc", ""));

        assertFalse(checkFilters(filters, triggerParameters));
    }
}
