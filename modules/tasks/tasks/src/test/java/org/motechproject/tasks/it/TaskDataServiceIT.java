package org.motechproject.tasks.it;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActionInformation;
import org.motechproject.tasks.domain.TaskBuilder;
import org.motechproject.tasks.domain.TaskTriggerInformation;
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

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class TaskDataServiceIT extends BasePaxIT {

    @Inject
    private TasksDataService tasksDataService;

    @Inject
    private TaskService taskService;

    @Test
    public void shouldAddAndUpdateTask() {
        TaskActionInformation action = new TaskActionInformation("send", "test", "test", "0.15", "SEND", new HashMap<String, String>());
        TaskTriggerInformation trigger = new TaskTriggerInformation("receive", "test", "test", "0.14", "RECEIVE");
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
                .withTrigger(new TaskTriggerInformation("receive", "test", "test", "0.14", "RECEIVE"))
                .addAction(new TaskActionInformation("send", "test", "test", "0.15", "SEND", new HashMap<String, String>()))
                .build();

        tasksDataService.create(expected);

        List<Task> tasks = tasksDataService.retrieveAll();

        assertEquals(asList(expected), tasks);
    }

    @Test
    public void shouldFindTasksByTriggerSubject() {
        TaskActionInformation action = new TaskActionInformation("send", "test", "test", "0.15", "SEND", new HashMap<String, String>());

        TaskTriggerInformation trigger1 = new TaskTriggerInformation("receive-1", "test", "test", "0.14", "RECEIVE-1");
        TaskTriggerInformation trigger2 = new TaskTriggerInformation("receive-2", "test", "test", "0.14", "RECEIVE-2");

        Task expected1 = new Task("name", trigger1, asList(action), null, true, false);
        Task expected2 = new Task("name", trigger2, asList(action), null, true, false);
        Task expected3 = new Task("name", new TaskTriggerInformation(trigger1), asList(action), null, true, false);

        tasksDataService.create(expected1);
        tasksDataService.create(expected2);
        tasksDataService.create(expected3);

        assertEquals(new ArrayList<Task>(), taskService.findTasksForTriggerSubject(""));
        assertEquals(asList(expected1, expected3), taskService.findTasksForTriggerSubject(trigger1.getSubject()));
        assertEquals(asList(expected2), taskService.findTasksForTriggerSubject(trigger2.getSubject()));
    }

    @Test
    public void shouldFindTasksThatDependOnAModule() {
        TaskTriggerInformation trigger1 = new TaskTriggerInformation("trigger1", "best", "test", "0.14", "RECEIVE-1");
        TaskTriggerInformation trigger2 = new TaskTriggerInformation("trigger2", "lest", "jest", "0.14", "RECEIVE-2");

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

    @Before
    public void setUp() {
        tasksDataService.deleteAll();
    }

    @After
    public void tearDown() {
        tasksDataService.deleteAll();
    }
}
