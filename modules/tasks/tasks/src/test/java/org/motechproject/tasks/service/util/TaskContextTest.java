package org.motechproject.tasks.service.util;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.event.MotechEvent;
import org.motechproject.tasks.domain.KeyInformation;
import org.motechproject.tasks.domain.mds.task.Task;
import org.motechproject.tasks.domain.mds.task.TaskActionInformation;
import org.motechproject.tasks.domain.mds.task.builder.TaskBuilder;
import org.motechproject.tasks.constants.TaskFailureCause;
import org.motechproject.tasks.exception.TaskHandlerException;
import org.motechproject.tasks.service.TaskActivityService;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.motechproject.tasks.domain.KeyInformation.TRIGGER_PREFIX;
import static org.motechproject.tasks.domain.KeyInformation.parse;

@RunWith(MockitoJUnitRunner.class)
public class TaskContextTest {

    private static final Long OBJECT_ID = 1L;
    private static final String EVENT_KEY = "event.key";
    private static final String EVENT_KEY_VALUE = "trigger.event.key.value";

    @Mock
    private TaskActivityService activityService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    public class TestDataSourceObject {
        private int id;
        private boolean checked;

        private TestDataSourceObject() {
            this.id = OBJECT_ID.intValue();
            this.checked = true;
        }

        public int getId() {
            return id;
        }

        public boolean isChecked() {
            return checked;
        }
    }

    @Test
    public void shouldThrowExceptionWhenAccessingUnconfiguredDataSource() throws Exception {
        Task task = new TaskBuilder().addAction(new TaskActionInformation()).build();
        TaskContext taskContext = new TaskContext(task, null, null, activityService);
        taskContext.addDataSourceObject("1", new TestDataSourceObject(), false);

        KeyInformation key = parse("ad.1.Integer#2.id");

        expectedException.expect(TaskHandlerException.class);
        expectedException.expect(new TaskHandlerExceptionMatcher(TaskFailureCause.DATA_SOURCE, "task.error.objectOfTypeNotFound", key.getObjectType()));
        taskContext.getDataSourceObjectValue(key.getObjectId().toString(), key.getKey(), key.getObjectType());
    }

    @Test
    public void shouldThrowExceptionWhenDataSourceIsNull() throws Exception {
        Task task = new TaskBuilder().addAction(new TaskActionInformation()).build();
        TaskContext taskContext = new TaskContext(task, null, null, activityService);
        taskContext.addDataSourceObject("1", null, true);

        KeyInformation key = parse("ad.1.Integer#1.id");

        expectedException.expect(TaskHandlerException.class);
        expectedException.expect(new TaskHandlerExceptionMatcher(TaskFailureCause.DATA_SOURCE, "task.error.objectOfTypeNotFound", key.getObjectType()));
        taskContext.getDataSourceObjectValue(key.getObjectId().toString(), key.getKey(), key.getObjectType());
    }

    @Test
    public void shouldNotThrowExceptionWhenDataSourceIsNull_IfFailNotFoundIsFalse() throws Exception {
        Task task = new TaskBuilder().addAction(new TaskActionInformation()).build();
        TaskContext taskContext = new TaskContext(task, null, null, activityService);
        taskContext.addDataSourceObject("1", null, false);

        KeyInformation key = parse("ad.1.Integer#1.id");

        assertNull(taskContext.getDataSourceObjectValue(key.getObjectId().toString(), key.getKey(), key.getObjectType()));
    }

    @Test
    public void shouldThrowExceptionWhenDataSourceFieldValueEvaluationThrowsException() throws Exception {
        Task task = new TaskBuilder().addAction(new TaskActionInformation()).build();
        TaskContext taskContext = new TaskContext(task, null, null, activityService);
        taskContext.addDataSourceObject("1", new TestDataSourceObject(), true);

        KeyInformation key = parse("ad.1.Integer#1.providerId");

        expectedException.expect(TaskHandlerException.class);
        expectedException.expect(new TaskHandlerExceptionMatcher(TaskFailureCause.DATA_SOURCE, "task.error.objectDoesNotContainField", key.getKey()));
        taskContext.getDataSourceObjectValue(key.getObjectId().toString(), key.getKey(), key.getObjectType());
    }

    @Test
    public void shouldNotThrowExceptionWhenDataSourceFieldValueEvaluationThrowsException_IfFailNotFoundIsFalse() throws Exception {
        Task task = new TaskBuilder().addAction(new TaskActionInformation()).build();
        TaskContext taskContext = new TaskContext(task, null, null, activityService);
        taskContext.addDataSourceObject("1", new TestDataSourceObject(), false);

        KeyInformation key = parse("ad.1.Integer#1.providerId");

        assertNull(taskContext.getDataSourceObjectValue(key.getObjectId().toString(), key.getKey(), key.getObjectType()));
    }

    @Test
    public void testGetDataSourceValue() throws Exception {
        Task task = new TaskBuilder().addAction(new TaskActionInformation()).build();
        TaskContext taskContext = new TaskContext(task, null, null, activityService);
        taskContext.addDataSourceObject("1", new TestDataSourceObject(), true);

        KeyInformation key = parse("ad.1.Integer#1.id");

        assertEquals(OBJECT_ID.intValue(), taskContext.getDataSourceObjectValue(key.getObjectId().toString(), key.getKey(), key.getObjectType()));
    }

    @Test
    public void testGetDataSourceValueForBooleanWithGetterIs() throws Exception {
        Task task = new TaskBuilder().addAction(new TaskActionInformation()).build();
        TaskContext taskContext = new TaskContext(task, null, null, activityService);
        taskContext.addDataSourceObject("1", new TestDataSourceObject(), true);

        KeyInformation key = parse("ad.1.Boolean#1.checked");

        assertEquals(true, taskContext.getDataSourceObjectValue(key.getObjectId().toString(), key.getKey(), key.getObjectType()));
    }

    @Test
    public void testGetTriggerKey() throws Exception {
        Map<String, Object> parameters = new HashMap<>();

        Task task = new TaskBuilder().addAction(new TaskActionInformation()).build();
        TaskContext taskContext = new TaskContext(task, null, null, activityService);

        KeyInformation key = parse(String.format("%s.%s", TRIGGER_PREFIX, EVENT_KEY));
        assertEquals(null, taskContext.getTriggerValue(key.getKey()));

        Map<String, String> child = new HashMap<>();
        child.put("key", EVENT_KEY_VALUE);
        parameters.put("event", child);

        taskContext = new TaskContext(task, parameters, null, activityService);

        assertEquals(EVENT_KEY_VALUE, taskContext.getTriggerValue(key.getKey()));
    }

    @Test(expected = IllegalStateException.class)
    public void testGetTriggerKeyShouldThrowException() throws Exception {
        MotechEvent event = mock(MotechEvent.class);
        when(event.getParameters()).thenReturn(new HashMap<>());

        KeyInformation key = parse(String.format("%s.%s", TRIGGER_PREFIX, EVENT_KEY));

        Task task = new TaskBuilder().addAction(new TaskActionInformation()).build();
        new TaskContext(task, event.getParameters(), null, activityService).getTriggerValue(key.getKey());
    }

    @Test
    public void testGetNullTriggerKey() throws Exception {
        Map<String, Object> parameters = new HashMap<>();

        MotechEvent event = mock(MotechEvent.class);
        KeyInformation key = parse(String.format("%s.%s", TRIGGER_PREFIX, EVENT_KEY));

        Map<String, String> child = new HashMap<>();
        child.put("key", null);

        parameters.put("event", child);

        Task task = new TaskBuilder().addAction(new TaskActionInformation()).build();

        assertEquals(null, new TaskContext(task, parameters, null, activityService).getTriggerValue(key.getKey()));

        // should not throw any exceptions
        assertNull(new TaskContext(task, parameters, null, activityService).getTriggerValue(key.getKey()));
    }

    private class TaskHandlerExceptionMatcher extends TypeSafeMatcher<Object> {

        TaskFailureCause expectedFailureCause;
        String expectedMessageKey;
        String expectedObject;

        private TaskHandlerExceptionMatcher(TaskFailureCause expectedFailureCause, String expectedMessageKey, String expectedObject) {
            this.expectedFailureCause = expectedFailureCause;
            this.expectedMessageKey = expectedMessageKey;
            this.expectedObject = expectedObject;
        }

        @Override
        public boolean matchesSafely(Object item) {
            TaskHandlerException actual = (TaskHandlerException) item;
            return new EqualsBuilder()
                    .append(expectedFailureCause, actual.getFailureCause())
                    .append(expectedMessageKey, actual.getMessage())
                    .append(expectedObject, actual.getArgs().get(0)).isEquals();
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("\nExpected Failure Cause:")
                    .appendValue(expectedFailureCause.toString())
                    .appendText("\nExpected Message Key:")
                    .appendValue(expectedMessageKey)
                    .appendText("\nExpected  Argument:")
                    .appendValue(expectedObject);
        }
    }
}
