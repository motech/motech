package org.motechproject.tasks.repository;

import org.ektorp.CouchDbConnector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tasks.domain.TaskActivity;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.motechproject.tasks.domain.TaskActivityType.ERROR;
import static org.motechproject.tasks.domain.TaskActivityType.SUCCESS;
import static org.motechproject.tasks.domain.TaskActivityType.WARNING;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/motech/*.xml"})
public class AllTaskActivitiesIT extends SpringIntegrationTest {

    @Autowired
    private AllTaskActivities allTaskActivities;

    @Autowired
    @Qualifier("taskDbConnector")
    private CouchDbConnector couchDbConnector;

    @Test
    public void shouldFindTaskActivitiesByTaskId() {
        TaskActivity errorMsg = new TaskActivity(ERROR.getValue(), "12345", ERROR);
        TaskActivity successMsg = new TaskActivity(SUCCESS.getValue(), "12345", SUCCESS);
        TaskActivity warningMsg = new TaskActivity(WARNING.getValue(), "54321", WARNING);

        allTaskActivities.add(errorMsg);
        allTaskActivities.add(warningMsg);
        allTaskActivities.add(successMsg);

        assertEquals(3, allTaskActivities.getAll().size());

        List<TaskActivity> messages = allTaskActivities.byTaskId("12345");

        assertEquals(2, messages.size());

        assertEquals(ERROR, messages.get(0).getActivityType());
        assertEquals("12345", messages.get(0).getTask());
        assertEquals(ERROR.getValue(), messages.get(0).getMessage());

        assertEquals(SUCCESS, messages.get(1).getActivityType());
        assertEquals("12345", messages.get(1).getTask());
        assertEquals(SUCCESS.getValue(), messages.get(1).getMessage());

        markForDeletion(messages);

        messages = allTaskActivities.byTaskId("54321");

        assertEquals(1, messages.size());

        assertEquals(WARNING, messages.get(0).getActivityType());
        assertEquals("54321", messages.get(0).getTask());
        assertEquals(WARNING.getValue(), messages.get(0).getMessage());

        markForDeletion(messages);
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return couchDbConnector;
    }

}
