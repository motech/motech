package org.motechproject.tasks.repository;

import org.ektorp.CouchDbConnector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActionInformation;
import org.motechproject.tasks.domain.TaskBuilder;
import org.motechproject.tasks.domain.TaskEventInformation;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/motech/*.xml"})
public class AllTasksIT extends SpringIntegrationTest {

    @Autowired
    private AllTasks allTasks;

    @Autowired
    @Qualifier("taskDbConnector")
    private CouchDbConnector couchDbConnector;

    @Test
    public void shouldAddAndUpdateTask() {
        TaskActionInformation action = new TaskActionInformation("send", "test", "test", "0.15", "SEND", new HashMap<String, String>());
        TaskEventInformation trigger = new TaskEventInformation("receive", "test", "test", "0.14", "RECEIVE");
        Task expected = new Task("name", trigger, asList(action));

        allTasks.addOrUpdate(expected);

        List<Task> tasks = allTasks.getAll();

        assertEquals(asList(expected), tasks);

        Task actual = tasks.get(0);

        actual.setName("newName");

        allTasks.addOrUpdate(actual);

        tasks = allTasks.getAll();

        assertEquals(asList(actual), tasks);

        markForDeletion(allTasks.getAll());
    }

    @Test
    public void shouldAddTaskAsNewIfItHasIDAndTaskNotExistInDB() {
        Task expected = new TaskBuilder()
                .withId("12345")
                .withName("name")
                .withTrigger(new TaskEventInformation("receive", "test", "test", "0.14", "RECEIVE"))
                .addAction(new TaskActionInformation("send", "test", "test", "0.15", "SEND", new HashMap<String, String>()))
                .build();

        allTasks.addOrUpdate(expected);

        List<Task> tasks = allTasks.getAll();

        assertEquals(asList(expected), tasks);

        markForDeletion(allTasks.getAll());
    }

    @Test
    public void shouldFindTasksByTriggerSubject() {
        TaskActionInformation action = new TaskActionInformation("send", "test", "test", "0.15", "SEND", new HashMap<String, String>());

        TaskEventInformation trigger1 = new TaskEventInformation("receive-1", "test", "test", "0.14", "RECEIVE-1");
        TaskEventInformation trigger2 = new TaskEventInformation("receive-2", "test", "test", "0.14", "RECEIVE-2");

        Task expected1 = new Task("name", trigger1, asList(action));
        Task expected2 = new Task("name", trigger2, asList(action));
        Task expected3 = new Task("name", trigger1, asList(action));

        allTasks.addOrUpdate(expected1);
        allTasks.addOrUpdate(expected2);
        allTasks.addOrUpdate(expected3);

        assertEquals(new ArrayList<Task>(), allTasks.byTriggerSubject(""));
        assertEquals(asList(expected1, expected3), allTasks.byTriggerSubject(trigger1.getSubject()));
        assertEquals(asList(expected2), allTasks.byTriggerSubject(trigger2.getSubject()));

        markForDeletion(allTasks.getAll());
    }

    @Test
    public void shouldFindTasksThatDependOnAModule() {
        TaskEventInformation trigger1 = new TaskEventInformation("trigger1", "best", "test", "0.14", "RECEIVE-1");
        TaskEventInformation trigger2 = new TaskEventInformation("trigger2", "lest", "jest", "0.14", "RECEIVE-2");

        TaskActionInformation action1 = new TaskActionInformation("action1", "test", "test", "0.15", "SEND");
        TaskActionInformation action2 = new TaskActionInformation("action2", "fest", "test", "0.12", "actionSubject");
        TaskActionInformation action3 = new TaskActionInformation("action2", "fest", "jest", "0.12", "actionSubject");

        Task[] tasks = new Task[]{
                new Task("task1", trigger1, asList(action1)),
                new Task("task2", trigger2, asList(action3)),
                new Task("task3", trigger1, asList(action2)),
                new Task("task4", trigger2, asList(action1)),
        };
        for (Task task : tasks) {
            allTasks.addOrUpdate(task);
        }

        List<String> tasksUsingTestModule = extract(allTasks.dependentOnModule("test"), on(Task.class).getName());
        assertTrue(tasksUsingTestModule.contains("task1"));
        assertTrue(tasksUsingTestModule.contains("task3"));
        assertTrue(tasksUsingTestModule.contains("task4"));
        assertFalse(tasksUsingTestModule.contains("task2"));

        markForDeletion(allTasks.getAll());
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return couchDbConnector;
    }
}
