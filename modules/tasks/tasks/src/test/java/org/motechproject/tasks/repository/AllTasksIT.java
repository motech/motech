package org.motechproject.tasks.repository;

import org.ektorp.CouchDbConnector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tasks.domain.Task;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/motech/*.xml"})
public class AllTasksIT extends SpringIntegrationTest {

    @Autowired
    private AllTasks allTasks;

    @Autowired
    @Qualifier("taskDbConnector")
    private CouchDbConnector couchDbConnector;

    @Test
    public void test_addOrUpdate() {
        Task expected = new Task("test:test:15:SEND", "test:test:14:RECEIVE", new HashMap<String, String>());

        allTasks.addOrUpdate(expected);

        List<Task> tasks = allTasks.getAll();

        assertEquals(1, tasks.size());

        Task actual = tasks.get(0);

        assertEquals(expected, actual);

        allTasks.addOrUpdate(actual);

        tasks = allTasks.getAll();

        assertEquals(1, tasks.size());

        actual = tasks.get(0);

        assertEquals(expected, actual);

        markForDeletion(actual);
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return couchDbConnector;
    }

}
