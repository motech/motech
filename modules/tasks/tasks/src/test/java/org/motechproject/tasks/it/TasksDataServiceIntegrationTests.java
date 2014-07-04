package org.motechproject.tasks.it;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ChannelsDataServiceIT.class,  TaskDataServiceIT.class,
                     TaskActivitiesDataServiceIT.class})
public class TasksDataServiceIntegrationTests {
}
