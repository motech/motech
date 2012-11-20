package org.motechproject.tasks.repository;

import org.ektorp.CouchDbConnector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tasks.domain.TaskStatusMessage;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.motechproject.tasks.domain.Level.ERROR;
import static org.motechproject.tasks.domain.Level.SUCCESS;
import static org.motechproject.tasks.domain.Level.WARNING;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/motech/*.xml"})
public class AllTaskStatusMessagesIT extends SpringIntegrationTest {

    @Autowired
    private AllTaskStatusMessages allTaskStatusMessages;

    @Autowired
    @Qualifier("taskDbConnector")
    private CouchDbConnector couchDbConnector;

    @Test
    public void test_byTaskId() {
        TaskStatusMessage errorMsg = new TaskStatusMessage(ERROR.getValue(), "12345", ERROR);
        TaskStatusMessage successMsg = new TaskStatusMessage(SUCCESS.getValue(), "12345", SUCCESS);
        TaskStatusMessage warningMsg = new TaskStatusMessage(WARNING.getValue(), "54321", WARNING);

        allTaskStatusMessages.add(errorMsg);
        allTaskStatusMessages.add(warningMsg);
        allTaskStatusMessages.add(successMsg);

        assertEquals(3, allTaskStatusMessages.getAll().size());

        List<TaskStatusMessage> messages = allTaskStatusMessages.byTaskId("12345");

        assertEquals(2, messages.size());

        assertEquals(ERROR, messages.get(0).getLevel());
        assertEquals("12345", messages.get(0).getTask());
        assertEquals(ERROR.getValue(), messages.get(0).getMessage());

        assertEquals(SUCCESS, messages.get(1).getLevel());
        assertEquals("12345", messages.get(1).getTask());
        assertEquals(SUCCESS.getValue(), messages.get(1).getMessage());

        markForDeletion(messages);

        messages = allTaskStatusMessages.byTaskId("54321");

        assertEquals(1, messages.size());

        assertEquals(WARNING, messages.get(0).getLevel());
        assertEquals("54321", messages.get(0).getTask());
        assertEquals(WARNING.getValue(), messages.get(0).getMessage());

        markForDeletion(messages);
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return couchDbConnector;
    }

}
