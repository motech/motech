package org.motechproject.tasks.service;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.commons.api.MotechException;
import org.motechproject.commons.date.util.DateTimeSourceUtil;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.event.MotechEvent;
import org.motechproject.tasks.domain.EventParameter;
import org.motechproject.tasks.domain.Filter;
import org.motechproject.tasks.domain.KeyInformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.motechproject.tasks.domain.KeyInformation.TRIGGER_PREFIX;
import static org.motechproject.tasks.domain.KeyInformation.parse;
import static org.motechproject.tasks.domain.OperatorType.AFTER;
import static org.motechproject.tasks.domain.OperatorType.AFTER_NOW;
import static org.motechproject.tasks.domain.OperatorType.BEFORE;
import static org.motechproject.tasks.domain.OperatorType.BEFORE_NOW;
import static org.motechproject.tasks.domain.OperatorType.CONTAINS;
import static org.motechproject.tasks.domain.OperatorType.ENDSWITH;
import static org.motechproject.tasks.domain.OperatorType.EQUALS;
import static org.motechproject.tasks.domain.OperatorType.EXIST;
import static org.motechproject.tasks.domain.OperatorType.GT;
import static org.motechproject.tasks.domain.OperatorType.LESS_DAYS_FROM_NOW;
import static org.motechproject.tasks.domain.OperatorType.LESS_MONTHS_FROM_NOW;
import static org.motechproject.tasks.domain.OperatorType.LT;
import static org.motechproject.tasks.domain.OperatorType.MORE_DAYS_FROM_NOW;
import static org.motechproject.tasks.domain.OperatorType.MORE_MONTHS_FROM_NOW;
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
import static org.motechproject.tasks.service.HandlerUtil.getFieldValue;
import static org.motechproject.tasks.service.HandlerUtil.getTriggerKey;
import static org.motechproject.tasks.service.HandlerUtil.manipulate;

public class HandlerUtilTest {
    private static final String EVENT_KEY = "event.key";
    private static final Long OBJECT_ID = 1L;
    private static final String EVENT_KEY_VALUE = "trigger.event.key.value";

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

    @Test(expected = IllegalStateException.class)
    public void testGetTriggerKey() throws Exception {
        Map<String, Object> parameters = new HashMap<>();

        MotechEvent event = mock(MotechEvent.class);
        KeyInformation key = parse(String.format("%s.%s", TRIGGER_PREFIX, EVENT_KEY));

        when(event.getParameters()).thenReturn(null);

        assertEquals(null, getTriggerKey(event, key));

        when(event.getParameters()).thenReturn(parameters);

        Map<String, String> child = new HashMap<>();
        child.put("key", EVENT_KEY_VALUE);

        parameters.put("event", child);

        assertEquals(EVENT_KEY_VALUE, getTriggerKey(event, key));

        parameters.clear();

        getTriggerKey(event, key);
    }

    @Test
    public void testGetNullTriggerKey() throws Exception {
        Map<String, Object> parameters = new HashMap<>();

        MotechEvent event = mock(MotechEvent.class);
        KeyInformation key = parse(String.format("%s.%s", TRIGGER_PREFIX, EVENT_KEY));

        when(event.getParameters()).thenReturn(parameters);

        Map<String, String> child = new HashMap<>();
        child.put("key", null);

        parameters.put("event", child);

        assertEquals(null, getTriggerKey(event, key));

        // should not throw any exceptions
        assertNull(getTriggerKey(event, key));
    }

    @Test
    public void testCheckFilters() {
        DateTime dateTime = DateTime.now().minusDays(2);

        assertTrue(checkFilters(null, null, null));
        assertTrue(checkFilters(new ArrayList<Filter>(), null, null));

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

        filters.add(new Filter("CMS Lite.StreamContent#0.Name", "ad.1d030089d262f6709924f7b224024e21.StreamContent#0.name", UNICODE, true, CONTAINS.getValue(), "am"));
        filters.add(new Filter("CMS Lite.StreamContent#0.Name", "ad.1d030089d262f6709924f7b224024e21.StreamContent#0.name", UNICODE, true, EXIST.getValue(), ""));
        filters.add(new Filter("CMS Lite.StreamContent#0.Name", "ad.1d030089d262f6709924f7b224024e21.StreamContent#0.name", UNICODE, true, EQUALS.getValue(), "name"));
        filters.add(new Filter("CMS Lite.StreamContent#0.Name", "ad.1d030089d262f6709924f7b224024e21.StreamContent#0.name", UNICODE, true, STARTSWITH.getValue(), "na"));
        filters.add(new Filter("CMS Lite.StreamContent#0.Name", "ad.1d030089d262f6709924f7b224024e21.StreamContent#0.name", UNICODE, true, ENDSWITH.getValue(), "me"));

        filters.add(new Filter("MRS.Person#1.Age", "ad.b1a0a7356621106bded4487f8500a13b.Person#1.age", INTEGER, true, GT.getValue(), "30"));
        filters.add(new Filter("MRS.Person#1.Age", "ad.b1a0a7356621106bded4487f8500a13b.Person#1.age", INTEGER, true, LT.getValue(), "50"));
        filters.add(new Filter("MRS.Person#1.Age", "ad.b1a0a7356621106bded4487f8500a13b.Person#1.age", INTEGER, true, EQUALS.getValue(), "46"));
        filters.add(new Filter("MRS.Person#1.Age", "ad.b1a0a7356621106bded4487f8500a13b.Person#1.age", INTEGER, true, EXIST.getValue(), ""));
        filters.add(new Filter("MRS.Person#1.Age", "ad.b1a0a7356621106bded4487f8500a13b.Person#1.age", INTEGER, false, GT.getValue(), "100"));

        filters.add(new Filter("MRS.Person#2.Age", "ad.b1a0a7356621106bded4487f8500a13b.Person#2.age", INTEGER, false, EXIST.getValue(), ""));

        assertFalse(checkFilters(filters, new HashMap<String, Object>(), new HashMap<String, Object>()));

        Map<String, Object> triggerParameters = new HashMap<>();
        triggerParameters.put("eventName", "etName");
        triggerParameters.put("externalId", "12345");

        Map<String, Object> dataSources = new HashMap<>();
        dataSources.put("0", new StreamContent("Eman"));
        dataSources.put("1", new Person(150));

        assertFalse(checkFilters(filters, triggerParameters, dataSources));

        triggerParameters.put("eventName", "event name");
        triggerParameters.put("externalId", "123456789");
        dataSources.put("0", new StreamContent("name"));
        dataSources.put("1", new Person(46));

        assertTrue(checkFilters(filters, triggerParameters, dataSources));

        Filter equals = new Filter(new EventParameter("Test date", "test_date", DATE), true, EQUALS.getValue(), dateTime.toString());
        Filter after = new Filter(new EventParameter("Test date", "test_date", DATE), false, AFTER.getValue(), DateUtil.now().toString());
        Filter afterNow = new Filter(new EventParameter("Test date", "test_date", DATE), false, AFTER_NOW.getValue(), "");
        Filter before = new Filter(new EventParameter("Test date", "test_date", DATE), true, BEFORE.getValue(), DateUtil.now().toString());
        Filter beforeNow = new Filter(new EventParameter("Test date", "test_date", DATE), true, BEFORE_NOW.getValue(), "");
        Filter lessDays = new Filter(new EventParameter("Test date", "test_date", DATE), true, LESS_DAYS_FROM_NOW.getValue(), "3");
        Filter moreDays = new Filter(new EventParameter("Test date", "test_date", DATE), true, MORE_DAYS_FROM_NOW.getValue(), "0");

        filters.add(equals);
        filters.add(after);
        filters.add(afterNow);
        filters.add(before);
        filters.add(beforeNow);
        filters.add(new Filter(new EventParameter("Test date", "test_date", DATE), true, EXIST.getValue(), ""));
        filters.add(lessDays);
        filters.add(moreDays);

        triggerParameters.put("test_date", dateTime.toString());
        assertTrue(checkFilters(filters, triggerParameters, dataSources));

        dateTime = dateTime.plusDays(4);
        triggerParameters.put("test_date", dateTime.toString());

        filters.remove(equals);
        filters.remove(after);
        filters.remove(afterNow);
        filters.remove(before);
        filters.remove(beforeNow);
        filters.remove(lessDays);
        filters.remove(moreDays);
        filters.add(new Filter(new EventParameter("Test date", "test_date", DATE), true, LESS_MONTHS_FROM_NOW.getValue(), "5"));
        filters.add(new Filter(new EventParameter("Test date", "test_date", DATE), true, MORE_MONTHS_FROM_NOW.getValue(), "1"));
        dateTime = DateTime.now().minusMonths(3);

        triggerParameters.put("test_date", dateTime.toString());
        assertTrue(checkFilters(filters, triggerParameters, dataSources));

        dateTime = dateTime.plusMonths(6);

        triggerParameters.put("test_date", dateTime.toString());
        assertTrue(checkFilters(filters, triggerParameters, dataSources));

        equals.setExpression(dateTime.toString());
        after.setNegationOperator(!after.isNegationOperator());
        afterNow.setNegationOperator(!afterNow.isNegationOperator());
        before.setNegationOperator(!before.isNegationOperator());
        beforeNow.setNegationOperator(!beforeNow.isNegationOperator());

        assertTrue(checkFilters(filters, triggerParameters, dataSources));

        Filter triggerFilter = new Filter("Trigger.Event Name", "trigger.eventName", UNICODE, true, "abc", "");
        filters.add(triggerFilter);
        Filter additionalDataFilter = new Filter("CMS Lite.StreamContent#0.Name", "ad.1d030089d262f6709924f7b224024e21.StreamContent#0.name", UNICODE, true, "abc", "");
        filters.add(additionalDataFilter);

        assertFalse(checkFilters(filters, triggerParameters, dataSources));

        filters.remove(triggerFilter);
        filters.add(new Filter("Trigger.External Id", "trigger.externalId", INTEGER, true, "abc", ""));
        filters.remove(additionalDataFilter);
        filters.add(new Filter("MRS.Person#1.Age", "ad.b1a0a7356621106bded4487f8500a13b.Person#1.age", INTEGER, true, "abc", ""));

        assertFalse(checkFilters(filters, triggerParameters, dataSources));
    }

    @Test(expected = MotechException.class)
    public void testManipulate() throws Exception {
        String string = "ala-has-a-cat";
        DateTime now = DateUtil.now();
        String toString = now.toString();
        String toStringWithPattern = now.toString("yyyy-MM-dd");

        assertEquals("lower_case", manipulate("tolower", "LOWER_CASE"));
        assertEquals("UPPER_CASE", manipulate("toupper", "upper_case"));
        assertEquals("Capitalize", manipulate("capitalize", "capitalize"));
        assertEquals("67890", manipulate("substring(5)", "1234567890"));
        assertEquals("67", manipulate("substring(5,7)", "1234567890"));
        assertEquals(string, manipulate("join(-)", "ala has a cat"));
        assertEquals("ala", manipulate("split(-,0)", string));
        assertEquals("cat", manipulate("split(-,3)", string));
        assertEquals(toStringWithPattern, manipulate("datetime(yyyy-MM-dd)", toString));
        assertEquals(now.plusDays(1).toString(), manipulate("plusDays(1)", toString));

        manipulate("undefined", "something");
    }

    private void assertTime(DateTime expected, DateTime actual) {
        assertEquals(expected.getHourOfDay(), actual.getHourOfDay());
        assertEquals(expected.getMinuteOfHour(), actual.getMinuteOfHour());
        assertEquals(expected.getZone(), actual.getZone());
    }

    public static class StreamContent {
        private String name;

        public StreamContent(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class Person {
        private int age;

        public Person(int age) {
            this.age = age;
        }

        public int getAge() {
            return age;
        }
    }
}
