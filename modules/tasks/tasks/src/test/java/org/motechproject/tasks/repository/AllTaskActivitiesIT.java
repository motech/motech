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

import static java.util.Arrays.asList;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.motechproject.tasks.domain.TaskActivityType.ERROR;
import static org.motechproject.tasks.domain.TaskActivityType.SUCCESS;
import static org.motechproject.tasks.domain.TaskActivityType.WARNING;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/motech/*.xml"})
public class AllTaskActivitiesIT extends SpringIntegrationTest {
    private static final String TASK_ID_1 = "12345";
    private static final String[] FIELD = new String[]{"phone"};
    private static final String TASK_ID_2 = "54321";

    @Autowired
    private AllTaskActivities allTaskActivities;

    @Autowired
    @Qualifier("taskDbConnector")
    private CouchDbConnector couchDbConnector;

    @Test
    public void shouldFindTaskActivitiesByTaskId() {
        TaskActivity errorMsg = new TaskActivity(ERROR.getValue(), FIELD, TASK_ID_1, ERROR);
        TaskActivity successMsg = new TaskActivity(SUCCESS.getValue(), TASK_ID_1, SUCCESS);
        TaskActivity warningMsg = new TaskActivity(WARNING.getValue(), TASK_ID_2, WARNING);

        allTaskActivities.add(errorMsg);
        allTaskActivities.add(warningMsg);
        allTaskActivities.add(successMsg);

        assertEquals(asList(errorMsg, warningMsg, successMsg), allTaskActivities.getAll());

        List<TaskActivity> messages = allTaskActivities.byTaskId(TASK_ID_1);

        assertEquals(asList(errorMsg, successMsg), messages);

        assertEquals(ERROR, messages.get(0).getActivityType());
        assertEquals(TASK_ID_1, messages.get(0).getTask());
        assertEquals(ERROR.getValue(), messages.get(0).getMessage());
        assertArrayEquals(FIELD, messages.get(0).getFields());

        assertEquals(SUCCESS, messages.get(1).getActivityType());
        assertEquals(TASK_ID_1, messages.get(1).getTask());
        assertEquals(SUCCESS.getValue(), messages.get(1).getMessage());

        messages = allTaskActivities.byTaskId(TASK_ID_2);

        assertEquals(asList(warningMsg), messages);

        assertEquals(WARNING, messages.get(0).getActivityType());
        assertEquals(TASK_ID_2, messages.get(0).getTask());
        assertEquals(WARNING.getValue(), messages.get(0).getMessage());

        markForDeletion(allTaskActivities.getAll());
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return couchDbConnector;
    }

}
