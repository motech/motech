package org.motechproject.tasks.it;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tasks.domain.mds.task.Task;
import org.motechproject.tasks.domain.mds.task.TaskActionInformation;
import org.motechproject.tasks.domain.mds.task.builder.TaskBuilder;
import org.motechproject.tasks.domain.mds.task.TaskTriggerInformation;
import org.motechproject.tasks.repository.TasksDataService;
import org.motechproject.tasks.service.TaskService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class TaskDataServiceBundleIT extends BasePaxIT {

    private static final String MDS_ENTITIES_BUNDLE = "org.motechproject.motech-platform-dataservices-entities";

    @Inject
    private TasksDataService tasksDataService;

    @Inject
    private TaskService taskService;

    @Test
    public void shouldAddAndUpdateTask() {
        TaskActionInformation action = new TaskActionInformation("send", "test", "test", "0.15", "SEND", new HashMap<String, String>());
        TaskTriggerInformation trigger = new TaskTriggerInformation("receive", "test", "test", "0.14", "RECEIVE", null);
        Task expected = new Task("name", trigger, asList(action));

        tasksDataService.create(expected);

        List<Task> tasks = tasksDataService.retrieveAll();

        assertEquals(asList(expected), tasks);

        Task actual = tasks.get(0);

        actual.setName("newName");

        tasksDataService.update(actual);

        tasks = tasksDataService.retrieveAll();

        assertEquals(asList(actual), tasks);
    }

    @Test
    public void shouldAddTaskAsNewIfItHasIDAndTaskNotExistInDB() {
        Task expected = new TaskBuilder()
                .withId(1L)
                .withName("name")
                .withTrigger(new TaskTriggerInformation("receive", "test", "test", "0.14", "RECEIVE", "RECEIVE"))
                .addAction(new TaskActionInformation("send", "test", "test", "0.15", "SEND", new HashMap<String, String>()))
                .build();

        tasksDataService.create(expected);

        List<Task> tasks = tasksDataService.retrieveAll();
        assertEquals(asList(expected), tasks);
    }

    @Test
    public void shouldFindActiveTasksByTriggerSubject() {
        TaskActionInformation action = new TaskActionInformation("send", "test", MDS_ENTITIES_BUNDLE, "0.15", "SEND", new HashMap<String, String>());

        TaskTriggerInformation trigger1 = new TaskTriggerInformation("receive-1", "test", MDS_ENTITIES_BUNDLE, "0.14", "RECEIVE-1", null);
        TaskTriggerInformation trigger2 = new TaskTriggerInformation("receive-2", "test", "test", "0.14", "RECEIVE-2", null);

        Task expected1 = new Task("name1", trigger1, asList(action), null, true, true);
        Task expected2 = new Task("name2", trigger2, asList(action), null, true, false);
        Task expected3 = new Task("name3", new TaskTriggerInformation(trigger1), asList(action), null, true, true);

        tasksDataService.create(expected1);
        tasksDataService.create(expected2);
        tasksDataService.create(expected3);

        assertEquals(new ArrayList<Task>(), taskService.findActiveTasksForTriggerSubject(""));
        assertEquals(asList(expected1, expected3), taskService.findActiveTasksForTriggerSubject(trigger1.getSubject()));
        assertEquals(new ArrayList<Task>(), taskService.findActiveTasksForTriggerSubject(trigger2.getSubject()));
    }

    @Test
    public void shouldFindTasksByName() {
        String taskName = "test";

        Task[] tasks = new Task[]{
                new Task(taskName, null, null),
                new Task("abc", null, null),
                new Task("test2", null, null),
        };

        for (Task task : tasks) {
            tasksDataService.create(task);
        }

        List<String> tasksByName = extract(tasksDataService.findTasksByName(taskName), on(Task.class).getName());

        assertTrue(tasksByName.contains(taskName));
        assertFalse(tasksByName.contains("abc"));
        assertFalse(tasksByName.contains("test2"));
    }

    @Test
    public void shouldFindTasksThatDependOnAModule() {
        TaskTriggerInformation trigger1 = new TaskTriggerInformation("trigger1", "best", "test", "0.14", "RECEIVE-1", null);
        TaskTriggerInformation trigger2 = new TaskTriggerInformation("trigger2", "lest", "jest", "0.14", "RECEIVE-2", null);

        TaskActionInformation action1 = new TaskActionInformation("action1", "test", "test", "0.15", "SEND");
        TaskActionInformation action2 = new TaskActionInformation("action2", "fest", "test", "0.12", "actionSubject");
        TaskActionInformation action3 = new TaskActionInformation("action2", "fest", "jest", "0.12", "actionSubject");

        Task[] tasks = new Task[]{
                new Task("task1", trigger1, asList(action1)),
                new Task("task2", trigger2, asList(action3)),
                new Task("task3", new TaskTriggerInformation(trigger1), asList(action2)),
                new Task("task4", new TaskTriggerInformation(trigger2), asList(action1)),
        };
        for (Task task : tasks) {
            tasksDataService.create(task);
        }

        List<String> tasksUsingTestModule = extract(taskService.findTasksDependentOnModule("test"),
                on(Task.class).getName());
        assertTrue(tasksUsingTestModule.contains("task1"));
        assertTrue(tasksUsingTestModule.contains("task3"));
        assertTrue(tasksUsingTestModule.contains("task4"));
        assertFalse(tasksUsingTestModule.contains("task2"));
    }

    @Test
    public void shouldSaveMoreThan255CharactersInTaskActionInformation() {
        String value1 = RandomStringUtils.randomAlphanumeric(500);
        String value2 = RandomStringUtils.randomAlphanumeric(450);
        Map<String, String> values = new HashMap<>();
        values.put("value1", value1);
        values.put("value2", value2);

        TaskActionInformation action = new TaskActionInformation("action", "test", "test", "0.15", "SEND", values);
        TaskTriggerInformation trigger = new TaskTriggerInformation("trigger", "best", "test", "0.14", "RECEIVE-1", null);
        Task task = new Task("task1", trigger, asList(action));
        task = tasksDataService.create(task);

        Task taskFromDatabase = tasksDataService.findById(task.getId());

        assertEquals(value1, taskFromDatabase.getActions().get(0).getValues().get("value1"));
        assertEquals(value2, taskFromDatabase.getActions().get(0).getValues().get("value2"));
    }

    @Before
    public void setUp() {
        setUpSecurityContextForDefaultUser("manageTasks");
        tasksDataService.deleteAll();
    }

    @After
    public void tearDown() {
        tasksDataService.deleteAll();
    }
}
