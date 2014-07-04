package org.motechproject.tasks.it;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tasks.domain.TaskActivity;
import org.motechproject.tasks.repository.TaskActivitiesDataService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.motechproject.tasks.domain.TaskActivityType.ERROR;
import static org.motechproject.tasks.domain.TaskActivityType.SUCCESS;
import static org.motechproject.tasks.domain.TaskActivityType.WARNING;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class TaskActivitiesDataServiceIT extends BasePaxIT {

    private static final Long TASK_ID_1 = 12345l;
    private static final List<String> FIELD = Arrays.asList("phone");
    private static final Long TASK_ID_2 = 54321l;

    @Inject
    private TaskActivitiesDataService taskActivitiesDataService;

    @Test
    public void shouldFindTaskActivitiesByTaskId() {
        TaskActivity errorMsg = new TaskActivity(ERROR.getValue(), FIELD, TASK_ID_1, ERROR);
        TaskActivity successMsg = new TaskActivity(SUCCESS.getValue(), TASK_ID_1, SUCCESS);
        TaskActivity warningMsg = new TaskActivity(WARNING.getValue(), TASK_ID_2, WARNING);

        taskActivitiesDataService.create(errorMsg);
        taskActivitiesDataService.create(warningMsg);
        taskActivitiesDataService.create(successMsg);

        assertEquals(asList(errorMsg, warningMsg, successMsg), taskActivitiesDataService.retrieveAll());

        List<TaskActivity> messages = taskActivitiesDataService.byTask(TASK_ID_1);

        assertEquals(asList(errorMsg, successMsg), messages);

        assertEquals(ERROR, messages.get(0).getActivityType());
        assertEquals(TASK_ID_1, messages.get(0).getTask());
        assertEquals(ERROR.getValue(), messages.get(0).getMessage());
        assertEquals(FIELD, messages.get(0).getFields());

        assertEquals(SUCCESS, messages.get(1).getActivityType());
        assertEquals(TASK_ID_1, messages.get(1).getTask());
        assertEquals(SUCCESS.getValue(), messages.get(1).getMessage());

        messages = taskActivitiesDataService.byTask(TASK_ID_2);

        assertEquals(asList(warningMsg), messages);

        assertEquals(WARNING, messages.get(0).getActivityType());
        assertEquals(TASK_ID_2, messages.get(0).getTask());
        assertEquals(WARNING.getValue(), messages.get(0).getMessage());
    }

    @After
    public void tearDown() {
        taskActivitiesDataService.deleteAll();
    }
}
