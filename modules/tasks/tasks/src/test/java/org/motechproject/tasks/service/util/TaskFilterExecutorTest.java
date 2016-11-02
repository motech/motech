package org.motechproject.tasks.service.util;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.tasks.domain.mds.task.DataSource;
import org.motechproject.tasks.domain.mds.task.Filter;
import org.motechproject.tasks.domain.enums.LogicalOperator;
import org.motechproject.tasks.domain.mds.task.Task;
import org.motechproject.tasks.domain.mds.task.TaskActionInformation;
import org.motechproject.tasks.domain.mds.task.builder.TaskBuilder;
import org.motechproject.tasks.domain.mds.task.TaskConfig;
import org.motechproject.tasks.exception.TaskHandlerException;
import org.motechproject.tasks.service.TaskActivityService;

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
import static org.motechproject.tasks.domain.mds.task.OperatorType.AFTER;
import static org.motechproject.tasks.domain.mds.task.OperatorType.AFTER_NOW;
import static org.motechproject.tasks.domain.mds.task.OperatorType.BEFORE;
import static org.motechproject.tasks.domain.mds.task.OperatorType.BEFORE_NOW;
import static org.motechproject.tasks.domain.mds.task.OperatorType.CONTAINS;
import static org.motechproject.tasks.domain.mds.task.OperatorType.ENDSWITH;
import static org.motechproject.tasks.domain.mds.task.OperatorType.EQUALS;
import static org.motechproject.tasks.domain.mds.task.OperatorType.EQUALS_IGNORE_CASE;
import static org.motechproject.tasks.domain.mds.task.OperatorType.EQ_NUMBER;
import static org.motechproject.tasks.domain.mds.task.OperatorType.EXIST;
import static org.motechproject.tasks.domain.mds.task.OperatorType.GT;
import static org.motechproject.tasks.domain.mds.task.OperatorType.LESS_DAYS_FROM_NOW;
import static org.motechproject.tasks.domain.mds.task.OperatorType.LESS_MONTHS_FROM_NOW;
import static org.motechproject.tasks.domain.mds.task.OperatorType.LT;
import static org.motechproject.tasks.domain.mds.task.OperatorType.MORE_DAYS_FROM_NOW;
import static org.motechproject.tasks.domain.mds.task.OperatorType.MORE_MONTHS_FROM_NOW;
import static org.motechproject.tasks.domain.mds.task.OperatorType.STARTSWITH;
import static org.motechproject.tasks.domain.mds.task.OperatorType.AND;
import static org.motechproject.tasks.domain.mds.task.OperatorType.OR;
import static org.motechproject.tasks.domain.enums.ParameterType.DATE;
import static org.motechproject.tasks.domain.enums.ParameterType.INTEGER;
import static org.motechproject.tasks.domain.enums.ParameterType.TEXTAREA;
import static org.motechproject.tasks.domain.enums.ParameterType.UNICODE;
import static org.motechproject.tasks.domain.enums.ParameterType.BOOLEAN;

@RunWith(MockitoJUnitRunner.class)
public class TaskFilterExecutorTest {

    @Mock
    private TaskActivityService activityService;

    @Test
    public void testcheckFilters() throws TaskHandlerException {
        DateTime dateTime = DateTime.now().minusDays(2);

        DataSource dataSource = new DataSource("ProviderName", null, 0L, "", "", "", null, false);
        TaskConfig taskConfig = mock(TaskConfig.class);
        when(taskConfig.getDataSource(anyString(), anyLong(), anyString())).thenReturn(dataSource);

        Task task = new TaskBuilder().addAction(new TaskActionInformation()).build();
        TaskContext taskContext = new TaskContext(task, null, null, activityService);
        TaskFilterExecutor taskFilterExecutor = new TaskFilterExecutor();

        assertTrue(taskFilterExecutor.checkFilters(null, null, taskContext));
        assertTrue(taskFilterExecutor.checkFilters(new ArrayList<>(), null, taskContext));

        List<Filter> filters = new ArrayList<>();
        filters.add(new Filter("EventName", "trigger.eventName", UNICODE, true, CONTAINS.getValue(), "ven"));
        filters.add(new Filter("EventName", "trigger.eventName", TEXTAREA, true, EXIST.getValue(), ""));
        filters.add(new Filter("EventName", "trigger.eventName", UNICODE, true, EQUALS.getValue(), "event name"));
        filters.add(new Filter("EventName", "trigger.eventName", UNICODE, true, EQUALS_IGNORE_CASE.getValue(), "EvEnT nAmE"));
        filters.add(new Filter("EventName", "trigger.eventName", UNICODE, true, STARTSWITH.getValue(), "ev"));
        filters.add(new Filter("EventName", "trigger.eventName", UNICODE, true, ENDSWITH.getValue(), "me"));

        filters.add(new Filter("ExternalID", "trigger.externalId", INTEGER, true, GT.getValue(), "19"));
        filters.add(new Filter("ExternalID", "trigger.externalId", INTEGER, false, GT.getValue(), "1234567891"));
        filters.add(new Filter("ExternalID", "trigger.externalId", INTEGER, true, LT.getValue(), "1234567891"));
        filters.add(new Filter("ExternalID", "trigger.externalId", INTEGER, false, LT.getValue(), "123"));
        filters.add(new Filter("ExternalID", "trigger.externalId", INTEGER, true, EQ_NUMBER.getValue(), "123456789"));
        filters.add(new Filter("ExternalID", "trigger.externalId", INTEGER, false, EQ_NUMBER.getValue(), "789"));
        filters.add(new Filter("ExternalID", "trigger.externalId", INTEGER, true, EXIST.getValue(), ""));

        filters.add(new Filter("CMS Lite.StreamContent#0.Name", "ad.1.StreamContent#0.name", UNICODE, true, CONTAINS.getValue(), "am"));
        filters.add(new Filter("CMS Lite.StreamContent#0.Name", "ad.1.StreamContent#0.name", UNICODE, true, EXIST.getValue(), ""));
        filters.add(new Filter("CMS Lite.StreamContent#0.Name", "ad.1.StreamContent#0.name", UNICODE, true, EQUALS.getValue(), "name"));
        filters.add(new Filter("CMS Lite.StreamContent#0.Name", "ad.1.StreamContent#0.name", UNICODE, true, EQUALS_IGNORE_CASE.getValue(), "nAmE"));
        filters.add(new Filter("CMS Lite.StreamContent#0.Name", "ad.1.StreamContent#0.name", UNICODE, true, STARTSWITH.getValue(), "na"));
        filters.add(new Filter("CMS Lite.StreamContent#0.Name", "ad.1.StreamContent#0.name", UNICODE, true, ENDSWITH.getValue(), "me"));

        filters.add(new Filter("MRS.Person#1.Age", "ad.2.Person#1.age", INTEGER, true, GT.getValue(), "30"));
        filters.add(new Filter("MRS.Person#1.Age", "ad.2.Person#1.age", INTEGER, true, LT.getValue(), "50"));
        filters.add(new Filter("MRS.Person#1.Age", "ad.2.Person#1.age", INTEGER, true, EQ_NUMBER.getValue(), "46"));
        filters.add(new Filter("MRS.Person#1.Age", "ad.2.Person#1.age", INTEGER, true, EXIST.getValue(), ""));
        filters.add(new Filter("MRS.Person#1.Age", "ad.2.Person#1.age", INTEGER, false, GT.getValue(), "100"));

        filters.add(new Filter("MRS.Person#1.Dead", "ad.2.Person#1.dead", BOOLEAN, false, EXIST.getValue(), ""));
        filters.add(new Filter("MRS.Person#1.Dead", "ad.2.Person#1.dead", BOOLEAN, false, AND.getValue(), "false"));
        filters.add(new Filter("MRS.Person#1.Dead", "ad.2.Person#1.dead", BOOLEAN, true, OR.getValue(), "true"));

        taskContext = new TaskContext(task, new HashMap<>(), new HashMap<>(), activityService);
        assertFalse(taskFilterExecutor.checkFilters(filters, LogicalOperator.AND, taskContext));
        assertTrue(taskFilterExecutor.checkFilters(filters, LogicalOperator.OR, taskContext));

        Map<String, Object> triggerParameters = new HashMap<>();
        triggerParameters.put("eventName", "etName");
        triggerParameters.put("externalId", "12345");

        taskContext = new TaskContext(task, triggerParameters, new HashMap<>(), activityService);
        taskContext.addDataSourceObject("0", new StreamContent("Eman"), false);
        taskContext.addDataSourceObject("1", new Person(150, true), false);
        assertFalse(taskFilterExecutor.checkFilters(filters, LogicalOperator.AND, taskContext));
        assertTrue(taskFilterExecutor.checkFilters(filters, LogicalOperator.OR, taskContext));

        triggerParameters.put("eventName", "event name");
        triggerParameters.put("externalId", "123456789");
        taskContext = new TaskContext(task, triggerParameters, new HashMap<>(), activityService);
        taskContext.addDataSourceObject("0", new StreamContent("name"), false);
        taskContext.addDataSourceObject("1", new Person(46, false), false);
        assertTrue(taskFilterExecutor.checkFilters(filters, LogicalOperator.AND, taskContext));

        Filter equals = new Filter("Test date", "trigger.test_date", DATE, true, EQUALS.getValue(), dateTime.toString());
        Filter after = new Filter("Test date", "trigger.test_date", DATE, false, AFTER.getValue(), DateUtil.now().toString());
        Filter afterNow = new Filter("Test date", "trigger.test_date", DATE, false, AFTER_NOW.getValue(), "");
        Filter before = new Filter("Test date", "trigger.test_date", DATE, true, BEFORE.getValue(), DateUtil.now().toString());
        Filter beforeNow = new Filter("Test date", "trigger.test_date", DATE, true, BEFORE_NOW.getValue(), "");
        Filter lessDays = new Filter("Test date", "trigger.test_date", DATE, true, LESS_DAYS_FROM_NOW.getValue(), "3");
        Filter moreDays = new Filter("Test date", "trigger.test_date", DATE, true, MORE_DAYS_FROM_NOW.getValue(), "0");

        filters.add(equals);
        filters.add(after);
        filters.add(afterNow);
        filters.add(before);
        filters.add(beforeNow);
        filters.add(new Filter("Test date", "trigger.test_date", DATE, true, EXIST.getValue(), ""));
        filters.add(lessDays);
        filters.add(moreDays);

        triggerParameters.put("test_date", dateTime.toString());
        taskContext = new TaskContext(task, triggerParameters, new HashMap<>(), activityService);
        taskContext.addDataSourceObject("0", new StreamContent("name"), false);
        taskContext.addDataSourceObject("1", new Person(46, false), false);
        assertTrue(taskFilterExecutor.checkFilters(filters, LogicalOperator.AND, taskContext));

        dateTime = dateTime.plusDays(4);
        triggerParameters.put("test_date", dateTime.toString());

        filters.remove(equals);
        filters.remove(after);
        filters.remove(afterNow);
        filters.remove(before);
        filters.remove(beforeNow);
        filters.remove(lessDays);
        filters.remove(moreDays);
        filters.add(new Filter("Test date", "trigger.test_date", DATE, true, LESS_MONTHS_FROM_NOW.getValue(), "5"));
        filters.add(new Filter("Test date", "trigger.test_date", DATE, true, MORE_MONTHS_FROM_NOW.getValue(), "1"));
        dateTime = DateTime.now().minusMonths(3);

        triggerParameters.put("test_date", dateTime.toString());
        taskContext = new TaskContext(task, triggerParameters, new HashMap<>(), activityService);
        taskContext.addDataSourceObject("0", new StreamContent("name"), false);
        taskContext.addDataSourceObject("1", new Person(46, false), false);
        assertTrue(taskFilterExecutor.checkFilters(filters, LogicalOperator.AND, taskContext));

        dateTime = dateTime.plusMonths(6);

        triggerParameters.put("test_date", dateTime.toString());
        taskContext = new TaskContext(task, triggerParameters, new HashMap<>(), activityService);
        taskContext.addDataSourceObject("0", new StreamContent("name"), false);
        taskContext.addDataSourceObject("1", new Person(46, false), false);
        assertTrue(taskFilterExecutor.checkFilters(filters, LogicalOperator.AND, taskContext));

        equals.setExpression(dateTime.toString());
        after.setNegationOperator(!after.isNegationOperator());
        afterNow.setNegationOperator(!afterNow.isNegationOperator());
        before.setNegationOperator(!before.isNegationOperator());
        beforeNow.setNegationOperator(!beforeNow.isNegationOperator());

        taskContext = new TaskContext(task, triggerParameters, new HashMap<>(), activityService);
        taskContext.addDataSourceObject("0", new StreamContent("name"), false);
        taskContext.addDataSourceObject("1", new Person(46, false), false);
        assertTrue(taskFilterExecutor.checkFilters(filters, LogicalOperator.AND, taskContext));

        Filter triggerFilter = new Filter("Trigger.Event Name", "trigger.eventName", UNICODE, true, "abc", "");
        filters.add(triggerFilter);
        Filter additionalDataFilter = new Filter("CMS Lite.StreamContent#0.Name", "ad.1.StreamContent#0.name", UNICODE, true, "abc", "");
        filters.add(additionalDataFilter);

        taskContext = new TaskContext(task, triggerParameters, new HashMap<>(), activityService);
        taskContext.addDataSourceObject("0", new StreamContent("name"), false);
        taskContext.addDataSourceObject("1", new Person(46, true), false);
        assertFalse(taskFilterExecutor.checkFilters(filters, LogicalOperator.AND, taskContext));
        assertTrue(taskFilterExecutor.checkFilters(filters, LogicalOperator.OR, taskContext));

        filters.remove(triggerFilter);
        filters.add(new Filter("Trigger.External Id", "trigger.externalId", INTEGER, true, "abc", ""));
        filters.remove(additionalDataFilter);
        filters.add(new Filter("MRS.Person#1.Age", "ad.2.Person#1.age", INTEGER, true, "abc", ""));

        taskContext = new TaskContext(task, triggerParameters, new HashMap<>(), activityService);
        taskContext.addDataSourceObject("0", new StreamContent("name"), false);
        taskContext.addDataSourceObject("1", new Person(46, true), false);
        assertFalse(taskFilterExecutor.checkFilters(filters, LogicalOperator.AND, taskContext));
        assertTrue(taskFilterExecutor.checkFilters(filters, LogicalOperator.OR, taskContext));
    }

    @Test(expected = TaskHandlerException.class)
    public void shouldThrowExceptionIfDataSourceObjectIsNotFound() throws TaskHandlerException {
        List<Filter> filters = new ArrayList<>();
        filters.add(new Filter("MRS.Person#2.Age", "ad.2.Person#2.age", INTEGER, false, EXIST.getValue(), ""));

        Task task = new TaskBuilder().addAction(new TaskActionInformation()).build();
        TaskContext taskContext = new TaskContext(task, new HashMap<>(), new HashMap<>(), activityService);
        new TaskFilterExecutor().checkFilters(filters, LogicalOperator.AND, taskContext);
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

        private boolean dead;

        public Person(int age, boolean dead) {
            this.age = age;
            this.dead = dead;
        }

        public int getAge() {
            return age;
        }

        public boolean getDead() { return dead; }
    }
}
