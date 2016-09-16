package org.motechproject.tasks.it;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.util.Order;
import org.motechproject.tasks.domain.mds.task.TaskActivity;
import org.motechproject.tasks.domain.enums.TaskActivityType;
import org.motechproject.tasks.domain.mds.task.TaskExecutionProgress;
import org.motechproject.tasks.repository.TaskActivitiesDataService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.testing.utils.TimeFaker;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.motechproject.tasks.domain.enums.TaskActivityType.ERROR;
import static org.motechproject.tasks.domain.enums.TaskActivityType.SUCCESS;
import static org.motechproject.tasks.domain.enums.TaskActivityType.WARNING;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class TaskActivitiesDataServiceBundleIT extends BasePaxIT {

    private static final Long TASK_ID_1 = 12345l;
    private static final List<String> FIELD = Arrays.asList("phone");
    private static final Long TASK_ID_2 = 54321l;

    @Inject
    private TaskActivitiesDataService taskActivitiesDataService;

    @Before
    @After
    public void clearActivities() {
        clearDb();
    }

    @Test
    public void shouldFindTaskActivitiesByTaskId() {
        TaskActivity errorMsg = new TaskActivity(ERROR.getValue(), FIELD, TASK_ID_1, ERROR, new TaskExecutionProgress(1));
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

    @Test
    public void shouldReturnLatestRecordsOrderedByDate() {
        setUpActivityRecords();

        List<TaskActivity> allActivities = taskActivitiesDataService.retrieveAll();
        List<TaskActivity> activities = taskActivitiesDataService.retrieveAll(new QueryParams(1, 10, new Order("date", Order.Direction.DESC)));

        //There should always be only 10 records returned
        assertEquals(10, activities.size());

        //The first activity should have the most recent one
        DateTime mostRecentDate = new DateTime(0);
        for (TaskActivity activity : allActivities) {
            if (activity.getDate().isAfter(mostRecentDate)) {
                mostRecentDate = activity.getDate();
            }
        }
        assertEquals(mostRecentDate, activities.get(0).getDate());

        //All recent activities should be sorted by date
        for (int i = 1; i < 10; i++) {
            assertTrue(activities.get(i - 1).getDate().isAfter(activities.get(i).getDate()));
        }
    }

    private void clearDb() {
        taskActivitiesDataService.deleteAll();
    }

    private void setUpActivityRecords() {
        for(int i = 0; i < 50; i++) {
            TimeFaker.fakeNow(new DateTime(2014, 12, 7, 12, 53, i));
            taskActivitiesDataService.create(new TaskActivity("task executed", 1L, TaskActivityType.SUCCESS));
        }
    }
}
