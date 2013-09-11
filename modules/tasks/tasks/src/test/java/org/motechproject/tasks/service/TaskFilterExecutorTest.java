package org.motechproject.tasks.service;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.event.MotechEvent;
import org.motechproject.tasks.domain.DataSource;
import org.motechproject.tasks.domain.EventParameter;
import org.motechproject.tasks.domain.Filter;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActionInformation;
import org.motechproject.tasks.domain.TaskBuilder;
import org.motechproject.tasks.domain.TaskConfig;
import org.motechproject.tasks.ex.TaskHandlerException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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
import static org.motechproject.tasks.domain.ParameterType.DATE;
import static org.motechproject.tasks.domain.ParameterType.INTEGER;
import static org.motechproject.tasks.domain.ParameterType.TEXTAREA;
import static org.motechproject.tasks.domain.ParameterType.UNICODE;

@RunWith(MockitoJUnitRunner.class)
public class TaskFilterExecutorTest {

    @Mock
    private TaskActivityService activityService;

    @Test
    public void testcheckFilters() throws TaskHandlerException {
        DateTime dateTime = DateTime.now().minusDays(2);

        DataSource dataSource = new DataSource("", 0L, "", "", null, false);
        TaskConfig taskConfig = mock(TaskConfig.class);
        when(taskConfig.getDataSource(anyString(), anyLong(), anyString())).thenReturn(dataSource);

        Task task = new TaskBuilder().addAction(new TaskActionInformation()).build();
        TaskContext taskContext = new TaskContext(task, new MotechEvent("foo", null), activityService);
        TaskFilterExecutor taskFilterExecutor = new TaskFilterExecutor();

        assertTrue(taskFilterExecutor.checkFilters(null, taskContext));
        assertTrue(taskFilterExecutor.checkFilters(new ArrayList<Filter>(), taskContext));

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

        taskContext = new TaskContext(task, new MotechEvent("foo", null), activityService);
        assertFalse(taskFilterExecutor.checkFilters(filters, taskContext));

        Map<String, Object> triggerParameters = new HashMap<>();
        triggerParameters.put("eventName", "etName");
        triggerParameters.put("externalId", "12345");

        taskContext = new TaskContext(task, new MotechEvent("foo", triggerParameters), activityService);
        taskContext.addDataSourceObject("0", new StreamContent("Eman"), false);
        taskContext.addDataSourceObject("1", new Person(150), false);
        assertFalse(taskFilterExecutor.checkFilters(filters, taskContext));

        triggerParameters.put("eventName", "event name");
        triggerParameters.put("externalId", "123456789");
        taskContext = new TaskContext(task, new MotechEvent("foo", triggerParameters), activityService);
        taskContext.addDataSourceObject("0", new StreamContent("name"), false);
        taskContext.addDataSourceObject("1", new Person(46), false);
        assertTrue(taskFilterExecutor.checkFilters(filters, taskContext));

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
        taskContext = new TaskContext(task, new MotechEvent("foo", triggerParameters), activityService);
        taskContext.addDataSourceObject("0", new StreamContent("name"), false);
        taskContext.addDataSourceObject("1", new Person(46), false);
        assertTrue(taskFilterExecutor.checkFilters(filters, taskContext));

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
        taskContext = new TaskContext(task, new MotechEvent("foo", triggerParameters), activityService);
        taskContext.addDataSourceObject("0", new StreamContent("name"), false);
        taskContext.addDataSourceObject("1", new Person(46), false);
        assertTrue(taskFilterExecutor.checkFilters(filters, taskContext));

        dateTime = dateTime.plusMonths(6);

        triggerParameters.put("test_date", dateTime.toString());
        taskContext = new TaskContext(task, new MotechEvent("foo", triggerParameters), activityService);
        taskContext.addDataSourceObject("0", new StreamContent("name"), false);
        taskContext.addDataSourceObject("1", new Person(46), false);
        assertTrue(taskFilterExecutor.checkFilters(filters, taskContext));

        equals.setExpression(dateTime.toString());
        after.setNegationOperator(!after.isNegationOperator());
        afterNow.setNegationOperator(!afterNow.isNegationOperator());
        before.setNegationOperator(!before.isNegationOperator());
        beforeNow.setNegationOperator(!beforeNow.isNegationOperator());

        taskContext = new TaskContext(task, new MotechEvent("foo", triggerParameters), activityService);
        taskContext.addDataSourceObject("0", new StreamContent("name"), false);
        taskContext.addDataSourceObject("1", new Person(46), false);
        assertTrue(taskFilterExecutor.checkFilters(filters, taskContext));

        Filter triggerFilter = new Filter("Trigger.Event Name", "trigger.eventName", UNICODE, true, "abc", "");
        filters.add(triggerFilter);
        Filter additionalDataFilter = new Filter("CMS Lite.StreamContent#0.Name", "ad.1d030089d262f6709924f7b224024e21.StreamContent#0.name", UNICODE, true, "abc", "");
        filters.add(additionalDataFilter);

        taskContext = new TaskContext(task, new MotechEvent("foo", triggerParameters), activityService);
        taskContext.addDataSourceObject("0", new StreamContent("name"), false);
        taskContext.addDataSourceObject("1", new Person(46), false);
        assertFalse(taskFilterExecutor.checkFilters(filters, taskContext));

        filters.remove(triggerFilter);
        filters.add(new Filter("Trigger.External Id", "trigger.externalId", INTEGER, true, "abc", ""));
        filters.remove(additionalDataFilter);
        filters.add(new Filter("MRS.Person#1.Age", "ad.b1a0a7356621106bded4487f8500a13b.Person#1.age", INTEGER, true, "abc", ""));

        taskContext = new TaskContext(task, new MotechEvent("foo", triggerParameters), activityService);
        taskContext.addDataSourceObject("0", new StreamContent("name"), false);
        taskContext.addDataSourceObject("1", new Person(46), false);
        assertFalse(taskFilterExecutor.checkFilters(filters, taskContext));
    }

    @Test(expected = TaskHandlerException.class)
    public void shouldThrowExceptionIfDataSourceObjectIsNotFound() throws TaskHandlerException {
        List<Filter> filters = new ArrayList<>();
        filters.add(new Filter("MRS.Person#2.Age", "ad.b1a0a7356621106bded4487f8500a13b.Person#2.age", INTEGER, false, EXIST.getValue(), ""));

        Task task = new TaskBuilder().addAction(new TaskActionInformation()).build();
        TaskContext taskContext = new TaskContext(task, new MotechEvent("foo", null), activityService);
        new TaskFilterExecutor().checkFilters(filters, taskContext);
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
